package com.itheima.netty.demo;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ChildChannelHandle extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		
		arg0.pipeline().addLast(new TimeServerHandler());
	}

}
