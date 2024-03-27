package org.apex.dataverse.core.context;

import lombok.Data;
import org.apex.dataverse.core.exception.InvalidCmdException;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author danny
 * @date 2023-02-16 22:12
 * @since 0.1.0
 */
@Data
public class Pipeline implements Serializable {

    /**
     * Request默认容量
     */
    private final static int DEFAULT_REQUEST_CAPACITY = 5000;

    /**
     * Response默认容量
     */
    private final static int DEFAULT_RESPONSE_CAPACITY = 5000;

    /**
     * Request队列容量
     */
    private int requestCapacity;

    /**
     * Response队列容量
     */
    private int responseCapacity;

    /**
     * 实例化默认配置的Pipeline
     *
     * @return Pipeline
     */
    public static Pipeline newDefaultPipeline() {
        return new Pipeline();
    }

    /**
     * 分别指定容量大小的实例化
     *
     * @param capacity Pipeline capacity
     * @return Pipeline
     */
    public static Pipeline newPipeline(int capacity) {
        return new Pipeline(capacity);
    }

    /**
     * 分别指定容量大小的实例化
     *
     * @param requestCapacity Request capacity
     * @param responseCapacity Response capacity
     * @return Pipeline
     */
    public static Pipeline newPipeline(int requestCapacity, int responseCapacity) {
        return new Pipeline(requestCapacity, responseCapacity);
    }

    /**
     * 默认无参构造， 默认容量
     */
    private Pipeline() {
        this.initialize(DEFAULT_REQUEST_CAPACITY, DEFAULT_RESPONSE_CAPACITY);
    }

    /**
     * 指定容量的有参构造
     *
     * @param capacity capacity
     */
    private Pipeline(int capacity) {
        int request = capacity > 0 ? capacity : DEFAULT_REQUEST_CAPACITY;
        int response = capacity > 0 ? capacity : DEFAULT_RESPONSE_CAPACITY;
        this.initialize(request, response);
    }

    /**
     * 分别指定Request和Response容量的有参构造
     *
     * @param requestCapacity Request capacity
     * @param responseCapacity Response capacity
     */
    private Pipeline(int requestCapacity, int responseCapacity) {
        int request = requestCapacity > 0 ? requestCapacity : DEFAULT_REQUEST_CAPACITY;
        int response = responseCapacity > 0 ? responseCapacity : DEFAULT_RESPONSE_CAPACITY;
        this.initialize(request, response);
    }

    /**
     * 请求队列
     */
    private LinkedBlockingQueue<Request<Packet>> requests;

    /**
     * 响应队列
     */
    private LinkedBlockingQueue<Response<Packet>> responses;

    /**
     * 请求对应的结果集
     */
    private Map<String, RspSet> rspSets;

    /**
     * 初始化方法
     *
     * @param requestCapacity Request capacity
     * @param responseCapacity Response capacity
     */
    private void initialize(int requestCapacity, int responseCapacity) {
        this.requestCapacity = requestCapacity;
        this.responseCapacity = responseCapacity;
        requests = new LinkedBlockingQueue<>(requestCapacity);
        responses = new LinkedBlockingQueue<>(responseCapacity);
        rspSets = new ConcurrentHashMap<>(requestCapacity);
    }

    /**
     * 执行命令
     *
     * @param request request
     * @throws InterruptedException LinkedBlockQueue InterruptedException
     */
    public Response<Packet> exeCmd(Request<Packet> request)
            throws InterruptedException, InvalidCmdException {

        if (!Request.isValid(request)) {
            throw new InvalidCmdException("The command '" + request + "' is invalid");
        }

        this.pushRequest(request);

        RspSet resultSet = rspSets.get(request.getPacket().getCommandId());
        return resultSet.takeRsp();
    }

    /**
     * 执行命令
     *
     * @param request request
     * @throws InterruptedException LinkedBlockQueue InterruptedException
     */
    public Response<Packet> exeCmd(Request<Packet> request, long timeOut)
            throws InterruptedException, InvalidCmdException {
        if (!Request.isValid(request)) {
            throw new InvalidCmdException("The command '" + request + "' is invalid");
        }

        this.pushRequest(request);

        RspSet resultSet = rspSets.get(request.getPacket().getCommandId());
        return resultSet.takeRsp(timeOut);
    }

    /**
     * 执行异步命令
     *
     * @param request request
     * @throws InterruptedException LinkedBlockQueue InterruptedException
     */
    public void exeAsyncCmd(Request<Packet> request)
            throws InterruptedException {
        this.pushRequest(request);
    }


