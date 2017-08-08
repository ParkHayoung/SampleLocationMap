package com.example.hayoung.samplelocationmap;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by hayoung on 2017. 8. 8..
 */

class GpsLocationListener implements LocationListener {
    private WeakReference<MainActivity> mainActivityRef;

    GpsLocationListener(MainActivity mainActivity) {
        this.mainActivityRef = new WeakReference<>(mainActivity);
    }

    @Override
    public void onLocationChanged(Location location) {
        // 사용자의 좌표를 얻어왔으면 지도를 해당 위치로 움직여주자!
        MainActivity mainActivity = mainActivityRef.get();
        if (mainActivity != null) {
            mainActivity.showCurrentLocation(location);
        }
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
}

