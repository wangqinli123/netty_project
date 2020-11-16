package com.itheima.netty.msgpack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class EchoClient {

	private final String host;
	private final int port;

	public EchoClient(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public void run() throws InterruptedException {
		NioEventLoopGroup group = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0,2));
							ch.pipeline().addLast("msgpack decoder", new MsgpackDecoder());
							ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
							ch.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
							ch.pipeline().addLast(new EchoClientHandle());

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
		new EchoClient("127.0.0.1", 8080).run();
	}

}
