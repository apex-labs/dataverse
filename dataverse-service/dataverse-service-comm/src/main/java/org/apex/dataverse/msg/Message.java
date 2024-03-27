package org.apex.dataverse.msg;

import lombok.Data;

/**
 * @version : v1.0
 * @projectName : nexus-dtvs
 * @package : com.apex.dtvs.msg
 * @className : Message
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/1/11 14:41
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
@Data
public class Message<T> {
    /**
     * 序列号
     */
    private String serialNo;

    /**
     * Netty Channel Id
     */
    private String channelId;

    /**
     * 消息体
     */
    private T messageBody;
}
