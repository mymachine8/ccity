package com.cargoexchange.cargocity.cargocity.fragments;


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
import com.cargoexchange.cargocity.cargocity.utils.SaveSignPad;
import com.cargoexchange.cargocity.cargocity.utils.SignPadView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignPadFragment extends Fragment
{

    private SignPadView mSignPadView;

    public SignPadFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view=inflater.inflate(R.layout.fragment_sign_pad, container, false);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_save:
                if(new SaveSignPad().saveScreen(mSignPadView))
                {
                    Toast.makeText(getContext(),"Saved", Toast.LENGTH_SHORT).show();
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
        return super.onOptionsItemSelected(item);
    }

}
