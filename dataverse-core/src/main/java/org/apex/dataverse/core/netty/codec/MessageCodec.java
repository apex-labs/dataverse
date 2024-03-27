package org.apex.dataverse.core.netty.codec;

import org.apex.dataverse.core.enums.CmdSet;
import org.apex.dataverse.core.exception.SerializeException;
import org.apex.dataverse.core.msg.Header;
import org.apex.dataverse.core.msg.Message;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.factory.SerializeFactory;
import org.apex.dataverse.core.msg.packet.di.DiReqPacket;
import org.apex.dataverse.core.msg.packet.engine.EngineEventPacket;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlReqPacket;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlRspPacket;
import org.apex.dataverse.core.msg.packet.info.output.HdfsOutput;
import org.apex.dataverse.core.msg.serialize.ISerialize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.core.msg.serialize.impl.JdkSerialize;
import org.apex.dataverse.core.msg.serialize.impl.ProtobufSerialize;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import org.apex.dataverse.core.util.UCodeUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2022/11/7 14:56
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message<Packet>> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          Message<Packet> message, ByteBuf byteBuf) throws Exception {
        try {
            encode1(channelHandlerContext, message, byteBuf);
        } catch (Exception e) {
            log.error("Encode message found error. MSG : {}", ObjectMapperUtil.toJson(message), e);
        }
    }

    private void encode1(ChannelHandlerContext channelHandlerContext,
                         Message<Packet> message, ByteBuf byteBuf)
            throws SerializeException {
        Header header = message.getHeader();
        Packet pocket = message.getPacket();
        // 写入header信息
        byteBuf.writeInt(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getSerialize());
        byteBuf.writeByte(header.getCommand());
        byteBuf.writeLong(header.getSerialNo());

        // 获取序列化算法
        ISerialize serialize = SerializeFactory.getSerialize(header.getSerialize());
        if (null == serialize) {
            log.error("Unknown serialize [{}], not found the serialize SerializeFactory.getSerialize({}})",
                    header.getSerialize(), header.getSerialize());
            return;
        }
        Class aClass = CmdSet.getClass(header.getCommand());
        if (null == aClass) {
            log.error("Encode message [{}] found error, not found the class CmdSet.getClass({}})",
                    message, header.getCommand());
            return;
        }
        byte[] bytes = serialize.serialize(pocket, aClass);
        header.setBodyLength(null == bytes ? 0 : bytes.length);

        // 写入数据包长度和数据包
        byteBuf.writeInt(header.getBodyLength());
        byteBuf.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
                          List<Object> list) throws Exception {
        try {
            decode1(channelHandlerContext, byteBuf, list);
        } catch (Exception e) {
            log.error("Decode message found error.", e);
        }
    }

    private void decode1(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
                         List<Object> list) throws SerializeException {
        // 读取Header信息
        Header header = new Header();
        header.setMagic(byteBuf.readInt());
        header.setVersion(byteBuf.readByte());
        header.setSerialize(byteBuf.readByte());
        header.setCommand(byteBuf.readByte());
        header.setSerialNo(byteBuf.readLong());
        header.setBodyLength(byteBuf.readInt());

        // 根据序列化类型获取序列化算法
        ISerialize serializeArithmetic = SerializeFactory.getSerialize(header.getSerialize());

        Packet pocket = null;
        if (header.getBodyLength() > 0) {
            // 读取数据包
            byte[] bytes = new byte[header.getBodyLength()];
            byteBuf.readBytes(bytes);

            // 反序列化数据包
            Class<? extends Packet> aClass = CmdSet.getClass(header.getCommand());
            if (null == aClass) {
                log.error("Encode message [Header {}] found error, not found the class CmdFactory.getClass({}})",
                        header, header.getCommand());
                return;
            }
            pocket = serializeArithmetic.deserialize(bytes, aClass);
        }

        // 构建消息
        Message<Packet> message;
        if (CmdSet.isRequest(header.getCommand())) {
            message = new Request<>();
        } else {
            message = new Response<>();
        }
        message.setHeader(header);
        message.setPacket(pocket);
        list.add(message);
    }

    public static void main(String[] args) throws Exception {
        //test2();
        test3();
    }

    public static void test2() throws Exception {
        MessageCodec odpcCodec = new MessageCodec();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(16384, 15,
                        4, 0, 0),
                new LoggingHandler(LogLevel.DEBUG),
                odpcCodec);
        Message<ExeSqlReqPacket> request = new Request<>();
        request.setHeader(Header.newHeader(CmdSet.EXE_SQL.getReq()));
        ExeSqlReqPacket exeSqlReqPacket = new ExeSqlReqPacket();
        exeSqlReqPacket.setSql("select * from abc");
        exeSqlReqPacket.setExeEnv((byte)1);
        exeSqlReqPacket.setMessage("test  message");
        exeSqlReqPacket.setFetchMaxRows(200);
        request.setPacket(exeSqlReqPacket);

        Message<ExeSqlRspPacket> response = new Response<>();
        response.setHeader(Header.newHeader(CmdSet.EXE_SQL.getRsp()));
        ExeSqlRspPacket packet = (ExeSqlRspPacket)exeSqlReqPacket.newRspPacket();
        packet.setMessage("test message");
        ArrayList<String> strings = new ArrayList<>();
        strings.add("{\"user_id\":\"abc\"}");
        packet.setResult(strings.toString());
        response.setPacket(packet);

        String s = "{\"channelId\":\"67a22c53\",\"header\":{\"magic\":120144,\"version\":0,\"serialize\":1,\"serialNo\":-1730083217,\"command\":15,\"bodyLength\":78},\"packet\":{\"id\":null,\"jobId\":null,\"commandId\":\"207gqvwp\",\"message\":null,\"exeLogs\":null,\"sql\":null,\"totalCount\":1,\"fetchedCount\":1,\"result\":{\"user_id\":10000,\"user_name\":\"kqrt0to3\",\"password\":\"823691d3a887aec33ca64edd9aafa782\",\"real_name\":\"弓嗓翠\",\"gender\":\"M\",\"birthday\":\"1945-09-27\",\"phone\":\"18814566222\",\"email\":\"kqrt0to@yeah.com\",\"open_id\":\"kqrt0to1\",\"union_id\":\"kqrt0to2\",\"card_id\":\"450111194509276408\",\"member_level\":0,\"growth_value\":0,\"profession\":\"铣工\",\"hobby\":\"CS\",\"county\":\"郊区\",\"city\":\"南宁市\",\"provice\":\"广西壮族自治区\",\"create_time\":\"2020-03-15T16:52:07.000+08:00\",\"is_deleted\":0}\",\"duration\":13012},\"request\":false}";

        s = "{\"channelId\":\"67a22c53\",\"header\":{\"magic\":120144,\"version\":0,\"serialize\":1,\"serialNo\":-1730083217,\"command\":15,\"bodyLength\":78},\"packet\":{\"id\":null,\"jobId\":null,\"commandId\":\"207gqvwp\",\"message\":null,\"exeLogs\":null,\"sql\":null,\"totalCount\":1,\"fetchedCount\":1,\"result\":\"{\\\"user_id\\\":10000,\\\"user_name\\\":\\\"kqrt0to3\\\",\\\"password\\\":\\\"823691d3a887aec33ca64edd9aafa782\\\",\\\"real_name\\\":\\\"弓嗓翠\\\",\\\"gender\\\":\\\"M\\\",\\\"birthday\\\":\\\"1945-09-27\\\",\\\"phone\\\":\\\"18814566222\\\",\\\"email\\\":\\\"kqrt0to@yeah.com\\\",\\\"open_id\\\":\\\"kqrt0to1\\\",\\\"union_id\\\":\\\"kqrt0to2\\\",\\\"card_id\\\":\\\"450111194509276408\\\",\\\"member_level\\\":0,\\\"growth_value\\\":0,\\\"profession\\\":\\\"铣工\\\",\\\"hobby\\\":\\\"CS\\\",\\\"county\\\":\\\"郊区\\\",\\\"city\\\":\\\"南宁市\\\",\\\"provice\\\":\\\"广西壮族自治区\\\",\\\"create_time\\\":\\\"2020-03-15T16:52:07.000+08:00\\\",\\\"is_deleted\\\":0}\",\"duration\":13012},\"request\":false}";
        Message object = ObjectMapperUtil.toObject(s, Request.class);

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        odpcCodec.encode(null, object, buf);

        ArrayList<Object> out = new ArrayList<>();
        odpcCodec.decode(null, buf, out);
        embeddedChannel.writeInbound(buf);

        System.out.println(ObjectMapperUtil.toJson(out.get(0)));
    }

    public static void test() throws Exception {
        MessageCodec odpcCodec = new MessageCodec();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(16384, 15,
                        4, 0, 0),
                new LoggingHandler(LogLevel.DEBUG),
                odpcCodec);
        Message<Packet> odpcPocketOdpcMessage = new Message<>();
        Header header = new Header();
        header.setMagic(1024);
        header.setVersion((byte) 1);
        header.setCommand(CmdSet.ENGINE_EVENT.getRsp());
        header.setSerialize(ISerialize.PROTOBUF_SERIALIZE);
        header.setSerialNo(System.currentTimeMillis());
        header.setBodyLength(19);
        odpcPocketOdpcMessage.setHeader(header);
        odpcPocketOdpcMessage.setPacket(new EngineEventPacket());
        System.out.println(embeddedChannel.writeInbound(odpcPocketOdpcMessage));

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        odpcCodec.encode(null, odpcPocketOdpcMessage, buf);

        ArrayList<Object> out = new ArrayList<>();
        odpcCodec.decode(null, buf, out);
        embeddedChannel.writeInbound(buf);

        System.out.println(ObjectMapperUtil.toJson(out.get(0)));
    }

    public static void  test3() throws Exception {
        DiReqPacket diPacket = new DiReqPacket();
        diPacket.setCommandId(UCodeUtil.produce());
        diPacket.setIncr(false);
        diPacket.setDriver("com.cj.mysql.Driver");
        diPacket.setUser("dev");
        diPacket.setPassword("123456");
        diPacket.setQuery("select * from a_user limit 10");
        diPacket.setFetchSize(500);
        diPacket.setNumPartitions((short)2);
        diPacket.setPartitionColumn(null);
        diPacket.setLowerBound(null);
        diPacket.setUpperBound(null);
        diPacket.setQueryTimeout(50000);
        diPacket.setUrl("jdbc:mysql://...");
        diPacket.setOutput(HdfsOutput.newOutput("/test"));
        Long storageId = 1L;
        diPacket.setSourceStorageId(storageId);

        Request<Packet> request = new Request<>();
        request.setPacket(diPacket);
        Header header = Header.newHeader(CmdSet.DATA_INTEGRATION.getReq());
        header.setSerialize(ISerialize.PROTOBUF_SERIALIZE);
        request.setHeader(header);

        System.out.println(ObjectMapperUtil.toJson(request));

        MessageCodec odpcCodec = new MessageCodec();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(16384, 15,
                        4, 0, 0),
                new LoggingHandler(LogLevel.DEBUG),
                odpcCodec);

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        odpcCodec.encode(null, request, buf);

        ArrayList<Object> out = new ArrayList<>();
        odpcCodec.decode(null, buf, out);
        embeddedChannel.writeInbound(buf);

        System.out.println("De:" + ObjectMapperUtil.toJson(out.get(0)));


        ProtobufSerialize protobufSerialize = new ProtobufSerialize();
        byte[] serialize = protobufSerialize.serialize(diPacket, DiReqPacket.class);
        DiReqPacket deserialize = protobufSerialize.deserialize(serialize, DiReqPacket.class);
        System.out.println(ObjectMapperUtil.toJson(deserialize));

        test4(diPacket);

    }

    public static void test4(DiReqPacket diPacket) throws IOException, ClassNotFoundException, SerializeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(diPacket);
        byte[] byteArray = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = (DiReqPacket) ois.readObject();

        System.out.println("JDK : " + ObjectMapperUtil.toJson(o));

        JdkSerialize jdkSerialize = new JdkSerialize();
        byte[] serialize = jdkSerialize.serialize(diPacket, DiReqPacket.class);
        System.out.println("length : " + serialize.length);
        DiReqPacket deserialize = jdkSerialize.deserialize(serialize, DiReqPacket.class);
        System.out.println("JDK2 : " + ObjectMapperUtil.toJson(deserialize));
    }

}
