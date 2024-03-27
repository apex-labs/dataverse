package org.apex.dataverse.core.msg.serialize;

import org.apex.dataverse.core.exception.SerializeException;
import org.apex.dataverse.core.msg.packet.Packet;

/**
 *
 * @author : Danny.Huo
 * @date : 2022/11/7 16:50
 * @version : v1.0
 */
public interface ISerialize {

    Byte PROTOBUF_SERIALIZE = 1;

    Byte JSON_SERIALIZE = 2;

    Byte JDK_SERIALIZE = 3;


    /**
     * 序列化
     *
     * @param o T
     * @param tClass Class<T>
     * @param <T> T
     * @return byte[]
     * @throws SerializeException SerializeException
     */
    <T extends Packet> byte[] serialize(T o, Class<T> tClass)
            throws SerializeException;

    /**
     * 反序列化
     *
     * @param o byte[]
     * @param tClass Class<T>
     * @param <T> T
     * @return T
     * @throws SerializeException SerializeException
     */
    <T extends Packet> T deserialize(byte[] o, Class<T> tClass)
            throws SerializeException;
}
