package org.apex.dataverse.core.msg.factory;

import org.apex.dataverse.core.msg.serialize.ISerialize;
import org.apex.dataverse.core.msg.serialize.impl.JdkSerialize;
import org.apex.dataverse.core.msg.serialize.impl.JsonSerialize;
import org.apex.dataverse.core.msg.serialize.impl.ProtobufSerialize;

import java.util.HashMap;
import java.util.Map;

/**
 * Serialize Factory
 * @author : Danny.Huo
 * @date : 2022/11/7 16:49
 * @version : v1.0
 * @since : 0.1.0
 */
public class SerializeFactory {

    /**
     * 序列化类
     */
    private final static Map<Byte, ISerialize> SERIALIZE = new HashMap<>();

    static {
        SERIALIZE.put(ISerialize.JSON_SERIALIZE, new JsonSerialize());
        SERIALIZE.put(ISerialize.JDK_SERIALIZE, new JdkSerialize());
        SERIALIZE.put(ISerialize.PROTOBUF_SERIALIZE, new ProtobufSerialize());
    }

    /**
     * 获取序列化接口
     * @param serializeType serializeType
     * @return ISerialize
     */
    public static ISerialize getSerialize (Byte serializeType) {
        return SERIALIZE.get(serializeType);
    }
}
