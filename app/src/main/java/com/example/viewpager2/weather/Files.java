package com.example.viewpager2.weather;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.viewpager2.MainFrameActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Files extends Thread {

    MainFrameActivity activity;
    Boolean isCelsius;
    JSONObject object;

    long TEN_MINUTES = 600000;

    public Files(MainFrameActivity activity, Boolean isCelsius, JSONObject object) {
        this.activity = activity;
        this.isCelsius = isCelsius;
        this.object = object;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        File path = activity.getFilesDir();
        String filename;
        try {
            JSONObject locationObject = object.getJSONObject("location");
            filename = locationObject.get("city").toString();
            File f = new File(path, filename + ".json");
            if (System.currentTimeMillis() - f.lastModified() > TEN_MINUTES) {
                if (!f.exists())
                    Log.e("SaveToFile", "File not exist, created the new one");
                else
                    Log.e("SaveToFile", "Update is necessary (file was updated more than 10min ago)");
                FileOutputStream stream = new FileOutputStream(f);
                stream.write(object.toString().getBytes());
                stream.close();
            } else {
                Log.e("SaveToFile", "Update is not necessary (file was updated less than 10min ago)");
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


}
