package com.itheima.netty.nio.deme03;

public class TimeClient {

	public static void main(String[] args) {
		
		int port = 8080;
		
		if (args.length > 0 && args != null) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				
			}
		}
		new Thread(new TimeClientHandle("127.0.0.1",port), "NIO-MultiplexerTimeClient-001").start();
	}
}
