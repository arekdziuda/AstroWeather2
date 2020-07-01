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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private int refresh;
    EditText latitude;
    EditText longitude;
    EditText cityName;
    Switch simpleSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button confirm = findViewById(R.id.button);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        cityName = findViewById(R.id.city_name);
        simpleSwitch = findViewById(R.id.simpleSwitch);

        Spinner spinner = findViewById(R.id.refresh);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.times, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    /*    simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(simpleSwitch.isChecked()){
                    longitude.setVisibility(View.GONE);
                    latitude.setVisibility(View.GONE);
                    cityName.setVisibility(View.VISIBLE);
                }
                else{
                    longitude.setVisibility(View.VISIBLE);
                    latitude.setVisibility(View.VISIBLE);
                    cityName.setVisibility(View.GONE);
                }
            }
        });
*/

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate(latitude.getText().toString(), longitude.getText().toString())) {
                    Intent intent = new Intent(MainActivity.this, MainFrameActivity.class);
                    intent.putExtra("latitude", Double.parseDouble(String.valueOf(latitude.getText())));
                    intent.putExtra("longitude", Double.parseDouble(String.valueOf(longitude.getText())));
                    intent.putExtra("cityName", String.valueOf(cityName.getText()));
                    intent.putExtra("switch", simpleSwitch.isChecked());
                    intent.putExtra("refresh", refresh);
                    startActivity(intent);
                }
            }
        });
    }

    boolean validate(String lat, String log) {
        if (lat.matches("")) {
            latitude.setText("" + ProjectConstants.DMCS_LATITUDE);
            lat = String.valueOf(ProjectConstants.DMCS_LATITUDE);
        }
        if (log.matches("")) {
            longitude.setText("" + ProjectConstants.DMCS_LONGITUDE);
            log = String.valueOf(ProjectConstants.DMCS_LONGITUDE);
        }
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
