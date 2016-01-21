package com.cargoexchange.cargocity.cargocity.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by root on 21/1/16.
 */
public class BatteryStatusReciever extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        int level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        int scale=intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
        Log.d("battery",(level/scale)*100+" %");
    }
}
