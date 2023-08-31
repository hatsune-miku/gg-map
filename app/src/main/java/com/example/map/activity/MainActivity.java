package com.example.map.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;

import com.amap.api.maps2d.MapView;
import com.example.map.R;
import com.example.map.databinding.ActivityMainBinding;
import com.example.map.fragment.AboutFragment;
import com.example.map.fragment.MapFragment;
import com.example.map.helper.AccountHelper;
import com.example.map.helper.ActivityHelper;
import com.google.android.material.elevation.SurfaceColors;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AboutFragment fragmentAbout;
    private ActivityMainBinding binding;
    private ActivityHelper activityHelper;
    private AccountHelper accountHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHelper = new ActivityHelper(this);
        accountHelper = new AccountHelper(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentAbout = new AboutFragment();

        bind();

        // 自动登录
        tryAutomaticLogin();

        selectFragment(new MapFragment());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        activityHelper.setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
    }

    private void bind() {
        binding.bottomNavigationView.setOnItemSelectedListener( item -> {
            // 此处不应当重用MapFragment，而是每次都创建一个新的
            // 否则发生重用后，MapView会显示不了标点
            var menuToFragmentMap = Map.of(
                R.id.itemMainPage, new MapFragment(),
                R.id.itemAboutPage, fragmentAbout
            );

            if (!menuToFragmentMap.containsKey(item.getItemId())) {
                return false;
            }

            var fragment = menuToFragmentMap.get(item.getItemId());
            assert fragment != null;

            selectFragment(fragment);
            return true;
        });
    }

    private void selectFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit();
    }

    private void tryAutomaticLogin() {
        Handler loginResultHandler = new LoginResultHandler(this);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                loginResultHandler.sendEmptyMessage(
                    accountHelper.tryLoginWithSavedToken().get()
                        ? LoginResultHandler.MessageLoginSucceeded
                        : LoginResultHandler.MessageLoginFailed
                );
            } catch (Exception e) {
                Log.e(TAG, "tryAutomaticLogin: ", e);
            }
        });
    }

    /**
     * 从线程回到UI线程的一种方法，使用Handler
     * Handler因为特殊性，需要持有对Activity的弱引用
     */
    private static class LoginResultHandler extends Handler {
        final static int MessageLoginFailed = 0;
        final static int MessageLoginSucceeded = 1;
        private final WeakReference<Activity> activityReference;

        public LoginResultHandler(Activity activity) {
            super(activity.getMainLooper());
            activityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            Activity activity = activityReference.get();
            switch (msg.what) {
                case MessageLoginFailed ->
                    // 如果自动登录失败，就跳转到登录页面
                    LoginActivity.show(activity);

                case MessageLoginSucceeded ->
                    // 如果自动登录成功，就显示欢迎信息
                    Toast.makeText(
                        activity,
                        "欢迎回来，" + new AccountHelper(activity).getLoggingInUsername().orElse(""),
                        Toast.LENGTH_SHORT
                    ).show();
            }
        }
    }

    protected void logout() {
        accountHelper.logout();

        // 清除账号信息后，再走一遍自动登录流程，使其弹出登录页面
        activityHelper.showAlertDialog(
            "确认要退出登录吗？", "退出登录",
            "退出登录", this::tryAutomaticLogin,
            "取消", null);
    }
}
