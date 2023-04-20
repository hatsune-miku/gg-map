package com.example.map.model;

import android.content.Context;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class SheetHelper {

    /**
     * Sheet对象
     */
    private XSSFSheet sharedSheet = null;

    /**
     * 读取excel数据构造parser
     *
     * @param addressBookName excel文件名不带后缀，比如 三方工作站地址
     */
    public SheetHelper(Context context, String addressBookName) throws IOException {
        // 后缀整上
        if (!addressBookName.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            addressBookName += ".xlsx";
        }

        // 从资源文件中读取数据
        InputStream stream = context.getAssets().open(addressBookName);
        reloadExcelData(stream);
        stream.close();
    }

    /**
     * 根据指定的is重新加载excel数据
     */
    private void reloadExcelData(InputStream inputStream) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            // 获取默认的第一个sheet
            sharedSheet = workbook.getSheetAt(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getFirstRowIndex() {
        return sharedSheet.getFirstRowNum() + 1;
    }

    public int getLastRowIndex() {
        return sharedSheet.getLastRowNum();
    }

    public XSSFSheet getSheet() {
        return sharedSheet;
    }
}

