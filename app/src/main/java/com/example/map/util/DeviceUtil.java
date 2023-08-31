package com.example.map.util;

import android.content.res.Resources;

public class DeviceUtil {
    public static int dpiToPixels(int dpi) {
        return (int) (dpi * Resources.getSystem().getDisplayMetrics().density);
    }
}
