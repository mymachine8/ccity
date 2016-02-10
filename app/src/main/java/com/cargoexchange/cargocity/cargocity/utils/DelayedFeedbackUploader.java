package com.cargoexchange.cargocity.cargocity.utils;

import com.cargoexchange.cargocity.cargocity.constants.RouteSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 9/2/16.
 */
public class DelayedFeedbackUploader
{
    private RouteSession routeSession;
    private List<String> delayedUploadFileNameList=new ArrayList();    //Stores the file names of the feedbacks which are not uploaded because of network failure
    private List<String> delayedUploadOrderNoList=new ArrayList();     //Stores the order nos  of the feedbacks which are not uploaded because of network failure
    private Deserializer deserializeObject;
    private Object feedbackObject;
    public DelayedFeedbackUploader()
    {
        routeSession=RouteSession.getInstance();
        delayedUploadFileNameList=routeSession.getDelayedUploadFileNameList();
        delayedUploadOrderNoList=routeSession.getDelayedUploadOrderNoList();
        UploadFeedback();
    }
    private void UploadFeedback()
    {
        deserializeObject=new Deserializer();
        for(int i=0;i<delayedUploadOrderNoList.size();i++)
        {
            feedbackObject=deserializeObject.deserialize(delayedUploadFileNameList.get(i));
            //TODO: Upload feedbackObject to server
        }
    }
}
