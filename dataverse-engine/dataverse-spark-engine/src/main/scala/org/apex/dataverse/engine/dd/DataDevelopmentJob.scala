package org.apex.dataverse.engine.dd

import org.apex.dataverse.core.msg.{Request, Response}
import org.apex.dataverse.core.msg.packet.dd.{ExeSqlJobReqPacket, ExeSqlJobRspPacket}
import org.apex.dataverse.core.msg.packet.Packet
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apex.dataverse.core.enums.{CmdState, EngineEvent}
import org.apex.dataverse.core.msg.packet.di.DiReqPacket
import org.apex.dataverse.core.msg.packet.info.{ResultInfo, SqlInfo, StoreInfo}
import org.apex.dataverse.core.util.ExceptionUtil
import org.apex.dataverse.engine.cmd.CmdTracker
import org.apex.dataverse.engine.tick.EventTicker
import org.apex.dataverse.engine.util.{StatisticsUtil, TmpViewUtil}

import java.util
import java.util.List

/**
 *
 * @author : Danny.Huo
 * @date : 2023/4/25 13:09
 * @version : v1.0
 */
object DataDevelopmentJob {

  /**
   * 处理数据开发Job
   *
   * @param spark   SparkSession
   * @param request Request[Packet]
   * @return
   */
  def doDataDevelopment(spark: SparkSession, request: Request[Packet]): Response[Packet] = {
    val response: Response[Packet] = request.newRsp(Response.SUCCESS)
    val rspPacket = response.getPacket.asInstanceOf[ExeSqlJobRspPacket]

    try {
      val packet = request.getPacket.asInstanceOf[ExeSqlJobReqPacket]
      CmdTracker.tracking("Convert request packet as ExeSqlJobReqPacket", request)

      // create temp view
      CmdTracker.tracking("The temporary view will be created ... ", request)
      TmpViewUtil.createTmpView(spark, packet.getStoreInfos, request)

      // exe sql
      CmdTracker.tracking("The SQL job will be executed ... ", request)
      val resultInfo = exeSqlJob(spark, packet.getSqlInfos, request)

      // return result
      rspPacket.setId(request.getPacket.getId)
      CmdTracker.tracking(s"The job finished. ${packet.getSqlInfos.size()} SQL were executed successfully. Set result info and return it", request)
      rspPacket.setResults(resultInfo)
    } catch {
      case e: Throwable => {
        val msg = s"The data integration command detects an exception : \n${ExceptionUtil.getStackTrace(e)}"
        rspPacket.setMessage(msg)
        response.setSuccess(Response.FAILED)
        EventTicker.tickEvent(msg, EngineEvent.ENGINE_ACC_CMD)
        CmdTracker.tracking(msg, request)
      }
    }

    response
  }

  /**
   * 执行SQL
   *
   * @param spark    SparkSession
   * @param sqlInfos List[SqlInfo]
   * @param request  Request[Packet]
   * @return
   */
  def exeSqlJob(spark: SparkSession, sqlInfos: List[SqlInfo], request: Request[Packet]): List[ResultInfo] = {
    val resultInfo: List[ResultInfo] = new util.ArrayList[ResultInfo]
    sqlInfos.forEach(sqlInfo => {
      // 执行SQL
      CmdTracker.tracking(s"SQL[${sqlInfo.getId}] to be executed => ${sqlInfo.getSql}", request)
      val dataFrame = spark.sql(sqlInfo.getSql)
      val count = dataFrame.count()
      CmdTracker.tracking(s"The SQL[${sqlInfo.getId}] result set contains ${count} records", request)

      // 构造执行结果
      val result = new ResultInfo()
      resultInfo.add(result)
      result.setId(sqlInfo.getId)
      result.setSql(sqlInfo.getSql)
      result.setTarget(sqlInfo.getTarget)
      result.setCount(count.intValue())
      result.setSchema(dataFrame.schema.json)
      result.setTableName(sqlInfo.getTableName)
      CmdTracker.tracking(s"The SQL[${sqlInfo.getId}] result set's schema is ${dataFrame.schema.json}", request)

      // 输出结果集
      CmdTracker.tracking(s"The SQL[${sqlInfo.getId}]'s result set will be written to ${sqlInfo.getTarget}", request)
      if (sqlInfo.getOverWrite) {
        dataFrame.write.mode(SaveMode.Overwrite).orc(sqlInfo.getTarget)
      } else {
        dataFrame.write.mode(SaveMode.Append).orc(sqlInfo.getTarget)
      }
      CmdTracker.tracking(s"The SQL[${sqlInfo.getId}]'s result set is ${StatisticsUtil.statisticOrcSize(spark, sqlInfo.getTarget)} bytes in total", request)

      // 构造结果消息
      CmdTracker.tracking(s"SQL[${sqlInfo.getId}] execute successfully", request)
      result.setMessage(s"SQL[${sqlInfo.getId}] execute successfully, result path is ${sqlInfo.getTarget}")
    })
    resultInfo
  }
}
