package com.dt.android.serverapi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.location.Location;
import android.util.Log;

public class GeoCoderClient {

	public static String[] fromLocation(Location location) {

		String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
				+ location.getLatitude()
				+ ","
				+ location.getLongitude()
				+ "&sensor=true&language=zh-CN";
		// String urlBeijing =
		// "https://maps.googleapis.com/maps/api/geocode/json?latlng=39.9543520,116.4662580&sensor=true&language=zh-CN";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet method = new HttpGet(url);
		String[] adds = null;
		try {

			Log.d("GeoCoderClient", url);
			HttpResponse response = httpClient.execute(method);

			JSONObject jsonObj = (JSONObject) new JSONTokener(
					getResponse(response.getEntity())).nextValue();
			JSONArray resArrays = jsonObj.getJSONArray("results");
			adds = new String[3];
			int idx = 0;
			String last = "";
			for (int i = resArrays.length() - 1; i >= 0; i--) {
				jsonObj = resArrays.getJSONObject(i);
				JSONArray ac = jsonObj.getJSONArray("address_components");
				for (int j = 0; j < ac.length(); j++) {
					JSONObject jobj = ac.getJSONObject(j);
					jobj.get("types");
					Log.d("GeoCoderClient",
							jobj.getString("long_name") + jobj.get("types"));
				}

				adds[idx++] = jsonObj.getString("formatted_address").replace(
						last, "");
				last = jsonObj.getString("formatted_address");

				if (idx == 3)
					break;
			}
			Log.d("GeoCoderClient", "result" + Arrays.toString(adds));
		} catch (ClientProtocolException e) {
			Log.e("GeoCoderClient", e.getMessage());
		} catch (IOException e) {
			Log.e("GeoCoderClient", e.getMessage());
		} catch (JSONException e) {
			Log.e("GeoCoderClient", e.getMessage());
		}

		return adds;

	}

	public static String fromLocationAddress(Location location) {

		String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
				+ location.getLatitude()
				+ ","
				+ location.getLongitude()
				+ "&sensor=true&language=zh-CN";
		// String urlBeijing =
		// "https://maps.googleapis.com/maps/api/geocode/json?latlng=39.9543520,116.4662580&sensor=true&language=zh-CN";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet method = new HttpGet(url);
		String last = null;
		try {

			Log.d("GeoCoderClient", url);
			HttpResponse response = httpClient.execute(method);

			JSONObject jsonObj = (JSONObject) new JSONTokener(
					getResponse(response.getEntity())).nextValue();
			JSONArray resArrays = jsonObj.getJSONArray("results");

			int idx = 0;

			jsonObj = resArrays.getJSONObject(0);
			JSONArray ac = jsonObj.getJSONArray("address_components");

			last = jsonObj.getString("formatted_address");

			Log.d("GeoCoderClient", "result " + last);
		} catch (ClientProtocolException e) {
			Log.e("GeoCoderClient", e.getMessage());
		} catch (IOException e) {
			Log.e("GeoCoderClient", e.getMessage());
		} catch (JSONException e) {
			Log.e("GeoCoderClient", e.getMessage());
		}

		return last;
	}

	private static String getResponse(HttpEntity entity) {
		String response = "";
		try {
			int bufferSize = 1024;
			StringBuffer sb = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(entity.getContent());
			char buff[] = new char[bufferSize];
			int cnt;
			while ((cnt = isr.read(buff, 0, bufferSize)) > 0) {
				sb.append(buff, 0, cnt);
			}

			response = sb.toString();
			isr.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return response;
	}
}
