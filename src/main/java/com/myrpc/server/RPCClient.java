package com.myrpc.server;

import com.myrpc.coder.RPCDecoder;
import com.myrpc.coder.RPCEncoder;
import com.myrpc.communication.RPCRequest;
import com.myrpc.communication.RPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.concurrent.CountDownLatch;


/**
 * @author zhuangyq
 * @create 2018-04-27 上午 11:34
 **/
public class RPCClient {
    private final String host;
    private final int port;
    private final CountDownLatch latch;
    public RPCClient(String host,int port) {
        this.host = host;
        this.port = port;
        this.latch = new CountDownLatch(1);
    }

    public RPCResponse send(RPCRequest request){
        EventLoopGroup group = new NioEventLoopGroup();
        final RPCClientHandler handler = new RPCClientHandler(request,latch);
        RPCResponse response = null;
        try {
            Bootstrap b = new Bootstrap().group(group)
                    .channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                                    .addLast(new RPCDecoder(RPCResponse.class))
                                    .addLast(new LengthFieldPrepender(2))
                                    .addLast(new RPCEncoder(RPCRequest.class))
                                    .addLast(handler);
                        }
                    });

            ChannelFuture f = b.connect(host, port).sync();
            latch.await();
            response = handler.getResponse();
            if(response != null)
                f.channel().close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            group.shutdownGracefully();
        }
        return response;
    }
}
