package com.example.map.mapper;

import androidx.annotation.DrawableRes;

import com.example.map.R;
import com.example.map.model.Address;

import java.util.HashMap;
import java.util.Map;

public class ServiceImageMapper {
    public final static Map<String, Integer> map = new HashMap<>() {{
        put("服务中心健康驿站", R.drawable.service_fuwuzhongxin);
        put("北太平庄街道工会服务站", R.drawable.service_beitaipingzhuang);
        put("花园路街道工会服务站", R.drawable.service_huayuanlu);
        put("马连洼街道工会服务站", R.drawable.service_malianwa);
        put("西北旺镇工会服务站", R.drawable.service_xibeiwang);
        put("西三旗街道工会服务站", R.drawable.service_xisanqi);
        put("清河街道工会服务站", R.drawable.service_qinghe);
        put("永定路街道工会服务站", R.drawable.service_yongdinglu);
        put("羊坊店街道工会服务站", R.drawable.service_yangfangdian);
        put("四季青镇工会服务站", R.drawable.service_sijiqing);
        put("中关村街道工会服务站", R.drawable.service_zhongguancunkexuecheng);
        put("上庄镇工会服务站", R.drawable.service_shangzhuang);
    }};

    public static Integer getImageResourceId(Address address) {
        return map.getOrDefault(address.getName(), R.drawable.noimage);
    }
}
