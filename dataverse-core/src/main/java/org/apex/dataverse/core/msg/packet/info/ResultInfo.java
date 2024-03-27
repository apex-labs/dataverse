package org.apex.dataverse.core.msg.packet.info;

import lombok.Data;

import java.io.Serializable;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/25 17:02
 */
@Data
public class ResultInfo implements Serializable {

    /**
     * 请求SQL的ID
     */
    private Integer id;

    /**
     * 请求的SQL语句
     */
    private String sql;

    /**
     * 执行结果的数量
     */
    private Integer count;

    /**
     * 结果消息
     */
    private String message;

    /**
     * 结果输出表名
     */
    private String tableName;

    /**
     * 目标结果
     */
    private String target;

    /**
     * 表结构
     */
    private String schema;
}
