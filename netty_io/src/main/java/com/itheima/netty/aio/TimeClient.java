package com.itheima.netty.aio;

public class TimeClient {

    public static void main(String[] args) {

        int port = 8181;
        new Thread(new  AsyncTimeClientHandle("127.0.0.1",  port),"AIO-AsyncTimeClientHandle-001").start();
    }
}
