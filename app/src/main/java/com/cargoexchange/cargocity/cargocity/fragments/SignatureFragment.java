package com.cargoexchange.cargocity.cargocity.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.constants.FragmentTag;
import com.cargoexchange.cargocity.cargocity.utils.SaveSignPad;
import com.cargoexchange.cargocity.cargocity.utils.SignPadView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignatureFragment extends Fragment
{

    private SignPadView mSignPadView;
    private SaveSignPad signPad;
    private static final int SIGNATURE_REQUEST_CODE = 100;

    public SignatureFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view=inflater.inflate(R.layout.content_signature, container, false);
        mSignPadView=(SignPadView)view.findViewById(R.id.signdrawPadView);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_sign_pad,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_save:
                signPad = new SaveSignPad();
                if(signPad.saveScreen(mSignPadView))
                {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("SIGN_FILE_NAME",signPad.getSaveSignFileName());
                    getActivity().setResult(Activity.RESULT_OK, resultIntent);
                    getActivity().finish();
                }
                else
                {
                    Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.action_clear:
                mSignPadView.clearScreen(mSignPadView);
                break;
        }
        return true;
    }
}
