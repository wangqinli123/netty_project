package com.itheima.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class LdTomcat {

    public void start(int port) throws Exception{

        //boss线程
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //worker线程
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            //Netty服务
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup,workerGroup)
                    //主线程处理类
                    .channel(NioServerSocketChannel.class)
                    //子线程处理类
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel client) throws Exception {
                            //服务端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                            client.pipeline().addLast(new HttpResponseEncoder());
                            //服务端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                            client.pipeline().addLast(new HttpRequestDecoder());
                            //业务逻辑处理
                            client.pipeline().addLast(new LdTomcatHandler());
                        }
                    })
                    //针对主线程配置
                    .option(ChannelOption.SO_BACKLOG,128)
                    //针对子线程配置
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture f = server.bind(port).sync();
            System.out.printf("Http server start.");
            f.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args) throws Exception {
        new LdTomcat().start(8080);
    }
}
