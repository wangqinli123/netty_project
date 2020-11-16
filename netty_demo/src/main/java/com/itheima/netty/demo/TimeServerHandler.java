package com.itheima.netty.demo;

import java.util.Date;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class TimeServerHandler extends ChannelInboundHandlerAdapter {
	
	private int counter;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		/**
		 * 通过ByteBuf的readableBytes方法可以获取缓冲区可读字节数，根据可读字节数创建byte数组
		 * 通过ByteBuf的readBytes方法将缓冲区中的字节数组复制到新建的byte数组中，最后通过
		 * new String构造函数获取请求信息；
		 */
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req,"UTF-8").substring(0,req.length- System.getProperty("line.separator").length());
		System.out.println("The time server receive order: " + body + "; the counter is : "+ ++counter);
		String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
		currentTime = currentTime + System.getProperty("line.separator");
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		//通过ChannelHandlerContext的write方法异步发送应答消息给客户端；
		ctx.write(resp);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		/**
		 * 作用是将消息发送队列中的消息写入到SocketChannel中发送给对方。从性能角度考虑，
		 * 为了防止频繁地唤醒Selector进行消息发送，Netty的write方法并不直接将消息写入到
		 * SocketChannel中，调用write方法只是把待发送的消息放到发送缓冲数组中在通过flush
		 * 方法将发送缓冲区中的消息全部写到SocketChannel中
		 */
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
