package org.apex.dataverse.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @version : v0.1.0
 * @author : Danny.Huo
 * @date : 2022/11/24 11:06
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
