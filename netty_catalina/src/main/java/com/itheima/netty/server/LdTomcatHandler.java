package com.itheima.netty.server;

import com.itheima.netty.http.LdRequest;
import com.itheima.netty.http.LdResponse;
import com.itheima.netty.servlets.MyServlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

public class LdTomcatHandler extends ChannelInboundHandlerAdapter {



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       if(msg instanceof HttpRequest){
           HttpRequest m = (HttpRequest)msg;

           LdRequest request = new LdRequest(ctx,m);

           LdResponse response = new LdResponse(ctx,m);

           MyServlet myServlet = new MyServlet();
           myServlet.doGet(request,response);
       }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
