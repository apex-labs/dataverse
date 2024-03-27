package org.apex.dataverse.core.netty.client;

import lombok.Getter;
import org.apex.dataverse.core.context.ClientContext;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.netty.codec.FrameDecoder;
import org.apex.dataverse.core.netty.codec.MessageCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for interacting with the computing engine.
 * 1 Sends the command to the engine client.
 * 2 receives the response returned by the engine client.
 *
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/2/16 19:31
 */
@Slf4j
public class EngineClient implements Runnable {

    private boolean ready = false;

    private boolean initFlag = false;

    private Channel channel;

    /**
     * Channel 初始化成功表示
     */
    private final LinkedBlockingQueue<Boolean> initQueue;

    /**
     * 线程池
     */
    private final NioEventLoopGroup executor;

    /**
     * 客户端Context
     */
    @Getter
    private final ClientContext context;

    public EngineClient(ClientContext context) {
        this.context = context;
        this.executor = new NioEventLoopGroup(this.context.getEnv().getThreads());
        this.initQueue = new LinkedBlockingQueue<>();
    }

    public void startClient() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(executor)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        ChannelPipeline pipeline = sc.pipeline();
                        pipeline.addLast(new FrameDecoder());
                        pipeline.addLast(new MessageCodec());
                        //pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                        pipeline.addLast(new SimpleChannelInboundHandler<Response<Packet>>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                channel = ctx.channel();

                                // 已成功建立链接，可进行通信
                                ready = true;

                                // 标记成功
                                initQueue.put(true);
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response<Packet> response) throws Exception {
                                context.pushResponse(response);
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof Response) {
                                    context.pushResponse((Response<Packet>) msg);
                                }
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                // 链接断开
                                ready = false;
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                                ready = false;
                                log.error("Engine netty client found error", cause);
                                closeChannel();
                            }
                        });
                    }
                });
        try {

            ChannelFuture future = bootstrap.connect(this.context.getEnv().getServerAddress(), this.context.getEnv().getServerPort()).sync();

            // add listener
            future.addListener(promise -> {
                if (promise.isSuccess()) {
                    // 启动线程
                    executor.execute(() -> {
                        while (ready) {
                            try {
                                Request<Packet> request = context.takeRequest();
                                channel.writeAndFlush(request);
                            } catch (InterruptedException e) {
                                log.error("take message from context[pipeline] found error", e);
                            }
                        }
                    });

                    log.info("Engine client start successfully, channel id is : {}, Address is : {}, Port is : {}",
                            future.channel().id(), this.context.getEnv().getServerAddress(),
                            this.context.getEnv().getServerPort());
                } else {
                    log.error("Engine client start failed", promise.cause());
                }
            });

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("Connect to engine server[{}:{}] found error",
                    this.context.getEnv().getServerAddress(),
                    this.context.getEnv().getServerPort(), e);
        } finally {
            executor.shutdownGracefully();
        }
    }

    @Override
    public void run() {
        try {
            startClient();
        } catch (InterruptedException e) {
            log.error("Start engine client found error");
        }
    }

    public boolean isReady() {
        return ready;
    }

    /**
     * 断开链接
     */
    public void closeChannel() throws InterruptedException {
        this.ready = false;

        if (this.isActive()) {
            // TODO 发送断开链接
            this.channel.close().sync();
        }

        this.executor.shutdownGracefully();

        //Thread.currentThread().interrupt();
    }

    /**
     * 发送命令请求
     *
     * @param request
     * @throws InterruptedException
     */
    public void pushRequest(Request<Packet> request) throws InterruptedException {
        this.context.pushRequest(request);
    }

    /**
     * 获取响应
     *
     * @return
     * @throws InterruptedException
     */
    public Response<Packet> takeResponse() throws InterruptedException {
        return this.context.takeResponse();
    }

    /**
     * 获取响应
     *
     * @return
     * @throws InterruptedException
     */
    public Response<Packet> pollResponse() throws InterruptedException {
        return this.context.pollResponse();
    }

    /**
     * 获取响应
     *
     * @return
     * @throws InterruptedException
     */
    public Response<Packet> pollResponse(Long time, TimeUnit timeUnit) throws InterruptedException {
        return this.context.pollResponse(time, timeUnit);
    }

    /**
     * 判断连接是否是isActive
     *
     * @return
     */
    public boolean isActive() {
        boolean flag = false;
        if (this.isConnected() && null != this.channel) {
            flag = this.channel.isActive();
        }
        return flag;
    }

    /**
     * 通道是否完成连接
     *
     * @return
     */
    public boolean isConnected() {
        if (initFlag) {
            return true;
        }
        try {
            Boolean isInitialized = this.initQueue.poll(this.context.getEnv().getConnTimeOutMs(), TimeUnit.MILLISECONDS);
            if (null != isInitialized) {
                initFlag = isInitialized;
                return isInitialized;
            } else {
                log.error("The Engine client fails to connect to the EngineServer[" +
                        this.context.getEnv().getServerAddress() + ":" +
                        this.context.getEnv().getServerPort() + "]");
                return false;
            }
        } catch (InterruptedException e) {
            log.error("Get channel initialize flag found error", e);
            return false;
        }
    }
}
