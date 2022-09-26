package com.example.map.annotation.processor;

import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.map.annotation.AutoBind;

import java.lang.reflect.Field;

public class PreferenceBinder {
    private final static String TAG = "PreferenceBinder";

    public static void bind(PreferenceFragmentCompat fragment) {
        Class<?> clazz = fragment.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(AutoBind.class)) {
                // Try to get the annotation.
                AutoBind bind = field.getAnnotation(AutoBind.class);
                assert bind != null;

                String prefName = bind.key().equals("")
                    ? field.getName()
                    : bind.key();

                // Autowire the preference.
                Preference preference = fragment.findPreference(prefName);

                if (preference != null) {
                    try {
                        boolean accessibilityBackup = field.isAccessible();
                        field.setAccessible(true);
                        field.set(fragment, preference);
                        field.setAccessible(accessibilityBackup);

                        Log.d(TAG, "binded " + prefName);
                    }
                    catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } // for
    }
}
