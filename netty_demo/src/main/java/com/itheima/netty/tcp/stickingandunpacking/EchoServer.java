package com.itheima.netty.tcp.stickingandunpacking;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 分隔符和定长解码器的应用
 *
 * 区分消息一般有4中方式：
 * 1：消息长度固定，累计读取到长度总和为定长LEN的报文后，就认为读取到一个完整的消息，将计数器置位，重新开始读取下一个数据报；
 * 2：将回车换行符作为消息结束符；
 * 3：将特殊的分隔符作为消息的结束的标志，回车换行符就是一种特殊的结束分隔符；
 * 4：通过在消息中定义长度字段来标识消息的总长度；
 *
 * 介绍DelimiterBasedFrameDecoder和FixedLengthFrameDecoder解码器：
 * DelimiterBasedFrameDecoder解码器：自动完成以分隔符做结束标志的消息解码
 * FixedLengthFrameDecoder解码器：自动完成对定长消息的解码
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
					/**
					 * 创建分隔符缓冲对象ByteBuf
					 */
					ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
					/**
					 * 创建DelimiterBasedFrameDecoder对象
					 * 第一个参数：1024表示单条消息的最大长度，当达到该长度后仍然没有查找到分隔符，就抛出TooLongFrameException异常，
					 * 防止由于异常码流缺失分隔符导致内存溢出；
					 * 第二个参数：分隔符缓冲区对象
					 * 由于DelimiterBasedFrameDecoder自动对请求消息进行解码，所以后续的ChannelHandler接受到的msg就是个完整的消息包；
					 * 第二个ChannelHandler是StringDecoder,它将ByteBuf解码成字符串对象
					 * 第三个EchoServerHandler接收到的msg消息就是解码后的字符串对象
					 */
					ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
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
