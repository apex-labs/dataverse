package org.apex.dataverse.core.msg.packet.info;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/25 17:02
 */
@Data
@NoArgsConstructor
public class StoreInfo implements Serializable {

    public StoreInfo(String tableName, String storePath) {
        this.tableName = tableName;
        this.storePath = storePath;
    }

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 存储路径
     */
    private String storePath;
}
