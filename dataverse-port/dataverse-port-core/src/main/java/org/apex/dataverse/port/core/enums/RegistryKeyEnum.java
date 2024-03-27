package org.apex.dataverse.port.core.enums;

import lombok.Getter;

/**
 * Engine type
 * @author : Danny.Huo
 * @date : 2023/11/12 14:10
 * @version : v1.0
 * @since : 0.1.0
 */
@Getter
public enum RegistryKeyEnum {

    /**
     * Port registry key
     * <p>
     * writeKey %s storageId
     * writeKey %s groupCode
     * writeKey %s portCode
     * <p>
     * findKey %s storageId
     * findKey %s groupCode
     */
    PORT ("dataverse:port:%s:%s:%s", "dataverse:port:*%s*:%s:*"),

    /**
     * Engine registry key
     * writeKey %s portCode
     * writeKey %s engineId
     * <p>
     * findKey %s portCode
     */
    ENGINE ("dataverse:engine:%s:%s", "dataverse:engine:%s:*"),

    /**
     * Port connection registry key
     * <p>
     * writeKey %s storageId
     * writeKey %s groupCode
     * writeKey %s connId
     * <p>
     * findKey %s storageId
     * findKey %s groupCode
     */
    PORT_CONN ("dataverse:port-conn:%s:%s:%s", "dataverse:port-conn:*%s*:%s:*"),

    /**
     * Engine registry key
     * writeKey %s storageId
     * writeKey %s connId
     */
    STORAGE_CONN ("dataverse:storage-conn:%s:%s", "dataverse:storage-conn:%s:*");

    /**
     * Write key
     */
    private final String writeKey;

    /**
     * Find key
     */
    private final String findKey;

    /**
     * Build Port registry key
     * @param storageIds storage ids
     * @param portCode  port code
     * @return String, RegistryKey
     */
    public static String buildPortKey (String[] storageIds, String portCode) {
        return buildPortKey(storageIds, "default", portCode);
    }

    /**
     * Build Port registry key
     * @param storageIds storage ids
     * @param groupCode group code
     * @param portCode  port code
     * @return String, RegistryKey
     */
    public static String buildPortKey (String[] storageIds, String groupCode, String portCode) {
        StringBuilder storageIdsBuffer = new StringBuilder("_");
        if(null != storageIds) {
            for (String storageId : storageIds) {
                storageIdsBuffer.append(storageId);
                storageIdsBuffer.append("_");
            }
        }
        return String.format(PORT.writeKey, storageIdsBuffer, groupCode, portCode);
    }

    /**
     * Build port find key
     * @param storageId storage id
     * @param groupCode group code
     * @return String
     */
    public static String buildPortFindKey (String storageId,  String groupCode) {
        return String.format(PORT.findKey, storageId, groupCode);
    }

    /**
     * Build Storage connection registry key
     * @param storageId storage id
     * @param connId storage connection id
     * @return String, RegistryKey
     */
    public static String buildStorageConnKey (String storageId, String connId) {
        return String.format(STORAGE_CONN.writeKey, storageId, connId);
    }

    /**
     * Build engine registry key
     * @param portCode portCode
     * @param engineCode engineCode
     * @return String, RegistryKey
     */
    public static String buildEngineKey (String portCode, String engineCode) {
        return String.format(ENGINE.writeKey, portCode, engineCode);
    }

    /**
     * Constructor with parameter
     * @param writeKey Write key
     * @param findKey Find key
     */
    RegistryKeyEnum(String writeKey, String findKey) {
        this.writeKey = writeKey;
        this.findKey = findKey;
    }
}
