package com.example.map.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private final SharedPreferences sharedPreferences;

    public PreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("map", Context.MODE_PRIVATE);
    }

    public boolean get(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public String get(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public int get(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void set(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void set(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    @SuppressLint("ApplySharedPref")
    public void setNow(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public void set(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }
}
