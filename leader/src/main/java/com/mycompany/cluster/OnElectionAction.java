/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cluster;

import com.mycompany.cluster.management.OnElectionCallback;
import com.mycompany.cluster.management.ServiceRegistry;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.zookeeper.KeeperException;

/**
 *
 * @author Will DeJs
 */
public class OnElectionAction implements OnElectionCallback {
    private final ServiceRegistry serviceRegistry;
    private final int port;
    public OnElectionAction(ServiceRegistry serviceRegistry, int port) {
        this.serviceRegistry = serviceRegistry;
        this.port = port;
    }
    @Override
    public void onSelectedToBeLeader() {
        try {
            serviceRegistry.unregisterFromCluster();
            serviceRegistry.registerForUpdates();
        } catch (KeeperException | InterruptedException ex) {
            System.err.println("Error unregistering from cluster");
        } 
    }

    @Override
    public void onWorker() {
        try {
            String currentServerAddress = String.format("http://%s:%d", InetAddress.getLocalHost().getCanonicalHostName(), port);
            serviceRegistry.registerToCluster(currentServerAddress);
        } catch (UnknownHostException | InterruptedException | KeeperException ex) {
          System.err.println("Error on registering worker: " + ex.getMessage());
        } 
        
    }
    
}
