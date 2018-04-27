package com.myrpc.proxy;

import com.myrpc.communication.RPCRequest;
import com.myrpc.communication.RPCResponse;
import com.myrpc.server.RPCClient;
import com.myrpc.zk.Constant;
import com.myrpc.zk.ServiceDiscovery;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

import java.lang.reflect.Method;
import java.util.UUID;


/**
 * @author zhuangyq
 * @create 2018-04-27 上午 11:31
 **/
public class RPCProxy {
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RPCProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RPCProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建并初始化 RPC 请求
                        RPCRequest request = new RPCRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        if (serviceDiscovery != null) {
                            // 发现服务
                            serverAddress = serviceDiscovery.discover();
                        }
//                      "123.23.213.23:9090"
                        String[] array = serverAddress.split(Constant.ZK_IP_SPLIT);
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        RPCClient client = new RPCClient(host, port); // 初始化 RPC
                        // 客户端
                        RPCResponse response = client.send(request); // 通过 RPC
                        if (response.getError() != null) {
                            throw response.getError();
                        } else {
                            return response.getResult();
                        }
                    }
                });
    }
}
