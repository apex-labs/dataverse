package org.apex.dataverse.core.session;

import org.apex.dataverse.core.exception.InvalidCmdException;
import org.apex.dataverse.core.exception.InvalidConnException;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;

/**
 * @author Danny.Huo
 * @date 2023/11/24 19:58
 * @since 0.1.0
 */
public interface ISession {

    /**
     * The default timeout period for executing the command synchronously
     * @param request Request
     * @return Response<Packet>
     * @throws InterruptedException InterruptedException
     * @throws InvalidCmdException InvalidCmdException
     */
    Response<Packet> exe(Request<Packet> request)
            throws InterruptedException, InvalidCmdException, InvalidConnException;

    /**
     * Execute the command synchronously and specify the timeout period
     * @param request Request
     * @param timeout timeout
     * @return Response<Packet>
     * @throws InterruptedException InterruptedException
     * @throws InvalidCmdException InvalidCmdException
     */
    Response<Packet> exe(Request<Packet> request, long timeout)
            throws InterruptedException, InvalidCmdException, InvalidConnException;

    /**
     * Asynchronous execution command
     * @param request Request
     * @throws InterruptedException InterruptedException
     * @throws InvalidCmdException InvalidCmdException
     */
    void exeAsync(Request<Packet> request)
            throws InterruptedException, InvalidCmdException, InvalidConnException;
}
