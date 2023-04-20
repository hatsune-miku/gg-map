package com.example.map.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.map.R;
import com.example.map.annotation.preference.AutoBind;
import com.example.map.annotation.preference.processor.PreferenceBinder;
import com.example.map.model.Address;

public class DetailsFragment extends PreferenceFragmentCompat {
    @AutoBind
    private Preference prefName;

    @AutoBind
    private Preference prefAddress;

    @AutoBind
    private Preference prefService;

    @AutoBind
    private Preference prefPhoneNumber;

    @AutoBind
    private Preference prefHeadName;

    private final Address address;

    public DetailsFragment(Address address) {
        this.address = address;
    }


    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.pref_details);
        PreferenceBinder.bind(this);

        prefName.setSummary(address.getName());
        prefAddress.setSummary(address.getAddress());
        prefService.setSummary(address.getService());
        prefPhoneNumber.setSummary(address.getPhoneNumber());
        prefHeadName.setSummary(address.getHeadName());

        requireActivity().setTitle(address.getName());
    }
}
