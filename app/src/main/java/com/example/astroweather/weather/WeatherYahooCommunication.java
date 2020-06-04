package com.example.astroweather.weather;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.example.astroweather.secret.Credentials;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Random;
import java.util.Collections;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class WeatherYahooCommunication extends AsyncTask {

	OkHttpClient client = new OkHttpClient();
	final String appId = Credentials.getAppId();
	final String consumerKey = Credentials.getClientId();
	final String consumerSecret = Credentials.getClientSecret();
	final String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss";
	String authorizationLine;
	String location = "Lodz";
	Boolean isCelsius = true;


	public String get(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.header("Authorization", authorizationLine)
				.header("X-Yahoo-App-Id", appId)
				.header("Content-Type", "application/json")
				.build();

		System.out.println(request.toString());
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}


	@Override
	protected Object doInBackground(Object[] objects) {
		String response = "";
		//TODO: handle celsiuses and fahrenheits
		try {
			if (isCelsius)
				response = get(url + "?location=" + location + "&format=json");
			else
				response = get(url + "?location=" + location + "&format=json");
			System.out.println(response);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return response;
	}


	@RequiresApi(api = Build.VERSION_CODES.O)
	public WeatherYahooCommunication(String location, boolean isCelsius) throws Exception {
		this.location = location.toLowerCase();
		this.isCelsius = isCelsius;
		long timestamp = new Date().getTime() / 1000;
		byte[] nonce = new byte[32];
		Random rand = new Random();
		for (byte i = 65; i < 32 + 65; ++i)
			nonce[i - 65] = i;
		//rand.nextBytes(nonce);
		//String oauthNonce = new String(nonce).replaceAll("\\W", "");
		String oauthNonce = "8nduRhXFXQ";

		List<String> parameters = new ArrayList<>();
		parameters.add("oauth_consumer_key=" + consumerKey);
		parameters.add("oauth_nonce=" + oauthNonce);
		parameters.add("oauth_signature_method=HMAC-SHA1");
		parameters.add("oauth_timestamp=" + timestamp);
		parameters.add("oauth_version=1.0");
		parameters.add("location=" + URLEncoder.encode(location, "UTF-8"));
		parameters.add("format=json");
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