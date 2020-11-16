package com.itheima.netty.msgpack;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandle extends ChannelInboundHandlerAdapter {
	
	private int count;
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		UserInfo[] users = getUsers();
		for (UserInfo userInfo : users) {
			ctx.write(userInfo);
		}
		ctx.flush();
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("Client receive the msgpack message【  " + ++count + "  】times:【" + msg + "】");
		if (count < 5) {
			ctx.write(msg);
		}
	}

	
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	private UserInfo[] getUsers() {
		UserInfo[] userInfos = new UserInfo[5];
		for (int i = 0; i < 5; i++) {
			UserInfo userInfo = new UserInfo();
			userInfo.setAge(String.valueOf(i));
			userInfo.setUserName("ABCDEF --->"+i);
			userInfos[i] = userInfo;
		}
		return userInfos;
	}
}
