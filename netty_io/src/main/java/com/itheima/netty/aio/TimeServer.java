package com.itheima.netty.aio;

public class TimeServer {
	
	public static void main(String[] args) {
		
		int port = 8080;

		AsycTimeServerHandle timeServer = new AsycTimeServerHandle(port);
		new Thread(timeServer, "AIO-AsycTimeServerHandle-001").start();
			

	}

}
