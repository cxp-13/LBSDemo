package com.example.lbsdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView locationInfo = null;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    MapView mMapView;
    BaiduMap mBaiduMap;

    boolean isFirstLocate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocationClient.setAgreePrivacy(true);
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);


//        SDKInitializer.setCoordType(CoordType.BD09LL);
        locationInfo = (TextView) findViewById(R.id.locationInfo);

        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationClient.registerLocationListener(myListener);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);


        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

        List<String> permissionList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();

    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//?????????????????????????????????????????????
//LocationMode.Hight_Accuracy???????????????
//LocationMode. Battery_Saving???????????????
//LocationMode. Device_Sensors?????????????????????
//LocationMode.Fuzzy_Locating, ?????????????????????v9.2.8?????????????????????????????????API??????????????????????????????????????????????????????

        option.setCoorType("bd09ll");
//???????????????????????????????????????????????????GCJ02
//GCJ02?????????????????????
//BD09ll???????????????????????????
//BD09???????????????????????????
//????????????????????????????????????????????????????????????WGS84????????????

//        option.setFirstLocType(LocationClientOption.FirstLocType.SPEED_IN_FIRST_LOC);
//???????????????????????????????????????????????????????????????????????????????????????????????????????????????
//????????????setOnceLocation(Boolean isOnceLocation)?????????????????????????????????????????????????????????setFirstLocType?????????????????????????????????????????????????????????
//FirstLocType.SPEED_IN_FIRST_LOC:??????????????????????????????????????????????????????????????????????????????
//FirstLocType.ACCUARACY_IN_FIRST_LOC:???????????????????????????????????????????????????????????????????????????

        option.setScanSpan(1000);
//?????????????????????????????????????????????int???????????????ms
//???????????????0?????????????????????????????????????????????????????????0
//???????????????0????????????1000ms???????????????

        option.setOpenGps(true);
//???????????????????????????gps?????????false
//???????????????????????????????????????????????????????????????????????????true

        option.setLocationNotify(true);
//????????????????????????GPS???????????????1S/1???????????????GPS???????????????false

        option.setIgnoreKillProcess(false);
//???????????????SDK???????????????service??????????????????????????????
//???????????????stop???????????????????????????????????????????????????????????????setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
//???????????????????????????Crash????????????????????????????????????false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
//?????????V7.2??????????????????
//?????????????????????????????????????????????????????????????????????Wi-Fi???????????????????????????????????????????????????????????????Wi-Fi???????????????

        option.setEnableSimulateGps(false);
//?????????????????????????????????GPS??????????????????????????????????????????false

        option.setNeedNewVersionRgc(true);
//????????????????????????????????????????????????????????????????????????????????????true
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
//mLocationClient???????????????????????????LocationClient??????
//??????????????????LocationClientOption???????????????setLocOption???????????????LocationClient????????????
//??????LocationClientOption?????????????????????????????????LocationClientOption??????????????????
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            navigateTo(location);
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("??????:").append(location.getLatitude()).append("\n");
            currentPosition.append("??????:").append(location.getLongitude()).append("\n");
            currentPosition.append("??????:").append(location.getCountry()).append("\n");
            currentPosition.append("???:").append(location.getProvince()).append("\n");
            currentPosition.append("???:").append(location.getCity()).append("\n");
            currentPosition.append("???:").append(location.getDistrict()).append("\n");
            currentPosition.append("??????:").append(location.getTown()).append("\n");
            currentPosition.append("??????:").append(location.getStreet()).append("\n");
            currentPosition.append("??????:").append(location.getAddrStr()).append("\n");
            currentPosition.append("???????????????");
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("??????");
            }
            locationInfo.setText(currentPosition);

        }
    }

    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            mBaiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }

        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.longitude(location.getLongitude());
        locationBuilder.latitude(location.getLatitude());

        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);

    }
}