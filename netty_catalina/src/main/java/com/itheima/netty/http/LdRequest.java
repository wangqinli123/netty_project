package com.itheima.netty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

public class LdRequest {

    private ChannelHandlerContext ctx;
    private HttpRequest request;

    public LdRequest(ChannelHandlerContext ctx,HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public String getUrl(){
        return request.uri();
    }

    public String getMethod(){
        return request.getMethod().name();
    }

    public Map<String, List<String>> getParameters(){
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        return decoder.parameters();
    }

    public String getParameter(String name){
        Map<String, List<String>> map = getParameters();
        List<String> param = map.get(name);
        if(null == param){
            return  null;
        }
        return param.get(0);
    }
}
