package com.example.map.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.map.R;
import com.example.map.databinding.ActivityMainBinding;
import com.example.map.model.Address;
import com.example.map.model.AddressBook;
import com.example.map.model.AddressAssetParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends BaseActivity {
    protected enum DisplayMode {
        SHOW_ALL, SHOW_ONE
    }

    private ActivityMainBinding binding;

    private MapView mapView = null;
    private AMap amap = null;

    private final AddressBook[] addresses = AddressBook.values();
    private int addrIndex = 0;

    private Map<String, AddressAssetParser> parsers = new HashMap<>();
    private final List<Marker> markers = new ArrayList<>();

    private DisplayMode displayMode = DisplayMode.SHOW_ONE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 获取地图控件引用
        mapView = binding.map;

        // 在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);

        // 初始化高德地图控制器对象
        if (amap == null) {
            amap = mapView.getMap();
        }

        bind();

        clearMarkers();
        showMarkersForAddressWithIndex(addrIndex);
    }

    /**
     * 启动DetailsActivity然后在其中显示地址信息
     * @param address -
     */
    private void showAddressInDetails(Address address) {
        Intent intent = new Intent();
        intent.putExtra("bundle", address.toBundle());
        intent.setClass(this, DetailsActivity.class);
        startActivity(intent);
    }

    /**
     * 事件绑定
     */
    private void bind() {
        // 标点点击事件
        amap.setOnMarkerClickListener(marker -> {
            // Marker not binded to an address?
            if (!(marker.getObject() instanceof Address)) {
                return false;
            }

            Address address = (Address) marker.getObject();
            showAddressInDetails(address);
            return false;
        });

        // 切换位置
        binding.fabSwitchArea.setOnClickListener(v -> {
            CharSequence[] array = Arrays.stream(addresses)
                .map(AddressBook::getName)
                .toArray(CharSequence[]::new);
            new AlertDialog.Builder(this)
                .setItems(array, (dialog, which) -> {
                    clearMarkers();
                    showMarkersForAddressWithIndex(which);
                    displayMode = DisplayMode.SHOW_ONE;
                })
                .setCancelable(true)
                .create()
                .show();
        });
    }

    /**
     * 切换地区，更新标点
     *
     * 不会自动清空之前的标点，甚至同地区的标点也会重复添加
     * 所以调用之前记得手动调用clearMarker()
     *
     * 带有缓存功能，不会重复解析xlsx文档
     *
     * @param newAddressIndex -
     */
    private void showMarkersForAddressWithIndex(int newAddressIndex) {
        String resourceName = addresses[newAddressIndex].getResourceName();
        AddressAssetParser parser;
        addrIndex = newAddressIndex;

        try {
            loadingBoxStart("加载地址信息...");

            // 分析地址数据
            try {
                if (parsers.containsKey(resourceName)) {
                    parser = parsers.get(resourceName);
                }
                else {
                    parser = new AddressAssetParser(
                        this, addresses[addrIndex].getResourceName());
                    parsers.put(resourceName, parser);
                }
            } catch (IOException e) {
                String errorMessage;

                if (e instanceof FileNotFoundException) {
                    // 资源文件没找到？
                    errorMessage = String.format(
                        "错误：找不到资源文件 (%s).", addresses[addrIndex].getResourceName());
                } else {
                    errorMessage = String.format(
                        "错误：无法读取资源文件 (%s).", e.getLocalizedMessage());
                }

                alertBox(errorMessage, getString(R.string.app_name), null);
                return;
            }

            loadingBoxUpdate("创建标点...");

            assert parser != null;
            int lastRowIndex = parser.getLastRowIndex();
            for (int i = parser.getFirstRowIndex(); i <= lastRowIndex; ++i) {
                try {
                    addMarker(parser.getAddressAt(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 更新标签内容
            binding.textCurrentLocation.setText(
                addresses[addrIndex].getName());
            setTitle(String.format(Locale.ROOT, "%s (共 %d 个标点)",
                addresses[addrIndex].getName(), lastRowIndex));

            // 缩放
            zoomToShowAllMarkers();
        } finally {
            loadingBoxClose();
        }
    }

    /**
     * 同时展示所有地区的所有标点！
     */
    private void showMarkersForAllAddresses() {
        clearMarkers();
        for (int i = 0; i < addresses.length; ++i) {
            showMarkersForAddressWithIndex(i);
        }
        binding.textCurrentLocation.setText("所有地区");
        setTitle("所有地区共 " + parsers.values().stream()
            .map(AddressAssetParser::getLastRowIndex)
            .reduce(0, Integer::sum) + " 个标点");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    protected void addMarker(Address address) {
        MarkerOptions option = new MarkerOptions()
            .title("")
            .position(new LatLng(address.getLat(), address.getLng()))
            .icon(BitmapDescriptorFactory.defaultMarker());
        // option.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
        //         .decodeResource(getResources(),R.drawable.location_marker)));

        Marker marker = amap.addMarker(option);
        marker.setTitle(null);
        marker.setObject(address);
        markers.add(marker);
    }

    protected void clearMarkers() {
        markers.forEach(Marker::remove);
        markers.clear();
    }

    protected void zoomToShowAllMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        amap.moveCamera(CameraUpdateFactory.newLatLngBounds(
            builder.build(), 0
        ));
    }

    protected void toggleDisplayMode() {
        if (displayMode == DisplayMode.SHOW_ONE) {
            showMarkersForAllAddresses();

            displayMode = DisplayMode.SHOW_ALL;
            Toast.makeText(this, "现在将同时展示所有标点", Toast.LENGTH_SHORT).show();
        }
        else {
            clearMarkers();
            showMarkersForAddressWithIndex(addrIndex);

            displayMode = DisplayMode.SHOW_ONE;
            Toast.makeText(this, "现在将仅显示指定标点", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItemZoomToMarkers) {
            zoomToShowAllMarkers();
            return true;
        }
        else if (item.getItemId() == R.id.menuItemToggleDisplayMode) {
            toggleDisplayMode();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
}
