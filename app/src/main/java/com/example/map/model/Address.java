package com.example.map.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Address implements Serializable {
    String name;
    String address;
    String service;
    double lat;
    double lng;
    String phoneNumber;
    String headName;

    public Address(String name, String address, String service, double lat, double lng, String number, String headname) {
        this.name = name;
        this.address = address;
        this.service = service;
        this.lat = lat;
        this.lng = lng;
        this.phoneNumber = number;
        this.headName = headname;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHeadName() {
        return headName;
    }

    public void setHeadName(String headName) {
        this.headName = headName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
