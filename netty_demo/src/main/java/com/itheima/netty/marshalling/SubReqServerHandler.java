package com.itheima.netty.marshalling;

import com.itheima.netty.protobuf.SubscribeReqProto;
import com.itheima.netty.protobuf.SubscribeRespProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SubReqServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq)msg;
		if("LilinFeng".equalsIgnoreCase(req.getUsername())){
			System.out.println("Service accept client subscribe req :["+ req.toString()+"]");
			ctx.writeAndFlush(Resp(req.getSubReqID()));
		}
	}

	private SubscribeRespProto.SubscribeResp Resp(int subReqID){
		SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
		builder.setSubReqID(subReqID);
		builder.setRespCode(0);
		builder.setDesc("Netty book order succeed, 3 day later,sent to the desigated address");
		return builder.build();
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
