package com.example.astroweather;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private Double x;
    private Double y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    }
                    //Intent intent = new Intent(MainActivity.this, BasicCalculatorActivity.class);
                    //startActivity(intent);
                } catch (Exception e) {
                    about_dialog.show();
                }
            }
        });
    }


}
