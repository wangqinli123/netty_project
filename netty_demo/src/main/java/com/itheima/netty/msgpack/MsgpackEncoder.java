package com.itheima.netty.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

public class MsgpackEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

		/**
		 * MsgpackEncoder继承MessageToByteEncoder，他负责将Object类型的POJO对象编为byte数组，然后写到ByteBuf
		 */
		MessagePack msgPack = new MessagePack();
		byte[] raw = msgPack.write(msg);
		out.writeBytes(raw);
	}
}
