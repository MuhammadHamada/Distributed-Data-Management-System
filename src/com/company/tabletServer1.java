package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tabletServer1 {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private static ServerSocket serverSocket;
    private static double lstMagInTablet1;

    public static String myIp = "localhost";
    public static String myPass = "johncena102010";
    public static String masterIP = "localhost";
    public static String masterPass = "johncena102010";

    public static void main(String[] args){

       // setMyTabletRanges();

        try {
            serverSocket = new ServerSocket(6789);
            System.out.println("h-receive ahooho tablet 1 ");
            Statement mystm = null;
            Connection tabletServer1Conn = getConnection("tablet_server1",myIp,myPass);
            Socket datasocket = serverSocket.accept();
            System.out.println("est2blt el dataaaaaaaaaaaa tablet 1");
            ObjectInputStream in = new ObjectInputStream(datasocket.getInputStream());

                ArrayList<ArrayList<String>> data1=( ArrayList<ArrayList<String>>)in.readObject();
                for(int i=0;i<2;++i){
                    for(int j=0;j<data1.get(i).size();++j){
                        try {
                            mystm = tabletServer1Conn.createStatement();
                            mystm.executeUpdate(data1.get(i).get(j));
                            System.out.println("query: " + data1.get(i).get(j) + " is inserted in tablet 1");
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

                 System.out.println("tablet 1 finished");
            while (true){

                Socket socket = serverSocket.accept();

                System.out.println("2bl socket");
                threadPool.execute(new requestTabletMan1(socket,lstMagInTablet1));
                System.out.println("b3d socket");

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
