package org.apex.dataverse.netty.processor;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @version : v1.0
 * @projectName : nexus-dtvs
 * @package : com.apex.dtvs.quue
 * @className : NettyMsgQueue
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/1/11 12:49
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
@Data
public class ServerProcessor<I, O> implements Serializable {

    /**
     * 请求消息队列
     */
    private LinkedBlockingQueue<I> requestQueue;

    /**
     * 响应消息队列
     */
    private LinkedBlockingQueue<O> responseQueue;

    private I nextRequest;

    /**
     * 默认消息队列的容量大小
     */
    private final static Integer DEFAULT_QUEUE_CAPACITY = 10000;

    public ServerProcessor() {
        requestQueue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
        responseQueue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
    }

    public ServerProcessor(int capacity) {
        requestQueue = new LinkedBlockingQueue<>(capacity);
        responseQueue = new LinkedBlockingQueue<>(capacity);
    }

    /**
     * 读取输入消息
     * @param msg
     * @throws InterruptedException
     */
    public void read (I msg) throws InterruptedException {
        this.requestQueue.put(msg);
    }

    /**
     * 写入输出消息
     * @param o
     * @throws InterruptedException
     */
    public void write (O o) throws InterruptedException {
        this.responseQueue.put(o);
    }

    public I take () throws InterruptedException {
        return this.requestQueue.take();
    }

    public I poll () {
        return this.requestQueue.poll();
    }

    public I poll (Long time, TimeUnit unit) throws InterruptedException {
        return this.requestQueue.poll(time, unit);
    }

    public boolean hasNext () throws InterruptedException {
        this.nextRequest = take();
        return this.nextRequest != null;
    }

    public I next () {
        return this.nextRequest;
    }
}
