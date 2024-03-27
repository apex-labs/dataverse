package org.apex.dataverse.core.enums;

import lombok.Getter;

/**
 * Engine type
 * @author : Danny.Huo
 * @date : 2023/1/12 14:10
 * @version : v1.0
 * @since : 0.1.0
 */
@Getter
public enum EngineState {

    /**
     * Engine starting
     */
    STARTING ((byte) 1),

    /**
     * Engine online
     */
    ONLINE ((byte) 2),

    /**
     * Engine offline
     */
    OFFLINE ((byte) 2);

    /**
     * Engine status code
     */
    private final Byte code;

    EngineState(Byte code) {
        this.code = code;
    }
}
