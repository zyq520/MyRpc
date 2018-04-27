package com.myrpc.zk;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhuangyq
 * @create 2018-04-27 上午 11:09
 **/
public class ServiceRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
    private final CountDownLatch latch = new CountDownLatch(1);

    private ZooKeeper zk;

    public ServiceRegistry() {
    }

    public void register(String data) {
        if (data != null) {
            zk = connectServer();
            if (zk != null) {
                createNode(Constant.ZK_DATA_PATH, data);
            }
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(Constant.ZK_CONNECT, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    // 判断是否已连接ZK,连接后计数器递减.
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });

            // 若计数器不为0,则等待.
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("", e);
        }
        return zk;
    }

    private void createNode(String dir, String data) {
        try {
            byte[] bytes = data.getBytes();
            String root = "/";
            List<String> nodes = zk.getChildren(root, false);
            boolean flag=true;
            for (String node : nodes) {
                if(("/"+node).equals(Constant.ZK_REGISTRY_PATH)){
                    flag=false;
                    break;
                }
            }
            if(flag){
                zk.create(Constant.ZK_REGISTRY_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            String path = zk.create(dir, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.info("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }
}
