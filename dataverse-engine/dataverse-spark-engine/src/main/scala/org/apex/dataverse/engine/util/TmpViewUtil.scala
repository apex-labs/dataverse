package org.apex.dataverse.engine.util

import org.apex.dataverse.core.msg.Request
import org.apex.dataverse.core.msg.packet.Packet
import org.apache.spark.sql.SparkSession
import org.apex.dataverse.core.msg.packet.info.StoreInfo
import org.apex.dataverse.engine.cmd.CmdTracker

import java.util.List

/**
 * @author : Danny.Huo
 * @date : 2023/4/28 15:08
 * @version : v1.0
 */
object TmpViewUtil {

    /**
     * 创建临时视图
     *
     * @param spark     SparkSession
     * @param storeInfo List[StoreInfo]
     */
    def createTmpView(spark: SparkSession, storeInfo: List[StoreInfo], request: Request[Packet]): Unit = {
        if (null == storeInfo || storeInfo.isEmpty) {
            return
        }

        storeInfo.forEach(si => {
            CmdTracker.tracking(s"Read the data[${si.getStorePath}] and create temporary view[${si.getTableName}]", request)
            spark.read.orc(si.getStorePath).createOrReplaceTempView(si.getTableName)
        })
    }
}
