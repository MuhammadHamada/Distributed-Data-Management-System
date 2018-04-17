package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class tabletServer2 {

    public static void main(String[] args){

        try {
            ServerSocket serverSocket = new ServerSocket(6789);

            while (true){

                Socket socket = serverSocket.accept();




            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
