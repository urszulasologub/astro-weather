package com.example.astroweather;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.astroweather.weather.WeatherYahooCommunication;


public class MainActivity extends AppCompatActivity {
    private Double x;
    private Double y;
    private int default_update_time = 15 * 60;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WeatherYahooCommunication test = null;
        try {
            test = new WeatherYahooCommunication();
        } catch (Exception e) {
            e.printStackTrace();
        }
        test.execute();
        final AlertDialog.Builder about_dialog = new AlertDialog.Builder(this);
        about_dialog.setTitle("Incorrect input");
        about_dialog.setMessage("Entered incorrect coords");
        Button ok_button = (Button)findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText x_input = (EditText)findViewById(R.id.x_input);
                EditText y_input = (EditText)findViewById(R.id.y_input);
                try {
                    x = Double.parseDouble(x_input.getText().toString());
                    y = Double.parseDouble(y_input.getText().toString());
                    if (x < -90 || x > 90 || y < -180 || y > 180) {
                        about_dialog.show();
                    } else {
                        Intent intent = new Intent(MainActivity.this, FragmentView.class);
                        Bundle b = new Bundle();
                        b.putDouble("x", x);
                        b.putDouble("y", y);
                        b.putInt("update_time", default_update_time);
                        intent.putExtras(b);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    about_dialog.show();
                }
            }
        });
    }


}
