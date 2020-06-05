package com.example.astroweather.weather;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.astroweather.activities.PreferencesActivity;
import com.example.astroweather.secret.Credentials;

import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Random;
import java.util.Collections;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class WeatherYahooCommunication extends AsyncTask<Void, Void, String> {

	OkHttpClient client = new OkHttpClient();
	final String appId = Credentials.getAppId();
	final String consumerKey = Credentials.getClientId();
	final String consumerSecret = Credentials.getClientSecret();
	final String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss";
	String authorizationLine;
	String location = "lodz";
	Boolean isCelsius = true;
	Activity mainActivity;


	public String get(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.header("Authorization", authorizationLine)
				.header("X-Yahoo-App-Id", appId)
				.header("Content-Type", "application/json")
				.build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}


	public String createFile(String jsonContent, Activity activity) throws Exception {
		JSONObject object = new JSONObject(jsonContent);
		JSONObject locationObject = object.getJSONObject("location");
		String location_name = locationObject.get("city").toString();
		String filename = location_name.toLowerCase().replaceAll("\\s","");
		PrintWriter out = new PrintWriter(new FileWriter(activity.getCacheDir().toString() + "/AstroWeather/" + filename));
		out.write(object.toString());
		out.close();
		return location_name;
	}


	public String createMainFile(String jsonContent, Activity activity) throws Exception {
		JSONObject object = new JSONObject(jsonContent);
		JSONObject locationObject = object.getJSONObject("location");
		String location_name = locationObject.get("city").toString();
		//String filename = location_name.toLowerCase().replaceAll("\\s","");
		String filename = "default.json";
		PrintWriter out = new PrintWriter(new FileWriter(activity.getCacheDir().toString() + "/AstroWeather/" + filename));
		out.write(object.toString());
		out.close();
		return location_name;
	}


	@Override
	protected String doInBackground(Void... voids) {
		String response = "";
		try {
			if (isCelsius)
				response = get(url + "?location=" + location + "&format=json&u=c");
			else
				response = get(url + "?location=" + location + "&format=json");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return response;
	}


	protected void onPostExecute(String result) {
		//if (result == null)
		//	Toast.makeText(mainActivity, "Couldn't download city info. Data may be outdated", Toast.LENGTH_LONG).show();
	}


	@RequiresApi(api = Build.VERSION_CODES.O)
	public WeatherYahooCommunication(String location, Activity mainActivity, Boolean isCelsius) throws Exception {
		this.isCelsius = isCelsius;
		this.mainActivity = mainActivity;
		this.location = location.toLowerCase();
		long timestamp = new Date().getTime() / 1000;
		byte[] nonce = new byte[32];
		Random rand = new Random();
		for (byte i = 65; i < 32 + 65; ++i)
			nonce[i - 65] = i;
		String oauthNonce = "8nduRhXFXQ";

		List<String> parameters = new ArrayList<>();
		parameters.add("oauth_consumer_key=" + consumerKey);
		parameters.add("oauth_nonce=" + oauthNonce);
		parameters.add("oauth_signature_method=HMAC-SHA1");
		parameters.add("oauth_timestamp=" + timestamp);
		parameters.add("oauth_version=1.0");
		parameters.add("location=" + URLEncoder.encode(this.location, "UTF-8"));
		parameters.add("format=json");
		if (this.isCelsius)
			parameters.add("u=c");
		Collections.sort(parameters);

		StringBuffer parametersList = new StringBuffer();
		for (int i = 0; i < parameters.size(); i++) {
			parametersList.append(((i > 0) ? "&" : "") + parameters.get(i));
		}

		String signatureString = "GET&" +
				URLEncoder.encode(url, "UTF-8") + "&" +
				URLEncoder.encode(parametersList.toString(), "UTF-8");

		String signature = null;
		try {
			SecretKeySpec signingKey = new SecretKeySpec((consumerSecret + "&").getBytes(), "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			byte[] rawHMAC = mac.doFinal(signatureString.getBytes());
			Encoder encoder = Base64.getEncoder();
			signature = encoder.encodeToString(rawHMAC);
		} catch (Exception e) {
			System.err.println("Unable to append signature");
			System.exit(0);
		}

		authorizationLine = "OAuth " +
				"oauth_consumer_key=\"" + consumerKey + "\", " +
				"oauth_nonce=\"" + oauthNonce + "\", " +
				"oauth_timestamp=\"" + timestamp + "\", " +
				"oauth_signature_method=\"HMAC-SHA1\", " +
				"oauth_signature=\"" + signature + "\", " +
				"oauth_version=\"1.0\"";

	}

}