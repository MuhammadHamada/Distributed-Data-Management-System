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


public class Main {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private static ServerSocket serverSocket;

    public static HashMap<Pair<Integer,Integer>,String> metaData = new HashMap<>();
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

        //initMaster();
        metaData.put(new Pair(1,662),"tablet_server1");
        metaData.put(new Pair(663,992),"tablet_server2");

        try {
            serverSocket = new ServerSocket(4000);
            System.out.println("Create serverSocket at port 4000");

            while (true){

                Socket socket = serverSocket.accept();

                System.out.println("2bl socket");
                threadPool.execute(new requestMasterMan(socket,metaData));
                System.out.println("b3d socket");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void initMaster() throws IOException, JSONException {

        Connection MasterConn = getConnection("master");

        JSONObject jsonObject = readJsonFromUrl("https://earthquake.usgs.gov/fdsnws/event/1/" +
                "query?format=geojson&starttime=2014-01-01&endtime=2014-02-02&limit=1000");
        JSONArray jsonArray = jsonObject.getJSONArray("features");

        ArrayList<Quak> allQuakes = new ArrayList<>();

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
                place = "'" + place + "'";
                mag = jsonProperties.getDouble("mag");
                tsunami = jsonProperties.getInt("tsunami");


                time = jsonProperties.getLong("time");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                String date = "'" + mYear + "-" + mMonth + "-" + mDay + "'";

                title = jsonProperties.getString("title");
                title = "'" + title + "'";

                Quak quak = new Quak(id,place,title,date,mag,tsunami,longitude,latitude,depth);
                allQuakes.add(quak);

                String query = "insert into EarthQuakes (mag,place,earthQuakDate,Tsunami,longitude,latitude,depth,title) values ("
                       + mag + "," + place + "," + date + "," + tsunami + "," + longitude + "," + latitude + "," + depth + "," + title  + ");";
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

        //Collections.sort(allQuakes, comparingDouble(q -> q.mag));

        Collections.sort(allQuakes,new Comparator<Quak>() {
            @Override
            public int compare(Quak q1, Quak q2) {
                return q1.id.compareToIgnoreCase(q2.id);
            }
        });

        int ln = allQuakes.size()/3;

        metaData.put(new Pair(-100,allQuakes.get(2*ln-1).id),"tablet_server1");

        metaData.put(new Pair(allQuakes.get(2*ln).id,allQuakes.get(allQuakes.size()-1).id),"tablet_server2");
        Connection tabletServer1Conn = getConnection("tablet_server1");
        Connection tabletServer2Conn = getConnection("tablet_server2");

        for (int i =  0; i < allQuakes.size(); ++i){

            Statement mystm = null;



            if(i < ln){

                String query = "insert into earthquakes_1 (mag,place,earthQuakDate,Tsunami,longitude,latitude,depth,title) values ("
                        + allQuakes.get(i).mag + "," + allQuakes.get(i).place + ","
                        + allQuakes.get(i).date + "," + allQuakes.get(i).tsunami + ","
                        + allQuakes.get(i).longitude + "," + allQuakes.get(i).latitude+ ","
                        + allQuakes.get(i).depth + "," + allQuakes.get(i).title  + ");";

                try {
                    mystm = tabletServer1Conn.createStatement();
                    mystm.executeUpdate(query);
                } catch (SQLException e) {
                    System.out.println(query);
                    System.out.println(allQuakes.get(i).mag);
                    System.out.println(allQuakes.get(i).place);
                    System.out.println(allQuakes.get(i).date);
                    System.out.println(allQuakes.get(i).tsunami);
                    System.out.println(allQuakes.get(i).longitude);
                    System.out.println(allQuakes.get(i).latitude);
                    System.out.println(allQuakes.get(i).depth);
                    System.out.println(allQuakes.get(i).title);
                    e.printStackTrace();
                }


            }else if(i < ln*2){

                String query = "insert into earthquakes_2 (mag,place,earthQuakDate,Tsunami,longitude,latitude,depth,title) values ("
                        + allQuakes.get(i).mag + "," + allQuakes.get(i).place + ","
                        + allQuakes.get(i).date + "," + allQuakes.get(i).tsunami + ","
                        + allQuakes.get(i).longitude + "," + allQuakes.get(i).latitude+ ","
                        + allQuakes.get(i).depth + "," + allQuakes.get(i).title  + ");";


                try {
                    mystm = tabletServer1Conn.createStatement();
                    mystm.executeUpdate(query);
                } catch (SQLException e) {
                    System.out.println(query);
                    System.out.println(allQuakes.get(i).mag);
                    System.out.println(allQuakes.get(i).place);
                    System.out.println(allQuakes.get(i).date);
                    System.out.println(allQuakes.get(i).tsunami);
                    System.out.println(allQuakes.get(i).longitude);
                    System.out.println(allQuakes.get(i).latitude);
                    System.out.println(allQuakes.get(i).depth);
                    System.out.println(allQuakes.get(i).title);
                    e.printStackTrace();
                }

            }else{

                String query = "insert into earthquakes_3 (mag,place,earthQuakDate,Tsunami,longitude,latitude,depth,title) values ("
                        + allQuakes.get(i).mag + "," + allQuakes.get(i).place + ","
                        + allQuakes.get(i).date + "," + allQuakes.get(i).tsunami + ","
                        + allQuakes.get(i).longitude + "," + allQuakes.get(i).latitude+ ","
                        + allQuakes.get(i).depth + "," + allQuakes.get(i).title  + ");";

                try {
                    mystm = tabletServer2Conn.createStatement();
                    mystm.executeUpdate(query);
                } catch (SQLException e) {
                    System.out.println(query);
                    System.out.println(allQuakes.get(i).mag);
                    System.out.println(allQuakes.get(i).place);
                    System.out.println(allQuakes.get(i).date);
                    System.out.println(allQuakes.get(i).tsunami);
                    System.out.println(allQuakes.get(i).longitude);
                    System.out.println(allQuakes.get(i).latitude);
                    System.out.println(allQuakes.get(i).depth);
                    System.out.println(allQuakes.get(i).title);
                    e.printStackTrace();
                }

            }

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
