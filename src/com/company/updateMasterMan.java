package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.company.tabletServer1.getConnection;
import static com.company.tabletServer1.masterIP;
import static com.company.tabletServer1.masterPass;


public class updateMasterMan implements Runnable {

    private final Socket tabletSocket;
    private final String res;
    public updateMasterMan(Socket tabletSocket,String res) {
        this.tabletSocket = tabletSocket;
        this.res= res;
    }
    @Override
    public void run() {
        ObjectInputStream in = null;

            System.out.println("I'm in updateMasterMan now ");

            //BufferedReader tabletServerIn = new BufferedReader(new InputStreamReader(tabletSocket.getInputStream()));
            //String res = tabletServerIn.readLine();
            System.out.println("the data to be updated is hereeee");
            String [] arr =res.split("!");
            Connection masterConn = getConnection("master",masterIP,masterPass);
            Statement mystm = null;
            for (int i = 1; i < arr.length; ++i){
                try {
                    mystm = masterConn.createStatement();
                    mystm.executeUpdate(arr[i]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("the update is finished");





    }
}
