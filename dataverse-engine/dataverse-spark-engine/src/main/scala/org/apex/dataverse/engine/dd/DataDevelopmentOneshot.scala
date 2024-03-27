package org.apex.dataverse.engine.dd

import org.apex.dataverse.core.msg.packet.Packet
import org.apex.dataverse.core.msg.packet.dd._
import org.apex.dataverse.core.msg.{Request, Response}
import org.apache.spark.sql.SparkSession
import org.apex.dataverse.core.util.ExceptionUtil
import org.apex.dataverse.engine.cmd.CmdTracker
import org.apex.dataverse.engine.util.{StatisticsUtil, TmpViewUtil}
import org.apex.dataverse.core.util.ObjectMapperUtil;


/**
 *
 * @author : Danny.Huo
 * @date : 2023/4/25 13:09
 * @version : v1.0
 */
object DataDevelopmentOneshot {

    /**
     * 处理数据开发临时SQL
     *
     * @param spark   SparkSession
     * @param request Request[Packet]
     * @return
     */
    def doSql(spark: SparkSession, request: Request[Packet]): Response[Packet] = {
        val start = System.currentTimeMillis()

        val response: Response[Packet] = request.newRsp(Response.SUCCESS)
        val rspPacket = response.getPacket.asInstanceOf[ExeSqlRspPacket]

        try {
            CmdTracker.tracking("Convert request packet as ExeSqlReqPacket", request)
            val packet = request.getPacket.asInstanceOf[ExeSqlReqPacket]

            // create temp view
            CmdTracker.tracking("The temporary view will be created ... ", request)
            TmpViewUtil.createTmpView(spark, packet.getStoreInfos, request)

            // exe sql
            CmdTracker.tracking("The SQL job will be executed ... ", request)
            val dataframe = spark.sql(packet.getSql)
            rspPacket.setTotalCount(dataframe.count().intValue())

            CmdTracker.tracking(s"Collect result set, max rows is ${packet.getFetchMaxRows} ... ", request)
            val collectedFrame = dataframe.limit(packet.getFetchMaxRows)
            rspPacket.setResult(collectedFrame.toJSON.collectAsList())
            rspPacket.setFetchedCount(collectedFrame.count().intValue())

            // return result
            rspPacket.setId(request.getPacket.getId)
            CmdTracker.tracking(s"The SQL were executed successfully. Set result info and return it", request)
        } catch {
            case e: Throwable => {
                val msg = s"The data exe sql command detects an exception : \n${ExceptionUtil.getStackTrace(e)}"
                rspPacket.setMessage(msg)
                response.setSuccess(Response.FAILED)
                rspPacket.setDuration((System.currentTimeMillis() - start).intValue())
                CmdTracker.tracking(msg, request)
            }
        }


        // Calculate duration
        rspPacket.setDuration(System.currentTimeMillis() - start)

        response
    }
}
