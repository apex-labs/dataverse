package org.apex.dataverse.core.util;

import org.apex.dataverse.core.exception.ProtobufException;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * <p>项目名称:
 *
 * <p>文件名称:
 *
 * <p>描述:
 *
 * <p>创建时间: 2021/6/18 4:49 PM
 *
 * <p>公司信息: 创略科技
 *
 * @author Danny.Huo <br>
 * @version v1.0 <br>
 * @date 2021/6/18 4:49 PM <br>
 */
@Slf4j
public class ProtoBufUtil {

    /**
     * ProtoBuff 序列化
     *
     * @param object
     * @param schema
     * @param <T>
     * @return
     */
    public static <T extends Serializable> byte[] serialize(T object, Schema<T> schema)
            throws ProtobufException {
        if (null == object) {
            return null;
        }
        if (null == schema) {
            throw new ProtobufException("Protobuf Serializable found null schema clazz");
        }
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        return ProtobufIOUtil.toByteArray(object, schema, buffer);
    }

    /**
     * ProtoBuff 序列化
     *
     * @param object
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends Serializable> byte[] serialize(T object, Class<T> clazz)
            throws ProtobufException {
        if (null == object) {
            return null;
        }
        if (null == clazz) {
            throw new ProtobufException("Protobuf Serializable found null schema clazz");
        }
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        return serialize(object, schema);
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @param clazz
     * @param schema
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T extends Serializable> T deSerialize(byte[] bytes, Class<T> clazz, Schema<T> schema)
            throws ProtobufException {
        if (null == bytes || null == clazz || null == schema) {
            return null;
        }
        T newObject;
        try {
            newObject = clazz.newInstance();
            ProtobufIOUtil.mergeFrom(bytes, newObject, schema);
        } catch (InstantiationException e) {
            throw new ProtobufException(e);
        } catch (IllegalAccessException e) {
            throw new ProtobufException(e);
        } catch (Exception e) {
            throw new ProtobufException(e);
        }
        return newObject;
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T extends Serializable> T deSerialize(byte[] bytes, Class<T> clazz)
            throws ProtobufException {
        if (null == bytes) {
            return null;
        }
        if (null == clazz) {
            throw new ProtobufException("Protobuf Deserialize found null schema clazz");
        }
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        return deSerialize(bytes, clazz, schema);
    }
}
