package com.example.map.fragment;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.map.R;

import java.util.Objects;

public class DetailsFragment extends PreferenceFragmentCompat {
    private Preference prefName;
    private Preference prefAddress;
    private Preference prefService;
    private Preference prefLat;
    private Preference prefLng;
    private Preference prefNum;
    private Preference prefHeadname;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.pref_details);

        Bundle arguments = getArguments();

        if (arguments == null) {
            requireActivity().finish();
            return;
        }

        prefName = findPreference("prefName");
        prefAddress = findPreference("prefAddress");
        prefService = findPreference("prefService");
        //prefLat = findPreference("prefLat");
        //prefLng = findPreference("prefLng");
        prefNum = findPreference("prefNum");
        prefLng = findPreference("prefHeadname");

        prefName.setSummary(arguments.getString("name"));
        prefAddress.setSummary(arguments.getString("address"));
        prefService.setSummary(arguments.getString("service"));
        //prefLat.setSummary(String.valueOf(arguments.getDouble("lat")));
        //prefLng.setSummary(String.valueOf(arguments.getDouble("lng")));
        prefNum.setSummary(arguments.getString("number"));
        prefHeadname.setSummary(arguments.getString("headname"));

        requireActivity().setTitle("详细信息");
    }
}
