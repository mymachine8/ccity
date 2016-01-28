package com.cargoexchange.cargocity.cargocity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
        //IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //Intent batterystatus=getApplicationContext().registerReceiver(new BatteryStatusReciever(),ifilter);

        String source=getIntent().getStringExtra("source");
        if(source!=null) {
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
    protected void onResume()
    {
        super.onResume();
    }
}
