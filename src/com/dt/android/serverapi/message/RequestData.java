package com.dt.android.serverapi.message;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONStringer;

import android.util.Log;

public class RequestData {
	private Map<String,Object> data=new HashMap<String,Object>();
	
	public void set(String key,Object value){
		data.put(key, value);
	}
	
	public Object remove(String key){
		return data.remove(key);
	}
	public Object get(String key){
		return data.get(key);
	}
	
	public String toJsonString() {
		JSONStringer stringer = new JSONStringer();
		try {
			stringer.object();
			for(String key:data.keySet()){
				if(data.get(key)!=null);
				stringer.key(key);
				stringer.value(data.get(key));
			}
			stringer.endObject();
		} catch (JSONException e) {
			Log.e("RequestData",e.getMessage(),e);
		}
		return stringer.toString();
	}
}
