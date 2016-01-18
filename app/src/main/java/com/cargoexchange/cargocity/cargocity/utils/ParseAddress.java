package com.cargoexchange.cargocity.cargocity.utils;

/**
 * Created by root on 18/1/16.
 */
public class ParseAddress
{
    private String maddress;
    private String processed;
    ParseAddress(String maddress)
    {
        this.maddress=maddress;
        parseAddress();
    }

    public String getProcessedaddress()
    {
        return processed;
    }

    private void parseAddress()
    {
        String [] mListOfWords;
        maddress=maddress.trim();
        String processed=new String();
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
