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

import static com.company.tabletServer2.getConnection;

public class requestTabletMan2 implements Runnable {

    private final Socket clientSocket;

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


                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                Connection tabletServer2Conn = getConnection("tablet_server2");
                Statement mystm = null;


                String msg = in.readLine();
                System.out.println("msg = " + msg);

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
                        query = "insert into " + table + "(mag,place,dayy,monthh,yearr,Tsunami,longitude,latitude,depth,title) values ("
                                + mag + "," + place + ","
                                + day + "," + month + "," + year
                                + "," + (Tsunami.equals("*") ? "null" : Tsunami) + ","
                                + (longitude.equals("*") ? "null" : longitude) + ","
                                + (latitude.equals("*") ? "null" : latitude) + ","
                                + depth + "," + (title.equals("'*'") ? "null" : title) + ");";

                        System.out.println("working on adding (option 4) ...");



                    } else if (option.equals("dc")) {

                        System.out.println("size of longitude :" + longitude.length());
                        System.out.println("longitude : " + longitude);

                        if(longitude.equals("y") || latitude.equals("y") ||
                                Tsunami.equals("y") || title.equals("'y'")) {
                            query = "UPDATE " + table + " SET "
                                    + (longitude.equals("y") ? " longitude = null " : " ")
                                    + (longitude.equals("y") && (latitude.equals("y") || Tsunami.equals("y") || title.equals("y")) ? " ," : " ")
                                    + (latitude.equals("y") ? " latitude = null " : " ")
                                    + (latitude.equals("y") && (Tsunami.equals("y") || title.equals("y")) ? " ," : " ")
                                    + (Tsunami.equals("y") ? " Tsunami = null " : " ")
                                    + (Tsunami.equals("y") && (title.equals("'y'")) ? " ," : " ")
                                    + (title.equals("'y'") ? " title = null " : " ")
                                    + " where mag = "
                                    + mag + " and place = " + place + " and dayy = "
                                    + day + " and monthh = " + month + " and yearr = "
                                    + year + " and depth = " + depth + ";";
                        }else{
                            System.out.println("no available data to be updated");
                            update = false;
                        }

                        out.println("working on deletion (option 2) ...");

                    } else {
                        System.out.println("size of title :" + title.length());
                        System.out.println("title : " + title);
                        if(!longitude.equals("*") || !latitude.equals("*") ||
                                !Tsunami.equals("*") || !title.equals("'*'")) {
                            query = "UPDATE " + table + " SET "
                                    + (longitude.equals("*") ? " " : " longitude = " + longitude)
                                    + (!longitude.equals("*") && (!latitude.equals("*") || !Tsunami.equals("*") || !title.equals("*")) ? " ," : " ")
                                    + (latitude.equals("*") ? " " : " latitude = " + latitude)
                                    + (!latitude.equals("*") && (!Tsunami.equals("*") || !title.equals("*")) ? " ," : " ")
                                    + (Tsunami.equals("*") ? " " : " Tsunami = " + Tsunami)
                                    + (!Tsunami.equals("*") && (!title.equals("'*'")) ? " ," : " ")
                                    + (title.equals("'*'") ? " " : " title = " + title)
                                    + " where mag = "
                                    + mag + " and place = " + place + " and dayy = "
                                    + day + " and monthh = " + month + " and yearr = "
                                    + year + " and depth = " + depth + ";";
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
                            e.printStackTrace();
                        }
                    }

                } else if (option.equals("dr")) {
                    query = "delete from " + table + " where mag = "
                            + mag + " and place = " + place + " and dayy = "
                            + day + " and monthh = " + month + " and yearr = "
                            + year + " and depth = " + depth;
                    System.out.println("working on deletion (option 3) ...");
                    try {
                        mystm = tabletServer2Conn.createStatement();
                        mystm.executeUpdate(query);
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
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                    System.out.println(query);

                }

                //  out.close();
                //socket.close();

                System.out.println("msg = " + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
