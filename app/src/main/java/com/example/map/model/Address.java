package com.example.map.model;

import android.os.Bundle;

public class Address {
    String name;
    String address;
    String service;
    double lat;
    double lng;
    String number;
    String headname;

    public Address(String name, String address, String service, double lat, double lng, String number, String headname) {
        this.name = name;
        this.address = address;
        this.service = service;
        this.lat = lat;
        this.lng = lng;
        this.number = number;
        this.headname = headname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getNumber(){ return number; }

    public void setNumber(String number) { this.number = number; }

    public String getHeadname() { return headname; }

    public void setHeadname(String headname) { this.headname = headname; }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Bundle toBundle() {
        Bundle ret = new Bundle();
        ret.putString("name", getName());
        ret.putString("address", getAddress());
        ret.putDouble("lat", getLat());
        ret.putDouble("lng", getLng());
        ret.putString("service", getService());
        return ret;
    }

    public static Address fromBundle(Bundle bundle) {
        return new Address(
                bundle.getString("name"),
                bundle.getString("address"),
                bundle.getString("service"),
                bundle.getDouble("lat"),
                bundle.getDouble("lng"),
                bundle.getString("number"),
                bundle.getString("headname")
        );
    }
}
