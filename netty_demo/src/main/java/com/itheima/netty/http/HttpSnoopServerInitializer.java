package com.itheima.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpSnoopServerInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;

	public HttpSnoopServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}
		p.addLast(new HttpRequestDecoder());
		// Uncomment the following line if you don't want to handle HttpChunks.
		// 目的是将多个消息转换为单一的request或者response对象
		p.addLast(new HttpObjectAggregator(1048576));
		p.addLast(new HttpResponseEncoder());
		// Remove the following line if you don't want automatic content
		// compression.
		// p.addLast(new HttpContentCompressor());
		//目的是支持异步大文件传输（）
		p.addLast(new ChunkedWriteHandler());
		p.addLast(new HttpSnoopServerHandler());
	}
}
