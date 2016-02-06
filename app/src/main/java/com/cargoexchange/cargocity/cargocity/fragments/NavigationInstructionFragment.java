package com.cargoexchange.cargocity.cargocity.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.adapters.InstructionListAdapter;
import com.cargoexchange.cargocity.cargocity.models.NavigationInstructionModel;
import com.cargoexchange.cargocity.cargocity.utils.ParseDirections;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by root on 18/1/16.
 */
public class NavigationInstructionFragment extends Fragment
{
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    //String url="https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyAa_HArC674Ruuyw91n2jNvntSoaIPyZ64&origin=madhapur,hyderabad&destination=uppal,hyderabad&transit_mode=bus&departure_time=now&traffic_model=pessimistic";
    private ArrayList<NavigationInstructionModel> navigationList=new ArrayList<>();
    JSONObject object;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        String response=this.getArguments().getString("mapData");
        View view=inflater.inflate(R.layout.fragment_navigation_instruction, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.html_instructions_recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        try
        {
            object = new JSONObject(response);
        }
        catch(JSONException e)
        {e.printStackTrace();}
        navigationList=new ParseDirections(object).getNavigationList();
        mRecyclerView.setAdapter(new InstructionListAdapter(navigationList));
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

}


