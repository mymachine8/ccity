package com.cargoexchange.cargocity.cargocity.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * Created by root on 27/1/16.
 */
public class NetworkAvailability
{
    Context mContext;
    public NetworkAvailability(Context mContext)
    {
        this.mContext=mContext;
    }
    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)mContext.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null)
        {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo!=null && activeNetworkInfo.isAvailable())
            {
                if (activeNetworkInfo.isConnected())
                {
                    return true;
                }
                else
                {
                    //TODO:Ask the user to enable data services and then return true
                    return false;
                }
            }
            else
                return false;
        }
        else
            return false;
    }
}