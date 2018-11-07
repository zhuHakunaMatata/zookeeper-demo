package com.my.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by kyle on 2018/11/6.
 */
public class ZookeeperDemo {
    public static void main(String[] args) {
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            ZooKeeper zooKeeper = new ZooKeeper(
                    "192.168.5.129:2181,192.168.5.131 :2181",
                    4000,
                    //全局watcher
                    new Watcher() {
                public void process(WatchedEvent event) {
                    if(event.getState()== Event.KeeperState.SyncConnected){
                        countDownLatch.countDown();
                    }
                }
            }
            );
            countDownLatch.await();
            System.out.println(zooKeeper.getState());//CONNECTED

            String znodeName = "/kyle6";
            //create znode
            zooKeeper.create(znodeName,"kylevalue".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            Thread.sleep(1000);
            Stat stat = new Stat();

                //得到值
            byte[] bytes = zooKeeper.getData(znodeName,null,stat);
            System.out.println(new String(bytes));
            System.out.println("stat.version " + stat.getVersion());

            //update znode
            zooKeeper.setData(znodeName,"newValue".getBytes(),stat.getVersion());
            byte[] bytes1 = zooKeeper.getData(znodeName,null,stat);
            System.out.println(new String(bytes1));
            System.out.println("stat . version " + stat.getVersion());

            //delete znode
            zooKeeper.delete(znodeName,stat.getVersion());
            zooKeeper.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
