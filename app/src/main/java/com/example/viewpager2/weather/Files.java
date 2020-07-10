package com.example.viewpager2.weather;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.viewpager2.MainFrameActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Files extends Thread {

    MainFrameActivity activity;
    Boolean isFahrenheit;
    JSONObject object;

    long TEN_MINUTES = 600000;

    public Files(MainFrameActivity activity, Boolean isFahrenheit, JSONObject object) {
        this.activity = activity;
        this.isFahrenheit = isFahrenheit;
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
            File f;
            if (isFahrenheit)
                f = new File(path, filename + "_f.json");
            else
                f = new File(path, filename + "_c.json");
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

    public void update() throws IOException, JSONException {
        File path = activity.getFilesDir();
        File file = new File(path, "allCities" + ".json");
        JSONArray arr;
        if (file.exists()) {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            String responce = stringBuilder.toString();
            arr = new JSONArray(responce);
        } else
            arr = new JSONArray();

        boolean flag = false;
        JSONObject locationObject = object.getJSONObject("location");
        for (int i = 0; i < arr.length(); i++) {
            if(isFahrenheit){
                if (arr.getJSONObject(i).getString("city").equals(locationObject.getString("city") + "_f")) {
                    flag = true;
                    Log.e("SaveToFile", "City is currently added to file");
                    break;
                }
            }
            else{
                if (arr.getJSONObject(i).getString("city").equals(locationObject.getString("city") + "_c")) {
                    flag = true;
                    Log.e("SaveToFile", "City is currently added to file");
                    break;
                }
            }

        }
        if (!flag) {
            JSONObject list1 = new JSONObject();
            if (isFahrenheit)
                list1.put("city", locationObject.getString("city") + "_f");
            else
                list1.put("city", locationObject.getString("city") + "_c");

            list1.put("lat", locationObject.getString("lat"));
            list1.put("long", locationObject.getString("long"));
            arr.put(list1);
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(arr.toString().getBytes());
            Log.e("SaveToFile", "City is added to file");
        }
    }


}
