package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tabletServer2 {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private static ServerSocket serverSocket;



    public static String myIp = "localhost";
    public static String myPass = "johncena102010";
    public static String masterIP = "localhost";
    public static String masterPass = "johncena102010";

    public static void main(String[] args){

        try {
            serverSocket = new ServerSocket(6788);
            System.out.println("h-receive ahooho tablet 2");
            Statement mystm = null;
            Connection tabletServer1Conn = getConnection("tablet_server2",myIp,myPass);
            Socket datasocket = serverSocket.accept();
            System.out.println("est2blt el dataaaaaaaaaaaa tablet 2 ");
            ObjectInputStream in = new ObjectInputStream(datasocket.getInputStream());

                ArrayList<String> data1=( ArrayList<String>)in.readObject();
                for(int i=0;i<data1.size();++i){
                    try {

                        mystm = tabletServer1Conn.createStatement();
                        mystm.executeUpdate(data1.get(i));
                        System.out.println("query: " + data1.get(i) + " is inserted in tablet 2");
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

          System.out.println("tablet 2 finished");
            while (true){

                Socket socket = serverSocket.accept();

                System.out.println("2bl socket");
                threadPool.execute(new requestTabletMan2(socket));
                System.out.println("b3d socket");

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

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
