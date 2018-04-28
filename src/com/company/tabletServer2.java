package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tabletServer2 {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private static ServerSocket serverSocket;



    public static String myIp = "localhost";
    public static String myPass = "johncena102010";
    public static String masterIP = "localhost";
    public static String masterPass = "johncena102010";

    public static void main(String[] args){

        try {
            serverSocket = new ServerSocket(6788);
            System.out.println("Talbet 2 is waiting for the data from the Master");
            Statement mystm = null;
            Connection tabletServer1Conn = getConnection("tablet_server2",myIp,myPass);
            Socket datasocket = serverSocket.accept();


            BufferedReader tabletServerIn = new BufferedReader(new InputStreamReader(datasocket.getInputStream()));
            System.out.println("the data is hereee");
            String res = tabletServerIn.readLine();
              String [] arr= res.split("!");

                for(int i=1;i<arr.length;++i){
                    try {

                        mystm = tabletServer1Conn.createStatement();
                        mystm.executeUpdate(arr[i]);

                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

              //datasocket.close();
            System.out.println("tablet 2 finished inserting the data into it's database");
            while (true){

                Socket socket = serverSocket.accept();


                threadPool.execute(new requestTabletMan2(socket));
               //socket.close();

            }

        } catch (IOException e) {
            e.printStackTrace();
        } /*catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        */

    }

    public static Connection getConnection(String databaseName,String IP,String pass){
        Connection conn = null;
        try {

            String url1 = "jdbc:mysql://" + IP + ":3306/" + databaseName;
            String user = "root";
            String password = pass;

            conn = DriverManager.getConnection(url1, user, password);
            if (conn != null) {
                System.out.println("Connected to the database " + databaseName);
            }

        } catch (SQLException ex) {
            System.out.println("An error occurred. Maybe user/password is invalid");
            ex.printStackTrace();
        }
        return conn;
    }

}
