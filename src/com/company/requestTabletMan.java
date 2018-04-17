package com.company;


import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class requestTabletMan implements Runnable {

    private final Socket clientSocket;

    public requestTabletMan(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        try {

            System.out.println("socket : " + clientSocket);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String msg = in.readLine();
            System.out.println("msg = " + msg);

            String[] arr = msg.split("!");
             String option= arr[0];
            int key = Integer.parseInt(arr[1]);
            String tabletServer = "";
            if(option.equals("s")){
                 String query="update";



            }else if(option.equals("dc")){

            }else if(option.equals("dr")){

            }else if(option.equals("a")){

            }else {

            }


            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println();
            //out.close();
            //socket.close();

            System.out.println("msg = " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
