/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.httpserver;

/**
 *
 * @author Will DeJs
 */
public class Application {

    public static void main(String[] args) {
        int serverPort = 8080;
        if (args.length==1) {
            serverPort = Integer.parseInt(args[0]);
            
        }
        WebServer server = new WebServer(serverPort);
        server.startServer();
        System.out.println("Server is listening on port " + serverPort);
    }
}
