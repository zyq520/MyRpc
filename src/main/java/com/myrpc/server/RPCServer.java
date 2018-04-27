package com.myrpc.server;

import com.myrpc.coder.RPCDecoder;
import com.myrpc.coder.RPCEncoder;
import com.myrpc.communication.RPCRequest;
import com.myrpc.communication.RPCResponse;
import com.myrpc.service.IDiffService;
import com.myrpc.service.IStrService;
import com.myrpc.service.ISumService;
import com.myrpc.service.impl.DiffServiceImpl;
import com.myrpc.service.impl.StrServiceImpl;
import com.myrpc.service.impl.SumServiceImpl;
import com.myrpc.zk.Constant;
import com.myrpc.zk.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author zhuangyq
 * @create 2018-04-27 上午 11:22
 **/
public class RPCServer {
    private Map<String, Object> getServices() {
        Map<String, Object> services = new HashMap<String, Object>();
        // 先将服务确定好，才能区调用，不允许客户端自动添加服务
        services.put(ISumService.class.getName(), new SumServiceImpl());
        services.put(IDiffService.class.getName(), new DiffServiceImpl());
        services.put(IStrService.class.getName(), new StrServiceImpl());
        return services;
    }

    private void bind(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final Map<String, Object> handlerMap = getServices();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                                    .addLast(new RPCDecoder(RPCRequest.class))
                                    .addLast(new LengthFieldPrepender(2))
                                    .addLast(new RPCEncoder(RPCResponse.class))
                                    .addLast(new RPCServerHandler(handlerMap));
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public static String getAddress(){
        InetAddress host = null;
        try {
//          获取本机ip
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String address = host.getHostAddress();
        return address;
    }

    public void initService(int port)  {
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        String ip = getAddress();
//      向zookeeper注册服务地址
        serviceRegistry.register(ip+ Constant.ZK_IP_SPLIT+port);
        bind(port);
    }

    public static void main(String[] args) {
        int port = 9090;
        new RPCServer().initService(port);
    }
}
