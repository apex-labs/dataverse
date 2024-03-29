package org.apex.dataverse.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @version : v0.1.0
 * @author : Danny.Huo
 * @date : 2023/1/11 14:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Response<T> extends Message<T> {

    public Response () {

    }

    public Response (String serialNo, String channelId) {
        this.setSerialNo(serialNo);
        this.setChannelId(channelId);
    }
}
