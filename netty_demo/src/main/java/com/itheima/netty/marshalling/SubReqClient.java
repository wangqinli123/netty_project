package com.itheima.netty.marshalling;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SubReqClient {
	
	public void connect(int port,String host) throws InterruptedException {
		NioEventLoopGroup group = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(MarshallingCodeFactory.buildMarshallingDecoder());
							ch.pipeline().addLast(MarshallingCodeFactory.buildMarshallingEncoder());
							ch.pipeline().addLast(new SubReqClientHandle());

						}
					});
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();

		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {

		int port = 8080;
		if (args != null && args.length > 0) {

			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {

			}
		}
		new SubReqClient().connect(8080,"127.0.0.1");
	}
}
