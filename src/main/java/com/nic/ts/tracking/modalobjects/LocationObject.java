package com.nic.ts.tracking.modalobjects;

public class LocationObject {
    private String latitude,longitude,address,dateTime;

    public LocationObject(String latitude, String longitude,String address, String dateTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.dateTime = dateTime;

    }

    public LocationObject(){

    }
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
