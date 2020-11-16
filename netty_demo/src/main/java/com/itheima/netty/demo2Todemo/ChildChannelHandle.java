package com.itheima.netty.demo2Todemo;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class ChildChannelHandle extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		
		// 添加解码器
		arg0.pipeline().addLast(new LineBasedFrameDecoder(1024));
		arg0.pipeline().addLast(new StringDecoder());
		arg0.pipeline().addLast(new TimeServerHandler());
	}
}
