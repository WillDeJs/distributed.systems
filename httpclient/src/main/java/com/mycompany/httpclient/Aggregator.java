/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.httpclient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Will DeJs
 */
public class Aggregator {

    private WebClient webClient;

    public Aggregator() {
        this.webClient = new WebClient();
    }

    public List<String> SendTasksToWorkers(List<String> workerAddresses, List<String> tasks) {
        CompletableFuture<String>[] futures = new CompletableFuture[workerAddresses.size()];
        for (int i = 0; i < workerAddresses.size(); i++) {
            String workerAddress = workerAddresses.get(i);
            String task = tasks.get(i);
            byte[] requestPayload = task.getBytes();
            futures[i] = webClient.sendTas(workerAddress, requestPayload);

        }
//        List<String> results = new ArrayList<>();
//        for (int i = 0; i < tasks.size(); i++) {
//            results.add(futures[i].join());
//        }
        
        List<String> results = Stream.of(futures).map(CompletableFuture::join).collect(Collectors.toList());
        
        return results;
    }
}
