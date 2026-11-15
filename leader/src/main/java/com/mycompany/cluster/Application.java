/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.cluster;


import com.mycompany.cluster.management.LeaderElection;
import com.mycompany.cluster.management.ServiceRegistry;
import java.io.IOException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 *
 * @author Will DeJs
 */
public class Application implements Watcher {

    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private ZooKeeper zooKeeper = null;
    private final static int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        try {
            int currentServerPort = args.length == 1 ? Integer.parseInt(args[0]): DEFAULT_PORT;
            Application application = new Application();
            ZooKeeper zooKeeper = application.connectToZookeeper();
            ServiceRegistry serviceRegistry = new ServiceRegistry(zooKeeper);
            
            OnElectionAction onElectionAction = new OnElectionAction(serviceRegistry, currentServerPort);
            LeaderElection leaderElection = new LeaderElection(zooKeeper, onElectionAction);
            leaderElection.volunterForLeadership();
            leaderElection.reelectLeader();
            application.run();
            application.close();
            System.out.println("Disconnected from Zookeeper, exiting applicaiton");

        } catch (IOException | InterruptedException | KeeperException ex) {
            System.out.println("caught excpetion: " + ex.getMessage());
        }
    }

    public ZooKeeper connectToZookeeper() throws IOException {
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
        return this.zooKeeper;
    }

    public void run() throws InterruptedException {
        synchronized (zooKeeper) {
            zooKeeper.wait();
        }
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    @Override
    public void process(WatchedEvent we) {
        switch (we.getType()) {
            case None -> {
                if (we.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    System.out.println("We successfully connected to Zookeeper");
                } else {
                    synchronized (zooKeeper) {
                        System.out.println("Disconnected from Zookeper event");
                        zooKeeper.notifyAll();
                    }
                }
            }
        }
    }
}
