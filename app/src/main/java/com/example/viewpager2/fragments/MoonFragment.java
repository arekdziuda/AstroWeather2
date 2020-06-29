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
 * Use the {@link MoonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoonFragment extends Fragment implements MainFrameActivity.SunMoonRefreshableUI {
    private TextView currentTimeTextView;
    private TextView moonRiseTextView;
    private TextView moonWaneTextView;
    private TextView moonNewMoonTextView;
    private TextView moonFullTextView;
    private TextView moonPhaseTextView;
    private TextView moonSynodicMonth;
    private TextView moonLongitute;


    private TextView moonLatitude;
    private static final String ARG_COUNT = "param1";
    private Integer counter;

    public MoonFragment() {
        // Required empty public constructor
    }

    public static MoonFragment newInstance(Integer counter) {
        MoonFragment fragment = new MoonFragment();
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
        return inflater.inflate(R.layout.fragment_moon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_500));


        currentTimeTextView = getView().findViewById(R.id.moon_current_time_txt_view);
        moonRiseTextView = getView().findViewById(R.id.moon_rise_txt_view);
        moonWaneTextView = getView().findViewById(R.id.moon_wane_txt_view);
        moonNewMoonTextView = getView().findViewById(R.id.moon_new_moon_txt_view);
        moonFullTextView = getView().findViewById(R.id.moon_full_txt_view);
        moonPhaseTextView = getView().findViewById(R.id.moon_phase_moon_txt_view);
        moonSynodicMonth = getView().findViewById(R.id.moon_synodic_month_txt_view);
        moonLatitude = getView().findViewById(R.id.moon_latitude);
        moonLongitute = getView().findViewById(R.id.moon_longitude);

        currentTimeTextView.setText(getArguments().getString("DATE", "NO DATA"));
        moonRiseTextView.setText(getArguments().getString(ProjectConstants.BUNDLE_MOON_RISE_TIME, "NO DATA"));
        moonWaneTextView.setText(getArguments().getString(ProjectConstants.BUNDLE_MOON_SET_TIME, "NO DATA"));
        moonNewMoonTextView.setText(getArguments().getString(ProjectConstants.BUNDLE_MOON_NEW_MOON, "NO DATA"));
        moonFullTextView.setText(getArguments().getString(ProjectConstants.BUNDLE_MOON_FULL, "NO DATA"));
        moonPhaseTextView.setText(getArguments().getString(ProjectConstants.BUNDLE_MOON_PHASE, "NO DATA"));
        moonSynodicMonth.setText(getArguments().getString(ProjectConstants.BUNDLE_MOON_SYNODIC, "NO DATA"));
        moonLongitute.setText(getArguments().getString("longitude"));
        moonLatitude.setText(getArguments().getString("latitude"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() instanceof MainFrameActivity) {
            ((MainFrameActivity) getActivity()).addSubscriberFragment(this);
        }
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
        AstroCalculator.MoonInfo moonInfo = astroCalculator.getMoonInfo();

        moonRiseTextView.setText(moonInfo.getMoonset().toString());
        moonRiseTextView.setText(moonInfo.getMoonset().toString());
        moonWaneTextView.setText(moonInfo.getMoonrise().toString());
        moonNewMoonTextView.setText(moonInfo.getNextNewMoon().toString());
        moonFullTextView.setText(moonInfo.getNextFullMoon().toString());
        moonPhaseTextView.setText(String.valueOf(moonInfo.getIllumination()));
        moonSynodicMonth.setText(String.valueOf(moonInfo.getAge()));
        moonLongitute.setText(String.valueOf(longitude));
        moonLatitude.setText(String.valueOf(latitude));
    }
}