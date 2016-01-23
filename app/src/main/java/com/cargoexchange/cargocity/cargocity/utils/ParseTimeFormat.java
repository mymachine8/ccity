package com.cargoexchange.cargocity.cargocity.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 22/1/16.
 */
public class ParseTimeFormat
{
    private List<String> time;
    public ParseTimeFormat(List<String> time)
    {
        this.time=time;
    }
    public List<String> parseTime()
    {
        String s[];
        String formattedTime;
        String tempFormattedHours;
        String tempFormattedMinutes;
        List<String> formattedTimeList=new ArrayList<>();
        for(int i=0;i<time.size();i++)
        {
            if(time.get(i).length()<=7)
            {
                //TODO:Format xx mins
                s=time.get(i).split(" ");
                //TODO: 0->minute value 1->"minutes"
                if(s[0].length()==1)
                {
                    formattedTime="00:0"+s[0];
                }
                else
                {
                    formattedTime="00:"+s[0];
                }
            }
            else
            {
                //TODO:Format xx hours xx mins
                s=time.get(i).split(" ");
                //TODO:0->hour value 1->"hours" 2->minute value 3->"mins"
                if(s[0].length()==1)
                {
                    tempFormattedHours="0"+s[0];
                }
                else
                {
                    tempFormattedHours=s[0];
                }
                if(s[2].length()==1)
                {
                    tempFormattedMinutes="0"+s[2];
                }
                else
                {
                    tempFormattedMinutes=s[2];
                }
                formattedTime=tempFormattedHours+":"+tempFormattedMinutes;
            }
            formattedTimeList.add(formattedTime);

        }

        return formattedTimeList;
    }
}
