package com.example.map.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置驿站名称、地址、经纬度所在的列索引
 */
@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum AddressColumn {
    INDEX(0, "", ""),
    NAME(1, "name", "名称"),
    ADDRESS(2, "address", "地址"),
    LNG(3, "longitude", "经度"),
    LAT(4, "latitude", "纬度"),
    SERVICE(5, "service", "服务项目"),
    PHONE_NUMBER(6, "phone_number", "电话号码"),
    HEAD_NAME(7,"head_name","负责人信息");

    private final int index;
    private final String title;
    private final String description;
}
