package org.apex.dataverse.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @version : v1.0
 * @projectName : dynamic-rule-engine
 * @package : com.chinapex.fdre.util
 * @className : ObjectMapperUtil
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2022/11/24 11:06
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
public class ObjectMapperUtil {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 转Json对象
     * @param o
     * @return
     * @throws JsonProcessingException
     */
    public static String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    /**
     * Json转对象
     * @param json
     * @param clz
     * @return
     * @param <T>
     * @throws JsonProcessingException
     */
    public static <T> T toObject(String json, Class<T> clz)
            throws JsonProcessingException {
        return objectMapper.readValue(json, clz);
    }

}
