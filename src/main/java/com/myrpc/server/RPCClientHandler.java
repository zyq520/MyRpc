package com.myrpc.server;

import com.myrpc.communication.RPCRequest;
import com.myrpc.communication.RPCResponse;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CountDownLatch;


/**
 * @author zhuangyq
 * @create 2018-04-27 上午 11:34
 **/
public class RPCClientHandler extends ChannelHandlerAdapter {
    private final RPCRequest request;
    private RPCResponse response;
    private final CountDownLatch latch;

    public RPCClientHandler(RPCRequest request, CountDownLatch latch) {
        this.request = request;
        this.latch = latch;
    }

    public RPCResponse getResponse() {
        return response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.getCause().printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(request);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = (RPCResponse) msg;
        latch.countDown();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
