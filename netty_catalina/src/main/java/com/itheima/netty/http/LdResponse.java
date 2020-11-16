package com.itheima.netty.http;

import io.netty.buffer.Unpooled;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.EXPIRES;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

public class LdResponse {

    private ChannelHandlerContext ctx;
    private HttpRequest request;

    public LdResponse(ChannelHandlerContext ctx,HttpRequest request) {

        this.ctx = ctx;
        this.request = request;
    }

    public void write(String out) throws Exception{

        try {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(out.getBytes("UTF-8")));
            response.headers().set(CONTENT_TYPE, "text/json");
            response.headers().set(CONTENT_LENGTH,response.content().readableBytes());
            response.headers().set(EXPIRES, 0);
            if (HttpHeaders.isKeepAlive(request)) {
                response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);
        }catch (Exception e){

        }finally {
            ctx.flush();
            ctx.close();
        }
    }
}
