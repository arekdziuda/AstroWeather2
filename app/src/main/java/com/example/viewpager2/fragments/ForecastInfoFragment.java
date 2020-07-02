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


public class ForecastInfoFragment extends Fragment implements MainFrameActivity.ApiRefreshableUI {
    private static final String ARG_COUNT = "param1";
    private Integer counter;
    private TextView currentTimeTextView;
    private TextView forecast_day1;
    private TextView forecast_temp1;
    private ImageView forecast_image1;
    private TextView forecast_day2;
    private TextView forecast_temp2;
    private ImageView forecast_image2;
    private TextView forecast_day3;
    private TextView forecast_temp3;
    private ImageView forecast_image3;
    private TextView forecast_day4;
    private TextView forecast_temp4;
    private ImageView forecast_image4;
    private TextView forecast_day5;
    private TextView forecast_temp5;
    private ImageView forecast_image5;


    public ForecastInfoFragment() {
        // Required empty public constructor
    }


    public static ForecastInfoFragment newInstance(Integer counter) {
        ForecastInfoFragment fragment = new ForecastInfoFragment();
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
        return inflater.inflate(R.layout.fragment_forecast_info, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentTimeTextView = getView().findViewById(R.id.city_current_time_txt_view);
        forecast_day1 = getView().findViewById(R.id.forecast_day1);
        forecast_temp1 = getView().findViewById(R.id.forecast_temp1);
        forecast_image1 = getView().findViewById(R.id.forecast_image1);
        forecast_day2 = getView().findViewById(R.id.forecast_day2);
        forecast_temp2 = getView().findViewById(R.id.forecast_temp2);
        forecast_image2 = getView().findViewById(R.id.forecast_image2);
        forecast_day3 = getView().findViewById(R.id.forecast_day3);
        forecast_temp3 = getView().findViewById(R.id.forecast_temp3);
        forecast_image3 = getView().findViewById(R.id.forecast_image3);
        forecast_day4 = getView().findViewById(R.id.forecast_day4);
        forecast_temp4 = getView().findViewById(R.id.forecast_temp4);
        forecast_image4 = getView().findViewById(R.id.forecast_image4);
        forecast_day5 = getView().findViewById(R.id.forecast_day5);
        forecast_temp5 = getView().findViewById(R.id.forecast_temp5);
        forecast_image5 = getView().findViewById(R.id.forecast_image5);

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
        else if(jsonObjectFromWeb!=null)
            jsonObject = jsonObjectFromWeb;
        else
            return;


        JSONArray forecastArray = jsonObject.getJSONArray("forecasts");
        forecast_day1.setText(forecastArray.getJSONObject(0).getString("day"));
        forecast_temp1.setText(forecastArray.getJSONObject(0).getString("low") + " / " + forecastArray.getJSONObject(0).getString("high")  + unitTemperature(isFahrenheit));
        setImage(Integer.valueOf(forecastArray.getJSONObject(0).getString("code")), (ImageView) getView().findViewById(R.id.forecast_image1));
        forecast_day2.setText(forecastArray.getJSONObject(1).getString("day"));
        forecast_temp2.setText(forecastArray.getJSONObject(1).getString("low") + " / " + forecastArray.getJSONObject(1).getString("high")  + unitTemperature(isFahrenheit));
        setImage(Integer.valueOf(forecastArray.getJSONObject(1).getString("code")), (ImageView) getView().findViewById(R.id.forecast_image2));
        forecast_day3.setText(forecastArray.getJSONObject(2).getString("day"));
        forecast_temp3.setText(forecastArray.getJSONObject(2).getString("low") + " / " + forecastArray.getJSONObject(2).getString("high")  + unitTemperature(isFahrenheit));
        setImage(Integer.valueOf(forecastArray.getJSONObject(2).getString("code")), (ImageView) getView().findViewById(R.id.forecast_image3));
        forecast_day4.setText(forecastArray.getJSONObject(3).getString("day"));
        forecast_temp4.setText(forecastArray.getJSONObject(3).getString("low") + " / " + forecastArray.getJSONObject(3).getString("high")  + unitTemperature(isFahrenheit));
        setImage(Integer.valueOf(forecastArray.getJSONObject(3).getString("code")), (ImageView) getView().findViewById(R.id.forecast_image4));
        forecast_day5.setText(forecastArray.getJSONObject(4).getString("day"));
        forecast_temp5.setText(forecastArray.getJSONObject(4).getString("low") + " / " + forecastArray.getJSONObject(4).getString("high")  + unitTemperature(isFahrenheit));
        setImage(Integer.valueOf(forecastArray.getJSONObject(4).getString("code")), (ImageView) getView().findViewById(R.id.forecast_image5));
    }

    private String unitTemperature(boolean isFahrenheit){
        return isFahrenheit ? ProjectConstants.fahrenheit : ProjectConstants.celsius;
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