package org.apex.dataverse.core.msg.packet.di;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.msg.packet.abs.AbsReqPacket;
import org.apex.dataverse.core.msg.packet.info.output.Output;

/**
 * 数据集成请求消息体
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/23 19:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DiReqPacket extends AbsReqPacket {

    /**
     * Is increment integration or not
     * true : increment integration
     * false : full integration
     */
    private boolean isIncr;

    /**
     * Out put info
     */
    private Output output;

    /**
     * name
     */
    private String name;

    /**
     * The JDBC URL of the form jdbc:subprotocol:subname to connect to.
     * The source-specific connection properties may be specified in the URL.
     * e.g., jdbc:postgresql://localhost/test?user=fred&password=secret
     */
    private String url;

    /**
     * The jdbc user
     */
    private String user;

    /**
     * The jdbc password
     */
    private String password;

    /**
     * dbtable or query
     */
    private String query;

    /**
     * The class name of the JDBC driver to use to connect to this URL.
     */
    private String driver;

    /**
     * These options must all be specified if any of them is specified. In addition, numPartitions must be specified.
     * They describe how to partition the table when reading in parallel from multiple workers.
     * partitionColumn must be a numeric, date, or timestamp column from the table in question.
     * Notice that lowerBound and upperBound are just used to decide the partition stride, not for filtering the rows in table.
     * So all rows in the table will be partitioned and returned.
     * This option applies only to reading.
     * Example:
     * spark.read.format("jdbc")
     * .option("url", jdbcUrl)
     * .option("dbtable", "(select c1, c2 from t1) as subq")
     * .option("partitionColumn", "c1")
     * .option("lowerBound", "1")
     * .option("upperBound", "100")
     * .option("numPartitions", "3")
     * .load()
     */
    private String partitionColumn;

    /**
     * Notice that lowerBound and upperBound are just used to decide the partition stride,
     * not for filtering the rows in table.
     */
    private String lowerBound;

    /**
     * Notice that lowerBound and upperBound are just used to decide the partition stride,
     * not for filtering the rows in table.
     */
    private String upperBound;

    /**
     * The maximum number of partitions that can be used for parallelism in table reading and writing.
     * This also determines the maximum number of concurrent JDBC connections.
     * If the number of partitions to write exceeds this limit,
     * we decrease it to this limit by calling coalesce(numPartitions) before writing.
     */
    private Short numPartitions;

    /**
     * The number of seconds the driver will wait for a Statement object to execute to the given number of seconds.
     * Zero means there is no limit.
     * In the write path, this option depends on how JDBC drivers implement the API setQueryTimeout, e.g.,
     * the h2 JDBC driver checks the timeout of each query instead of an entire JDBC batch.
     */
    private Integer queryTimeout;

    /**
     * The JDBC fetch size, which determines how many rows to fetch per round trip.
     * This can help performance on JDBC drivers which default to low fetch size (e.g. Oracle with 10 rows).
     */
    private Integer fetchSize;

    /**
     * New a Response packet by request packet
     * @return DiRspPacket
     */
    @Override
    public DiRspPacket newRspPacket() {
        DiRspPacket packet = new DiRspPacket();
        this.extend(packet);
        return packet;
    }
}
