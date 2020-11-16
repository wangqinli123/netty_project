package com.itheima.netty.nio.deme03;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

	private Selector selector;
	private ServerSocketChannel socketChannel;
	private volatile boolean stop;
	
	/**
	 * 初始化多路复用器，绑定监听端口
	 * @param port
	 */
	public MultiplexerTimeServer(int port) {
		
		try {
			//第一步：创建多路复用器
			selector = Selector.open();
			//第二步：打开ServerSocketChannel，用于监听客户端端连接
			socketChannel = ServerSocketChannel.open();
			//第三步：绑定监听端口，设置连接为非阻塞模式
			socketChannel.socket().bind(new InetSocketAddress("127.0.0.1", port));
			socketChannel.configureBlocking(false);
			//第四步：将ServerSocketChannel注册到Reactor线程的多路复用器selector上
			socketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("The time server is start in port :" + port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void stop(){
		this.stop = true;
	}

	public void run() {
		while(!stop){
			try {
				//第五步：多路复用器在线程run方法的无线循环体内轮询准备就绪的key
				//该方法是阻塞的，最多等1s，如果还没有就绪的就返回0;
				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				SelectionKey key = null;
				while(it.hasNext()){
					key = it.next();
					it.remove();
					try {
						handlerInput(key);
					} catch (Exception e) {
						if(key != null){
							key.cancel();
							if(key.channel()!= null){
								key.channel().close();
							}
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//多路复用器关闭后，所有注册在上面的Channel和Pip等资源都会被自动去注册并关闭，所以不需要重复释放资源
		if(selector != null){
			try {
				selector.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void handlerInput(SelectionKey key) throws IOException {
		//处理新接入的请求资源
		if(key.isValid()){
			if(key.isAcceptable()){
				//第六步：多路复用器监听到有新的客户端接入，处理新的接入请求,完成TCP三次握手，建立物理链路
				ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
				SocketChannel sc = ssc.accept();
				//第七步：设置客户端链路为非阻塞模式
				sc.configureBlocking(false);
				sc.socket().setReuseAddress(true);
				//第八步:将新接入的客户端连接注册到Reactor线程的多路复用器上，监听读操作，读取客户端发送的网络信息
				sc.register(selector, SelectionKey.OP_ACCEPT);
			}
			//第九步：异步读取客户端消息到缓存区
			if(key.isReadable()){
				SocketChannel sc = (SocketChannel) key.channel();
				//在能够读和写之前，必须有一个缓冲区，用静态方法 allocate()来分配缓冲区
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if(readBytes > 0){
					//通常情况下，在准备从缓冲区中读取数据时调用flip方法
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					//第十步：对ByteBuffer进行编解码
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("The time server receive order: " + body);
					String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
					doWrite(sc,currentTime);
				}else if(readBytes < 0) {
					//对链路关闭
					key.cancel();
					sc.close();
				}else {
					
				}
				
			}
		}
		
	}

	private void doWrite(SocketChannel channel, String response) throws IOException  {	
		if(response != null && response.trim().length()>0){
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer);
		}
		
	}

}
