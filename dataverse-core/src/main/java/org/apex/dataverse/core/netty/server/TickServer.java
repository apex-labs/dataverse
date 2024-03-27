package org.apex.dataverse.core.netty.server;

import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.netty.codec.FrameDecoder;
import org.apex.dataverse.core.netty.codec.MessageCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.core.netty.worker.ServerWorker;

/**
 * Engine状态检测Server, Engine通过此Server上报状态
 * 此Server运行在Port端
 *
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/2/16 18:57
 */
@Slf4j
public class TickServer implements IServer {

    private final NioEventLoopGroup boss;

    private final NioEventLoopGroup worker;

    private final ServerContext context;

    public static TickServer newStateServer(ServerContext context) {
        return new TickServer(context);
    }

    public TickServer(ServerContext context) {
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
        serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new FrameDecoder());
                pipeline.addLast(new MessageCodec());
                pipeline.addLast(new StateHandler());
            }
        });

        try {
            // bind
            ChannelFuture feature = serverBootstrap.bind(this.context.getEnv().getServerPort()).sync();

            // add listener
            feature.addListener(promise -> {
                if (promise.isSuccess()) {
                    // 启动线程
                    boss.execute(ServerWorker.newWorker(context));

                    log.info("Tick server[port : {}] start successfully, channel id is : {}",
                            this.context.getEnv().getServerPort(), feature.channel().id());
                } else {
                    log.error("Tick server[port : {}] start failed",
                            this.context.getEnv().getServerPort(), promise.cause());
                }
            });

            // close
            feature.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Initialize Odpc server found exception", e);
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                log.error("shutdown netty NioEventLoopGroup for Odpc Server found error", e);
            }
        }
    }

    class StateHandler extends SimpleChannelInboundHandler<Request<Packet>> {
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
                context.pushRequest(request);
            }
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request<Packet> request) throws Exception {
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
