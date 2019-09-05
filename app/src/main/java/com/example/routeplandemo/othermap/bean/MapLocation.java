package com.example.routeplandemo.othermap.bean;

/**
 * 经纬度
 *
 * @author zpan
 */
public class MapLocation {

    /** 经度 */
    private double longitude;
    /** 纬度 */
    private double latitude;

    /**
     * @param longitude 经度
     * @param latitude 纬度
     */
    public MapLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Location{" + "longitude='" + longitude + '\'' + ", latitude='" + latitude + '\'' + '}';
    }
}