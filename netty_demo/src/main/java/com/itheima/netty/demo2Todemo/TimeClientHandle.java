package com.itheima.netty.demo2Todemo;

import java.util.logging.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandle extends ChannelInboundHandlerAdapter {

	private static final Logger logger = Logger.getLogger(TimeClientHandle.class.getName());
	private int counter;
	private byte[] req;
	
	public TimeClientHandle() {
		req = ("QUERY TIME ORDER"+ System.getProperty("line.separator")).getBytes();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf firstMessage = null;
		for(int i=0;i<100;i++){
			firstMessage = Unpooled.buffer(req.length);
			firstMessage.writeBytes(req);
			ctx.writeAndFlush(firstMessage);
		}
		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		String body = (String)msg;
		System.out.println("Now is : " + body + "; the counter is :" + ++ counter);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.warning("Unexcepted exception from downstream : " + cause.getMessage());
		ctx.close();
	}
}
