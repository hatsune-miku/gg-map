package com.example.map.util;

import android.content.Intent;

import com.example.map.model.Address;

public class AddressUtil {
    public static Address extractFromIntent(Intent intent) {
        return (Address) intent.getSerializableExtra("address");
    }

    public static void putIntoIntent(Address address, Intent intent) {
        intent.putExtra("address", address);
    }
}
