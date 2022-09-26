package com.example.map.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.example.map.R;
import com.example.map.databinding.ActivityDetailsBinding;
import com.example.map.fragment.DetailsFragment;
import com.example.map.model.Address;

import java.io.IOException;

public class DetailsActivity extends BaseActivity {
    ActivityDetailsBinding binding;
    private ImageView image_1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PreferenceFragmentCompat fragmentCompat =
            new DetailsFragment();
        fragmentCompat.setArguments(getIntent().getBundleExtra("bundle"));

        Address address1;
        address1 = Address.fromBundle(getIntent().getBundleExtra("bundle"));
        String name1 = null;
        name1 = address1.getName();

        switch (name1) {
            case "服务中心健康驿站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.a7f2e67d44583271cb7c4d016ff7639);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "北太平庄街道工会服务站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.beitaipingzhuang);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "花园路街道工会服务站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.huayuanlu);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "马连洼街道工会服务站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.malianwa);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "西北旺镇工会服务站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.xibeiwang);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "西三旗街道工会服务站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.xisanqi);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "清河街道工会服务站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.qinghe);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "永定路街道工会服务站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.yongdinglu);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "羊坊店街道工会服务站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.yangfangdian);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "四季青镇工会服务站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.sijiqing);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "中关村街道工会服务站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.zhongguancunkexuecheng);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "上庄镇工会服务站":
                image_1 = findViewById(R.id.image1);
                image_1.setImageResource(R.drawable.shangzhuang);
                image_1.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            default: break;
        }
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, fragmentCompat)
            .commit();
    }
}
