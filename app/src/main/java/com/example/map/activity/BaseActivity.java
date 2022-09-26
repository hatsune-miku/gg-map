package com.example.map.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.map.databinding.ViewLoadingBinding;

public class BaseActivity extends AppCompatActivity {
    protected ViewLoadingBinding loadingBinding;
    protected AlertDialog loadingDialog;

    protected void alertBox(
        String message,
        String title,
        DialogInterface.OnClickListener completion
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("好", completion)
            .create()
            .show();
    }

    @SuppressLint("InflateParams")
    protected void loadingBoxStart(String message) {
        if (loadingDialog != null || loadingBinding != null) {
            loadingBoxStart(message);
        }

        loadingBinding = ViewLoadingBinding.inflate(LayoutInflater.from(this));
        loadingDialog = new AlertDialog.Builder(this).setTitle("加载中")
            .setCancelable(false)
            .setView(loadingBinding.getRoot())
            .create();

        loadingBoxUpdate(message);
        loadingDialog.show();
    }

    protected void loadingBoxUpdate(String message) {
        if (loadingDialog == null || loadingBinding == null) {
            loadingBoxStart(message);
        }

        loadingBinding.textLoadingMessage.setText(message);
    }

    protected void loadingBoxClose() {
        if (loadingDialog == null || loadingBinding == null) {
            return;
        }

        loadingDialog.dismiss();
        loadingDialog = null;
        loadingBinding = null;
    }
}
