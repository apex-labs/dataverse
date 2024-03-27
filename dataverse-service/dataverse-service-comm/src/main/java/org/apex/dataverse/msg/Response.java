package org.apex.dataverse.msg;

import lombok.Data;

/**
 * @version : v1.0
 * @projectName : nexus-dtvs
 * @package : com.apex.dtvs.msg
 * @className : Response
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/1/11 14:38
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
@Data
public class Response<T> extends Message<T> {

    public Response () {

    }

    public Response (String serialNo, String channelId) {
        this.setSerialNo(serialNo);
        this.setChannelId(channelId);
    }
}
