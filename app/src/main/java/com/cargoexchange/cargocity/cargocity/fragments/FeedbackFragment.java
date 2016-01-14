package com.cargoexchange.cargocity.cargocity.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cargoexchange.cargocity.cargocity.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FeedbackFragment extends Fragment {

    public FeedbackFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delivery_feedback, container, false);
    }
}
