package com.example.map.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.example.map.R;
import com.example.map.databinding.ActivityDetailsBinding;
import com.example.map.fragment.DetailsFragment;
import com.example.map.mapper.ServiceImageMapper;
import com.example.map.util.AddressUtil;
import com.example.map.model.Address;

public class DetailsActivity extends BaseActivity {
    ActivityDetailsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get address from intent.
        Intent intent = getIntent();
        Address address = AddressUtil.extractFromIntent(intent);

        PreferenceFragmentCompat fragmentCompat = new DetailsFragment(address);

        // Prepare the image.
        binding.addressImageView.setImageResource(
            ServiceImageMapper.getImageResourceId(address));

        binding.addressImageView.setScaleType(
            ImageView.ScaleType.FIT_XY);

        // Prepare the fragment.
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, fragmentCompat)
            .commit();
    }
}
