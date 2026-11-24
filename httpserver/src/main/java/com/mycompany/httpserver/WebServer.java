/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.httpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;


/**
 *
 * @author Will DeJs
 */
public class WebServer {
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";
    
    private final int port;
    private HttpServer server;
    
    public WebServer(int port) {
        this.port = port;
    }
    
    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch(IOException ex) {
            System.err.println("Error starting HTTP Server on port " + this.port + " " + ex.getMessage());
            return;
        }
        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(TASK_ENDPOINT);
        
        statusContext.setHandler(this::handleStatusCheckrequest);
        taskContext.setHandler(this::handleTaskRequest);
        
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        
        
    }
    
    private void handleStatusCheckrequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        String responseMessage = "{\"status\":\"Ok\")";
        sendResponse(responseMessage.getBytes(), exchange);
    }
    
    private void handleTaskRequest(HttpExchange exchange)  throws IOException{
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        Headers headers = exchange.getRequestHeaders();
        if (headers.containsKey("X-Test") && headers.get("X-Test").get(0).equalsIgnoreCase("true")) {
            String dummyRespoinse = "123";
            sendResponse(dummyRespoinse.getBytes(), exchange);
            return;
        }
        
        boolean isDebugMode =false;  
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true;
        }
        long startTime = System.nanoTime();
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        byte[] responseBytes = calculateResponse(requestBytes);
        
        long finisheTime = System.nanoTime();
        if (isDebugMode) {
            String debugMessage = String.format("Operation took %d ns", finisheTime - startTime);
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
        }
        
        sendResponse(responseBytes, exchange);
        
    }
    byte[] calculateResponse(byte[] requestBytes) {
        String bodyString = new String(requestBytes);
        String[] stringNumbers = bodyString.split(",");
        BigInteger result = BigInteger.ONE;
        for (String number : stringNumbers) {
            BigInteger bigInteger = new BigInteger(number.trim());
            result = result.multiply(bigInteger);
            
        }
        return String.format("Result of multiplicaiton is %s\n", result).getBytes();
    }
    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException { 
        exchange.sendResponseHeaders(200, responseBytes.length);
        
        try(OutputStream outputStream = exchange.getResponseBody()) { 
            outputStream.write(responseBytes);
            outputStream.flush();
        }
    }
    
    
}
