/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cluster.management;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 *
 * @author Will DeJs
 */
public class LeaderElection implements Watcher {

    private static final String ELECTION_NAMESPACE = "/election";
    private String currentZnodeName;
    private ZooKeeper zooKeeper;
    private final OnElectionCallback onElectionCallback;

    public LeaderElection(ZooKeeper zooKeeper, OnElectionCallback onElectionCallback) {
        this.zooKeeper   = zooKeeper;
        this.onElectionCallback = onElectionCallback;
    }
    public void run() throws InterruptedException {
        synchronized (this.zooKeeper) {
            zooKeeper.wait();
        }
    }

    public void volunterForLeadership() throws KeeperException, InterruptedException {
        String znodePrefix = ELECTION_NAMESPACE + "/c";
        String znodeFullPath = zooKeeper.create(znodePrefix, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("znode name" + znodeFullPath);
        this.currentZnodeName = znodeFullPath.replace(ELECTION_NAMESPACE + "/", "");
    }

    public void reelectLeader() throws KeeperException, InterruptedException {
        Stat predecessorStat = null;
        while (predecessorStat == null) {
            List<String> children = zooKeeper.getChildren(ELECTION_NAMESPACE, false);
            Collections.sort(children);
            System.out.println("Children: " + children);
            String predecesorZnodeName;
            String smallestChild = children.get(0);
            if (smallestChild.equals(currentZnodeName)) {
                System.out.println("I am the leader");
                onElectionCallback.onSelectedToBeLeader();
                return;
            } else {
                System.out.println("I am not the leader, " + smallestChild + " is the leader");
                int predecessor = Collections.binarySearch(children, currentZnodeName) - 1;
                predecesorZnodeName = children.get(predecessor);
                predecessorStat = zooKeeper.exists(ELECTION_NAMESPACE + "/" + predecesorZnodeName, this);
            }
        }
        onElectionCallback.onWorker();
    }

    @Override
    public void process(WatchedEvent we) {
        switch (we.getType()) {
            case NodeDeleted -> {
                try {
                    reelectLeader();
                } catch (KeeperException | InterruptedException ex) {
                    System.out.println("Exception re-electing leader");
                }
            }
        }
    }

}
