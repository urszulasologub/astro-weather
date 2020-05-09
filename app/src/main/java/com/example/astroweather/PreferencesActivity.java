package com.example.astroweather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PreferencesActivity extends AppCompatActivity {
	private Double x;
	private Double y;
	private int update_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		 Intent this_intent = getIntent();
		x = this_intent.getDoubleExtra("x", 0);
		y = this_intent.getDoubleExtra("y", 0);
		update_time = this_intent.getIntExtra("update_time", 15 * 60);

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
			}
		});

	}
}
