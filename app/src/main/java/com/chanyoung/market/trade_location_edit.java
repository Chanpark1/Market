package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static com.chanyoung.market.signup_location.REQUIRED_PERMISSIONS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

public class trade_location_edit extends AppCompatActivity implements OnMapReadyCallback {

    public MapView mapView;
    public NaverMap naverMap;

    Location location;

    Button submit;

    double longitude;
    double latitude;

    private final int PERMISSIONS_REQUEST_CODE = 100;
    private final int GPS_ENABLE_REQUEST_CODE = 2001;

    public String post_authNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_location_edit);
        mapView = (MapView) findViewById(R.id.edit_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        checkRunTimePermission();
        checkLocationServiceStatus();

        submit = (Button) findViewById(R.id.trade_location_edit);

        post_authNum = getIntent().getStringExtra("post_authNum");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(trade_location_edit.this,edit_sale_post.class);

                String trade_lat = String.valueOf(latitude);
                String trade_lng = String.valueOf(longitude);

                intent.putExtra("trade_lat",trade_lat);
                intent.putExtra("trade_lng",trade_lng);
                intent.putExtra("post_authNum",post_authNum);
                intent.putExtra("authNum",getIntent().getStringExtra("authNum"));
                intent.putExtra("status",getIntent().getStringExtra("status"));

                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        UiSettings uiSettings = naverMap.getUiSettings();

        uiSettings.setLocationButtonEnabled(true);

        final LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!checkLocationServiceStatus()) {
            showDialogForLocationServiceSetting();
        } else {

            try {
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }

        }

        if (ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( trade_location_edit.this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
        }
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location != null) {

            double lng = location.getLongitude();
            double lat = location.getLatitude();

            CameraPosition cameraPosition = new CameraPosition(
                    new LatLng(lat,lng), 17
            );

            naverMap.setCameraPosition(cameraPosition);
            Marker marker = new Marker();
            setMark(marker,lat,lng,R.drawable.ic_baseline_location_on_24);

            latitude = lat;
            longitude = lng;

            naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                    setMark(marker, latLng.latitude, latLng.longitude,R.drawable.ic_baseline_location_on_24);

                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                }
            });

        } else {
            Log.d(TAG,"Unknown Location");
        }


    }

    private void setMark(Marker marker, double lat, double lng, int resource) {
        marker.setIconPerspectiveEnabled(true);
        // 원근감 표시

        marker.setIcon(OverlayImage.fromResource(resource));
        // 마커 아이콘 지정

        marker.setAlpha(1);
        // 마커 투명도 지정

        marker.setHeight(130);
        marker.setWidth(130);

        marker.setPosition(new LatLng(lat,lng));
        //마커 위치 지정
        marker.setMap(naverMap);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
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
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        Log.d("trade_location","OnDestroy Called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public boolean checkLocationServiceStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(trade_location_edit.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(trade_location_edit.this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(trade_location_edit.this,REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(this, "위치 권한을 확인 해주세요.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(trade_location_edit.this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(trade_location_edit.this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(trade_location_edit.this);
        builder.setTitle("위치 서비스 비활성화;");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다. \n"
                + "위치 설정을 수정 하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent,GPS_ENABLE_REQUEST_CODE);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }


}