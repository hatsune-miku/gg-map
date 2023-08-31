package com.example.map.model;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.Serializable;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class Address implements Serializable {
    private String name;
    private String address;
    private String service;
    private double latitude;
    private double longitude;
    private String phoneNumber;
    private String headName;

    private static String TextEmptyData = "(数据为空)";

    public static Address fromRow(XSSFRow row) {
        return Address.builder()
            .name(cellValueOrDefault(row, AddressColumn.NAME.getIndex(), TextEmptyData))
            .address(cellValueOrDefault(row, AddressColumn.ADDRESS.getIndex(), TextEmptyData))
            .service(cellValueOrDefault(row, AddressColumn.SERVICE.getIndex(), TextEmptyData))
            .phoneNumber(cellValueOrDefault(row, AddressColumn.PHONE_NUMBER.getIndex(), TextEmptyData))
            .headName(cellValueOrDefault(row, AddressColumn.HEAD_NAME.getIndex(), TextEmptyData))
            .latitude(row.getCell(AddressColumn.LAT.getIndex()).getNumericCellValue())
            .longitude(row.getCell(AddressColumn.LNG.getIndex()).getNumericCellValue())
            .build();
    }

    /**
     * 获取地址的唯一标识符
     */
    public String getSiteIdentifier() {
        return String.format(Locale.ROOT, latitude + "," + longitude);
    }

    /**
     * 获取一行单元格中第index个的值，如果单元格不存在或者单元格的值为空，则返回默认值。
     * @param index 从1开始
     */
    private static String cellValueOrDefault(XSSFRow row, int index, String defaultValue) {
        try {
            var cell = row.getCell(index);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            return cell.getStringCellValue();
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
}
