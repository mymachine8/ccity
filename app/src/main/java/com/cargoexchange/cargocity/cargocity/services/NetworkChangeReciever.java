package com.cargoexchange.cargocity.cargocity.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cargoexchange.cargocity.cargocity.utils.DelayedFeedbackUploader;
import com.cargoexchange.cargocity.cargocity.utils.NetworkAvailability;

/**
 * Created by root on 9/2/16.
 */
public class NetworkChangeReciever extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        NetworkAvailability networkAvailability=new NetworkAvailability(context);
        if(networkAvailability.isNetworkAvailable())
        {
            //TODO:Try uploading files to server
            new DelayedFeedbackUploader();
        }

    }
}
