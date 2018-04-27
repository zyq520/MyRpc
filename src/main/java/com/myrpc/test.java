package com.myrpc;

import com.myrpc.proxy.RPCProxy;
import com.myrpc.service.IDiffService;
import com.myrpc.service.IStrService;
import com.myrpc.service.ISumService;
import com.myrpc.zk.Constant;
import com.myrpc.zk.ServiceDiscovery;

/**
 * @author zhuangyq
 * @create 2018-04-27 上午 11:02
 **/
public class test {
    public static void serviceTest() {
        ServiceDiscovery discovery = new ServiceDiscovery(Constant.ZK_CONNECT);

        RPCProxy rpcProxy = new RPCProxy(discovery);
        IDiffService diff = rpcProxy.create(IDiffService.class);
        double result = diff.diff(1321, 32.2);
        ISumService sum = rpcProxy.create(ISumService.class);
        int result2 = sum.sum(1000, 1000);
        System.out.println(result+":"+result2);
        IStrService istr=rpcProxy.create(IStrService.class);
        System.out.println(istr.getString("a","b"));
    }

    public static void main(String[] args) {
        serviceTest();

    }
}
