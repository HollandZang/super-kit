package com.holland.kit.netty;

import com.holland.kit.netty.protobuf.Msg;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class Server {
    public static void main(String[] args) throws InterruptedException {
        int port = 40404;

        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 1 表示单个线程，用于接受连接
        try {
            EventLoopGroup workerGroup = new NioEventLoopGroup(1); // 默认线程数
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        // 其他配置...
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                // 初始化 pipeline
                                ChannelPipeline pipeline = ch.pipeline();

                                //用于decode前解决半包和粘包问题（利用包头中的包含数组长度来识别半包粘包）
                                pipeline.addLast(new ProtobufVarint32FrameDecoder());

                                //这里解码器解析的对象是 最外层的消息体/类
                                pipeline.addLast(new ProtobufDecoder(Msg.getDefaultInstance()));

                                //用于在序列化的字节数组前加上一个简单的包头，只包含序列化的字节长度。
                                pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());

                                //用于对Protobuf类型序列化。
                                pipeline.addLast(new ProtobufEncoder());

                                pipeline.addLast(new SimpleChannelInboundHandler<Msg>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                            }
                        });

                ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

                Channel channel = channelFuture.channel();

                channel.closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
            }
        } finally {
            bossGroup.shutdownGracefully();
        }
    }
}