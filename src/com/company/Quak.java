package com.company;

public class Quak {

    public String id,place,title;
    public int tsunami,day,month,year;
    public double mag,longitude,latitude,depth;
    Quak(String Id,String Place,String Title,
         int day,int month,int year,double Mag,int Tsunami,
         double Longitude,double Latitude,double Depth){

        this.id = Id;
        this.place = Place;
        this.title = Title;
        this.day = day;
        this.month = month;
        this.year = year;
        this.mag = Mag;
        this.tsunami = Tsunami;
        this.longitude = Longitude;
        this.latitude = Latitude;
        this.depth = Depth;


    }

}
