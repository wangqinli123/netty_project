package com.itheima.netty.http;

public abstract class LdServlet {

    public abstract void doGet(LdRequest request, LdResponse response) throws Exception;
    public abstract void doPost(LdRequest request, LdResponse response);
}
