package org.apex.dataverse.engine.util

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.SparkSession

/**
 * @author Danny.Huo
 * @date 2023/12/15 20:31
 * @since 0.1.0
 */
object SparkUtil {

    var spark : SparkSession = _

    var fs : FileSystem = _

}
