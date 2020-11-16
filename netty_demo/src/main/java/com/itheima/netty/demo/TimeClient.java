package com.itheima.netty.demo;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {
	
	/**
	 * 
	 * @param port
	 * @param host
	 * @throws InterruptedException
	 * 实现initChannel方法，其作用是当创建NioSocketChannel成功之后，
	 * 在进行初始化时，将它的ChannelHandle设置到ChannlePipline中，
	 * 用于处理网络事件
	 *
	 */
	
	public void connect(int port,String host) throws InterruptedException{
		
		//配置客户端NIO数组
		NioEventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new TimeClientHandle());
				}
				
			});
			
			//发起异步连接操作
			ChannelFuture f = b.connect(host, port).sync();
			
			//等待客户端链路关闭
			f.channel().closeFuture().sync();
		} finally {
			//优雅的退出
			group.shutdownGracefully();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		int port = 8080;
		new TimeClient().connect(port, "127.0.0.1");
	}
}
