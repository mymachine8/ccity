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
    public void deserialize(String file) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream
                    (Environment.getExternalStorageDirectory() + "/FeedBack" + "/failed.ser"));
            int count = 0;
            while (true) {
                count++;
                try
                {
                    Object obj = in.readObject();
                    Log.d("Object",obj.toString());
                }
                catch (ClassNotFoundException e)
                {
                    System.out.println("can't read obj #" + count + ": " + e);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
