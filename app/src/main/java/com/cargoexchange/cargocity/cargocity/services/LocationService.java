package com.cargoexchange.cargocity.cargocity.services;

/**
 * Created by root on 19/1/16.
 */

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by root on 19/1/16.
 */
public class LocationService extends IntentService
{
    private LocationManager mLocationManager;

    public LocationService(String name)
    {
        super(name);
    }
    public LocationService()
    {
        super("com.cargoexchange.cargocity.cargocity.LocationService");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return super.onBind(intent);
    }

    @Override
    public void onDestroy()
    {
        Log.d("LocationChange", "no more locations");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        final LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 10, 0, new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.d("Error",location.getLatitude()+"--");
                //TODO:Send the cordinates to server using Pubnub
                //location.getLatitude();
                //location.getLongitude();
                //location.getSpeed();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

            @Override
            public void onProviderEnabled(String provider)
            {
                if(provider.equalsIgnoreCase(mLocationManager.GPS_PROVIDER))
                {
                    //TODO:Send message to the server that GPS was enabled
                }

            }

            @Override
            public void onProviderDisabled(String provider)
            {
                if(provider.equalsIgnoreCase(mLocationManager.GPS_PROVIDER))
                {
                    //TODO:Send message to the server that GPS was disabled
                }
            }
        });
    }
}