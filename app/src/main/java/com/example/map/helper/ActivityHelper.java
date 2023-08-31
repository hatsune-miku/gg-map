package com.example.map.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;

import com.example.map.databinding.ViewLoadingBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.annotation.Nullable;

public class ActivityHelper {
    private final Activity activity;

    private ViewLoadingBinding loadingBinding;
    private AlertDialog loadingDialog;

    public ActivityHelper(Activity activity) {
        this.activity = activity;
    }

    public void showAlertDialog(String message, String title) {
        showAlertDialog(
            message,
            title,
            "好",
            null,
            null,
            null
        );
    }

    public void showAlertDialog(
        String message,
        String title,
        @Nullable String primaryButtonText,
        @Nullable Runnable primaryButtonAction
    ) {
        showAlertDialog(
            message,
            title,
            primaryButtonText,
            primaryButtonAction,
            null,
            null
        );
    }

    public void showAlertDialog(
        String message,
        String title,
        @Nullable String primaryButtonText,
        @Nullable Runnable primaryButtonAction,
        @Nullable String secondaryButtonText,
        @Nullable Runnable secondaryButtonAction
    ){
        var builder = new MaterialAlertDialogBuilder(activity);
        var dialog = builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(primaryButtonText, (di, i) -> {
                if (primaryButtonAction != null) {
                    primaryButtonAction.run();
                }
            })
            .setNegativeButton(secondaryButtonText, (di, i) -> {
                if (secondaryButtonAction != null) {
                    secondaryButtonAction.run();
                }
            })
            .create();
        dialog.show();
    }

    public void setStatusBarColor(@ColorInt int color) {
        activity.getWindow().setStatusBarColor(color);
    }

    @SuppressLint("InflateParams")
    public void loadingDialogOpen(String message) {
        if (loadingDialog != null || loadingBinding != null) {
            loadingDialogOpen(message);
        }

        loadingBinding = ViewLoadingBinding.inflate(LayoutInflater.from(activity));
        loadingDialog = new AlertDialog.Builder(activity).setTitle("加载中")
            .setCancelable(false)
            .setView(loadingBinding.getRoot())
            .create();

        loadingDialogUpdate(message);
        loadingDialog.show();
    }

    public void loadingDialogUpdate(String message) {
        if (loadingDialog == null || loadingBinding == null) {
            loadingDialogOpen(message);
        }

        loadingBinding.textLoadingMessage.setText(message);
    }

    public void loadingDialogClose() {
        if (loadingDialog == null || loadingBinding == null) {
            return;
        }

        loadingDialog.dismiss();
        loadingDialog = null;
        loadingBinding = null;
    }
}
