package org.apex.dataverse.msg;

import lombok.Data;

/**
 * @version : v1.0
 * @projectName : nexus-dtvs
 * @package : com.apex.dtvs.msg
 * @className : Request
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/1/11 14:38
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
@Data
public class Request<T> extends Message<T> {

    /**
     * 命令类型
     */
    private short cmdType;
}
