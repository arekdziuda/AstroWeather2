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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class BasicInfoFragment extends Fragment implements MainFrameActivity.ApiRefreshableUI {
    private static final String ARG_COUNT = "param1";
    private Integer counter;
    private TextView currentTimeTextView;
    private TextView city_longitude;
    private TextView city_latitude;
    private TextView city_temperature;
    private TextView city_pressure;
    private TextView city_weather_describe;
    private TextView city_name;
    private ImageView imageView;
    private boolean isFahrenheit;


    public BasicInfoFragment() {
        // Required empty public constructor
    }


    public static BasicInfoFragment newInstance(Integer counter) {
        BasicInfoFragment fragment = new BasicInfoFragment();
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
        return inflater.inflate(R.layout.fragment_basic_info, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentTimeTextView = getView().findViewById(R.id.city_current_time_txt_view);
        city_temperature = getView().findViewById(R.id.city_temperature);
        city_pressure = getView().findViewById(R.id.city_pressure);
        city_latitude = getView().findViewById(R.id.city_latitude);
        city_longitude = getView().findViewById(R.id.city_longitude);
        city_name = getView().findViewById(R.id.city_name);
        city_weather_describe = getView().findViewById(R.id.city_weather_describe);
        imageView = getView().findViewById(R.id.imageView);

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

    public void refreshUI(Context context, JSONObject jsonObjectFromWeb, String nameOfCity, boolean isFahrenheit) throws IOException, JSONException {
        File path = context.getFilesDir();
        File file;
        if (isFahrenheit)
            file = new File(path, nameOfCity + "_f.json");
        else
            file = new File(path, nameOfCity + "_c.json");

        JSONObject jsonObject;
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
            jsonObject = new JSONObject(responce);
        } else if (jsonObjectFromWeb != null)
            jsonObject = jsonObjectFromWeb;
        else {
            return;
        }

        JSONObject locationObject = jsonObject.getJSONObject("location");
        city_longitude.setText(locationObject.getString("long"));
        city_latitude.setText(locationObject.getString("lat"));
        city_name.setText(locationObject.getString("city"));
        JSONObject current_observationObject = jsonObject.getJSONObject("current_observation");
        JSONObject atmosphereObject = current_observationObject.getJSONObject("atmosphere");
        city_pressure.setText(atmosphereObject.getString("pressure") + unitPressure(isFahrenheit));
        JSONObject conditionObject = current_observationObject.getJSONObject("condition");
        city_temperature.setText(conditionObject.getString("temperature") + unitTemperature(isFahrenheit));
        city_weather_describe.setText(conditionObject.getString("text"));
        setImage(Integer.valueOf(conditionObject.getString("code")), (ImageView) getView().findViewById(R.id.imageView));

    }

    private String unitTemperature(boolean isFahrenheit) {
        return isFahrenheit ? ProjectConstants.fahrenheit : ProjectConstants.celsius;
    }

    private String unitPressure(boolean isFahrenheit) {
        return isFahrenheit ? ProjectConstants.HG : ProjectConstants.HPA;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    void setImage(Integer code, ImageView imageView) {
        switch (code) {
            default:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.error));
                break;
            case 0:
            case 1:
            case 2:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.windy));
                break;
            case 3:
            case 4:
            case 37:
            case 38:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.storm));
                break;
            case 5:
            case 6:
            case 7:
            case 18:
            case 35:
            case 42:
            case 43:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.snow));
                break;
            case 8:
            case 9:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.drop));
                break;
            case 10:
            case 11:
            case 12:
            case 39:
            case 40:
            case 45:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.rain));
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            case 41:
            case 46:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.snowflake));
                break;
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fog));
                break;
            case 24:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.wind));
                break;
            case 25:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.cold));
                break;
            case 26:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.clouds));
                break;
            case 27:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.moon));
                break;
            case 29:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.cloud3));
                break;
            case 28:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.cloudy2));
                break;
            case 30:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.cloudy));
                break;
            case 31:
            case 33:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.night));
                break;
            case 32:
            case 34:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.sunny));
                break;
            case 36:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.hot));
                break;
            case 47:
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.rain_bolt));
                break;
        }
    }


}