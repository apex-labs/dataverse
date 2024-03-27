package org.apex.dataverse.engine.cmd

import org.apex.dataverse.core.enums.CmdSet
import org.apex.dataverse.core.msg.packet.Packet
import org.apex.dataverse.core.msg.{Request, Response}
import org.apache.spark.sql.SparkSession
import org.apex.dataverse.engine.dd.{DataDevelopmentJob, DataDevelopmentOneshot}
import org.apex.dataverse.engine.di.DataIntegrationJob

/**
 * 处理请求命令
 *
 * @author : Danny.Huo
 * @date : 2023/4/25 11:32
 * @version : v1.0
 */
object CmdDistributor {

  /**
   * 处理命令
   *
   * @param request Request[Packet]
   */
  def doCmd(spark: SparkSession, request: Request[Packet]): Response[Packet] = {
    distributeCmd(spark, request)
  }

  /**
   * 解析和分发命令
   *
   * @param request Request[Packet]
   */
  private def distributeCmd(spark: SparkSession, request: Request[Packet]): Response[Packet] = {
    val cmd = request.getHeader.getCommand

    var response: Response[Packet] = null

    /**
     * 集成，抽数任务
     */
    if (cmd == CmdSet.DATA_INTEGRATION.getReq) {
      response = DataIntegrationJob.doDataIntegration(spark, request)
    }

    /**
     * ETL SQL作业任务
     */
    else if (cmd == CmdSet.EXE_SQL_JOB.getReq) {
      response = DataDevelopmentJob.doDataDevelopment(spark, request)
    }

    /**
     * ETL SQL
     */
    else if (cmd == CmdSet.EXE_SQL.getReq) {
      response = DataDevelopmentOneshot.doSql(spark, request)
    }

    // Return response
    response
  }
}
