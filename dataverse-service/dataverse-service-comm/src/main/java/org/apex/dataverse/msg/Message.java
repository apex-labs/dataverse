package org.apex.dataverse.msg;

import lombok.Data;

/**
 * @version : v0.1.0
 * @author : Danny.Huo
 * @date : 2023/1/11 14:41
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
