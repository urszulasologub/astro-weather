package com.example.astroweather.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.astroweather.MainActivity;
import com.example.astroweather.R;
import com.example.astroweather.fragments.WeatherFragment;
import com.example.astroweather.weather.UpdateWeatherFiles;
import com.example.astroweather.weather.WeatherYahooCommunication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PreferencesActivity extends AppCompatActivity {
	private Double x;
	private Double y;
	private int update_time;
	private String default_location_name;
	private final Map<Integer, String> spinner_dictionary = new HashMap<Integer, String>();
	private Boolean isCelsius = true;
	private Boolean shouldUpdate = false;
	private Date update_date;

	private Integer getKeyFromValue(String value) {
		Integer key = null;
		for (Map.Entry entry: spinner_dictionary.entrySet()) {
			if (value.equals(entry.getValue())) {
				key = (Integer)entry.getKey();
				break;
			}
		}
		return key;
	}


	public Intent prepareIntent() {
		Intent intent = new Intent(PreferencesActivity.this, FragmentView.class);
		Bundle b = new Bundle();
		b.putDouble("x", x);
		b.putDouble("y", y);
		b.putString("location_name", default_location_name);
		b.putInt("update_time", update_time);
		b.putBoolean("isCelsius", isCelsius);
		b.putBoolean("should_update", shouldUpdate);
		b.putString("update_date", update_date.toString());
		intent.putExtras(b);
		return intent;
	}


	public void createDefaultData(String location_name) throws Exception {
		try {
			WeatherYahooCommunication communication = new WeatherYahooCommunication(location_name, this, true);
			communication.execute();
			if (communication.get() != null) {
				String content = communication.get();
				JSONObject jsonObject = new JSONObject(content);
				JSONObject locationObject = jsonObject.getJSONObject("location");
				x = Double.parseDouble(locationObject.get("lat").toString());
				y = Double.parseDouble(locationObject.get("long").toString());
				default_location_name = locationObject.get("city").toString();
				communication.createMainFile(content, this);
			} else {
				Toast.makeText(this, "Couldn't update data", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		Intent this_intent = getIntent();
		x = this_intent.getDoubleExtra("x", 0);
		y = this_intent.getDoubleExtra("y", 0);
		default_location_name = this_intent.getStringExtra("location_name");
		update_time = this_intent.getIntExtra("update_time", 15 * 60);
		isCelsius = this_intent.getBooleanExtra("isCelsius", isCelsius);
		update_date = new Date(this_intent.getStringExtra("update_date"));

		final Spinner spinner = (Spinner)findViewById(R.id.time_spinner);

		ArrayList<String> spinner_list = new ArrayList<>();

		spinner_dictionary.put(60 * 15, "15 minutes");
		spinner_dictionary.put(60 * 60, "1 hour");
		spinner_dictionary.put(60 * 60 * 3, "3 hours");
		spinner_dictionary.put(60 * 60 * 6, "6 hours");
		spinner_dictionary.put(60 * 60 * 12, "12 hours");

		Iterator<Map.Entry<Integer, String>> it = spinner_dictionary.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, String> pair = (Map.Entry<Integer, String>) it.next();
			spinner_list.add(pair.getValue());
		}

		ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(this,
							android.R.layout.simple_spinner_item, spinner_list);
		array_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(array_adapter);

		int position;
		try {
			position = array_adapter.getPosition(spinner_dictionary.get(update_time));
		} catch (Exception e) {
			position = 0;
		}
		spinner.setSelection(position);


		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				update_time = getKeyFromValue((String) spinner.getSelectedItem());
				shouldUpdate = true;
			}

			@Override
			public void onNothingSelected(AdapterView <?> parent) {
			}
		});


		EditText location_input = (EditText)findViewById(R.id.location_input);
		location_input.setText(default_location_name);
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Incorrect input");
		dialog.setMessage("Entered incorrect data");

		Switch units_switch = findViewById(R.id.units_switch);
		units_switch.setChecked(!isCelsius);
		units_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isCelsius = !units_switch.isChecked();
				shouldUpdate = true;
			}
		});


		Button ok_button_p = findViewById(R.id.ok_button);
		ok_button_p.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText location_input = (EditText) findViewById(R.id.location_input);
				if (!location_input.getText().toString().equals(default_location_name)) {
					String location = location_input.getText().toString().toString().toLowerCase().replaceAll("\\s", "_");
					try {
						createDefaultData(location);
						startActivity(prepareIntent());
						finish();
					} catch (Exception e) {
						dialog.show();
						e.printStackTrace();
					}
				} else {
					startActivity(prepareIntent());
					finish();
				}
			}
		});

		Button add_city_button = findViewById(R.id.add_city_button);
		add_city_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText add_city_input = (EditText)findViewById(R.id.add_city_input);
				String location_name = add_city_input.getText().toString().toLowerCase().replaceAll("\\s","_");
				try {
					Intent intent = prepareIntent();
					try {
						WeatherYahooCommunication communication = new WeatherYahooCommunication(location_name, PreferencesActivity.this, isCelsius);
						communication.execute();
						if (communication.get() != null) {
							communication.createFile(communication.get(), PreferencesActivity.this);
						} else {
							Toast.makeText(PreferencesActivity.this, "Couldn't add city", Toast.LENGTH_LONG).show();
						}
					} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(PreferencesActivity.this, "An error occured", Toast.LENGTH_LONG).show();
					}
					startActivity(intent);
					finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Button remove_city_button = findViewById(R.id.remove_city_button);
		remove_city_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File f = new File(getCacheDir().toString() + "/AstroWeather");
				String[] pathnames;
				pathnames = f.list();
				for (String pathname : pathnames) {
					if (!pathname.equals("default.json") && !pathname.equals("config.json")) {
						System.out.println("File to remove: " + getCacheDir().toString() + "/AstroWeather/" + pathname);
						String fullFilePath = getCacheDir().toString() + "/AstroWeather/" + pathname;
						File fileToDelete = new File(fullFilePath);
						fileToDelete.delete();
					}
				}
				startActivity(prepareIntent());
				Toast.makeText(PreferencesActivity.this, "Deleted all cities", Toast.LENGTH_LONG).show();
				finish();
			}
		});

		FloatingActionButton exit_preferences_button = findViewById(R.id.exit_preferences_button);
		exit_preferences_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(prepareIntent());
				finish();
			}
		});


		FloatingActionButton refresh_button = findViewById(R.id.refresh_button);
		refresh_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shouldUpdate = true;
				Toast.makeText(PreferencesActivity.this, "Trying to update...", Toast.LENGTH_LONG).show();
				startActivity(prepareIntent());
				finish();
			}
		});

	}
}
