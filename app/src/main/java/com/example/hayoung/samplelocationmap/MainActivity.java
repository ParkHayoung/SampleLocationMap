package com.example.hayoung.samplelocationmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    private static final int REQUEST_CODE_FOR_LOCATION_PERMISSION = 100;

    private LocationManager locationManager;
    private GoogleMap map;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
            }
        });

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMyLocation();
            }
        });
    }

    private boolean checkLocationPermission() {
        boolean permissionGranted = true;

        for (int i = 0; i < PERMISSIONS.length; i++) {
            int permissionGrantState = ContextCompat.checkSelfPermission(this, PERMISSIONS[i]);
            if (permissionGrantState == PackageManager.PERMISSION_DENIED)
                permissionGranted = false;
                break;
        }

        if (!permissionGranted) {
            Toast.makeText(this, "위치 정보 찾기 권한 없음", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_FOR_LOCATION_PERMISSION);
            return false;
        } else {
            Toast.makeText(this, "위치 정보 찾기 권한 있음", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_FOR_LOCATION_PERMISSION) {
            boolean denied = false;
            for (int i = 0 ; i < permissions.length ; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, permissions[i] + " Denied", Toast.LENGTH_SHORT).show();
                    denied = true;
                    break;
                }
            }
            if (!denied) {
                // 퍼미션을 다 받았으면 다시 원래 하려고 했던 액션을 수행해준다.
                requestMyLocation();
            }
        }
    }

    private LocationListener locationListener = new LocationListener() {
        /**
         * GPS 를 이용해 사용자의 위치를 얻어올때 호출 되는 메서드
         */
        @Override
        public void onLocationChanged(Location location) {
            // 사용자의 좌표를 얻어왔으면 지도를 해당 위치로 움직여주자!
            showCurrentLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void requestMyLocation() {
        checkLocationPermission();

        try {
            // 먼저 사용자의 기기에 등록된 마지막 GPS 위치를 획득해서 빠르게 지도를 이동시켜주자!
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                showCurrentLocation(lastLocation);
            }

            // 다음으로는 실제로 GPS 에게 일을 시켜서 현재 기기의 좌표값을 얻어오자!

            long minTime = 10000; // 위치를 요청하는 시간 간격(milliseconds), == 10초
            float minDistance = 0; // 허용 오차범위(meter). 힌트로만 사용될 뿐 실제로 설정된 오차범위 내의 값을 리턴한다는 보장은 없다.

            // 요청!
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 액티비티가 종료 될 때, 액티비티 객체의 메모리 누수를 막기 위해 LocationListener 를 등록해지 한다.
        // 그렇지 않으면 계속해서 위치정보를 업데이트 받으려 하게 되고,
        // MainActivity 객체 또한 메모리에서 해제되지 못한다!
        locationManager.removeUpdates(locationListener);
    }

    private void showCurrentLocation(Location location) {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

        // 기존에 존재하는 마커는 제거!
        if (marker != null) {
            marker.remove();
        }

        // 새 위치에 새 마커를 등록!
        MarkerOptions options = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("하용이 요깅네?!");
        marker = map.addMarker(options);
    }
}
