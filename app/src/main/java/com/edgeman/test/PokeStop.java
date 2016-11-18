package com.edgeman.test;

/**
 * Created by webberwu on 2016/10/23.
 */

public class PokeStop {
    private String stopid;
    private double lat;
    private double lng;
    private int file;

    public String getStopID() {
        return stopid;
    }

    public void setStopID(String stopid) {
        this.stopid = stopid;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getFile() {
        return file;
    }

    public void setFile(int file) {
        this.file = file;
    }

}
