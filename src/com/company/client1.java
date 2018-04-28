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
            //Socket socket = new Socket("localhost",4000);


            while (true) {
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

                Scanner sc = new Scanner(System.in);
                int option = sc.nextInt();
                System.out.println("Option : " + option);


                String msg = "";

                if (option == 1) {

                    msg += "s!";
                    System.out.println("Please Enter the key of row (mag of the earthquak) : ");
                    msg += sc.next() + "!";
                    for (int i = 1; i <= 9; ++i) {
                        printToGetUserData(i);
                        msg += sc.next() + "!";
                    }

                } else if (option == 2) {

                    msg += "dc!";
                    System.out.println("Please Enter the key of row (mag of the earthquak) : ");
                    msg += sc.next() + "!";
                    for (int i = 1; i <= 5; ++i) {
                        printToGetUserData(i);
                        msg += sc.next() + "!";
                    }
                    for (int i = 6; i <= 9; ++i){
                        if(i == 6){
                            System.out.println("Delete longitude [y,n] ? : ");
                        }else if(i == 7){
                            System.out.println("Delete latitude [y,n] ? : ");
                        }else if(i == 8){
                            System.out.println("Delete Tusnami [y,n] ? : ");
                        }else {
                            System.out.println("Delete title [y,n] ? : ");
                        }
                        msg += sc.next() + "!";
                    }

                } else if (option == 3) {
                    msg += "dr!";
                    System.out.println("Please Enter the key of row (mag of the earthquak) : ");
                    msg += sc.next() + "!";
                    for (int i = 1; i <= 5; ++i) {
                        printToGetUserData(i);
                        msg += sc.next() + "!";
                    }

                } else if (option == 4) {
                    msg += "a!";
                    System.out.println("Please Enter the key of row (mag of the earthquak) : ");
                    msg += sc.next() + "!";
                    for (int i = 1; i <= 9; ++i) {
                        printToGetUserData(i);
                        msg += sc.next() + "!";
                    }

                } else if(option == 5){
                    msg += "r!";
                    System.out.println("Please Enter the key of row (mag of the earthquak) : ");
                    msg += sc.next() + "!";
                    for (int i = 1; i <= 5; ++i) {
                        printToGetUserData(i);
                        msg += sc.next() + "!";
                    }

                }else{
                    break;
                }
                System.out.println("msg : " + msg);
                out.println(msg);
               // out.close();

                String res = in.readLine();
                System.out.println("Going to Tablet server : " + res);


                Socket tabletServerSocket = new Socket(res,6789);
                PrintWriter tabletServerOut = new PrintWriter(tabletServerSocket.getOutputStream(), true);
                BufferedReader tabletServerIn = new BufferedReader(new InputStreamReader(tabletServerSocket.getInputStream()));

                tabletServerOut.println(msg);
                res = tabletServerIn.readLine();

                System.out.println(res);
                tabletServerSocket.close();

                socket.close();

            }

            //socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printToGetUserData(int i){
        if(i == 1){
            System.out.println("Enter earthquak place :");
        }else if(i == 2){
            System.out.println("Enter earthquak day : ");
        }else if (i == 3) {
            System.out.println("Enter earthquak month : ");
        }else if(i == 4){
            System.out.println("Enter earthquak year : ");
        }else if(i == 5){
            System.out.println("Enter earthquak depth : ");
        }else if(i == 6){
            System.out.println("Insert value of longitude OR Enter '*' to skip : ");
        }else if(i == 7){
            System.out.println("Insert value of latitude OR Enter '*' to skip : ");
        }else if(i == 8){
            System.out.println("Insert value of Tusnami (0/1) OR Enter '*' to skip : ");
        }else{
            System.out.println("Insert value of title OR Enter '*' to skip : ");
        }
    }

}
