package org.apex.dataverse.netty.server;

import org.apex.dataverse.exception.WebSocketServerException;
import org.apex.dataverse.msg.Request;
import org.apex.dataverse.msg.Response;
import org.apex.dataverse.netty.config.WsServerConfig;
import org.apex.dataverse.netty.processor.HandleRequest;
import org.apex.dataverse.netty.processor.ServerProcessor;
import org.apex.dataverse.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @version : v1.0
 * @projectName : nexus-dtvs
 * @package : com.apex.dtvs.netty.server
 * @className : WebsocketServer
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/1/11 11:18
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
@Slf4j
public class WebSocketServer implements NettyServer {

    private WsServerConfig config;

    /**
     * Netty server 业务处理器
     */
    private ServerProcessor<Request<String>, Response<String>> serverProcessor;

    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    private HandleRequest<String, String> handleRequest;

    /**
     * Channel Map
     */
    private Map<String, Channel> channelMap = new HashMap<>();

    /**
     * 带ServerProcessor的有参构造
     * @param config
     * @param serverProcessor
     * @throws WebSocketServerException
     */
    public WebSocketServer(WsServerConfig config, ServerProcessor serverProcessor, HandleRequest<String, String> handleRequest)
            throws WebSocketServerException {
        if (null == config) {
            throw new WebSocketServerException("WebSocketServerConfig is null");
        }

        if (null == serverProcessor) {
            throw new WebSocketServerException("WebSocketServer message queue is null");
        }

        this.config = config;
        this.serverProcessor = serverProcessor;
        this.handleRequest = handleRequest;
    }

    @Override
    public void startServer() throws
            WebSocketServerException, InterruptedException {
        this.boss = new NioEventLoopGroup(config.getBossThread());
        this.worker = new NioEventLoopGroup(config.getWorkerThreads());

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();

                            // Http Server 编解码器
                            pipeline.addLast(new HttpServerCodec());

                            // 以块的方式写
                            pipeline.addLast(new ChunkedWriteHandler());

                            // http在传输中是分段的，HttpObjectAggregator可将多个分段聚合起来
                            // 当浏览器发送大量数据时，会发出多次http请求
                            pipeline.addLast(new HttpObjectAggregator(config.getMaxContentLength()));

                            // WebSocketServerProtocolHandler
                            WebSocketServerProtocolHandler wsHandler = new WebSocketServerProtocolHandler("/" + config.getWebsocketPath(), config.getProtocol());
                            pipeline.addLast(wsHandler);

                            // 业务自定义处理器
                            pipeline.addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    String id = ctx.channel().id().toString();
                                    log.info("Channel active, will add channel. Channel id is {}", id);
                                    channelMap.put(id, ctx.channel());
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    String id = ctx.channel().id().toString();
                                    log.info("Channel inactive, will remove it. Channel id is {}", id);
                                    channelMap.remove(id);
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame frame) throws Exception {
                                    readRequest(channelHandlerContext.channel(), frame.text());
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    if (msg instanceof TextWebSocketFrame) {
                                        TextWebSocketFrame frame = (TextWebSocketFrame) msg;
                                        readRequest(ctx.channel(), frame.text());
                                    } else if (msg instanceof FullHttpRequest) {
                                        FullHttpRequest request = (FullHttpRequest) msg;
                                    }
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    String id = ctx.channel().id().toString();
                                    log.info("Channel exception, will remove it. Channel id is {}", id);
                                    channelMap.remove(id);
                                }
                            });
                        }
                    });

            // 开启请求处理
            this.openHandleRequest();

            // 开启响应处理
            this.openHandleResponse();

            // 启动server，绑定端口
            ChannelFuture channelFuture = server.bind(config.getPort()).sync();

            // 关闭服务
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("Start WebSocketServer found Exception", e);
            throw new WebSocketServerException(e);
        } finally {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        }
    }

    /**
     * 读取请求消息
     * @param msg
     * @throws InterruptedException
     */
    private void readRequest(Channel channel, String msg) throws InterruptedException, JsonProcessingException {
        Request<String> request;
        String trimMsg = msg.trim();
        if (log.isDebugEnabled()) {
            log.debug("Message [{}] from client, will read to netty biz processor request queue", msg);
        }
        if (trimMsg.startsWith(JSON_START_CHAR) && trimMsg.endsWith(JSON_END_CHAR)) {
            try {
                request = ObjectMapperUtil.toObject(trimMsg, Request.class);
                request.setChannelId(channel.id().toString());
                serverProcessor.read(request);
                return;
            } catch (Exception e) {
                log.error("Request json message can't convert to Request class : {}", msg, e);
            }
        }
        request = new Request();
        request.setChannelId(channel.id().toString());
        request.setMessageBody(msg);
        serverProcessor.read(request);
    }

    /**
     * 开启处理请求
     */
    @Override
    public void openHandleRequest () {
        worker.execute(() -> {
            try {
                while (serverProcessor.hasNext()) {
                    Request<String> request = serverProcessor.next();
                    worker.execute(() -> {
                        try {
                            Response<String> response = new Response<>(request.getSerialNo(), request.getChannelId());
                            handleRequest.handleRequest(request, response);
                            if (log.isDebugEnabled()) {
                                log.debug("Request handled successfully, Request is {}, Response is {}",
                                        ObjectMapperUtil.toJson(request), ObjectMapperUtil.toJson(response));
                            }
                            serverProcessor.write(response);
                        } catch (InterruptedException e) {
                            if (null == request) {
                                log.error("Request null, handle it found error", e);
                            } else {
                                try {
                                    log.error("Handle request {} found error", ObjectMapperUtil.toJson(request), e);
                                } catch (JsonProcessingException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        } catch (JsonProcessingException e) {
                            log.error("Object to json error", e);
                        }
                    });
                }
            } catch (Exception e) {
                log.error("Handle request from queue found error", e);
            }
        });
    }

    /**
     * 开启响应处理
     */
    @Override
    public void openHandleResponse() {
        worker.execute(() -> {
            LinkedBlockingQueue<Response<String>> responseQueue = serverProcessor.getResponseQueue();
            Response<String> response = null;
            while (true) {
                try {
                    response = responseQueue.take();
                    Channel channel = channelMap.get(response.getChannelId());
                    String responseJson = ObjectMapperUtil.toJson(response);
                    if (null != channel) {
                        channel.writeAndFlush(new TextWebSocketFrame(responseJson));
                    } else {
                        log.warn("The channel[{}] not exist, response message[{}] can't back to client from server",
                                response.getChannelId(), responseJson);
                    }
                } catch (InterruptedException e) {
                    try {
                        if (null != response) {
                            log.error("Send response found error", ObjectMapperUtil.toJson(response), e);
                        } else {
                            log.error("Response is null", e);
                        }
                    } catch (JsonProcessingException ex) {
                        log.error("Object to json error, for error log", e);
                    }
                } catch (JsonProcessingException e) {
                    log.error("Object to json error, for response", e);
                }
            }
        });
    }

    @Override
    public void run() {
        try {
            this.startServer();
        } catch (WebSocketServerException e) {
            log.error("Start WebSocket server found WebSocketServerException", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.error("Start WebSocket server found InterruptedException", e);
            throw new RuntimeException(e);
        }
    }
}
