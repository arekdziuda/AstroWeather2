package com.example.viewpager2.fragments;

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
import com.example.viewpager2.R;
import com.example.viewpager2.weather.Files;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class BasicInfoFragment extends Fragment implements MainFrameActivity.ApiRefreshableUI {
    private static final String ARG_COUNT = "param1";
    private Integer counter;
    private TextView currentTimeTextView;
    private TextView city_longitude;
    private TextView city_latitude;
    private TextView city_temperature;
    private TextView city_pressure;
    private TextView city_name;
    private ImageView imageView;

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
        imageView = getView().findViewById(R.id.imageView);

        //currentTimeTextView.setText(getArguments().getString("DATE", "NO DATA"));
       // city_longitude.setText(getArguments().getString("longitude"));
      //  city_latitude.setText(getArguments().getString("latitude"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() instanceof MainFrameActivity) {
            ((MainFrameActivity) getActivity()).addSubscriberApiListener(this);
        }
    }

    @Override
    public void refreshTime(Bundle bundle) {
       // if (bundle != null)
            currentTimeTextView.setText(bundle.getString("DATE"));


    }

    @Override
    public void refreshApiWeather(Context context, JSONObject jsonObject) throws IOException, JSONException {

            Files update = new Files((MainFrameActivity) context, true, jsonObject);
            update.start();
            //  city_name.setText(bundle.getString("LOCATION"));
         //   File path = context.getFilesDir();

        //    File file = new File(path, "Lodz.json");
         //   int length = (int) file.length();

         /*   byte[] bytes = new byte[length];
            FileInputStream in = new FileInputStream(file);
            in.read(bytes);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject)jsonParser.parse(
                new InputStreamReader(inputStream, "UTF-8"));
            JSONObject object = new JSONObject(Arrays.toString(bytes));
            in.close();*/

            JSONObject locationObject = jsonObject.getJSONObject("location");
            city_longitude.setText(locationObject.getString("long"));
            city_latitude.setText(locationObject.getString("lat"));
            city_name.setText(locationObject.getString("city"));
            JSONObject current_observationObject = jsonObject.getJSONObject("current_observation");
            JSONObject atmosphereObject = current_observationObject.getJSONObject("atmosphere");
            city_pressure.setText(atmosphereObject.getString("pressure") + " hPa");
            JSONObject conditionObject = current_observationObject.getJSONObject("condition");
            city_temperature.setText(conditionObject.getString("temperature"));
            imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.clouds));

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Bundle bundle);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}