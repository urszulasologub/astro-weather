package com.example.astroweather;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.astroweather.activities.FragmentView;
import com.example.astroweather.activities.PreferencesActivity;
import com.example.astroweather.fragments.WeatherFragment;
import com.example.astroweather.weather.UpdateWeatherFiles;
import com.example.astroweather.weather.WeatherYahooCommunication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
	private Double x;
	private Double y;
	private String location_name;
	private int default_update_time = 15 * 60;
	private String default_data_path;
	private Boolean isCelsius = true;
	private Date update_date = new Date();
	private String config_json_path;


	public void readDefaultDataFromJson(String jsonContent) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonContent);
		JSONObject locationObject = jsonObject.getJSONObject("location");
		x = Double.parseDouble(locationObject.get("lat").toString());
		y = Double.parseDouble(locationObject.get("long").toString());
		this.location_name = locationObject.get("city").toString();
		isCelsius = jsonObject.getString("unit").equals("c");
	}


	public void createConfigJson() throws Exception {
		JSONObject object = new JSONObject();
		object.put("update_time", default_update_time);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, default_update_time);
		update_date = calendar.getTime();
		object.put("update_date", update_date);
		PrintWriter out = new PrintWriter(new FileWriter(config_json_path));
		out.write(object.toString());
		out.close();
	}


	public void readConfigJson(String jsonContent) throws Exception {
		JSONObject jsonObject = new JSONObject(jsonContent);
		default_update_time = jsonObject.getInt("update_time");
		update_date = new Date(jsonObject.getString("update_date"));
	}


	public void createDefaultData(String location_name) throws Exception {
		try {
			WeatherYahooCommunication communication = new WeatherYahooCommunication(location_name, this, isCelsius);
			communication.execute();
			if (communication.get() != null) {
				String content = communication.get();
				communication.createMainFile(content, this);
				readDefaultDataFromJson(new String(Files.readAllBytes(Paths.get(default_data_path))));
				createConfigJson();
			} else {
				try {
					String content = new String(Files.readAllBytes(Paths.get(default_data_path)));
					readDefaultDataFromJson(content);
					readConfigJson(new String(Files.readAllBytes(Paths.get(config_json_path))));
					Toast.makeText(this, "Couldn't connect Internet. Weather may be outdated", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(this, "Couldn't connect Internet. Default data is set to Lodz. Weather may be outdated", Toast.LENGTH_LONG).show();
					this.x = 51.76174;
					this.y = 19.46801;
					this.location_name = "Lodz";
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}


	public boolean isOnline() {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager)this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			return (networkInfo != null && networkInfo.isAvailable() &&
					networkInfo.isConnected());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public Intent createIntent() {
		Intent intent = new Intent(MainActivity.this, FragmentView.class);
		Bundle b = new Bundle();
		b.putDouble("x", x);
		b.putDouble("y", y);
		b.putString("location_name", location_name);
		b.putBoolean("isCelsius", isCelsius);
		b.putInt("update_time", default_update_time);
		b.putString("update_date", update_date.toString());
		intent.putExtras(b);
		return intent;
	}


	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		File astroDirectory = new File(getCacheDir(),"AstroWeather");
		if (!astroDirectory.exists())
			astroDirectory.mkdirs();
		default_data_path = getCacheDir().toString() + "/AstroWeather/default.json";
		config_json_path = getCacheDir().toString() + "/AstroWeather/config.json";
		final AlertDialog.Builder about_dialog = new AlertDialog.Builder(this);
		about_dialog.setTitle("Error");
		about_dialog.setMessage("Can't update information");
		Button ok_button = (Button)findViewById(R.id.ok_button);
		ok_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText location_input = (EditText)findViewById(R.id.location_input);
				String location = location_input.getText().toString().toLowerCase().replaceAll("\\s","_");
				System.out.println(location);
				try {
					createDefaultData(location);
					startActivity(createIntent());
					finish();
				} catch (Exception e) {
					about_dialog.show();
					e.printStackTrace();
				}
			}
		});
		try {
			String content = new String(Files.readAllBytes(Paths.get(default_data_path)));
			readDefaultDataFromJson(content);
			readConfigJson(new String(Files.readAllBytes(Paths.get(config_json_path))));
			if (!isOnline())
				Toast.makeText(MainActivity.this, "No Internet connection detected. Data may be outdated", Toast.LENGTH_LONG).show();
			startActivity(createIntent());
			finish();
		} catch (Exception e) {

		}
	}



}
