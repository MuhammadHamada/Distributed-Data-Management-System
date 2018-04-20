package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tabletServer1 {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private static ServerSocket serverSocket;
    private static double lstMagInTablet1;

    public static String myIp = "localhost";
    public static String myPass = "1234";
    public static String masterIP = "localhost";
    public static String masterPass = "1234";

    public static void main(String[] args){

        setMyTabletRanges();

        try {
            serverSocket = new ServerSocket(6789);

            while (true){

                Socket socket = serverSocket.accept();

                System.out.println("2bl socket");
                threadPool.execute(new requestTabletMan1(socket,lstMagInTablet1));
                System.out.println("b3d socket");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void setMyTabletRanges(){
        Connection tabletServer1Conn = getConnection("tablet_server1",myIp,myPass);

        Statement mystm = null;
        String query = "SELECT max(mag) FROM EarthQuakes_1";
        System.out.println(query);
        try {
            mystm = tabletServer1Conn.createStatement();
            mystm.execute(query);
            ResultSet rs = mystm.getResultSet();
            if( rs.next() ){
                lstMagInTablet1 = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("lstMagInTablet1 : " + lstMagInTablet1);
    }

    public static Connection getConnection(String databaseName, String IP, String pass){
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
