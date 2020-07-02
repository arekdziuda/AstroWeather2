package com.example.viewpager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private int refresh;
    EditText latitude;
    EditText longitude;
    EditText cityName;
    Switch simpleSwitch;
    Switch simpleSwitchUnit;
    Spinner favourite;
    Button addToFav;
    Button clearFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button confirm = findViewById(R.id.button);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        cityName = findViewById(R.id.city_name);
        simpleSwitch = findViewById(R.id.simpleSwitch);
        simpleSwitchUnit = findViewById(R.id.simpleSwitchUnit);
        favourite = findViewById(R.id.favourite);
        addToFav = findViewById(R.id.buttonFav);
        clearFav = findViewById(R.id.buttonFavDelete);

        final Spinner spinner = findViewById(R.id.refresh);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.times, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        String[] fav = new String[]{"Lodz"
        };

        final List<String> favList = new ArrayList<>(Arrays.asList(fav));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, favList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
        favourite.setAdapter(spinnerArrayAdapter);

        addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityName.getText().toString();
                if (city.isEmpty())
                    Toast.makeText(getApplicationContext(), "Wpisz nazwe miasta", Toast.LENGTH_SHORT).show();
                else if (favList.contains(city)) {
                    Toast.makeText(getApplicationContext(), "Podane miasto jest juz w ulubionych", Toast.LENGTH_SHORT).show();
                } else {
                    favList.add(cityName.getText().toString());
                    spinnerArrayAdapter.notifyDataSetChanged();
                }
                cityName.setText("");
            }
        });

        clearFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favList.clear();
                spinnerArrayAdapter.notifyDataSetChanged();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favList.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, MainFrameActivity.class);
                    intent.putExtra("latitude", ProjectConstants.DMCS_LATITUDE);
                    intent.putExtra("longitude", ProjectConstants.DMCS_LONGITUDE);
                    intent.putExtra("cityName", favourite.getSelectedItem().toString());
                    intent.putExtra("switchName", true);
                    intent.putExtra("switchUnit", simpleSwitchUnit.isChecked());
                    intent.putExtra("refresh", refresh);
                    startActivity(intent);
                } else if (simpleSwitch.isChecked() && !cityName.getText().toString().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, MainFrameActivity.class);
                    intent.putExtra("latitude", ProjectConstants.DMCS_LATITUDE);
                    intent.putExtra("longitude", ProjectConstants.DMCS_LONGITUDE);
                    intent.putExtra("cityName", String.valueOf(cityName.getText()));
                    intent.putExtra("switchName", simpleSwitch.isChecked());
                    intent.putExtra("switchUnit", simpleSwitchUnit.isChecked());
                    intent.putExtra("refresh", refresh);
                    startActivity(intent);
                } else if (validateCoords(latitude.getText().toString(), longitude.getText().toString()) && !simpleSwitch.isChecked()) {
                    Intent intent = new Intent(MainActivity.this, MainFrameActivity.class);
                    intent.putExtra("latitude", Double.parseDouble(String.valueOf(latitude.getText())));
                    intent.putExtra("longitude", Double.parseDouble(String.valueOf(longitude.getText())));
                    intent.putExtra("cityName", "");
                    System.out.println("dupa");
                    intent.putExtra("switchName", simpleSwitch.isChecked());
                    intent.putExtra("switchUnit", simpleSwitchUnit.isChecked());
                    intent.putExtra("refresh", refresh);
                    startActivity(intent);
                } else {
                    if(latitude.getText().toString().isEmpty() || longitude.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Nie wybrano żadnej z opcji", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    boolean validateCoords(String lat, String log) {
        if (lat.isEmpty() || log.isEmpty())
            return false;
        if (Double.parseDouble(lat) < -90 || Double.parseDouble(lat) > 90) {
            Toast.makeText(getApplicationContext(), "Szerokość geograficzna musi być z zakresu (-90.0 ; 90.0)", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Double.parseDouble(log) < -180 || Double.parseDouble(log) > 180) {
            Toast.makeText(getApplicationContext(), "Długość geograficzna musi być z zakresu (-180.0 ; 180.0)", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch ((int) id) {
            case 0:
                refresh = ProjectConstants.FIVE_SECONDS;
                break;
            case 1:
                refresh = ProjectConstants.TEN_SECONDS;
                break;
            case 2:
                refresh = ProjectConstants.FIVETEEN_SECONDS;
                break;
            case 3:
                refresh = ProjectConstants.FIVE_MINUTES;
                break;
            case 4:
                refresh = ProjectConstants.TEN_MINUTES;
                break;
            default:
                refresh = ProjectConstants.FIVETEEN_MINUTES;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        refresh = ProjectConstants.FIVETEEN_MINUTES;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
