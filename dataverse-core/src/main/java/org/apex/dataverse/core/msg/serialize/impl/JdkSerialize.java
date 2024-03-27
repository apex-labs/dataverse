package org.apex.dataverse.core.msg.serialize.impl;

import org.apex.dataverse.core.exception.SerializeException;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.di.DiReqPacket;
import org.apex.dataverse.core.msg.serialize.ISerialize;

import java.io.*;

/**
 * @author : Danny.Huo
 * @version : v1.0
 * @date : 2022/11/7 20:49
 */
public class JdkSerialize implements ISerialize {

    @Override
    public <T extends Packet> byte[] serialize(T o, Class<T> tClass)
            throws SerializeException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(o);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public <T extends Packet> T deserialize(byte[] o, Class<T> tClass)
            throws SerializeException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(o);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return tClass.cast(ois.readObject());
        } catch (IOException e) {
            throw new SerializeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
