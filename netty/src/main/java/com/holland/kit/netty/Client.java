package com.holland.kit.netty;

import com.holland.kit.netty.protobuf.Msg;
import com.holland.kit.netty.protobuf.Person;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;


public class Client {
    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        int port = 40404;

        EventLoopGroup group = new NioEventLoopGroup(1);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    // 其他配置...
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 初始化 pipeline
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufDecoder(Msg.getDefaultInstance()));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(new ProtobufEncoder());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();

            Channel channel = channelFuture.channel();

            channel.writeAndFlush(Msg.newBuilder()
                    .setDataType(Msg.DataType.PersonType)
                    .setPerson(Person.newBuilder()
                            .setName("holland")
                            .setAddress("emmmmm")
                            .build())
                    .build());
        } finally {
            group.shutdownGracefully();
        }
    }
}
