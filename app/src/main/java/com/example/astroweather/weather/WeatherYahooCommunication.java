package com.example.astroweather.weather;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.transform.Result;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Random;
import java.util.Collections;
import java.net.URLEncoder;

import java.net.URI;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * <pre>
 * % java --version
 * % java 11.0.1 2018-10-16 LTS
 *
 * % javac WeatherYdnJava.java && java -ea WeatherYdnJava
 * </pre>
 *
 */
public class WeatherYahooCommunication extends AsyncTask {

	OkHttpClient client = new OkHttpClient();

	public String get(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();

		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}


	@Override
	protected Object doInBackground(Object[] objects) {
		try {
			System.out.println(get("https://weather-ydn-yql.media.yahoo.com/forecastrss?location=sunnyvale,ca"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	@RequiresApi(api = Build.VERSION_CODES.O)
	public static void main(String[] args) throws Exception {

		final String appId = "test-app-id";
		final String consumerKey = "your-consumer-key";
		final String consumerSecret = "your-consumer-secret";
		final String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss";

		long timestamp = new Date().getTime() / 1000;
		byte[] nonce = new byte[32];
		Random rand = new Random();
		rand.nextBytes(nonce);
		String oauthNonce = new String(nonce).replaceAll("\\W", "");

		List<String> parameters = new ArrayList<>();
		parameters.add("oauth_consumer_key=" + consumerKey);
		parameters.add("oauth_nonce=" + oauthNonce);
		parameters.add("oauth_signature_method=HMAC-SHA1");
		parameters.add("oauth_timestamp=" + timestamp);
		parameters.add("oauth_version=1.0");
		// Make sure value is encoded
		parameters.add("location=" + URLEncoder.encode("sunnyvale,ca", "UTF-8"));
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

		String authorizationLine = "OAuth " +
				"oauth_consumer_key=\"" + consumerKey + "\", " +
				"oauth_nonce=\"" + oauthNonce + "\", " +
				"oauth_timestamp=\"" + timestamp + "\", " +
				"oauth_signature_method=\"HMAC-SHA1\", " +
				"oauth_signature=\"" + signature + "\", " +
				"oauth_version=\"1.0\"";

		/*HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url + "?location=sunnyvale,ca&format=json"))
				.header("Authorization", authorizationLine)
				.header("X-Yahoo-App-Id", appId)
				.header("Content-Type", "application/json")
				.build();


		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		System.out.println(response.body());*/
	}

}