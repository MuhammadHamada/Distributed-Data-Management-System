package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tabletServer1 {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private static ServerSocket serverSocket;

    public static void main(String[] args){

        try {
            serverSocket = new ServerSocket(6789);

            while (true){

                Socket socket = serverSocket.accept();

                System.out.println("2bl socket");
                threadPool.execute(new requestTabletMan(socket));
                System.out.println("b3d socket");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
