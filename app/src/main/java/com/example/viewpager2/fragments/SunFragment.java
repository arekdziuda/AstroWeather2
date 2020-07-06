package com.example.viewpager2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.example.viewpager2.MainFrameActivity;
import com.example.viewpager2.ProjectConstants;
import com.example.viewpager2.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SunFragment extends Fragment implements MainFrameActivity.SunMoonRefreshableUI {
    private static final String ARG_COUNT = "param1";
    private Integer counter;

    private TextView currentTimeTextView;
    private TextView sunRiseTextView;
    private TextView sunRiseAzimuthTextView;
    private TextView sunSetTextView;
    private TextView sunSunsetAzimuthTextView;
    private TextView sunCivilMorningTwilightTextView;
    private TextView sunCivilEveningTwilightTextView;
    private TextView sunLongitute;
    private TextView sunLatitude;


    public SunFragment() {
        // Required empty public constructor
    }

    public static SunFragment newInstance(Integer counter) {
        SunFragment fragment = new SunFragment();
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
        return inflater.inflate(R.layout.fragment_sun, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green_500));
        currentTimeTextView = getView().findViewById(R.id.sun_current_time_txt_view);
        sunRiseTextView = getView().findViewById(R.id.sun_rise_time);
        sunRiseAzimuthTextView = getView().findViewById(R.id.sun_rise_azymut);
        sunSetTextView = getView().findViewById(R.id.sunset_time);
        sunSunsetAzimuthTextView = getView().findViewById(R.id.sunset_azymut);
        sunCivilMorningTwilightTextView = getView().findViewById(R.id.sun_rise_civil);
        sunCivilEveningTwilightTextView = getView().findViewById(R.id.sun_set_civil);
        sunLatitude = getView().findViewById(R.id.sun_latitude);
        sunLongitute = getView().findViewById(R.id.sun_longitude);

        currentTimeTextView.setText(getArguments().getString("DATE", "NO DATA"));
        sunLongitute.setText(getArguments().getString("longitude"));
        sunLatitude.setText(getArguments().getString("latitude"));
        sunRiseTextView.setText(getArguments().getString(ProjectConstants.BUNDLE_SUN_RISE_TIME, "NO DATA"));
        sunRiseAzimuthTextView.setText(getArguments().getString(ProjectConstants.BUNDLE_SUN_RISE_AZIMUTH, "NO DATA"));
        sunSetTextView.setText(getArguments().getString(ProjectConstants.BUNDLE_SUN_SET_TIME, "NO DATA"));
        sunSunsetAzimuthTextView.setText(getArguments().getString(ProjectConstants.BUNDLE_SUN_SET_AZIMUTH, "NO DATA"));
        sunCivilEveningTwilightTextView.setText(getArguments().getString(ProjectConstants.BUNDLE_SUN_CIVIL_EVENING_TWILIGHT, "NO DATA"));
        sunCivilMorningTwilightTextView.setText(getArguments().getString(ProjectConstants.BUNDLE_SUN_CIVIL_MORNING_TWILIGHT, "NO DATA"));
        if (getActivity() instanceof MainFrameActivity) {
            ((MainFrameActivity) getActivity()).addSubscriberFragment(this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Bundle bundle);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void refreshTime(Bundle bundle, Double latitude, Double longitude) {
        currentTimeTextView.setText(bundle.getString("DATE"));
    }

    @Override
    public void refreshSunMoonWeather(Double latitude, Double longitude) {
        Calendar instance = Calendar.getInstance();
        double timeOffsetGreenwich = (instance.get(Calendar.ZONE_OFFSET)) / (1000 * 60 * 60);
        AstroDateTime astroDateTime = new AstroDateTime(instance.get(Calendar.YEAR),
                instance.get(Calendar.MONTH) + 1,
                instance.get(Calendar.DAY_OF_MONTH),
                instance.get(Calendar.HOUR),
                instance.get(Calendar.MINUTE),
                instance.get(Calendar.SECOND),
                (int) timeOffsetGreenwich,
                true);

        AstroCalculator.Location location = new AstroCalculator.Location(longitude, latitude);
        AstroCalculator astroCalculator = new AstroCalculator(astroDateTime, location);
        AstroCalculator.SunInfo sunInfo = astroCalculator.getSunInfo();

        sunLongitute.setText(String.valueOf(longitude));
        sunLatitude.setText(String.valueOf(latitude));
        sunRiseTextView.setText(sunInfo.getSunrise().toString());
        sunRiseAzimuthTextView.setText(String.valueOf(sunInfo.getAzimuthRise()));
        sunSetTextView.setText(sunInfo.getSunset().toString());
        sunSunsetAzimuthTextView.setText(String.valueOf(sunInfo.getAzimuthSet()));
        sunCivilEveningTwilightTextView.setText(sunInfo.getTwilightEvening().toString());
        sunCivilMorningTwilightTextView.setText(sunInfo.getTwilightMorning().toString());
    }
}