    /**
     * 根据命令ID获取结果
     *
     * @param commandId command id
     * @return Response<Packet>
     * @throws InterruptedException LinkedBlockQueue InterruptedException
     */
    public Response<Packet> getResponse(String commandId)
            throws InterruptedException {
        RspSet resultSet = this.rspSets.get(commandId);
        if (null != resultSet) {
            return resultSet.takeRsp();
        }
        return null;
    }

    /**
     * 根据命令ID获取结果
     *
     * @param commandId command id
     * @param timeOut timeout
     * @return Response<Packet>
     * @throws InterruptedException LinkedBlockQueue InterruptedException
     */
    public Response<Packet> getResponse(String commandId, long timeOut)
            throws InterruptedException {
        RspSet resultSet = this.rspSets.get(commandId);
        if (null != resultSet) {
            return resultSet.takeRsp(timeOut);
        }
        return null;
    }

    /**
     * 推送请求
     *
     * @param request request
     * @throws InterruptedException  LinkedBlockQueue InterruptedException
     */
    public void pushRequest(Request<Packet> request)
            throws InterruptedException {
        this.requests.put(request);

        // 放入结果容器
        this.rspSets.put(request.getPacket().getCommandId(), RspSet.getRspSet());
    }

    /**
     * 推送响应
     *
     * @param response response
     * @throws InterruptedException  LinkedBlockQueue InterruptedException
     */
    public void pushResponse(Response<Packet> response)
            throws InterruptedException {
        this.responses.put(response);
        RspSet resultSet = this.rspSets.get(response.getPacket().getCommandId());
        if (null != resultSet) {
            resultSet.pushRsp(response);
        }
    }

    /**
     * 获取请求
     *
     * @return Request<Packet>
     * @throws InterruptedException  LinkedBlockQueue InterruptedException
     */
    public Request<Packet> takeRequest()
            throws InterruptedException {
        return this.requests.take();
    }

    /**
     * 获取请求
     *
     * @return Request<Packet>
     * @throws InterruptedException  LinkedBlockQueue InterruptedException
     */
    public Request<Packet> pollRequest()
            throws InterruptedException {
        return this.requests.poll();
    }

    /**
     * 获取请求，带超时时间
     *
     * @param time time
     * @param timeUnit time unit
     * @return Request<Packet>
     * @throws InterruptedException  LinkedBlockQueue InterruptedException
     */
    public Request<Packet> pollRequest(Long time, TimeUnit timeUnit)
            throws InterruptedException {
        return this.requests.poll(time, timeUnit);
    }

    /**
     * 阻塞获取响应
     *
     * @return Response<Packet>
     * @throws InterruptedException  LinkedBlockQueue InterruptedException
     */
    public Response<Packet> takeResponse()
            throws InterruptedException {
        Response<Packet> take = this.responses.take();
        RspSet removed = rspSets.remove(take.getPacket().getCommandId());
        removed.close();
        return take;
    }

    /**
     * 获取响应
     *
     * @return Response<Packet>
     * @throws InterruptedException  LinkedBlockQueue InterruptedException
     */
    public Response<Packet> pollResponse()
            throws InterruptedException {
        Response<Packet> poll = this.responses.poll();
        if (null != poll) {
            RspSet removed = rspSets.remove(poll.getPacket().getCommandId());
            removed.close();
        }
        return poll;
    }

    /**
     * 获取响应，带超时时间
     *
     * @param time time
     * @param timeUnit time unit
     * @return Response<Packet>
     * @throws InterruptedException LinkedBlockQueue InterruptedException
     */
    public Response<Packet> pollResponse(Long time, TimeUnit timeUnit)
            throws InterruptedException {
        Response<Packet> poll = this.responses.poll(time, timeUnit);
        if (null != poll) {
            RspSet removed = rspSets.remove(poll.getPacket().getCommandId());
            removed.close();
        }
        return poll;
    }

    /**
     * 请求队列中的消息数量
     *
     * @return Integer(request size)
     */
    public Integer requestSize() {
        return requests.size();
    }

    /**
     * 响应队列中的消息数量
     *
     * @return Integer(response size)
     */
    public Integer responseSize() {
        return responses.size();
    }

    /**
     * 请求队列是否已满
     *
     * @return boolean
     */
    public boolean requestFull() {
        return requests.size() == requestCapacity;
    }

    /**
     * 响应队列是否已满
     *
     * @return boolean
     */
    public boolean responseFull() {
        return responses.size() == responseCapacity;
    }

    /**
     * 请求队列是否空
     *
     * @return boolean
     */
    public boolean requestEmpty() {
        return requests.isEmpty();
    }

    /**
     * 响应队列是否为空
     *
     * @return boolean
     */
    public boolean responseEmpty() {
        return responses.isEmpty();
    }
}
