package com.company;


import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.company.tabletServer2.*;

public class requestTabletMan2 implements Runnable {

    private static final int MAX_Q = 2;
    private final Socket clientSocket;
    private static Object object = new Object();
    private static int period = MAX_Q;
    private static ArrayList<String> allQueries = new ArrayList<>();

    public requestTabletMan2(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        while (true) {
            try {

                System.out.println("socket : " + clientSocket);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                String msg = in.readLine();
                System.out.println("msg = " + msg);

                String[] arr = msg.split("!");
                String option = arr[0];

                if(!option.equals("r")){
                    period--;
                    if(period == 0){
                        period = MAX_Q;
                        Connection masterConn = getConnection("master",masterIP,masterPass);
                        Statement mystm = null;
                        for (int i = 0; i < period; ++i){
                            try {
                                mystm = masterConn.createStatement();
                                mystm.executeUpdate(allQueries.get(i));
                                allQueries.add(allQueries.get(i));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if(option.equals("s") || option.equals("dc") || option.equals("dr")){
                    synchronized (object){
                        executeQuery(msg);
                    }
                }else{
                    executeQuery(msg);
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void executeQuery(String msg){


        PrintWriter out = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Connection tabletServer2Conn = getConnection("tablet_server2",myIp,myPass);
        Statement mystm = null;


        String[] arr = msg.split("!");
        String option = arr[0];
        double mag = Double.parseDouble(arr[1]);
        String place = arr[2];
        place = "'" + place + "'";
        int day = Integer.parseInt(arr[3]);
        int month = Integer.parseInt(arr[4]);
        int year = Integer.parseInt(arr[5]);
        double depth = Double.parseDouble(arr[6]);
        String longitude, latitude, Tsunami, title;
        String table = "EarthQuakes_3";

        String query = "";
        if (option.equals("s") || option.equals("dc") || option.equals("a")) {
            boolean update = true;
            longitude = arr[7];
            latitude = arr[8];
            Tsunami = arr[9];
            title = arr[10];
            title = "'" + title + "'";

            if (option.equals("a")) {
                String tmp = "(mag,place,dayy,monthh,yearr,Tsunami,longitude,latitude,depth,title) values ("
                        + mag + "," + place + ","
                        + day + "," + month + "," + year
                        + "," + (Tsunami.equals("*") ? "null" : Tsunami) + ","
                        + (longitude.equals("*") ? "null" : longitude) + ","
                        + (latitude.equals("*") ? "null" : latitude) + ","
                        + depth + "," + (title.equals("'*'") ? "null" : title) + ");";
                query = "insert into " + table + tmp;
                allQueries.add("insert into EarthQuakes " + tmp);

                System.out.println("working on adding (option 4) ...");



            } else if (option.equals("dc")) {

                System.out.println("size of longitude :" + longitude.length());
                System.out.println("longitude : " + longitude);

                if(longitude.equals("y") || latitude.equals("y") ||
                        Tsunami.equals("y") || title.equals("'y'")) {
                    String tmp = " SET "
                            + (longitude.equals("y") ? " longitude = null " : " ")
                            + (longitude.equals("y") && (latitude.equals("y") || Tsunami.equals("y") || title.equals("'y'")) ? " ," : " ")
                            + (latitude.equals("y") ? " latitude = null " : " ")
                            + (latitude.equals("y") && (Tsunami.equals("y") || title.equals("'y'")) ? " ," : " ")
                            + (Tsunami.equals("y") ? " Tsunami = null " : " ")
                            + (Tsunami.equals("y") && (title.equals("'y'")) ? " ," : " ")
                            + (title.equals("'y'") ? " title = null " : " ")
                            + " where mag = "
                            + mag + " and place = " + place + " and dayy = "
                            + day + " and monthh = " + month + " and yearr = "
                            + year + " and depth = " + depth + ";";
                    query = "UPDATE " + table + tmp;
                    allQueries.add("UPDATE master " + tmp);
                }else{
                    out.println("no available data to be updated");
                    update = false;
                }

                System.out.println("working on deletion cells (option 2) ...");

            } else {
                System.out.println("size of title :" + title.length());
                System.out.println("title : " + title);
                if(!longitude.equals("*") || !latitude.equals("*") ||
                        !Tsunami.equals("*") || !title.equals("'*'")) {
                    String tmp = " SET "
                            + (longitude.equals("*") ? " " : " longitude = " + longitude)
                            + (!longitude.equals("*") && (!latitude.equals("*") || !Tsunami.equals("*") || !title.equals("'*'")) ? " ," : " ")
                            + (latitude.equals("*") ? " " : " latitude = " + latitude)
                            + (!latitude.equals("*") && (!Tsunami.equals("*") || !title.equals("'*'")) ? " ," : " ")
                            + (Tsunami.equals("*") ? " " : " Tsunami = " + Tsunami)
                            + (!Tsunami.equals("*") && (!title.equals("'*'")) ? " ," : " ")
                            + (title.equals("'*'") ? " " : " title = " + title)
                            + " where mag = "
                            + mag + " and place = " + place + " and dayy = "
                            + day + " and monthh = " + month + " and yearr = "
                            + year + " and depth = " + depth + ";";
                    query = "UPDATE " + table + tmp;
                    allQueries.add("UPDATE EarthQuakes " + tmp);

                }else{
                    out.println("no available data to be updated");
                    update = false;
                }


                System.out.println("working on updating (option 1) ...");
            }

            System.out.println(query);
            System.out.println("update : " + update);

            if(update) {
                try {
                    mystm = tabletServer2Conn.createStatement();
                    mystm.executeUpdate(query);
                    out.println("Tablet Server #1 is updated successfully");
                } catch (SQLException e) {
                    out.println("This row is already exists");
                    e.printStackTrace();
                }
            }

        } else if (option.equals("dr")) {
            String tmp = " where mag = "
                    + mag + " and place = " + place + " and dayy = "
                    + day + " and monthh = " + month + " and yearr = "
                    + year + " and depth = " + depth;
            query = "delete from " + table + tmp;
            allQueries.add("delete from EarthQuakes " + tmp);
            System.out.println("working on deletion (option 3) ...");
            try {
                mystm = tabletServer2Conn.createStatement();
                mystm.executeUpdate(query);
                allQueries.add(query);
                out.println("Tablet Server #1 is updated successfully");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println(query);
        } else {


            query = "select * from " + table + " where mag = "
                    + mag + " and place = " + place + " and dayy = "
                    + day + " and monthh = " + month + " and yearr = "
                    + year + " and depth = " + depth;
            System.out.println("working on selection (option 5) ...");

            try {
                mystm = tabletServer2Conn.createStatement();
                mystm.execute(query);
                ResultSet rs = mystm.getResultSet();

                if ( rs.next() ){
                    out.println("mag : " + rs.getDouble(1)
                            + " , place : " + rs.getString(2)
                            + " , day : " + rs.getInt(3)
                            + " , month : " + rs.getInt(4)
                            + " , year : " + rs.getInt(5)
                            + " , Tsunami : " + rs.getInt(6)
                            + " , longitude : " + rs.getDouble(7)
                            + " , latitude : " + rs.getDouble(8)
                            + " , depth : " + rs.getDouble(9)
                            + " , title : " + rs.getString(10));
                }else{
                    out.println(" Empty Row ...");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            System.out.println(query);

        }

        //  out.close();
        //socket.close();

        System.out.println("msg = " + msg);

    }
}
