package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.util.Comparator.comparingDouble;


public class Main {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private static ServerSocket serverSocket;

    public static String myIp = "localhost";
    public static String myPass = "johncena102010";
    public static String tabletServer1IP = "localhost";
    public static String tabletServer1Pass = "johncena102010";
    public static String tabletServer2IP = "localhost";
    public static String tabletServer2Pass = "johncena102010";

    private static HashMap<Pair<Double,Double>,String> metaData = new HashMap<>();
    public static ArrayList<Quak> allQuakes = new ArrayList<>();
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        System.out.println("heloooooo");
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            System.out.println("hel222222222");
            JSONObject json = new JSONObject(jsonText);
            System.out.println("hel3333333333");
            return json;
        } finally {
            System.out.println("hel4444444444");
            is.close();
        }
    }

    public static void main(String[] args) throws IOException, JSONException {

        initMaster();
        metaData.put(new Pair(-10.0,1.74),"tablet_server1");
        metaData.put(new Pair(1.75,10.0),"tablet_server2");

        try {
            serverSocket = new ServerSocket(4000);
            System.out.println("Create serverSocket at port 4000");

            while (true){

                Socket socket = serverSocket.accept();
                PrintWriter tabletServerOut = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader tabletServerIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String res = tabletServerIn.readLine();
                if(!(res.charAt(0)=='u')) {
                    System.out.println("da client");

                    threadPool.execute(new requestMasterMan(socket, metaData,res));

                }
                else {
                    System.out.println("da tablet");
                    threadPool.execute(new updateMasterMan(socket));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void initMaster() throws IOException, JSONException {

        Connection MasterConn = getConnection("master",myIp,myPass);

        JSONObject jsonObject = readJsonFromUrl("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=1100");
        JSONArray jsonArray = jsonObject.getJSONArray("features");



        String id,place,title;
        long time;
        int tsunami;
        double mag,longitude,latitude,depth;

        System.out.println(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++){

            JSONObject jsonQuak = jsonArray.getJSONObject(i);

            id = jsonQuak.getString("id");
            id = "'" + id + "'";
            System.out.println("id : " + id);

            JSONObject jsonGeometry = jsonQuak.getJSONObject("geometry");
            JSONArray jsonCoordinates = jsonGeometry.getJSONArray("coordinates");
            longitude = jsonCoordinates.getDouble(0);
            latitude = jsonCoordinates.getDouble(1);
            depth = jsonCoordinates.getDouble(2);

            try {
                JSONObject jsonProperties = jsonQuak.getJSONObject("properties");
                place = jsonProperties.getString("place");

                String[] placeArr = place.split(",");
                place = "'" + placeArr[placeArr.length-1] + "'";
                String tmp = "";
                for (int j = 0; j < place.length(); j++){
                    if(place.charAt(j) != ' '){
                        tmp += place.charAt(j);
                    }
                }
                place = tmp;
                mag = jsonProperties.getDouble("mag");
                tsunami = jsonProperties.getInt("tsunami");


                time = jsonProperties.getLong("time");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                title = jsonProperties.getString("title");
                title = "'" + title + "'";

                Quak quak = new Quak(id,place,title,mDay,mMonth,mYear,mag,tsunami,longitude,latitude,depth);
                allQuakes.add(quak);

                String query = generateInsertQuery("earthquakes",i);
                System.out.println(query);
                Statement mystm = null;

                try {
                    mystm = MasterConn.createStatement();
                    mystm.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }catch (JSONException ex){
                System.out.println(ex.getMessage());
            }

        }

        Collections.sort(allQuakes, comparingDouble(q -> q.mag));



        int ln = allQuakes.size()/3;

        metaData.put(new Pair(-100.0,allQuakes.get(2*ln-1).mag),"tablet_server1");
        metaData.put(new Pair(allQuakes.get(2*ln).mag,allQuakes.get(allQuakes.size()-1).mag),"tablet_server2");

        Connection tabletServer1Conn = getConnection("tablet_server1",tabletServer1IP,tabletServer1Pass);
        Connection tabletServer2Conn = getConnection("tablet_server2",tabletServer2IP,tabletServer2Pass);
        // ArrayList<String> tabletServer1Data1 =  new ArrayList<>();
        // ArrayList<String> tabletServer1Data2 = new ArrayList<>();
        // ArrayList<String> tabletServer2Data = new ArrayList<>();
         StringBuilder tabletServer1Data1 = new StringBuilder("");
        StringBuilder tabletServer1Data2 = new StringBuilder("");
        StringBuilder tabletServer2Data = new StringBuilder("");
        tabletServer1Data1.append("w");
        tabletServer1Data2.append("w");
        tabletServer2Data.append("w");
        for (int i =  0; i < allQuakes.size(); ++i){

            Statement mystm = null;



            if(i < ln || allQuakes.get(i).mag == allQuakes.get(ln-1).mag){


                String query = generateInsertQuery("earthquakes_1",i);
                System.out.println(query);
                tabletServer1Data1.append("!");
                tabletServer1Data1.append(query);
                /*try {
                    mystm = tabletServer1Conn.createStatement();
                    mystm.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                */


            }else if(i < ln*2 || allQuakes.get(i).mag == allQuakes.get(2*ln-1).mag){

                String query = generateInsertQuery("earthquakes_2",i);
                System.out.println(query);
                tabletServer1Data2.append("!");
                tabletServer1Data2.append(query);
                /*try {
                    mystm = tabletServer1Conn.createStatement();
                    mystm.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                */

            }else{

                String query = generateInsertQuery("earthquakes_3",i);
                System.out.println(query);
                tabletServer2Data.append("!");
                tabletServer2Data.append(query);
               /* try {
                    mystm = tabletServer2Conn.createStatement();
                    mystm.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                */

            }

        }


        //hereeeeee
        // sending data to tablet 1
        System.out.println("sending data to tableeeeeeeeeeet");


        Socket echoSocket = new Socket("localhost", 6789);
        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        out.println(tabletServer1Data1.toString()+"%"+tabletServer1Data2.toString());
        //sending data to tablet 2
        Socket echoSocket2 = new Socket("localhost", 6788);
        PrintWriter out2 = new PrintWriter(echoSocket2.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(echoSocket2.getInputStream()));
        out2.println(tabletServer2Data.toString());
        System.out.println("el master b3t 5alas");
    }

    private static Connection getConnection(String databaseName,String IP,String pass){
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

    public static String generateInsertQuery(String tableName,int idx) {

        String query = "insert into " +  tableName +  "(mag,place,dayy,monthh,yearr,Tsunami,longitude,latitude,depth,title) values ("
                + allQuakes.get(idx).mag + "," + allQuakes.get(idx).place + ","
                + allQuakes.get(idx).day + "," + allQuakes.get(idx).month + "," + allQuakes.get(idx).year
                + "," + allQuakes.get(idx).tsunami + ","
                + allQuakes.get(idx).longitude + "," + allQuakes.get(idx).latitude+ ","
                + allQuakes.get(idx).depth + "," + allQuakes.get(idx).title  + ");";

        return query;
    }
}
