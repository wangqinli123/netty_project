package com.itheima.netty.demo2Todemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 解决TCP粘包和拆包问题
 * 解决方案：
 *
 * 通过使用LineBasedFrameDecoder和StringDecoder解决TCP粘包导致的读半包问题，对于使用者来说，
 * 只要将支持半包解码的Handle添加到ChannelPipeline中即可；
 *
 * LineBasedFrameDecoder原理分析：
 * LineBasedFrameDecoder的工作原理是它依次遍历ByteBuf中可读字节，判断看是否有"\n"或者"\r\n"，
 * 如果有就以此位置为结束位置，从可读索引到结束，从可读索引到结束位置区间的字节就组成了一行。它是以
 * 换行符为结束标志的解码器，支持携带结束符或者不携带结束符两种解码方式，同时支持配置单行的最大长度。
 * 如果连续读取到最大长度后，仍然没有发现换行符，就会抛出异常，同时忽略掉之前读到的异常码流；
 *
 * StringDecoder原理分析：
 * 就是将接受到的对象转换为字符串，然后继续调用后面的Handler。LineBasedFrameDecoder+StringDecoder
 * 组合就是按行切换的文字解码器，它被设计用来支持TCP的粘包和拆包；
 *
 */
public class TimeServer {
	
	public void bind(int port) {
		
		/**
		 * 配置服务端的NIO线程组
		 * NioEventLoopGroup是个线程组
		 * 包含一组NIO线程，专门用于网络事件的处理，实际上它们就是Reactor组；
		 * 这里创建两个的原因是：用于服务端接受客户端连接
		 * 另一个用于进行SocketChannel的网络读写
		 */
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			/**
			 * ServerBootstrap对象是Nio服务端的辅助启动类。
			 * 目的是降低服务端开发复杂度
			 * group方法：将两个NIO线程组当做参数传递到ServerBootstrap
			 * 设置创建的channel为NioServerSocketChannel，它的功能对应于JDK nio类库中的ServerSocketChannel类
			 * 设置NioServerSocketChannel的TCP参数
			 * 
			 */
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childHandler(new ChildChannelHandle());
			//绑定端口，同步等待成功
			ChannelFuture f = serverBootstrap.bind(port).sync();
			//等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			//优雅退出，释放线程安全池
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	public static void main(String[] args) {
		
		int port = 8080;
		new TimeServer().bind(port);
	}

}
