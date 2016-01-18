package com.cargoexchange.cargocity.cargocity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cargoexchange.cargocity.cargocity.fragments.EnterRouteFragment;
import com.cargoexchange.cargocity.cargocity.fragments.LoginFragment;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        Fragment enterRouteFragment = new EnterRouteFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.orders_container,enterRouteFragment).commit();
    }
}
