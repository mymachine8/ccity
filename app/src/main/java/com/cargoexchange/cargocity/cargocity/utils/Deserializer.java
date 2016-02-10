package com.cargoexchange.cargocity.cargocity.utils;

import android.os.Environment;
import android.util.Log;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by root on 9/2/16.
 */
public class Deserializer
{
    private Object feedbackObject;
    public Object deserialize(String fileName)
    {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
            int count = 0;
            while (true)
            {
                count++;
                try
                {
                    feedbackObject = in.readObject();
                }
                catch (ClassNotFoundException e)
                {
                    Log.d("Error-Deserializer","Cant read object");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return feedbackObject;
    }
}
