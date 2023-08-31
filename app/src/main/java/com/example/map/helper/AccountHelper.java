package com.example.map.helper;

import android.content.Context;

import com.example.map.entity.Packets;
import com.example.map.util.HashUtil;
import com.example.map.util.NetworkUtil;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

import lombok.Setter;

public class AccountHelper {
    private final PreferencesHelper preferencesHelper;

    @Nullable
    @Setter
    private static String loggingInUsername = null;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AccountHelper(Context context) {
        preferencesHelper = new PreferencesHelper(context);
    }

    public boolean isLoggedIn() {
        return loggingInUsername != null;
    }

    public void clearLoggingInUsername() {
        loggingInUsername = null;
    }

    public Optional<String> getLoggingInUsername() {
        return Optional.ofNullable(loggingInUsername);
    }

    public Optional<String> getToken() {
        String token = preferencesHelper.get("token", "");
        if (token.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(token);
        }
    }

    public void setToken(String token) {
        preferencesHelper.setNow("token", token);
    }

    public void clearToken() {
        preferencesHelper.set("token", "");
    }

    /**
     * 登出
     */
    public void logout() {
        clearToken();
        clearLoggingInUsername();
    }

    /**
     * 登录。如果成功了，会设置token和loggingInUsername
     */
    public Future<Packets.LoginResponse> login(String username, String passwordPlaintext) {
        String hhp = HashUtil.sha256(HashUtil.sha256(passwordPlaintext));
        return executor.submit(() -> {
            var response = NetworkUtil.login(Packets.LoginPacket.builder()
                .username(username)
                .password(hhp)
                .build()
            ).get();

            if (response.isSuccess()) {
                setToken(response.getToken());
                setLoggingInUsername(username);
            }
            return response;
        });
    }

    /**
     * 使用本地的token自动登录。登录成功后，会设置loggingInUsername
     *
     * @return 是否成功，失败的话，说明token已经过期，或者本地并没有token。
     * 失败会自动清除token
     */
    public Future<Boolean> tryLoginWithSavedToken() {
        var tokenOpt = getToken();

        // 他所推荐的isEmpty要API 33才能用，这里只能用!isPresent
        if (!tokenOpt.isPresent()) {
            return CompletableFuture.completedFuture(false);
        }
        String token = tokenOpt.get();

        return executor.submit(() -> {
            try {
                var response = NetworkUtil.getUserInformation(token).get();
                setLoggingInUsername(response.getUsername());
                return true;
            } catch (Exception e) {
                clearToken();
                return false;
            }
        });
    }
}
