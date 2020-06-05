package com.example.astroweather.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.astroweather.MainActivity;
import com.example.astroweather.R;
import com.example.astroweather.fragments.WeatherFragment;
import com.example.astroweather.weather.WeatherYahooCommunication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PreferencesActivity extends AppCompatActivity {
	private Double x;
	private Double y;
	private int update_time;
	private String default_location_name;
	private final Map<Integer, String> spinner_dictionary = new HashMap<Integer, String>();

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

		final Spinner spinner = (Spinner)findViewById(R.id.time_spinner);

		ArrayList<String> spinner_list = new ArrayList<>();

		spinner_dictionary.put(1, "1 second");
		spinner_dictionary.put(5, "5 seconds");
		spinner_dictionary.put(30, "30 seconds");
		spinner_dictionary.put(60, "1 minute");
		spinner_dictionary.put(60 * 5, "5 minutes");
		spinner_dictionary.put(15 * 60, "15 minutes");

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
			}
			@Override
			public void onNothingSelected(AdapterView <?> parent) {
			}
		});


		EditText location_input = (EditText)findViewById(R.id.location_input);
		location_input.setText(default_location_name.toString());
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Incorrect input");
		dialog.setMessage("Entered incorrect data");

		Button ok_button_p = findViewById(R.id.ok_button);
		ok_button_p.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				update_time = getKeyFromValue((String)spinner.getSelectedItem());
				update_time = getKeyFromValue((String)spinner.getSelectedItem());
				EditText location_input = (EditText)findViewById(R.id.location_input);
				String location = location_input.getText().toString().toString().toLowerCase().replaceAll("\\s","");
				try {
					createDefaultData(location);
					Intent intent = new Intent(PreferencesActivity.this, FragmentView.class);
					Bundle b = new Bundle();
					b.putDouble("x", x);
					b.putDouble("y", y);
					System.out.println(default_location_name);
					b.putString("location_name", default_location_name);
					b.putInt("update_time", update_time);
					intent.putExtras(b);
					startActivity(intent);
					finish();
				} catch (Exception e) {
					dialog.show();
					e.printStackTrace();
				}
			}
		});

		Button add_city_button = findViewById(R.id.add_city_button);
		add_city_button.setOnClickListener(new View.OnClickListener() {
			@RequiresApi(api = Build.VERSION_CODES.O)
			@Override
			public void onClick(View v) {
				EditText add_city_input = (EditText)findViewById(R.id.add_city_input);
				String location_name = add_city_input.getText().toString().toLowerCase().replaceAll("\\s","");
				try {
					Intent intent = new Intent(PreferencesActivity.this, FragmentView.class);
					Bundle b = new Bundle();
					b.putDouble("x", x);
					b.putDouble("y", y);
					b.putString("location_name", default_location_name);
					b.putInt("update_time", update_time);
					try {
						WeatherYahooCommunication communication = new WeatherYahooCommunication(location_name, PreferencesActivity.this, true);
						communication.execute();
						if (communication.get() != null) {
							communication.createFile(communication.get(), PreferencesActivity.this);
						} else {
							Toast.makeText(PreferencesActivity.this, "Couldn't add city", Toast.LENGTH_LONG).show();
						}
					} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(PreferencesActivity.this, "Couldn't add city", Toast.LENGTH_LONG).show();
					}
					intent.putExtras(b);
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
					if (!pathname.equals("default.json")) {
						String fullFilePath = getCacheDir().toString() + "/AstroWeather/" + pathname;
						File fileToDelete = new File(fullFilePath);
						fileToDelete.delete();
					}
				}
				Intent intent = new Intent(PreferencesActivity.this, FragmentView.class);
				Bundle b = new Bundle();
				b.putDouble("x", x);
				b.putDouble("y", y);
				b.putString("location_name", default_location_name);
				b.putInt("update_time", update_time);
				intent.putExtras(b);
				startActivity(intent);
				Toast.makeText(PreferencesActivity.this, "Deleted all cities", Toast.LENGTH_LONG).show();
				finish();
			}
		});

	}
}
