package org.apex.dataverse.core.enums;

import lombok.Getter;

/**
 * @since : 0.1.0
 * @author : Danny.Huo
 * @date : 2023/1/12 14:10
 * @version : v1.0
 */
@Getter
public enum CmdState {

    /**
     * Failed
     */
    FAILED ((byte) 0),

    /**
     * Success
     */
    SUCCESS((byte) 1);

    /**
     * Code
     */
    private final Byte code;

    CmdState(Byte code) {
        this.code = code;
    }

}
