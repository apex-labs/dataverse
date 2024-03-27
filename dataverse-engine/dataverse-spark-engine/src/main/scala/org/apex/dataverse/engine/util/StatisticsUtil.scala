package org.apex.dataverse.engine.util

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @projectName : nexus-engine
 * @package : com.apex.engine.util
 * @className : StatisticsUtil
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/4/26 15:22
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 * @version : v1.0
 */
object StatisticsUtil {

  /**
   * 统计DataFrame数据大小
   *
   * @param spark
   * @param df
   * @return
   */
  def statisticSize(spark: SparkSession, df: DataFrame): Int = {
    spark.sessionState.executePlan(df.queryExecution.logical).optimizedPlan.stats.sizeInBytes.toInt
  }

  /**
   * 统计某个路径下文件的大小
   * @param spark
   * @param path
   * @return
   */
  def statisticOrcSize(spark: SparkSession, path: String): Int = {
    val df = spark.read.orc(path)
    statisticSize(spark, df)
  }

  /**
   * 统计某个路径下文件的大小
   *
   * @param spark
   * @param path
   * @return
   */
  def statisticParquetSize(spark: SparkSession, path: String): Int = {
    val df = spark.read.parquet(path)
    statisticSize(spark, df)
  }

  /**
   * 统计某个路径下文件的大小
   * @param spark
   * @param path
   * @param format
   * @return
   */
  def statisticSize(spark: SparkSession, path: String, format : String): Int = {
    val df = spark.read.format(format).load(path)
    statisticSize(spark, df)
  }

}
