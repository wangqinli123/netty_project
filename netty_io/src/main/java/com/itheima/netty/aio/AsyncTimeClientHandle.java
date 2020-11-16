package com.itheima.netty.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeClientHandle implements CompletionHandler<Void, AsyncTimeClientHandle>,Runnable {

    private String host;
    private int port;
    private CountDownLatch latch;
    private AsynchronousSocketChannel client;

    public AsyncTimeClientHandle(String host, int port) {
        this.port = port;
        this.host = host;

        try {
            /**
             * 首先通过AsynchronousSocketChannel的open方法创建一个新的
             * AsynchronousSocketChannel对象
             */
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        /**
         * 创建CountDownLatch进行等待，防止异步操作没有执行完就退出
         */
        latch = new CountDownLatch(1);
        /**
         * SocketAddress remote
         * A attachment
         * CompletionHandler<Void,? super A> handle
         * 通过connect方法发起异步操作
         */
        client.connect(new InetSocketAddress(host, port),this,this);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void completed(Void result, AsyncTimeClientHandle attachment) {

        /**
         * 创建消息请求体，对其进行编码，然后复制到发送缓冲区writeBuffer中，
         * 调用AsynchronousSocketChannel的write方法进行异步写；
         */
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {

            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if(buffer.hasRemaining()){
                    client.write(buffer);
                }else {
                    /**
                     * 客户端异步读取时间服务器服务端应答消息的处理逻辑
                     */
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {

                        @Override
                        public void completed(Integer result, ByteBuffer buffer) {
                            buffer.flip();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);

                            String body = null;
                            try {
                                body = new String(bytes,"utf-8");
                                System.out.println("Now is :" + body);
                                latch.countDown();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                client.close();
                                latch.countDown();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    client.close();
                    latch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandle attachment) {
        exc.printStackTrace();

        try {
            client.close();
            latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

