package com.cargoexchange.cargocity.cargocity.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * Created by root on 21/1/16.
 */
public class BatteryStatusReciever extends BroadcastReceiver
{
    private FileWriter writer;
    public int count;
    private int time=0;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        int level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        int scale=intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
        Log.d("battery", (level / scale) * 100.0 + " %");
        double per=(level/scale)*100.0;
        String bat=per+" %";
        updateToDeliveryTextFile(bat+":");
        count++;
    }
    public void initfile()
    {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root,"BatteryData.txt");
            writer = new FileWriter(gpxfile);
        }
        catch (IOException e)
        {e.printStackTrace();}
    }

    public void updateToDeliveryTextFile(String sBody)
    {
      if(time==0) {
          initfile();
          time++;
      }

        try {
            if(count>=20)
            {
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