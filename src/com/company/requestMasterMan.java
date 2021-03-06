package com.company;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import static com.company.Main.tabletServer1IP;
import static com.company.Main.tabletServer2IP;

public class requestMasterMan implements Runnable {

    private final Socket clientSocket;
    private final String msg;
    public static HashMap<Pair<Double,Double>,String> metaData = new HashMap<>();

    public requestMasterMan(Socket clientSocket, HashMap<Pair<Double,Double>,String> mD,String msg) {
        this.clientSocket = clientSocket;
        this.metaData = mD;
        this.msg=msg;
    }

    @Override
    public void run() {


            try {

                 System.out.println("ana fe request master man");
               // System.out.println("socket : " + clientSocket);


              /*  BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                String msg = in.readLine();
                */
                System.out.println("msg = " + msg);

                //String msg = "s!usc000mqnr!4.0!44km NW of Naisano Dua, Indonesia!2014-01-01!0";
                String[] arr = msg.split("!");

                double key = Double.parseDouble(arr[1]);
                String tabletServer = "";

                for (Pair<Double, Double> k : metaData.keySet()) {
                    System.out.println("key : " + key);
                    System.out.println("k : " + k);
                    System.out.println("k.getKey() : " + k.getKey());
                    System.out.println("k.getValue() : " + k.getKey());
                    if (key >= k.getKey() && key <= k.getValue()) {
                        tabletServer = (metaData.get(k).equals("tablet_server1") ? tabletServer1IP:tabletServer2IP);
                    }

                }

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(tabletServer);
                //out.close();
                //socket.close();

                System.out.println("msg = " + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
       System.out.println("5rgt mn request master man");

    }
}
