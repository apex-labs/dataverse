package org.apex.dataverse.core.msg.packet.info;

import lombok.Data;

import java.io.Serializable;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/25 17:02
 */
@Data
public class SqlInfo implements Serializable {

    /**
     * 有参构造
     *
     * @param sql
     * @param tableName
     * @param target
     */
    public SqlInfo(Integer id, String sql, String tableName, String target) {
        this.id = id;
        this.sql = sql;
        this.target = target;
        this.tableName = tableName;
        this.overWrite = true;
    }

    public SqlInfo(Integer id, String sql, String tableName, String target, Boolean overWrite) {
        this.id = id;
        this.sql = sql;
        this.target = target;
        this.tableName = tableName;
        this.overWrite = overWrite;
    }

    /**
     * ID
     */
    private Integer id;

    /**
     * SQL语句
     */
    private String sql;

    /**
     * SQL执行结果输出表名
     */
    private String tableName;

    /**
     * 结果输出路径
     */
    private String target;

    /**
     * true:覆盖抽取
     */
    private Boolean overWrite = false;
}
