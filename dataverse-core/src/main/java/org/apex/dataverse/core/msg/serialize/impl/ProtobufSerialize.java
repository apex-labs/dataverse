package org.apex.dataverse.core.msg.serialize.impl;

import org.apex.dataverse.core.exception.SerializeException;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.serialize.ISerialize;
import org.apex.dataverse.core.util.ProtoBufUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2022/11/7 16:53
 */
@Slf4j
public class ProtobufSerialize implements ISerialize {

    @Override
    public <T extends Packet> byte[] serialize(T o, Class<T> tClass)
            throws SerializeException {
        return ProtoBufUtil.serialize(o, tClass);
    }

    @Override
    public <T extends Packet> T deserialize(byte[] o, Class<T> tClass)
            throws SerializeException {
        return ProtoBufUtil.deSerialize(o, tClass);
    }

}
