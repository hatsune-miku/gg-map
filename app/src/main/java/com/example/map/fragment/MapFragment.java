package com.example.map.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.map.R;
import com.example.map.activity.DetailsActivity;
import com.example.map.databinding.FragmentMapBinding;
import com.example.map.helper.AccountHelper;
import com.example.map.helper.ActivityHelper;
import com.example.map.model.Address;
import com.example.map.model.AddressResource;
import com.example.map.model.SheetHelper;
import com.example.map.util.AddressUtil;

import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MapFragment extends Fragment {
    protected enum DisplayMode {
        SHOW_ALL, SHOW_ONE
    }

    private FragmentMapBinding binding;

    /**
     * 地图对象和地图SDK的对象
     */
    private MapView mapView = null;
    private AMap amap = null;

    private final List<AddressResource> addressResources = List.of(AddressResource.values());
    private AddressResource currentAddressResource = addressResources.get(0);

    private final Map<String, SheetHelper> cachedSheetHelpers = new HashMap<>();
    private final List<Marker> markers = new ArrayList<>();

    private DisplayMode displayMode = DisplayMode.SHOW_ONE;

    private Activity hostActivity;
    private AccountHelper accountHelper;
    private ActivityHelper activityHelper;
    private static final String TAG = "MainActivity";

    public MapFragment() {
        super(R.layout.fragment_map);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 获取地图控件引用
        mapView = binding.map;
        mapView.onCreate(savedInstanceState);

        // 初始化高德地图控制器对象
        if (amap == null) {
            amap = mapView.getMap();
        }

        hostActivity = requireActivity();
        accountHelper = new AccountHelper(hostActivity);
        activityHelper = new ActivityHelper(hostActivity);

        bind();
        clearMarkers();
        showMarkersForAddressFriendly(currentAddressResource);

        // 修正topAppBar的背景色
        binding.topAppBar.setBackground(
            binding.bottomAppBar.getBackground());
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 启动DetailsActivity然后在其中显示地址信息
     *
     * @param address -
     */
    private void showAddressInDetails(Address address) {
        var intent = new Intent();
        AddressUtil.putIntoIntent(address, intent);
        intent.setClass(hostActivity, DetailsActivity.class);
        startActivity(intent);
    }

    /**
     * 事件绑定
     */
    private void bind() {
        // 标点点击事件
        amap.setOnMarkerClickListener(marker -> {
            // Marker not binded to an address?
            if (!(marker.getObject() instanceof Address address)) {
                return false;
            }

            showAddressInDetails(address);
            return false;
        });

        // 切换位置
        binding.fabSwitchArea.setCompatElevation(0);
        binding.fabSwitchArea.setOnClickListener(v -> {
            var bottomSheet = new ModalSingleSelectionBottomSheet(
                addressResources
                    .stream()
                    .map(AddressResource::getName)
                    .collect(Collectors.toList()),
                position -> {
                    clearMarkers();
                    currentAddressResource = addressResources.get(position);
                    showMarkersForAddressFriendly(currentAddressResource);
                    displayMode = DisplayMode.SHOW_ONE;
                });
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "ModalSingleSelectionBottomSheet");
        });

        // 菜单项
        binding.bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuItemZoomToMarkers) {
                zoomToShowAllMarkers();
                return true;
            }

            if (item.getItemId() == R.id.menuItemToggleDisplayMode) {
                toggleDisplayMode();
                return true;
            }

            return false;
        });
    }

    private SheetHelper retrieveSheetHelper(String resourceName) throws IOException {
        if (cachedSheetHelpers.containsKey(resourceName)) {
            return cachedSheetHelpers.get(resourceName);
        }

        // Create new if not cached.
        var helper = new SheetHelper(hostActivity, resourceName);
        cachedSheetHelpers.put(resourceName, helper);
        return helper;
    }

    /**
     * 切换地区，更新标点
     * <p>
     * 不会自动清空之前的标点，甚至同地区的标点也会重复添加
     * 所以调用之前记得手动调用 <code>clearMarker()</code>
     * <p>
     * 带有缓存功能，不会重复解析xlsx文档
     */
    private void showMarkersForAddress(AddressResource addressResource) throws IOException {
        String resourceName = addressResource.getResourceName();
        var helper = retrieveSheetHelper(resourceName);

        activityHelper.loadingDialogOpen("创建标点...");

        try {
            int lastRowIndex = helper.getLastRowIndex();
            for (int i = helper.getFirstRowIndex(); i <= lastRowIndex; ++i) {
                try {
                    XSSFRow row = helper.getSheet().getRow(i);
                    addMarker(Address.fromRow(row));
                } catch (Exception e) {
                    Log.e(TAG, "showMarkersForAddress: ", e);
                }
            }

            // 更新标签内容
            binding.textCurrentLocation.setText(
                addressResource.getName());
            binding.topAppBar.setTitle(String.format(Locale.ROOT, "%s (共 %d 个标点)",
                addressResource.getName(), lastRowIndex));

            // 缩放
            zoomToShowAllMarkers();
        } finally {
            activityHelper.loadingDialogClose();
        }
    }

    /**
     * 清空当前标点，然后同时展示所有地区的所有标点！
     */
    private void showMarkersForAllAddresses() {
        clearMarkers();
        var errorMessages = new ArrayList<String>();

        for (AddressResource resource : addressResources) {
            try {
                showMarkersForAddress(resource);
            } catch (IOException e) {
                errorMessages.add(createFriendlyErrorMessage(e, resource));
            }
        }

        if (!errorMessages.isEmpty()) {
            String errorMessage = "遇到下列错误:\n\n" + String.join("\n", errorMessages);
            activityHelper.showAlertDialog(errorMessage, getString(R.string.app_name));
        }

        binding.textCurrentLocation.setText("所有地区");
        binding.topAppBar.setTitle("所有地区共 " + cachedSheetHelpers.values().stream()
            .map(SheetHelper::getLastRowIndex)
            .reduce(0, Integer::sum) + " 个标点");
    }

    protected void addMarker(Address address) {
        var option = new MarkerOptions()
            .title("")
            .position(new LatLng(address.getLatitude(), address.getLongitude()))
            .icon(BitmapDescriptorFactory.defaultMarker());

        /*
        option.icon(BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.noimage)));
         */

        var marker = amap.addMarker(option);
        marker.setTitle(null);
        marker.setObject(address);
        markers.add(marker);
    }

    protected void clearMarkers() {
        markers.forEach(Marker::remove);
        markers.clear();
    }

    protected void zoomToShowAllMarkers() {
        var builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        amap.moveCamera(CameraUpdateFactory.newLatLngBounds(
            builder.build(), 0));
    }

    protected void toggleDisplayMode() {
        switch (displayMode) {
            case SHOW_ONE -> {
                showMarkersForAllAddresses();
                displayMode = DisplayMode.SHOW_ALL;
                Toast.makeText(hostActivity, "现在将同时展示所有标点", Toast.LENGTH_SHORT).show();
            }

            case SHOW_ALL -> {
                clearMarkers();
                showMarkersForAddressFriendly(currentAddressResource);
                displayMode = DisplayMode.SHOW_ONE;
                Toast.makeText(hostActivity, "现在将仅显示指定标点", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String createFriendlyErrorMessage(Exception e, AddressResource resource) {
        if (e instanceof FileNotFoundException) {
            // 资源文件没找到？
            return String.format(
                "资源文件 (%s) 不存在",
                resource.getResourceName());
        }

        return String.format(
            "资源文件 (%s) 存在但无法读取，因为 (%s)",
            resource.getResourceName(),
            e.getLocalizedMessage());
    }

    private void showMarkersForAddressFriendly(AddressResource addressResource) {
        try {
            showMarkersForAddress(addressResource);
        } catch (IOException e) {
            activityHelper.showAlertDialog(createFriendlyErrorMessage(e, addressResource),
                getString(R.string.app_name));
        }
    }
}
