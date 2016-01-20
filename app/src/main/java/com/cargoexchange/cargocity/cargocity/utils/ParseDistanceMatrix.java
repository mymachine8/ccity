package com.cargoexchange.cargocity.cargocity.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 20/1/16.
 */
public class ParseDistanceMatrix
{
    private JSONObject distanceMatrixData;
    private List<String> distanceList=new ArrayList<>();
    private List<String> durationList=new ArrayList<>();
    public ParseDistanceMatrix(JSONObject distanceMatrixData)
    {
        this.distanceMatrixData=distanceMatrixData;
        parser();
    }

    private void parser()
    {

        try
        {
            JSONArray jRows=distanceMatrixData.getJSONArray("rows");
            JSONObject jElements;
            JSONArray jValues;
            JSONObject jDistance;
            JSONObject jDuration;
            String mDistance;
            String mDuration;

            for(int i=0;i<jRows.length();i++)
            {
                jElements=((JSONObject)jRows.get(i));
                for(int k=0;k<jElements.length();k++)
                {
                    jValues=jElements.getJSONArray("elements");
                    for(int j=0;j<jValues.length();j++)
                    {
                        jDistance=((JSONObject)jValues.get(j)).getJSONObject("distance");
                        jDuration=((JSONObject)jValues.get(j)).getJSONObject("duration_in_traffic");
                        mDistance=jDistance.getString("text");
                        mDuration=jDuration.getString("text");
                        Log.d("Distance",mDistance);
                        Log.d("Duration", mDuration);
                        distanceList.add(mDistance);
                        durationList.add(mDuration);
                    }
                }
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    public List<String> getDistanceList() {
        return distanceList;
    }

    public List<String> getDurationList() {
        return durationList;
    }
}
