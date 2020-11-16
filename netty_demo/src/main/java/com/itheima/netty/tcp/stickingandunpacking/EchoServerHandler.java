package com.itheima.netty.tcp.stickingandunpacking;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerHandler extends ChannelInboundHandlerAdapter{
 
	int counter = 0;
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		/**
		 * 将消息返回给客户端
		 * 由于DelimiterBasedFrameDecoder过滤掉了分隔符，所以给客户端时需要在请求消息的尾部拼接分隔符（$_）
		 * 最后创建ByteBuf，将原始消息重新返回给客户端
		 */
		String body = (String)msg;
		 System.out.println("This is "+ ++counter + "times receive client :{" + body +"}");
		 body += "$_";
		 ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
		 ctx.writeAndFlush(echo);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
