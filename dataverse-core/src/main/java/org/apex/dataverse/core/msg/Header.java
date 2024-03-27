package org.apex.dataverse.core.msg;

import org.apex.dataverse.core.msg.serialize.ISerialize;
import org.apex.dataverse.core.util.UCodeUtil;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/1/15 17:34
 */
@Data
public class Header implements Serializable, Cloneable {

    public Header() {

    }

    public Header(Byte command) {
        this.command = command;
    }

    public static Header newHeader(Byte command) {
        return new Header(command);
    }

    public static Header newHeader(Byte command, Byte serialize) {
        Header header = new Header(command);
        header.setSerialize(serialize);
        return header;
    }

    /**
     * 魔数
     * 默认值：120144
     */
    private Integer magic = 120144;

    /**
     * 版本号
     * 默认值：0
     */
    private Byte version = 0;

    /**
     * 序列化方式
     * 默认值：1
     */
    private Byte serialize = ISerialize.PROTOBUF_SERIALIZE;

    /**
     * 请求流水号
     * 每个请求唯一
     */
    private Long serialNo = Long.valueOf(UCodeUtil.produce().hashCode());

    /**
     * 消息指令
     */
    private Byte command;

    /**
     * 消息体的字节数
     */
    private Integer bodyLength;

    @Override
    protected Header clone() {
        Header header = new Header();
        header.setMagic(this.magic);
        header.setSerialize(this.serialize);
        header.setVersion(this.version);
        header.setSerialNo(this.serialNo);
        header.setCommand(this.command);
        header.setBodyLength(this.bodyLength);
        return header;
    }
}
