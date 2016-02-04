package com.cargoexchange.cargocity.cargocity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.constants.FragmentTag;
import com.cargoexchange.cargocity.cargocity.fragments.FeedbackFragment;
import com.cargoexchange.cargocity.cargocity.fragments.LoginFragment;
import com.cargoexchange.cargocity.cargocity.fragments.OrdersListFragment;

public class DeliveryFeedbackActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        callFragment();
    }

    private void callFragment(){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            FeedbackFragment feedbackFragment = new FeedbackFragment();
            ft.add(R.id.feedback_fragment_container,feedbackFragment, FragmentTag.Feedback);
            ft.addToBackStack(FragmentTag.Feedback);
            ft.commit();
    }

    @Override
    public void onBackPressed() {

    }
}
