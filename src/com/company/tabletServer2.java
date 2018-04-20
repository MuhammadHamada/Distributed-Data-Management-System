package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tabletServer2 {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private static ServerSocket serverSocket;

    public static void main(String[] args){

        try {
            serverSocket = new ServerSocket(6789);

            while (true){

                Socket socket = serverSocket.accept();

                System.out.println("2bl socket");
                threadPool.execute(new requestTabletMan2(socket));
                System.out.println("b3d socket");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnection(String databaseName){
        Connection conn = null;
        try {

            String url1 = "jdbc:mysql://localhost:3306/" + databaseName;
            String user = "root";
            String password = "1234";

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
