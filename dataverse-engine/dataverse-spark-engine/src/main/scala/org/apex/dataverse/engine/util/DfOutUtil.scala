package org.apex.dataverse.engine.util

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apex.dataverse.core.enums.{OutFormat, OutType}
import org.apex.dataverse.core.msg.packet.info.output.{HdfsOutput, HiveOutput, Output};

/**
 * @author Danny.Huo
 * @date 2023/12/15 19:21
 * @since 0.1.0
 */
object DfOutUtil {

  /**
   * Out put
   *
   * @param df  DataFrame
   * @param out Output
   */
  def output(df: DataFrame, out: Output): Unit = {
    // Repartition before write
    if (null != out.getPartitions && out.getPartitions > 1) {
      df.repartition(out.getPartitions)
    }

    // out by output type
    out.getOutType match {
      case OutType.HDFS => {
        // Output to hdfs
        outputHdfs(df, out.asInstanceOf[HdfsOutput])
      }

      case OutType.HIVE => {
        // Output to hive
        outputHive(df, out.asInstanceOf[HiveOutput])
      }

      case OutType.MYSQL => {
        // ...
      }

      case _ => {
        // else
      }
    }


  }

  /**
   * Output to hdfs
   *
   * @param df  Dataframe
   * @param out HdfsOutput
   */
  private def outputHdfs(df: DataFrame, out: HdfsOutput): Unit = {
    out.getFormat match {
      case OutFormat.ORC => {
        if (out.getOverWrite) {
          df.write.mode(SaveMode.Overwrite).orc(out.writePath())
        } else {
          df.write.orc(out.writePath())
        }
        HdfsUtil.move(out)
      }
      case OutFormat.PARQUET => {
        if (out.getOverWrite) {
          df.write.mode(SaveMode.Overwrite).parquet(out.writePath())
        } else {
          df.write.parquet(out.writePath())
        }
        HdfsUtil.move(out)
      }
      case OutFormat.AVRO => {
        if (out.getOverWrite) {
          df.write.mode(SaveMode.Overwrite).format("avro").save(out.writePath)
        } else {
          df.write.format("avro").save(out.writePath)
        }
        HdfsUtil.move(out)
      }
      case OutFormat.HUDI => {
        // TODO
      }
      case OutFormat.ICEBERG => {
        // TODO
      }
      case OutFormat.DELTA => {
        // TODO
      }

      case _ => {
        // else
      }
    }
  }

  /**
   * Output to hive
   *
   * @param df  Dataframe
   * @param out HiveOutput
   */
  private def outputHive(df: DataFrame, out: HiveOutput): Unit = {

  }

}
