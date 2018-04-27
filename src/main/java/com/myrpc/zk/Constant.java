package com.myrpc.zk;

public interface Constant {
    int ZK_SESSION_TIMEOUT = 10000;
    String ZK_CONNECT = "127.0.0.1:2181";
    String ZK_REGISTRY_PATH = "/zyq";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
    String ZK_IP_SPLIT = ":";
}
