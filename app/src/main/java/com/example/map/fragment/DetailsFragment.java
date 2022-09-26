package com.example.map.fragment;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.map.R;
import com.example.map.annotation.AutoBind;
import com.example.map.model.Address;

import java.util.Objects;

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

    private Address address;

    public DetailsFragment(Address address) {
        this.address = address;
    }


    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.pref_details);

        Bundle arguments = getArguments();

        if (arguments == null) {
            requireActivity().finish();
            return;
        }

        prefName.setSummary(arguments.getString("name"));
        prefAddress.setSummary(arguments.getString("address"));
        prefService.setSummary(arguments.getString("service"));
        prefPhoneNumber.setSummary(arguments.getString("phone_number"));
        prefHeadName.setSummary(arguments.getString("head_name"));

        requireActivity().setTitle("详细信息");
    }
}
