package com.example.map.activity;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.map.databinding.ViewLoadingBinding;

public class BaseActivity extends AppCompatActivity {
    protected ViewLoadingBinding loadingBinding;
    protected AlertDialog loadingDialog;

    protected void showAlertDialog(
        String message,
        String title
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("好", null)
            .create()
            .show();
    }

    @SuppressLint("InflateParams")
    protected void loadingDialogOpen(String message) {
        if (loadingDialog != null || loadingBinding != null) {
            loadingDialogOpen(message);
        }

        loadingBinding = ViewLoadingBinding.inflate(LayoutInflater.from(this));
        loadingDialog = new AlertDialog.Builder(this).setTitle("加载中")
            .setCancelable(false)
            .setView(loadingBinding.getRoot())
            .create();

        loadingDialogUpdate(message);
        loadingDialog.show();
    }

    protected void loadingDialogUpdate(String message) {
        if (loadingDialog == null || loadingBinding == null) {
            loadingDialogOpen(message);
        }

        loadingBinding.textLoadingMessage.setText(message);
    }

    protected void loadingDialogClose() {
        if (loadingDialog == null || loadingBinding == null) {
            return;
        }

        loadingDialog.dismiss();
        loadingDialog = null;
        loadingBinding = null;
    }
}
