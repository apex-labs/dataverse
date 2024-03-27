package org.apex.dataverse.core.enums;

import lombok.Getter;

/**
 *
 * @author : Danny.Huo
 * @date : 2023/1/12 14:10
 * @version : v1.0
 * @since : 0.1.0
 */
@Getter
public enum ExeEnv {

    /**
     * Independent environment
     */
    BASIC ((byte) 0),

    /**
     * Development environment
     */
    DEV ((byte) 1),

    /**
     * Test environment
     */
    TEST ((byte) 2),

    /**
     * Product environment
     */
    PROD ((byte) 3);

    private Byte code;

    ExeEnv(Byte code) {
        this.code = code;
    }

}
