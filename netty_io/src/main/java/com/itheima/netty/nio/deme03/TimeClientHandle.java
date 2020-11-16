package com.itheima.netty.nio.deme03;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable {

	private String host;
	private int port;
	private Selector selector;
	private SocketChannel clientChannel;
	private volatile boolean stop;

	public TimeClientHandle(String host, int port) {
		this.host = host == null ? "127.0.0.1" : host;
		this.port = port;
		try {
			
			// 第六步：创建Reactor线程，创建多路复用器并启动线程
			selector = Selector.open();
			// 第一步：打开SocketChannel，绑定客户端本地地址
			clientChannel = SocketChannel.open();
			// 第二步：设置SocketChannel为非阻塞模式，同时设置客户端连接的TCP参数
			clientChannel.configureBlocking(false);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void run() {
		try {
			doConnect();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		while (!stop) {
			try {
				// 第七步：多路复用器在线程run方法的无线循环体内轮询准备就绪的key
				// 该方法是阻塞的，最多等1s，如果还没有就绪的就返回0;
				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				SelectionKey key = null;
				while (it.hasNext()) {
					key = it.next();
					it.remove();
					try {
						handlerInput(key);
					} catch (Exception e) {
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		if (selector != null) {
			try {
				selector.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void handlerInput(SelectionKey key) throws IOException {
		
		if (key.isValid()) {
			// 判断是否连接成功
			SocketChannel sc = (SocketChannel) key.channel();
			//第八步：接收connect事件进行处理
			if (key.isConnectable()) {
				//第九步：判断连接结果，如果连接成功，注册读事件到多路复用器
				if (sc.finishConnect()) {
					//第十步：注册读事件到多路复用器
					sc.register(selector, SelectionKey.OP_READ);
					doWrite(sc);
				} else
					// 连接失败，进程退出
					System.exit(1);
			}

			if (key.isReadable()) {
				//异步读客户端请求消息到缓冲区
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if (readBytes > 0) {
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("Now is : " + body);
					this.stop = true;
				} else if (readBytes < 0) {

					// 对端链路关闭
					key.cancel();
					sc.close();

				} else
					; // 读到0字节，忽略

			}

		}

	}

	private void doConnect() {

		try {
			// 第三步：异步连接服务端
			boolean isConnect = clientChannel.connect(new InetSocketAddress("127.0.0.1", port));
			// 第四步：判断是否连接成功，如果连接成功，则直接注册读状态位到多路复用器中，
			// 如果当前没有连接成功（异步连接，返回false，说明客户端已经发送sync包，服务端没有返回ack包，物理链路还没有建立）
			if (isConnect) {
				//如果连接成功，则将SocketChannel注册到多路复用器Selector,注册SelectionKey.OP_READ
				clientChannel.register(selector, SelectionKey.OP_READ);
				doWrite(clientChannel);
			} else {
				//第五步：如果没有直接连接成功，则说明没有返回TCP握手应答消息，但这不代表连接失败，
				//向Reactor线程的多路复用器注册OP_CONNECT状态位，监听服务器上的TCP ACK应答
				clientChannel.register(selector, SelectionKey.OP_CONNECT);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void doWrite(SocketChannel socketChannel) throws IOException {
		//第十二步：对ByteBuffer进行编解码，如果有半包消息接收缓冲区，Reset，继续读取后续的报文，
		//将解码成功的消息封装成task，投递到业务线程池中，进行业务逻辑编排	
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		// 第十三步：将POJO对象encode成ByteBuffer，调用SocketChannel的异步write接口，将消息异步发送给客户端
		socketChannel.write(writeBuffer);
		if (!writeBuffer.hasRemaining()) {
			System.out.println("Send order 2 server succeed.");
		}
	}
}
