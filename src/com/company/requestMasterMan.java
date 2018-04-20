package com.company;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class requestMasterMan implements Runnable {

    private final Socket clientSocket;
    public static HashMap<Pair<Double,Double>,String> metaData = new HashMap<>();

    public requestMasterMan(Socket clientSocket, HashMap<Pair<Double,Double>,String> mD) {
        this.clientSocket = clientSocket;
        this.metaData = mD;
    }

    @Override
    public void run() {

        while (true) {
            try {


               // System.out.println("socket : " + clientSocket);


                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                String msg = in.readLine();
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
                        tabletServer = metaData.get(k);
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
        }

    }
}
