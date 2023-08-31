package com.example.map.model;

import android.content.Context;
import android.util.Log;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import lombok.Getter;

@Getter
public class SheetHelper {
    /**
     * Sheet对象
     */
    private XSSFSheet sheet = null;

    private static final String TAG = "SheetHelper";

    private final Context applicationContext;
    private final String addressBookName;

    /**
     * 读取excel数据构造parser
     *
     * @param addressBookName excel文件名不带后缀，比如 三方工作站地址
     */
    public SheetHelper(Context context, String addressBookName) throws IOException {
        this.applicationContext = context;

        // 如果没加后缀，把后缀整上
        if (!addressBookName.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            addressBookName += ".xlsx";
        }
        this.addressBookName = addressBookName;

        // 从资源文件中读取数据
        reloadExcelData();
    }

    /**
     * 根据指定的inputStream重新加载excel数据
     */
    public void reloadExcelData() {
        try {
            var stream = applicationContext.getAssets().open(addressBookName);
            var workbook = new XSSFWorkbook(stream);

            // 获取默认的第一个sheet
            this.sheet = workbook.getSheetAt(0);
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "reloadExcelData 失败: ", e);
        }
    }

    /**
     * 获取第一行的行号，这是一种索引，从1开始，一般就是1，但不一定
     */
    public int getFirstRowIndex() {
        return sheet.getFirstRowNum() + 1;
    }

    /**
     * 获取最后一行的行号
     */
    public int getLastRowIndex() {
        return sheet.getLastRowNum();
    }
}
