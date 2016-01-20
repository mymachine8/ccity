package com.cargoexchange.cargocity.cargocity.services;

/**
 * Created by root on 19/1/16.
 */

import android.Manifest;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.pubnub.api.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by root on 19/1/16.
 */
public class LocationService extends IntentService
{
    private LocationManager mLocationManager;
    private Pubnub mPubnub;
    private FileWriter writer;
    public int count;

    public LocationService(String name)
    {
        super(name);
    }
    public LocationService()
    {
        super("com.cargoexchange.cargocity.cargocity.LocationService");
        mPubnub = new Pubnub("publish_key", "subscribe_key");
        count=0;
        initfile();
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


        mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 10, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                count++;
                JSONObject data = new JSONObject();

                try {
                    data.put("latitude", location.getLatitude());
                    data.put("longitude", location.getLongitude());
                    data.put("speed", location.getSpeed());
                    updateToDeliveryTextFile("delivery_tracking.txt", data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                mPubnub.publish("location_details", data, new Callback() {
//                });

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                if (provider.equalsIgnoreCase(mLocationManager.GPS_PROVIDER)) {
                    //TODO:Send message to the server that GPS was enabled
                }

            }

            @Override
            public void onProviderDisabled(String provider) {
                if (provider.equalsIgnoreCase(mLocationManager.GPS_PROVIDER)) {
                    //TODO:Send message to the server that GPS was disabled
                }
            }
        });
    }
    public void initfile()
    {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root,"TestData.txt");
            writer = new FileWriter(gpxfile);
        }
        catch (IOException e)
        {e.printStackTrace();}
    }

    public void updateToDeliveryTextFile(String sFileName, String sBody){


        try {
            if(count>=10){
                writer.close();
            }
             else {
                writer.append(sBody + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
