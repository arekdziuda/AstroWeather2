package com.example.viewpager2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainFrameActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Handler handler;
    private Runnable runnable;
    private Handler handlerTwo;
    private Runnable runnableTwo;
    private double longitude;
    private double latitude;
    private int refresh;
    private List<SunMoonRefreshableUI> subscribersList = new ArrayList<>();

    private enum ScreenSizeOrientation {PHONE_PORTRAIT, PHONE_LANDSAPE}

    private ScreenSizeOrientation screenOrientation = ScreenSizeOrientation.PHONE_PORTRAIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);
        viewPager = findViewById(R.id.view_pager);

        tabLayout = findViewById(R.id.tabs);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getDouble("latitude");
            longitude = extras.getDouble("longitude");
            refresh = extras.getInt("refresh");
        } else {
            latitude = ProjectConstants.DMCS_LATITUDE;
            longitude = ProjectConstants.DMCS_LONGITUDE;
            refresh = ProjectConstants.FIVETEEN_MINUTES;
        }
    }

    private void initializePortraitLayout(Bundle bundle) {
        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0) {
                            tab.setText("Moon fragment");
                        } else if (position == 1) {
                            tab.setText("Sun fragment");
                        }
                    }
                }).attach();
    }

    private void initializeLandsacpeLayout(Bundle bundle) {
        SunFragment sunFragment = new SunFragment();
        sunFragment.setArguments(bundle);

        MoonFragment moonFragment = new MoonFragment();
        moonFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_sun, sunFragment)
                .commit();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_moon, moonFragment)
                .commit();
    }

    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.setArguments(doInBackground(Double.parseDouble(String.valueOf(longitude)), Double.parseDouble(String.valueOf(latitude))));
        return adapter;
    }

    private void startPeriodicWeatherUpates() {
        this.handlerTwo = new Handler();
        this.runnableTwo = new Runnable() {
            @Override
            public void run() {
                if (viewPager != null)
                    viewPager.setAdapter(createCardAdapter());
                else {
                    for (SunMoonRefreshableUI subscriber : subscribersList) {
                        subscriber.refreshSunMoonWeather(Double.parseDouble(String.valueOf(longitude)), Double.parseDouble(String.valueOf(latitude)));
                    }
                }
                handler.postDelayed(this, refresh);
            }
        };
        handlerTwo.post(this.runnableTwo);
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ScreenUtilities screenUtilities = new ScreenUtilities(this);
        int orientation = getResources().getConfiguration().orientation;
        if (screenUtilities.getWidth() > 600) {
            initializeLandsacpeLayout(doInBackground(Double.parseDouble(String.valueOf(longitude)), Double.parseDouble(String.valueOf(latitude))));
        } else {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                this.screenOrientation = ScreenSizeOrientation.PHONE_LANDSAPE;
                initializeLandsacpeLayout(doInBackground(Double.parseDouble(String.valueOf(longitude)), Double.parseDouble(String.valueOf(latitude))));
            } else {
                this.screenOrientation = ScreenSizeOrientation.PHONE_PORTRAIT;
                initializePortraitLayout(doInBackground(Double.parseDouble(String.valueOf(longitude)), Double.parseDouble(String.valueOf(latitude))));
            }
        }
        startPeriodicTimeUpdate();
        startPeriodicWeatherUpates();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(this.runnable);
        handlerTwo.removeCallbacks(this.runnableTwo);
    }

    private void startPeriodicTimeUpdate() {
        this.handler = new Handler();
        this.runnable = new Runnable() {
            @Override
            public void run() {
                Date currentTime = Calendar.getInstance().getTime();
                for (SunMoonRefreshableUI subscriber : subscribersList) {
                    Bundle bundle = new Bundle();
                    bundle.putString("DATE", String.valueOf(currentTime));
                    subscriber.refreshTime(bundle, Double.parseDouble(String.valueOf(longitude)), Double.parseDouble(String.valueOf(latitude)));
                }
                handler.postDelayed(this, ProjectConstants.ONE_SECOND_IN_MILISECONDS);
            }
        };
        handler.post(this.runnable);
    }


    protected Bundle doInBackground(Double... coordinates) {
        double latitude = coordinates[0];
        double longitude = coordinates[1];
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
        AstroCalculator.SunInfo sunInfo = astroCalculator.getSunInfo();
        Bundle bundle = new Bundle();
        // sun rise info
        bundle.putString(ProjectConstants.BUNDLE_SUN_RISE_TIME, sunInfo.getSunrise().toString());
        bundle.putString(ProjectConstants.BUNDLE_SUN_RISE_AZIMUTH, String.valueOf(sunInfo.getAzimuthRise()));
        bundle.putString(ProjectConstants.BUNDLE_SUN_SET_TIME, sunInfo.getSunset().toString());
        bundle.putString(ProjectConstants.BUNDLE_SUN_SET_AZIMUTH, String.valueOf(sunInfo.getAzimuthSet()));
        bundle.putString(ProjectConstants.BUNDLE_SUN_CIVIL_EVENING_TWILIGHT, sunInfo.getTwilightEvening().toString());
        bundle.putString(ProjectConstants.BUNDLE_SUN_CIVIL_MORNING_TWILIGHT, sunInfo.getTwilightMorning().toString());
        // moon info
        bundle.putString(ProjectConstants.BUNDLE_MOON_RISE_TIME, moonInfo.getMoonset().toString());
        bundle.putString(ProjectConstants.BUNDLE_MOON_SET_TIME, moonInfo.getMoonrise().toString());
        bundle.putString(ProjectConstants.BUNDLE_MOON_NEW_MOON, moonInfo.getNextNewMoon().toString());
        bundle.putString(ProjectConstants.BUNDLE_MOON_FULL, moonInfo.getNextFullMoon().toString());
        bundle.putString(ProjectConstants.BUNDLE_MOON_PHASE, String.valueOf(moonInfo.getIllumination()));
        bundle.putString(ProjectConstants.BUNDLE_MOON_SYNODIC, String.valueOf(moonInfo.getAge()));
        bundle.putString("longitude", String.valueOf(longitude));
        bundle.putString("latitude", String.valueOf(latitude));
        Date currentTime = Calendar.getInstance().getTime();
        bundle.putString("DATE", String.valueOf(currentTime));
        return bundle;
    }

    public void addSubscriberFragment(SunMoonRefreshableUI subsriber) {
        this.subscribersList.add(subsriber);
    }

    public interface SunMoonRefreshableUI {
        void refreshTime(Bundle bundle, Double longitude, Double latitude);
        void refreshSunMoonWeather(Double longitude, Double latitude);
    }
}
