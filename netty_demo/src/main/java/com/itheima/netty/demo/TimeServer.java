package com.itheima.netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 未考虑TCP粘包导致功能异常
 * 期望结果：
 * 客户端会收到100条服务端的系统时间；
 * 但实际服务端只有2次应答（出现了TCP粘包问题）
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
