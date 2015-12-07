package com.dt.android.serverapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class ServerApi {
	protected String host = "http://www.kaozhao123.com/api/qbank.php";

	private final static int TIMEOUT_CONNECTION = 30000;
	private final static int TIMEOUT_SOCKET = 120000;

	public String invoke(String url, AbstractHttpEntity formEntity)
			throws IOException, ClientProtocolException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost method = new HttpPost(url);
		method.setEntity(formEntity);
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
				TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(),
				TIMEOUT_SOCKET);
		HttpResponse response = httpClient.execute(method);
		Log.i("SERVERAPI", response.getStatusLine().toString());

		String result = getResponse(response.getEntity());
		Log.i("SERVERAPI", "Response = " + result);
		return result;
	}

	public String invoke(String url) throws IOException,
			ClientProtocolException {
		BasicHttpParams params = new BasicHttpParams();
		return invoke(url, params);
	}

	public String invoke(String url, HttpParams params) throws IOException,
			ClientProtocolException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet method = new HttpGet(url);
		method.setParams(params);

		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
				TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(),
				TIMEOUT_SOCKET);

		HttpResponse response = httpClient.execute(method);
		Log.i("SERVERAPI", response.getStatusLine().toString());
		String result = getResponse(response.getEntity());
		Log.i("SERVERAPI", "Response = " + result);
		return result;
	}

	public static InputStream openInputStream(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet method = new HttpGet(url);

		try {

			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
					TIMEOUT_CONNECTION);
			HttpConnectionParams.setSoTimeout(httpClient.getParams(),
					TIMEOUT_SOCKET);
			HttpResponse response = httpClient.execute(method);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
			return bufHttpEntity.getContent();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getBaseUrl() {
		return host;
	}
	
	public void setBaseUrl(String host){
		this.host=host;
	}

	public static List<NameValuePair> getParamList(Map<String, String> params) {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			qparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return qparams;
	}

	public static String addParams(String baseURI, Map<String, String> params,
			String encode) {
		if (baseURI == null)
			return null;
		String paramStr = URLEncodedUtils.format(getParamList(params), encode);
		char sp = (baseURI.indexOf('?') != -1) ? '&' : '?';
		return baseURI + sp + paramStr;
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
