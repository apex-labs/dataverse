package org.apex.dataverse.core.netty.server;

import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.netty.codec.FrameDecoder;
import org.apex.dataverse.core.netty.codec.MessageCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.core.netty.worker.ServerWorker;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/2/16 18:57
 */
@Slf4j
public class PortServer implements IServer {

    private final NioEventLoopGroup boss;

    private final NioEventLoopGroup worker;

    private final ServerContext context;

    public static PortServer newPortServer(ServerContext context) {
        return new PortServer(context);
    }

    public PortServer(ServerContext context) {
        this.context = context;
        this.boss = new NioEventLoopGroup(this.context.getEnv().getBossThreads());
        this.worker = new NioEventLoopGroup(this.context.getEnv().getWorkerThreads());
    }

    @Override
    public void run() {
        startServer();
    }

    private void startServer() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new FrameDecoder());
                pipeline.addLast(new MessageCodec());
                //pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                pipeline.addLast(new PortHandler());
            }
        });

        try {
            // bind
            ChannelFuture future = serverBootstrap.bind(this.context.getEnv().getServerPort()).sync();

            // add listener
            future.addListener(promise -> {
                if (promise.isSuccess()) {
                    // 启动线程
                    boss.execute(ServerWorker.newWorker(context));

                    log.info("Port server[port : {}] start successfully, channel id is : {}",
                            this.context.getEnv().getServerPort(), future.channel().id());
                } else {
                    log.error("Port server[port : {}] start failed",
                            this.context.getEnv().getServerPort(), promise.cause());
                }
            });

            // close
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Initialize Port server found exception", e);
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                log.error("shutdown netty NioEventLoopGroup for Port Server found error", e);
            }
        }
    }

    class PortHandler extends SimpleChannelInboundHandler<Request<Packet>> {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            context.addChannel(channel);
            log.info("Invoke channelActive(), channel is : {}", channel.toString());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            context.removeChannel(ctx.channel());
            log.info("Invoke channelInactive(), channel is : {}", ctx.channel().toString());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof Request) {
                Request<Packet> request = (Request<Packet>) msg;
                request.setChannelId(ctx.channel().id().toString());
                try {
                    context.pushRequest(request);
                } catch (Exception e) {
                    // TODO Request lose
                    log.error("Push request found error. Request : {}", request, e);
                }
            }
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request<Packet> request) throws Exception {
            request.setChannelId(channelHandlerContext.channel().id().toString());
            context.pushRequest(request);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            context.removeChannel(ctx.channel());
            log.error("Invoke exceptionCaught(), channel is : {}", ctx.channel().toString(), cause);
        }
    }
}
