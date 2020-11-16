package com.itheima.netty.bio.async;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 伪异步阻塞IO
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if(args != null && args.length >0){

            try {
                port = Integer.valueOf(args[0]);
            } catch (Exception e) {

            }

            ServerSocket server = null;
            Socket socket = null;
            try {
                server = new ServerSocket();
                System.out.println("The time server is start in port :" + port);
                //创建I/O任务的线程池
                TimeServerHandlerExecutePool singleExecute = new TimeServerHandlerExecutePool(50,1000);
                while(true){
                    socket = server.accept();
                    singleExecute.execute(new TimeServerHandler(socket));
                }
            } finally{
                if (server != null) {
                    System.out.println("The time server close");
                    server.close();
                    server = null;
                }
            }

        }
    }
}