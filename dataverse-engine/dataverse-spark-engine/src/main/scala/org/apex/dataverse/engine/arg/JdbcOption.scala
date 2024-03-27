package org.apex.dataverse.engine.arg

/**
 *
 * @author : Danny.Huo
 * @date : 2023/4/26 16:00
 * @version : v1.0
 */
object JdbcOption {

  /**
   * Spark JDBC format
   */
  var JDBC_FORMAT =  "jdbc"

  /**
   * The JDBC URL of the form jdbc:subprotocol:subname to connect to. The source-specific connection properties may be specified in the URL. e.g., jdbc:postgresql://localhost/test?user=fred&password=secret
   */
  val URL: JdbcKV = JdbcKV("url", null)

  /**
   * The JDBC table that should be read from or written into. Note that when using it in the read path anything that is valid in a FROM clause of a SQL query can be used. For example, instead of a full table you could also use a subquery in parentheses. It is not allowed to specify dbtable and query options at the same time.
   */
  val USER: JdbcKV = JdbcKV("user", null)

  /**
   * The jdbc password
   */
  val PASSWORD: JdbcKV = JdbcKV("password", null)

  /**
   * The JDBC table that should be read from or written into. Note that when using it in the read path anything that is valid in a FROM clause of a SQL query can be used. For example, instead of a full table you could also use a subquery in parentheses. It is not allowed to specify dbtable and query options at the same time.
   */
  val DB_TABLE: JdbcKV = JdbcKV("dbtable", null)

  /**
   * A prefix that will form the final query together with query. As the specified query will be parenthesized as a subquery in the FROM clause and some databases do not support all clauses in subqueries, the prepareQuery property offers a way to run such complex queries. As an example, spark will issue a query of the following form to the JDBC Source.
   *
   *  <prepareQuery> SELECT <columns> FROM (<user_specified_query>) spark_gen_alias
   *
   *  Below are a couple of examples.
   *  MSSQL Server does not accept WITH clauses in subqueries but it is possible to split such a query to prepareQuery and query:
   *  spark.read.format("jdbc")
   *  .option("url", jdbcUrl)
   *  .option("prepareQuery", "WITH t AS (SELECT x, y FROM tbl)")
   *  .option("query", "SELECT * FROM t WHERE x > 10")
   *  .load()
   *  MSSQL Server does not accept temp table clauses in subqueries but it is possible to split such a query to prepareQuery and query:
   *  spark.read.format("jdbc")
   *  .option("url", jdbcUrl)
   *  .option("prepareQuery", "(SELECT * INTO #TempTable FROM (SELECT * FROM tbl) t)")
   *  .option("query", "SELECT * FROM #TempTable")
   *  .load()
   */
  val PREPARE_QUERY: JdbcKV = JdbcKV("prepareQuery", "10000")

  /**
   * A query that will be used to read data into Spark. The specified query will be parenthesized and used as a subquery in the FROM clause. Spark will also assign an alias to the subquery clause. As an example, spark will issue a query of the following form to the JDBC Source.
   *
   *  SELECT <columns> FROM (<user_specified_query>) spark_gen_alias
   *
   *  Below are a couple of restrictions while using this option.
   *  It is not allowed to specify dbtable and query options at the same time.
   *  It is not allowed to specify query and partitionColumn options at the same time. When specifying partitionColumn option is required, the subquery can be specified using dbtable option instead and partition columns can be qualified using the subquery alias provided as part of dbtable.
   *  Example:
   *  spark.read.format("jdbc")
   *  .option("url", jdbcUrl)
   *  .option("query", "select c1, c2 from t1")
   *  .load()
   */
  val QUERY: JdbcKV = JdbcKV("query", "")

  /**
   * The number of seconds the driver will wait for a Statement object to execute to the given number of seconds. Zero means there is no limit. In the write path, this option depends on how JDBC drivers implement the API setQueryTimeout, e.g., the h2 JDBC driver checks the timeout of each query instead of an entire JDBC batch.
   */
  val QUERY_TIMEOUT: JdbcKV = JdbcKV("queryTimeout", "0")

  /**
   * The JDBC fetch size, which determines how many rows to fetch per round trip. This can help performance on JDBC drivers which default to low fetch size (e.g. Oracle with 10 rows).
   */
  val FETCH_SIZE: JdbcKV = JdbcKV("fetchSize", "10000")

  /**
   * The class name of the JDBC driver to use to connect to this URL.
   */
  val DRIVER: JdbcKV = JdbcKV("spark.driver", "com.mysql.jdbc.Driver")

  /**
   * These options must all be specified if any of them is specified. In addition, numPartitions must be specified. They describe how to partition the table when reading in parallel from multiple workers. partitionColumn must be a numeric, date, or timestamp column from the table in question. Notice that lowerBound and upperBound are just used to decide the partition stride, not for filtering the rows in table. So all rows in the table will be partitioned and returned. This option applies only to reading.
   *  Example:
   *  spark.read.format("jdbc")
   *  .option("url", jdbcUrl)
   *  .option("dbtable", "(select c1, c2 from t1) as subq")
   *  .option("partitionColumn", "c1")
   *  .option("lowerBound", "1")
   *  .option("upperBound", "100")
   *  .option("numPartitions", "3")
   *  .load()
   */
  val PARTITION_COLUMN: JdbcKV = JdbcKV("partitionColumn", null)

  /**
   * THE ${partitionColumn} >= lowerBound
   */
  val LOWER_BOUND: JdbcKV = JdbcKV("lowerBound", null)

  /**
   * THE ${partitionColumn} < upperBound
   */
  val UPPER_BOUND: JdbcKV = JdbcKV("upperBound", null)

  /**
   * The maximum number of partitions that can be used for parallelism in table reading and writing. This also determines the maximum number of concurrent JDBC connections. If the number of partitions to write exceeds this limit, we decrease it to this limit by calling coalesce(numPartitions) before writing.
   */
  val NUM_PARTITIONS: JdbcKV = JdbcKV("numPartitions", null)

  /**
   * JDBC数据源Options的Key和Default Value
   * @param key key
   * @param default 默认值
   */
  case class JdbcKV(key : String, default : String)
}


