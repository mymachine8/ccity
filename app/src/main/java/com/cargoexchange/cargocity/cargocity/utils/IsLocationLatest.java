package com.cargoexchange.cargocity.cargocity.utils;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.cargoexchange.cargocity.cargocity.constants.Constants;

/**
 * Created by root on 22/1/16.
 */
public class IsLocationLatest {
    private Location mLocation;

    public IsLocationLatest(Location mLocation) {
        mLocation = mLocation;
    }

    public IsLocationLatest() {
    }

    public boolean IsLatest() {
        if (mLocation.getTime() > System.currentTimeMillis() + Constants.LOCATION_MAX_TIME)
            return false;
        else
            return true;
    }

    public Location getSingleFix(Context mContext)
    {
        LocationManager mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return null;
    }
}
