package org.apex.dataverse.core.context;

import org.apex.dataverse.core.exception.InvalidCmdException;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;
import lombok.Data;
import org.apex.dataverse.core.session.ISession;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author : Danny.Huo
 * @date : 2023/2/16 19:57
 * @since : 0.1.0
 */
@Data
public class AbstractContext implements Serializable, ISession {

    /**
     * Message flow
     * Request and response messages flow through the Pipeline
     */
    private Pipeline pipeline;

    @Override
    public Response<Packet> exe(Request<Packet> request)
            throws InterruptedException, InvalidCmdException {
        return this.pipeline.exeCmd(request);
    }

    @Override
    public Response<Packet> exe(Request<Packet> request, long timeOut)
            throws InterruptedException, InvalidCmdException {
        return this.pipeline.exeCmd(request, timeOut);
    }

    @Override
    public void exeAsync(Request<Packet> request)
            throws InterruptedException, InvalidCmdException {

        if (!Request.isValid(request)) {
            throw new InvalidCmdException("The command '" + request + "' is invalid");
        }

        this.pushRequest(request);
    }

    public void pushRequest(Request<Packet> request) throws InterruptedException {
        pipeline.pushRequest(request);
    }

    public void pushResponse(Response<Packet> response) throws InterruptedException {
        pipeline.pushResponse(response);
    }

    public Request<Packet> takeRequest() throws InterruptedException {
        return pipeline.takeRequest();
    }

    public Request<Packet> pollRequest() throws InterruptedException {
        return pipeline.pollRequest();
    }

    public Request<Packet> pollRequest(Long time, TimeUnit timeUnit) throws InterruptedException {
        return pipeline.pollRequest(time, timeUnit);
    }

    public Response<Packet> takeResponse() throws InterruptedException {
        return pipeline.takeResponse();
    }

    public Response<Packet> pollResponse() throws InterruptedException {
        return pipeline.pollResponse();
    }

    public Response<Packet> pollResponse(Long time, TimeUnit timeUnit) throws InterruptedException {
        return pipeline.pollResponse(time, timeUnit);
    }


    public Integer requestSize () {
        return pipeline.requestSize();
    }

    public Integer responseSize () {
        return pipeline.responseSize();
    }

    public boolean requestFull () {
        return pipeline.requestFull();
    }

    public boolean responseFull () {
        return pipeline.responseFull();
    }

    public boolean requestEmpty () {
        return pipeline.requestEmpty();
    }

    public boolean responseEmpty () {
        return pipeline.responseEmpty();
    }
}
