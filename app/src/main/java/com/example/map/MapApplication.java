package com.example.map;

import com.google.android.material.color.DynamicColors;

public class MapApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
