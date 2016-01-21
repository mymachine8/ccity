package com.cargoexchange.cargocity.cargocity.utils;

/**
 * Created by root on 18/1/16.
 */
public class ParseAddress
{
    private String processed;

    public String getProcessedaddress(String maddress)
    {
        parseAddress(maddress);
        return processed;
    }

    private void parseAddress(String maddress)
    {
        String [] mListOfWords;
        processed=new String();
        maddress=maddress.trim();
        if(maddress.contains(" "))
        {
            mListOfWords=maddress.split(" ");
            for(int i=0;i<mListOfWords.length-1;i++)
            {
                processed=processed+mListOfWords[i]+"+";
            }
            processed=processed+mListOfWords[mListOfWords.length-1];
        }
        else
        {
            processed=maddress;
        }
    }
}
