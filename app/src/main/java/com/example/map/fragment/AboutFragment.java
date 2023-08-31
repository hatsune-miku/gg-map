package com.example.map.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.map.R;
import com.example.map.activity.LoginActivity;
import com.example.map.databinding.FragmentAboutBinding;
import com.example.map.databinding.FragmentMapBinding;
import com.example.map.helper.AccountHelper;
import com.example.map.helper.ActivityHelper;

public class AboutFragment extends Fragment {
    private FragmentAboutBinding binding;
    private AccountHelper accountHelper;
    private Activity hostActivity;

    public AboutFragment() {
        super(R.layout.fragment_about);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        hostActivity = requireActivity();
        accountHelper = new AccountHelper(hostActivity);

        bind();
    }

    private void bind() {
        binding.buttonLogout.setOnClickListener(v -> {
            accountHelper.logout();
            LoginActivity.show(hostActivity);
        });
    }
}
