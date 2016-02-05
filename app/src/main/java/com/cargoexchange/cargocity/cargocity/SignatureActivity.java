package com.cargoexchange.cargocity.cargocity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.cargoexchange.cargocity.cargocity.fragments.SignatureFragment;

public class SignatureActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Fragment signFragment = new SignatureFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.signature_fragment, signFragment).commit();
    }

}
