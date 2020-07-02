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


    public String updateFile(String filename, Activity activity) throws Exception {
        //   JSONObject object = new JSONObject(json);
        if (isCelsius)
            object.put("unit", "c");
        else
            object.put("unit", "f");
        JSONObject locationObject = object.getJSONObject("location");
        String location_name = locationObject.get("city").toString();
        String filepath = activity.getCacheDir().toString() + "/AstroWeather/" + filename;
        File f = new File(filepath);
        if (f.exists()) {
            PrintWriter out = new PrintWriter(new FileWriter(filepath));
            out.write(object.toString());
            out.close();
            return location_name;
        }
        throw new RuntimeException("File " + filepath + " does not exists");
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
     /*   String[] pathnames = f.list();
        int how_many_downloaded = 0;
        for (String pathname : pathnames) {
            System.out.println(pathname);
            if (!pathname.equals("config.json")) {
                String fullFilePath = null;*/
               /* try {
                    fullFilePath = activity.getCacheDir().toString() + "/AstroWeather/" + pathname;
                    System.out.println("File to update: " + fullFilePath);
                    File fp = new File(fullFilePath);
                    if (fp.exists()) {
                        YahooWeatherRequest yahooCommunication;
                       if (!pathname.equals("default.json"))
                            yahooCommunication = new YahooWeatherRequest(pathname, activity, isCelsius);
                        else {
                            String content = new String(java.nio.file.Files.readAllBytes(Paths.get(fullFilePath)));
                            JSONObject jsonObject = new JSONObject(content);
                            JSONObject locationObject = jsonObject.getJSONObject("location");
                            yahooCommunication = new WeatherYahooCommunication(locationObject.get("city").toString(), activity, isCelsius);
                        }
                        yahooCommunication.execute();
                        if (yahooCommunication.get() != null) {
                            yahooCommunication.updateFile(pathname, yahooCommunication.get(), activity);
                            ++how_many_downloaded;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (how_many_downloaded > 0)
            activity.shouldRefreshFragments = true;*/
    }


}
