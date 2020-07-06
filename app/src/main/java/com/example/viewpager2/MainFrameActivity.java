package com.example.viewpager2;


import android.content.Context;

import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.example.viewpager2.fragments.MoonFragment;
import com.example.viewpager2.fragments.SunFragment;
import com.example.viewpager2.weather.Files;
import com.example.viewpager2.weather.RequestManager;
import com.example.viewpager2.weather.YahooWeatherRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    double longitude;
    double latitude;
    private int refresh;
    private List<SunMoonRefreshableUI> subscribersList = new ArrayList<>();
    private List<ApiRefreshableUI> apiSubscribersList = new ArrayList<>();

    private JSONObject locationObject;
    private JSONObject jsonObject;
    private String DEFAULT_LOCATION = "Lodz";
    private String cityName;
    boolean searchByName;
    boolean isFahrenheit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);
        viewPager = findViewById(R.id.view_pager);

        tabLayout = findViewById(R.id.tabs);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (!searchByName) {
                latitude = extras.getDouble("latitude");
                longitude = extras.getDouble("longitude");
            }
            refresh = extras.getInt("refresh");
            searchByName = extras.getBoolean("switchName");
            isFahrenheit = extras.getBoolean("switchUnit");
            if (searchByName) {
                cityName = extras.getString("cityName");
                if (cityName.isEmpty()) {
                    cityName = DEFAULT_LOCATION;
                }
            }
            refresh = extras.getInt("refresh");
        } else {
            //   latitude = ProjectConstants.DMCS_LATITUDE;
            //     longitude = ProjectConstants.DMCS_LONGITUDE;
            refresh = ProjectConstants.FIVETEEN_MINUTES;
        }
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
                if (viewPager != null) {
                    viewPager.setAdapter(createCardAdapter());
                } else {
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
        try {
            if (searchByName) {
                Log.e("MainActivity", "Searching by name");
                sendCityNameApiRequest();
            } else {
                Log.e("MainActivity", "Searching by coords");
                sendCoordinatesApiRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0) {
                            tab.setText("Moon");
                        } else if (position == 1) {
                            tab.setText("Sun");
                        } else if (position == 2) {
                            tab.setText("Basic");
                        } else if (position == 3) {
                            tab.setText("Advanced");
                        } else if (position == 4) {
                            tab.setText("Forecast");
                        }
                    }
                }).attach();
        startPeriodicTimeUpdate();
        startPeriodicWeatherUpates();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(this.runnable);
        handlerTwo.removeCallbacks(this.runnableTwo);
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public String getNameOfCity() {
        return cityName;
    }

    public boolean getIsFahrenheit() {
        return isFahrenheit;
    }

    public Context getContextOfMainFrame() {
        return getApplicationContext();
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
                for (ApiRefreshableUI ApiSubscriber : apiSubscribersList) {
                    Bundle bundle = new Bundle();
                    bundle.putString("DATE", String.valueOf(currentTime));
                    try {
                        ApiSubscriber.refreshTime(bundle);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(this, ProjectConstants.ONE_SECOND_IN_MILISECONDS);
            }
        };
        handler.post(this.runnable);
    }


    protected Bundle doInBackground(Double... coordinates) {
        double longitude = coordinates[0];
        double latitude = coordinates[1];
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
        bundle.putString("longitude", String.valueOf(longitude));
        bundle.putString("latitude", String.valueOf(latitude));
        Date currentTime = Calendar.getInstance().getTime();
        bundle.putString("DATE", String.valueOf(currentTime));
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

        return bundle;
    }

    public void addSubscriberFragment(SunMoonRefreshableUI subsriber) {
        this.subscribersList.add(subsriber);
    }

    public void addSubscriberApiListener(ApiRefreshableUI subscriber) {
        this.apiSubscribersList.add(subscriber);
    }

    public interface SunMoonRefreshableUI {
        void refreshTime(Bundle bundle, Double longitude, Double latitude);

        void refreshSunMoonWeather(Double longitude, Double latitude);
    }

    public interface ApiRefreshableUI {
        void refreshTime(Bundle bundle) throws IOException, JSONException;

        void refreshApiWeather(Context context, JSONObject jsonObject, String name, boolean isFahrenheit) throws IOException, JSONException;
    }


    private void sendCoordinatesApiRequest() throws IOException, JSONException {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            Toast.makeText(this, "Network connection not available, weather may be deprecated", Toast.LENGTH_LONG).show();
            JSONObject jsonObject = searchAllCitiesFile(null, String.valueOf(latitude), String.valueOf(longitude), isFahrenheit);
            if (jsonObject != null) {
                Log.e("SearchInFiles", "Weather readed from file");
            } else {
                for (SunMoonRefreshableUI subscriber : subscribersList) {
                    subscriber.refreshSunMoonWeather(Double.parseDouble(jsonObject.getString("long")), Double.parseDouble(jsonObject.getString("lat")));
                }
            }
            for (ApiRefreshableUI ApiSubscriber : apiSubscribersList) {
                ApiSubscriber.refreshApiWeather(MainFrameActivity.this, jsonObject, cityName, isFahrenheit);
            }
        } else {
            RequestManager requestManager = RequestManager.getInstance(this);
            YahooWeatherRequest request = new YahooWeatherRequest(Request.Method.GET, null, null, String.valueOf(this.longitude), String.valueOf(this.latitude), isFahrenheit, new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    try {
                        locationObject = ((JSONObject) response).getJSONObject("location");
                        cityName = locationObject.getString("city");
                        latitude = Double.parseDouble(locationObject.getString("lat"));
                        longitude = Double.parseDouble(locationObject.getString("long"));
                        viewPager.setAdapter(createCardAdapter());
                        MainFrameActivity.this.jsonObject = (JSONObject) response;
                        for (ApiRefreshableUI ApiSubscriber : apiSubscribersList) {
                            ApiSubscriber.refreshApiWeather(MainFrameActivity.this, (JSONObject) response, cityName, isFahrenheit);
                        }
                        for (SunMoonRefreshableUI subscriber : subscribersList) {
                            subscriber.refreshSunMoonWeather(longitude, latitude);
                        }
                        Files update = new Files(MainFrameActivity.this, isFahrenheit, jsonObject);
                        update.update();
                        update.start();

                    } catch (JSONException | IOException e) {
                        Toast.makeText(MainFrameActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                    Log.e("Response", ((JSONObject) response).toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("API error: ", "#onErrorResponse in MainActivity");
                }
            });
            requestManager.addToRequestQueue(request);
        }
    }

    private void sendCityNameApiRequest() throws IOException, JSONException {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            Toast.makeText(this, "Network connection not available, weather may be deprecated", Toast.LENGTH_LONG).show();

            JSONObject jsonObject = searchAllCitiesFile(cityName, null, null, isFahrenheit);
            if (jsonObject != null) {
                Log.e("SearchInFiles", "Weather readed from file");
                latitude = Double.parseDouble(jsonObject.getString("lat"));
                longitude = Double.parseDouble(jsonObject.getString("long"));
                for (SunMoonRefreshableUI subscriber : subscribersList) {
                    subscriber.refreshSunMoonWeather(latitude, longitude);
                }
            }
            for (ApiRefreshableUI ApiSubscriber : apiSubscribersList) {
                ApiSubscriber.refreshApiWeather(MainFrameActivity.this, jsonObject, cityName, isFahrenheit);
            }
        } else {
            RequestManager requestManager = RequestManager.getInstance(this);

            YahooWeatherRequest request = new YahooWeatherRequest(Request.Method.GET, null, null, cityName, isFahrenheit, new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    try {
                        locationObject = ((JSONObject) response).getJSONObject("location");
                        cityName = locationObject.getString("city");
                        latitude = Double.parseDouble(locationObject.getString("lat"));
                        longitude = Double.parseDouble(locationObject.getString("long"));
                        viewPager.setAdapter(createCardAdapter());
                        for (SunMoonRefreshableUI subscriber : subscribersList) {
                            subscriber.refreshSunMoonWeather(Double.parseDouble(locationObject.getString("long")), Double.parseDouble(locationObject.getString("lat")));
                        }
                        MainFrameActivity.this.jsonObject = (JSONObject) response;
                        for (ApiRefreshableUI ApiSubscriber : apiSubscribersList) {
                            ApiSubscriber.refreshApiWeather(MainFrameActivity.this, (JSONObject) response, cityName, isFahrenheit);
                        }

                        Files update = new Files(MainFrameActivity.this, isFahrenheit, jsonObject);
                        update.update();
                        update.start();
                    } catch (JSONException | IOException e) {
                        Toast.makeText(MainFrameActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                    Log.e("Response", ((JSONObject) response).toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("API error: ", "#onErrorResponse in MainActivity");
                }
            });
            requestManager.addToRequestQueue(request);
        }
    }

    public JSONObject searchAllCitiesFile(String cityName, String latitude, String longitude, boolean isFahrenheit) throws IOException, JSONException {
        File path = MainFrameActivity.this.getFilesDir();
        File file = new File(path, "allCities" + ".json");
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
            JSONArray arr = new JSONArray(responce);

            for (int i = 0; i < arr.length(); i++) {
                if (cityName != null) {
                    if (isFahrenheit) {
                        if (arr.getJSONObject(i).getString("city").equals(cityName + "_f")) {
                            return arr.getJSONObject(i);
                        }
                    } else {
                        if (arr.getJSONObject(i).getString("city").equals(cityName + "_c")) {
                            return arr.getJSONObject(i);
                        }
                    }
                } else {
                    if (arr.getJSONObject(i).getString("lat").equals(latitude) && arr.getJSONObject(i).getString("long").equals(longitude)) {
                        return arr.getJSONObject(i);
                    }
                }
            }
        }
        return null;
    }
}
