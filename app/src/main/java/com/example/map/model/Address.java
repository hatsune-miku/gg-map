package com.example.map.model;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.Serializable;

public class Address implements Serializable {
    String name;
    String address;
    String service;
    double lat;
    double lng;
    String phoneNumber;
    String headName;

    public static Address fromRow(XSSFRow row) {
        Address address = new Address();
        address.setName(cellValueOrDefault(row, AddressColumn.NAME.getIndex(), "(数据为空)"));
        address.setAddress(cellValueOrDefault(row, AddressColumn.ADDRESS.getIndex(), "(数据为空)"));
        address.setService(cellValueOrDefault(row, AddressColumn.SERVICE.getIndex(), "(数据为空)"));
        address.setPhoneNumber(cellValueOrDefault(row, AddressColumn.PHONE_NUMBER.getIndex(), "(数据为空)"));
        address.setHeadName(cellValueOrDefault(row, AddressColumn.HEAD_NAME.getIndex(), "(数据为空)"));
        address.setLat(row.getCell(AddressColumn.LAT.getIndex()).getNumericCellValue());
        address.setLng(row.getCell(AddressColumn.LNG.getIndex()).getNumericCellValue());
        return address;
    }

    public static String cellValueOrDefault(XSSFRow row, int index, String defaultValue) {
        try {
            XSSFCell cell = row.getCell(index);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            return cell.getStringCellValue();
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public Address() {
    }

    public Address(
        String name,
        String address,
        String service,
        String phoneNumber,
        String headName,
        double lat,
        double lng
    ) {
        this.name = name;
        this.address = address;
        this.service = service;
        this.lat = lat;
        this.lng = lng;
        this.phoneNumber = phoneNumber;
        this.headName = headName;
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
