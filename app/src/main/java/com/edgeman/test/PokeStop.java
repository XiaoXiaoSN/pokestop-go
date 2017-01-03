package com.edgeman.test;

import android.support.annotation.VisibleForTesting;

/**
 * Created by webberwu on 2016/10/23.
 */

public class PokeStop {
    private int ID;
    private double lat;
    private double lng;
    private String name;
    private String pic;

    //private int file;
    public int getStopID() {
        return ID;
    }
    public void setStopID(int stopid) {
        this.ID = stopid;
    }

    public double getLat() {return lat;}
    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() { return lng;}
    public void setLng(double lng) {
        this.lng = lng;
    }


    public String getName() { return name;}
    public void setName(String name) {
        this.name = name;
    }

    public String getPic() { return pic;}
    public void setPic(String pic) {
        this.pic = pic;
    }



}
