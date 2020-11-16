package com.itheima.netty.FixedLengthFrameDecoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * FixedLengthFrameDecoder实例
 * FixedLengthFrameDecoder是固定长度解码器，它能够按照指定长度对消息进行自动解码，
 * 开发者不需要考虑TCP的粘包和拆包问题，非常实用；
 *
 * FixedLengthFrameDecoder实例：
 * 利用FixedLengthFrameDecoder解码器，无论一次接收到多少数据报，它都会按照构造函数中
 * 设置固定的长度进行解码，如果是半包信息，
 * FixedLengthFrameDecoder会缓存半包消息并等待下个包到达后进行拼包，直到读取到一个完整的包；
 *
 *
 * 客户端测试：客户端使用telnet localhost 8080命令测试
 */
public class EchoServer {

	public void bind(int port) throws InterruptedException{
		
		//配置服务端NIO线程组
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG,100)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new EchoServerHandler());
					
				}
			});
			
			//绑定端口，同步等待结果
			ChannelFuture f = b.bind(port).sync();
			
			//等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally {
			//优雅的退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		int port = 8080;
		if(args != null && args.length >0){
			
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				
			}		
		}
		new EchoServer().bind(port);
	}
}
