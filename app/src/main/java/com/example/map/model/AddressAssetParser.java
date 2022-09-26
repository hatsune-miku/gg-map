package com.example.map.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class AddressAssetParser {
    /**
     * 配置驿站名称、地址、经纬度所在的列索引
     */
    private enum AddressColumn {
        INDEX(0, ""),
        NAME(1, "name"),
        ADDRESS(2, "address"),
        LNG(3, "经度"),
        LAT(4, "纬度"),
        SERVICE(5, "service"),
        NUMBER(6, "number"),
        HEADNAME(7,"HEADNAME");



        private final int index;
        private final String title;

        private AddressColumn(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public String getTitle() {
            return title;
        }
    }

    private final static String TAG = "AddressAssetParser";

    /**
     * Sheet对象
     */
    private XSSFSheet sharedSheet = null;

    /**
     * 驿站名字
     */
    private final String addressBookName;

    /**
     * 读取excel数据构造parser
     * @param addressBookName excel文件名不带后缀，比如 三方工作站地址
     */
    public AddressAssetParser(Context context, String addressBookName)
        throws IOException {
        this.addressBookName = addressBookName;

        // 后缀整上
        if (!addressBookName.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            addressBookName += ".xlsx";
        }

        // 从资源文件中读取数据
        reloadExcelData(context.getAssets().open(addressBookName));
    }

    /**
     * 根据指定的is重新加载excel数据
     * @param inputStream -
     */
    private void reloadExcelData(InputStream inputStream) {
        // "D:\\AndroidProjects\\Map\\app\\jiankangyizhan.xlsx"
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            // 获取默认的第一个sheet
            sharedSheet = workbook.getSheetAt(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把当前excel数据打印到stream
     * @param stream -
     */
    public void printTo(PrintStream stream) {
        // 遍历每一行
        int lastRowIndex = getLastRowIndex();
        for (int i = getFirstRowIndex(); i <= lastRowIndex; ++i) {
            XSSFRow row = sharedSheet.getRow(i);

            // 获取当前行最后一个单元格号
            int lastCellNum = row.getLastCellNum();

            for (int j = row.getFirstCellNum(); j < lastCellNum; ++j) {
                XSSFCell cell = row.getCell(j);

                // 得到单元格数据
                String value = cell.getStringCellValue();
                stream.print(value + "  ");
            }
            stream.println();
        }
    }

    public int getFirstRowIndex() { return sharedSheet.getFirstRowNum() + 1; }

    public int getLastRowIndex() {
        return sharedSheet.getLastRowNum();
    }

    @NotNull
    public XSSFCell getCellAt(int rowIndex, AddressColumn column) throws Exception {
        XSSFRow row = sharedSheet.getRow(rowIndex);

        // 没有这行？
        if (row == null) {
            throw new Exception(
                String.format("工作表 %s 不存在行索引 %d (%s)", addressBookName, rowIndex, column.getTitle()));
        }

        return row.getCell(column.getIndex());
    }

    public String cellStringValueOrDefault(XSSFRow row, int index, String defaultValue) {
        try {
            return row.getCell(index).getStringCellValue();
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public Address getAddressAt(int rowIndex) throws Exception {
        XSSFRow row = sharedSheet.getRow(rowIndex);
        return new Address(
            cellStringValueOrDefault(row, AddressColumn.NAME.getIndex(), "名称读取失败"),
            cellStringValueOrDefault(row, AddressColumn.ADDRESS.getIndex(), "地址读取失败"),
            cellStringValueOrDefault(row, AddressColumn.SERVICE.getIndex(), "服务项目读取失败"),
            getLatAt(rowIndex),
            getLngAt(rowIndex),
                cellStringValueOrDefault(row, AddressColumn.NUMBER.getIndex(), "名称读取失败"),
                cellStringValueOrDefault(row, AddressColumn.HEADNAME.getIndex(), "地址读取失败")
        );
    }

    public double getLatAt(int rowIndex) throws Exception {
        return getCellAt(rowIndex, AddressColumn.LAT)
            .getNumericCellValue();
    }

    public double getLngAt(int rowIndex) throws Exception {
        return getCellAt(rowIndex, AddressColumn.LNG)
            .getNumericCellValue();
    }

    public List<Map<String, String> > parseData(AddressColumn[] columns) {
        List<Map<String, String> > res = new ArrayList<>();
        int lastRowIndex = getLastRowIndex();

        if (lastRowIndex < 1) {
            // 空的
            return res;
        }

        // +1 跳过表头
        for (int i = getFirstRowIndex(); i <= lastRowIndex; ++i) {
            XSSFRow row = sharedSheet.getRow(i);
            if (row == null) {
                continue;
            }
            Map<String, String> map = new HashMap<>(

            );

            for (AddressColumn column : columns) {
                XSSFCell cell = row.getCell(column.getIndex());
                if (cell == null) {
                    continue;
                }
                map.put(column.name(), cell.getStringCellValue());
            }
            res.add(map);
        }
        return res;
    }
}

