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
public class Request<T> extends Message<T> {

    /**
     * 命令类型
     */
    private short cmdType;
}
