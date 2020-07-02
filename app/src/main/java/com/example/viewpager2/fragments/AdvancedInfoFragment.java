package com.example.viewpager2.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.viewpager2.MainFrameActivity;
import com.example.viewpager2.ProjectConstants;
import com.example.viewpager2.R;
import com.example.viewpager2.weather.Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class AdvancedInfoFragment extends Fragment implements MainFrameActivity.ApiRefreshableUI {
    private static final String ARG_COUNT = "param1";
    private Integer counter;
    private TextView currentTimeTextView;
    private TextView city_wind;
    private TextView city_wind_direction;
    private TextView city_humidity;
    private TextView city_visability;



    public AdvancedInfoFragment() {
        // Required empty public constructor
    }


    public static AdvancedInfoFragment newInstance(Integer counter) {
        AdvancedInfoFragment fragment = new AdvancedInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt(ARG_COUNT);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_advanced_info, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentTimeTextView = getView().findViewById(R.id.city_current_time_txt_view);
        city_wind = getView().findViewById(R.id.city_wind);
        city_wind_direction = getView().findViewById(R.id.city_wind_direction);
        city_humidity = getView().findViewById(R.id.city_humidity);
        city_visability = getView().findViewById(R.id.city_visability);


        if (getActivity() instanceof MainFrameActivity) {
            try {
                refreshApiWeather(((MainFrameActivity) getActivity()).getContextOfMainFrame(), ((MainFrameActivity) getActivity()).getJsonObject(), ((MainFrameActivity) getActivity()).getNameOfCity(), ((MainFrameActivity) getActivity()).getIsFahrenheit());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            ((MainFrameActivity) getActivity()).addSubscriberApiListener(this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void refreshTime(Bundle bundle) {
        // if (bundle != null)
        currentTimeTextView.setText(bundle.getString("DATE"));


    }


    @Override
    public void refreshApiWeather(Context context, JSONObject jsonObject, String nameOfCity, boolean isFahrenheit) throws IOException, JSONException {
        refreshUI(context, jsonObject, nameOfCity, isFahrenheit);
    }

    //  public void refreshApiWeather(JSONObject jsonObject) throws IOException, JSONException {
    public void refreshUI(Context context, JSONObject jsonObjectFromWeb, String nameOfCity, boolean isFahrenheit) throws IOException, JSONException {
        File path = context.getFilesDir();
        File file = new File(path, nameOfCity + ".json");

        JSONObject jsonObject;
        if(file.exists()){
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            String responce = stringBuilder.toString();
            jsonObject  = new JSONObject(responce);
        }
        else
            jsonObject = jsonObjectFromWeb;


        JSONObject current_observationObject = jsonObject.getJSONObject("current_observation");
        JSONObject windObject = current_observationObject.getJSONObject("wind");
        city_wind.setText(windObject.getString("speed") + unitSpeed(isFahrenheit));
        city_wind_direction.setText(windObject.getString("direction"));
        JSONObject atmosphereObject = current_observationObject.getJSONObject("atmosphere");
        city_humidity.setText(atmosphereObject.getString("humidity") + " %");
        city_visability.setText(atmosphereObject.getString("visibility") + unitDistance(isFahrenheit));
    }

    private String unitSpeed(boolean isFahrenheit){
        return isFahrenheit ? ProjectConstants.MILE_PER_HOUR : ProjectConstants.KILOMETER_PER_HOUR;
    }

    private String unitDistance(boolean isFahrenheit){
        return isFahrenheit ? ProjectConstants.MILE : ProjectConstants.KILOMETER;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}