package com.my.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by kyle on 2018/11/7.
 */
public class WatcherDemo {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ZooKeeper zooKeeper = new ZooKeeper(
                "192.168.5.129:2181,192.168.5.131 :2181",
                4000,
                //ȫ��watcher
                new Watcher() {
                    public void process(WatchedEvent event) {
                        System.out.println("global watcher : " + event.getPath());

                        if(event.getState()== Event.KeeperState.SyncConnected){
                            countDownLatch.countDown();
                        }
                    }
                }
        );
        countDownLatch.await();
        System.out.println(zooKeeper.getState());//CONNECTED

        //create znode
        final String znodeName ="/k1";
        zooKeeper.create(znodeName,"value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //ע��/��watcher�¼�: exists, getData, getChildren
        Stat stat = zooKeeper.exists(
                znodeName,
                //��ǰ�ڵ��watcher
                new Watcher() {
                    public void process(WatchedEvent event) {
                        System.out.println("watcher event : " + event.getPath());
                        try {
                            zooKeeper.exists(event.getPath(),true);
                        } catch (KeeperException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        //�����¼������������setData, delete,
        stat = zooKeeper.setData(znodeName,"newValue".getBytes(),stat.getVersion());

        Thread.sleep(1000);
        zooKeeper.delete(znodeName,stat.getVersion());

    }
}
