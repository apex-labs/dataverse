package org.apex.dataverse.core.enums;

import lombok.Getter;

/**
 * @author : Danny.Huo
 * @version : v1.0
 * @date : 2023/1/12 14:10
 * @since : 0.1.0
 */
@Getter
public enum ConnType {

    /**
     * JDBC connection
     */
    JDBC((byte) 1, "JDBC"),

    /**
     * ODPC connection
     */
    ODPC((byte) 2, "ODPC");

    /**
     * Code
     */
    private final Byte code;

    /**
     * Connection type
     */
    private final String connType;

    /**
     * Connection type enum constructor
     *
     * @param code code
     * @param connType connection type
     */
    ConnType(Byte code, String connType) {
        this.code = code;
        this.connType = connType;
    }

}
