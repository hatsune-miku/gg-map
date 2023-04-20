package com.example.map.annotation.preference.processor;

import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.map.annotation.preference.AutoBind;

import java.lang.reflect.Field;
import java.util.Arrays;

public class PreferenceBinder {
    private final static String TAG = "PreferenceBinder";

    public static void bind(PreferenceFragmentCompat fragment) {
        Class<?> clazz = fragment.getClass();
        Field[] fields = clazz.getDeclaredFields();

        Arrays.stream(fields)
            .filter(field -> field.isAnnotationPresent(AutoBind.class))
            .forEach(field -> bind(fragment, field));
    }

    private static void bind(PreferenceFragmentCompat fragment, Field field) {
        // Try to get the annotation.
        AutoBind bind = field.getAnnotation(AutoBind.class);
        assert bind != null;

        String prefName = bind.key().isEmpty()
            ? field.getName()
            : bind.key();

        // Auto-wire the preference.
        Preference preference = fragment.findPreference(prefName);
        if (preference == null) {
            Log.e(TAG, "Could not find preference " + prefName);
            throw new RuntimeException("Could not find preference " + prefName);
        }

        try {
            boolean accessibilityBackup = field.isAccessible();
            field.setAccessible(true);
            field.set(fragment, preference);
            field.setAccessible(accessibilityBackup);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Could not bind preference " + prefName);
            throw new RuntimeException("Could not bind preference " + prefName);
        }
    }
}
