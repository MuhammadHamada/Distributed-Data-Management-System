package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class client1 {

    public static void main(String[] args){

        try {
            Socket socket = new Socket("localhost",4000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("PLease Choose from the following options : ");
            System.out.println("[1] --> Set Row");
            System.out.println("[2] --> Delete Cell");
            System.out.println("[3] --> Delete Row");
            System.out.println("[4] --> Add Row");
            System.out.println("[5] --> Read Row");
            System.out.println("[6] --> Exit");

            while (true) {


                Scanner sc = new Scanner(System.in);
                int option = sc.nextInt();
                System.out.println("Option : " + option);
                System.out.println("Please insert the key of row ");

                String msg = "";

                if (option == 1) {

                    msg += "s!";
                    for (int i = 0; i < 9; ++i) {
                        msg += sc.next() + "!";
                    }

                } else if (option == 2) {

                    msg += "dc!";
                    for (int i = 0; i < 2; ++i) {
                        msg += sc.next() + "!";
                    }

                } else if (option == 3) {
                    msg += "dr!";
                    msg += sc.next() + "!";

                } else if (option == 4) {
                    msg += "a!";
                    for (int i = 0; i < 9; ++i) {
                        msg += sc.next() + "!";
                    }

                } else if(option == 5){
                    msg += "r!";
                    msg += sc.next() + "!";

                }else{
                    break;
                }
                System.out.println("msg : " + msg);
                out.println(msg);
               // out.close();

                String res = in.readLine();
                System.out.println("Go to Tablet server : " + res);

                // TODO : hnb2a n8yr isa el localhost da lel ip lma el master  yb3t ip bdl string
                Socket tabletServerSocket = new Socket("localhost",6789);
                PrintWriter tabletServerOut = new PrintWriter(tabletServerSocket.getOutputStream(), true);
                BufferedReader tabletServerIn = new BufferedReader(new InputStreamReader(tabletServerSocket.getInputStream()));

                tabletServerOut.println(msg);

                res = tabletServerIn.readLine();

                System.out.println("result from tablet : " + res);


            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
