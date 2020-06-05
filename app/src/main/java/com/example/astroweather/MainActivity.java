package com.example.astroweather;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.astroweather.activities.FragmentView;
import com.example.astroweather.activities.PreferencesActivity;
import com.example.astroweather.fragments.WeatherFragment;
import com.example.astroweather.weather.UpdateWeatherFiles;
import com.example.astroweather.weather.WeatherYahooCommunication;

import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;


public class MainActivity extends AppCompatActivity {
	private Double x;
	private Double y;
	private String location_name;
	private int default_update_time = 15 * 60;



	public void createDefaultData(String location_name) {
		try {
			WeatherYahooCommunication communication = new WeatherYahooCommunication(location_name, this, true);
			communication.execute();
			if (communication.get() != null) {
				String content = communication.get();
				communication.createFile(content, this);
				JSONObject jsonObject = new JSONObject(content);
				JSONObject locationObject = jsonObject.getJSONObject("location");
				x = Double.parseDouble(locationObject.get("lat").toString());
				y = Double.parseDouble(locationObject.get("long").toString());
				this.location_name = locationObject.get("city").toString();
			} else {
				Toast.makeText(this, "Couldn't connect Internet. Default data is set to Lodz. Weather may be outdated", Toast.LENGTH_LONG).show();
				x = 51.76174;
				y = 19.46801;
				this.location_name = "Lodz";
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Couldn't connect Internet. Default data is set to Lodz. Weather may be outdated", Toast.LENGTH_LONG).show();
			x = 51.76174;
			y = 19.46801;
			this.location_name = "Lodz";
		}
	}



	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final AlertDialog.Builder about_dialog = new AlertDialog.Builder(this);
		about_dialog.setTitle("Incorrect input");
		about_dialog.setMessage("City name is not valid");
		Button ok_button = (Button)findViewById(R.id.ok_button);
		ok_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText location_input = (EditText)findViewById(R.id.location_input);
				String location = location_input.getText().toString();
				try {
					createDefaultData(location);
					Intent intent = new Intent(MainActivity.this, FragmentView.class);
					Bundle b = new Bundle();
					b.putDouble("x", x);
					b.putDouble("y", y);
					System.out.println(location_name);
					b.putString("location_name", location_name);
					b.putInt("update_time", default_update_time);
					intent.putExtras(b);
					startActivity(intent);
					finish();
				} catch (Exception e) {
					about_dialog.show();
					e.printStackTrace();
				}
			}
		});
	}


}
