package com.company;

public class Quak {

    public String id,place,title,date;
    public int tsunami;
    public double mag,longitude,latitude,depth;
    Quak(String Id,String Place,String Title,
         String Date,double Mag,int Tsunami,
         double Longitude,double Latitude,double Depth){

        this.id = Id;
        this.place = Place;
        this.title = Title;
        this.date = Date;
        this.mag = Mag;
        this.tsunami = Tsunami;
        this.longitude = Longitude;
        this.latitude = Latitude;
        this.depth = Depth;


    }

}
