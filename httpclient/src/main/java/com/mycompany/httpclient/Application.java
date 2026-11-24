/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.httpclient;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Will DeJs
 */
public class Application {
    private static final String WORKER_ADDRESS_1 = "http://localhost:8081/task";
    private static final String WORKER_ADDRESS_2 = "http://localhost:8082/task";

    
    public static void main(String[] args) {
        Aggregator aggregator = new Aggregator();
        String task1 = "10,200";
        String task2 = "1234567,89012345,67890123456";
        List<String> results = aggregator.SendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1, WORKER_ADDRESS_2), Arrays.asList(task1, task2));
        for (String result: results) {
            System.err.println(result);
        }
    }
}
