package com.itheima.netty.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler
		implements CompletionHandler<AsynchronousSocketChannel,AsycTimeServerHandle> {

	public void completed(AsynchronousSocketChannel result, AsycTimeServerHandle attachment) {
		attachment.serverSocketChannel.accept(attachment,this);
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		result.read(byteBuffer, byteBuffer, new ReadCompletionHandler(result));
	}

	public void failed(Throwable exc, AsycTimeServerHandle attachment) {
		exc.printStackTrace();
		attachment.latch.countDown();
	}

}
