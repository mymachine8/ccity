package com.cargoexchange.cargocity.cargocity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cargoexchange.cargocity.cargocity.fragments.FeedbackFragment;
import com.cargoexchange.cargocity.cargocity.fragments.OrdersListFragment;
import com.cargoexchange.cargocity.cargocity.fragments.SignPadFragment;

public class DeliveryFeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String source=getIntent().getStringExtra("source");
        if(source!=null)
        {
            if (source.equalsIgnoreCase("FeedbackFragment"))
            {
                Fragment mSignPadFragment=new SignPadFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.feedback_fragment_container, mSignPadFragment).commit();
            }
        }
        else
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            FeedbackFragment feedbackFragment = new FeedbackFragment();
            ft.add(R.id.feedback_fragment_container,feedbackFragment).commit();
        }

    }
}
