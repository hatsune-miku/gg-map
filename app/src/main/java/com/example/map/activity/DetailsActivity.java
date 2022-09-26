package com.example.map.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.example.map.R;
import com.example.map.databinding.ActivityDetailsBinding;
import com.example.map.fragment.DetailsFragment;

public class DetailsActivity extends BaseActivity {
    ActivityDetailsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PreferenceFragmentCompat fragmentCompat = new DetailsFragment();
        fragmentCompat.setArguments(getIntent().getBundleExtra("bundle"));

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, fragmentCompat)
            .commit();
    }
}
