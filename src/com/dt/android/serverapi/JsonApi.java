package com.dt.android.serverapi;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.dt.android.serverapi.message.RequestData;
import com.dt.android.utils.BeanUtils;

public class JsonApi<R> extends ServerApi {

	protected String host = "http://www.kaozhao123.com/api/qbank.php";

	private Class<R> rClass;
	
	public JsonApi(Class<R> c) {
		this.rClass = c;
	}
	

	public R get(String paramName, String paramValue, RequestData datas) {
		R response = null;
		try {			
			Map<String, String> params = new HashMap<String, String>();
			params.put(paramName, paramValue);
			params.put("data", datas.toJsonString());
			Log.d("JsonApi", "param:" + params);
			String param = addParams(getBaseUrl(), params, "UTF-8");
			String result = invoke(param);
			response = rClass.newInstance();
			JSONObject jsonObj = (JSONObject) new JSONTokener(result)
					.nextValue();
			BeanUtils.convertJsonToBean(jsonObj, response);
		} catch (Exception e) {
			Log.e("JsonApi", e.getMessage(), e);
		}
		return response;

	}
	
	public R post(RequestData datas){
		R response = null;
		try {		

			Log.d("JsonApi", "url:"+this.getBaseUrl()+",param:" + datas.toJsonString());
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("data", datas.toJsonString()));
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					postParameters,"UTF-8");
			
			String result =invoke(this.getBaseUrl(),formEntity);
			response = rClass.newInstance();
			JSONObject jsonObj = (JSONObject) new JSONTokener(result)
					.nextValue();
			BeanUtils.convertJsonToBean(jsonObj, response);
		} catch (Exception e) {
			Log.e("JsonApi", e.getMessage(), e);
		}
		return response;
	}

	
}
