package com.itheima.netty.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsycTimeServerHandle implements Runnable {
	
	private int port;
	CountDownLatch latch;
	AsynchronousServerSocketChannel serverSocketChannel;
	
	public AsycTimeServerHandle(int port) {
		this.port = port;
		try {
			serverSocketChannel = AsynchronousServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress(port));
			System.out.println("The time server is start in port : " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void run() {
		latch = new CountDownLatch(1);
		doAccept();
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private void doAccept() {
		serverSocketChannel.accept(this,new AcceptCompletionHandler());
	}

}
