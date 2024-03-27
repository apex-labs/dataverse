package org.apex.dataverse.core.msg.serialize.impl;

import org.apex.dataverse.core.exception.SerializeException;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.serialize.ISerialize;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2022/11/7 20:12
 */
@Slf4j
public class JsonSerialize implements ISerialize {

    @Override
    public <T extends Packet> byte[] serialize(T o, Class<T> tClass)
            throws SerializeException {
        try {
            String s = ObjectMapperUtil.toJson(o);
            return s.getBytes();
        } catch (Exception e) {
            throw new SerializeException("Json serialize found exception", e);
        }
    }

    @Override
    public <T extends Packet> T deserialize(byte[] o, Class<T> tClass)
            throws SerializeException {
        try {
            return ObjectMapperUtil.toObject(new String(o), tClass);
        } catch (Exception e) {
            throw new SerializeException("Json deserialize found exception", e);
        }
    }
}
