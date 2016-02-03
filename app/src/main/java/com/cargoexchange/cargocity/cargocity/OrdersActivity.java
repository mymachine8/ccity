package com.cargoexchange.cargocity.cargocity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.fragments.EnterRouteFragment;
import com.cargoexchange.cargocity.cargocity.fragments.LoginFragment;
import com.cargoexchange.cargocity.cargocity.fragments.OrdersListFragment;
import com.cargoexchange.cargocity.cargocity.services.BatteryStatusReciever;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        grantLocationPermissions();
        //TODO: Have to look into battery status (temportary comment)
        //IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //Intent batterystatus=getApplicationContext().registerReceiver(new BatteryStatusReciever(),ifilter);
    }

    private void callFragment() {
        String source=getIntent().getStringExtra("context_of_intent");
        if(source!=null) { // When the intent is coming from feedback fragment
            if (source.equalsIgnoreCase("FeedbackFragment")) {
                Fragment ordersListFragment = new OrdersListFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.orders_container, ordersListFragment).commit();
            }
        }
        else {
            Fragment enterRouteFragment = new EnterRouteFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.orders_container, enterRouteFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private void grantLocationPermissions(){
        int accessFineLocationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int accessCoarseLocationPermissionCheck=ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(accessFineLocationPermissionCheck!= PackageManager.PERMISSION_GRANTED && accessCoarseLocationPermissionCheck!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSION_ACCESS_LOCATION);
        }
        else {
            callFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case Constants.PERMISSION_ACCESS_LOCATION:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    callFragment();
                }
                else
                {
                    Log.d("outgoing", "hello");
                    System.runFinalization();
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    //TODO:Why we cannot hide the activity here
                }
                break;
            }
        }
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        grantLocationPermissions();
    }
}
