package org.apex.dataverse.core.msg;

import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.enums.CmdSet;
import org.apex.dataverse.core.msg.packet.di.DiReqPacket;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.engine.TrackingPacket;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.apex.dataverse.core.util.StringUtil;

/**
 * @author  : Danny.Huo
 * @date : 2023/1/12 14:04
 * @since : 0.1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Request<T extends Packet> extends Message<T> {

    public Request() {
        this.setRequest(true);
    }

    public Request(Header header) {
        this.setRequest(true);
        this.setHeader(header);
    }

    /**
     * The Response is generated based on the Request
     * and the data packet is based on the data packet specified by the generics
     *
     * @return Response<P extends Packet>
     */
    public <P extends Packet> Response<P> newRsp(boolean success) {
        Header header = this.getHeader();
        Response<P> response = new Response<>();
        header.setCommand(CmdSet.getRspByReq(header.getCommand()));
        response.setHeader(header);
        response.setSuccess(success);
        response.setPacket((P) this.getPacket().newRspPacket());
        response.setChannelId(this.getChannelId());
        return response;
    }

    /**
     * The Response is generated based on the Request
     * and the corresponding Packet is generated based on the data packet in the Request
     * @param success Success flag
     * @return Response<Packet>
     */
    public Response<Packet> newSelfRsp(boolean success) {
        Header header = this.getHeader();
        Response<Packet> response = new Response<>();
        header.setCommand(CmdSet.getRspByReq(header.getCommand()));
        response.setHeader(header);
        response.setSuccess(success);
        Class<? extends Packet> aClass = CmdSet.getClass(this.getHeader().getCommand());
        response.setPacket(aClass.cast(this.getPacket().newRspPacket()));
        response.setChannelId(this.getChannelId());
        return response;
    }

    /**
     * New and tracking request without message text
     * @return Request<Packet>
     */
    public Request<Packet> newTracking() {
        return newTracking(null);
    }

    /**
     * New and tracking request with message text
     * @param text request txt
     * @return Request<Packet>
     */
    public Request<Packet> newTracking(String text) {
        Header header = this.getHeader().clone();
        header.setCommand(CmdSet.CMD_TRACKING.getReq());
        header.setBodyLength(null);
        Request<Packet> tracking = new Request<>(header);
        tracking.setPacket(new TrackingPacket(text));
        tracking.getPacket().setCommandId(this.getPacket().getCommandId());
        tracking.getPacket().setJobId(this.getPacket().getJobId());
        return tracking;
    }

    public static void main(String[] args) throws JsonProcessingException {
        Header header = new Header();
        header.setCommand(CmdSet.DATA_INTEGRATION.getReq());
        header.setBodyLength(10);
        header.setMagic(1);
        Request<DiReqPacket> request = new Request<>();
        request.setHeader(header);
        DiReqPacket packet = new DiReqPacket();
        packet.setId("10001");
        request.setPacket(packet);

        Response<Packet> pResponse = request.newRsp(Response.SUCCESS);
        System.out.println(ObjectMapperUtil.toJson(pResponse));
    }

    /**
     * Jude request is valid or not
     * @param req Request
     * @return Boolean
     */
    public static boolean isValid (Request<Packet> req) {
        return null != req && null != req.getPacket()
                && StringUtil.isNotBlank(req.getPacket().getCommandId())
                && null != req.getHeader();
    }

}
