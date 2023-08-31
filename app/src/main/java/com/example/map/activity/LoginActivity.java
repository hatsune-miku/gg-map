package com.example.map.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.map.R;
import com.example.map.databinding.ActivityLoginBinding;
import com.example.map.helper.AccountHelper;
import com.example.map.helper.ActivityHelper;
import com.example.map.viewmodel.LoginViewModel;
import com.google.android.material.elevation.SurfaceColors;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private AccountHelper accountHelper;
    private ActivityHelper activityHelper;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHelper = new ActivityHelper(this);
        accountHelper = new AccountHelper(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setViewModel(new ViewModelProvider(this).get(LoginViewModel.class));

        binding.buttonCreateAccount.setOnClickListener(v -> onCreateAccoutClicked());
        binding.buttonLogin.setOnClickListener(v -> onLoginClicked());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        activityHelper.setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
    }

    public static void show(Activity context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    private void onCreateAccoutClicked() {

    }

    private void onLoginClicked() {
        String username = binding.getViewModel().getUsername();
        String password = binding.getViewModel().getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            activityHelper.showAlertDialog("用户名和密码不能为空", "错误");
            return;
        }

        Log.d(TAG, "onLoginClicked: " + username + " " + password + " " + accountHelper);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                var result = accountHelper.login(username, password).get();
                if (result.isSuccess()) {
                    accountHelper.tryLoginWithSavedToken();

                    // 从线程回到UI线程的另一种方法，它实质也是在调用handler
                    runOnUiThread(this::finish);
                }
                else {
                    // 显示失败原因
                    runOnUiThread(() -> {
                        activityHelper.showAlertDialog(result.getMessage(), "登录错误");
                    });
                }
            }
            catch (Exception e) {
                Log.e(TAG, "onLoginClicked: ", e);
            }
        });
    }
}
