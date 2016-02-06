package com.cargoexchange.cargocity.cargocity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.fragments.EnterRouteFragment;
import com.cargoexchange.cargocity.cargocity.fragments.OrdersListFragment;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
            getSupportFragmentManager().beginTransaction().add(R.id.orders_container, enterRouteFragment).commitAllowingStateLoss();
        }
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
                    goToHomeScreen();
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
        int accessFineLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int accessCoarseLocationPermissionCheck=ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if(accessFineLocationPermissionCheck!= PackageManager.PERMISSION_GRANTED &&
                accessCoarseLocationPermissionCheck!=PackageManager.PERMISSION_GRANTED){
            grantLocationPermissions();
        }
    }

    private void goToHomeScreen(){
        Log.d("outgoing", "hello");
        System.runFinalization();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onBackPressed() {

    }
}
