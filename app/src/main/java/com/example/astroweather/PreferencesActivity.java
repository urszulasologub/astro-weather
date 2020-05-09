package com.example.astroweather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PreferencesActivity extends AppCompatActivity {
	private Double x;
	private Double y;
	private int update_time;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		Intent this_intent = getIntent();
		x = this_intent.getDoubleExtra("x", 0);
		y = this_intent.getDoubleExtra("y", 0);
		update_time = this_intent.getIntExtra("update_time", 15 * 60);

		final Spinner spinner = (Spinner)findViewById(R.id.time_spinner);

		ArrayList<String> spinner_list = new ArrayList<>();

		//spinner_dictionary.put(5, "5 seconds");
		spinner_dictionary.put(60, "1 minute");
		spinner_dictionary.put(15 * 60, "15 minutes");
		spinner_dictionary.put(60 * 60, "60 minutes");
		spinner_dictionary.put(180 * 60, "180 minutes");
		spinner_dictionary.put(600 * 60, "360 minutes");

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


		EditText x_input = (EditText)findViewById(R.id.x_input);
		x_input.setText(x.toString());
		EditText y_input = (EditText)findViewById(R.id.y_input);
		y_input.setText(y.toString());
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Incorrect input");
		dialog.setMessage("Entered incorrect data");

		Button ok_button_p = findViewById(R.id.ok_button);
		ok_button_p.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText x_input = (EditText)findViewById(R.id.x_input);
				EditText y_input = (EditText)findViewById(R.id.y_input);
				x = Double.parseDouble(x_input.getText().toString());
				y = Double.parseDouble(y_input.getText().toString());
				update_time = getKeyFromValue((String)spinner.getSelectedItem());
				try {
					if (x < -90 || x > 90 || y < -180 || y > 180) {
						dialog.show();
					} else {
						Intent intent = new Intent(PreferencesActivity.this, FragmentView.class);
						Bundle b = new Bundle();
						b.putDouble("x", x);
						b.putDouble("y", y);
						b.putInt("update_time", update_time);
						intent.putExtras(b);
						startActivity(intent);
						finish();
					}
				} catch (Exception e) {
					dialog.show();
				}
			}
		});

	}
}
