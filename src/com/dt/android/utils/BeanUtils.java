package com.dt.android.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class BeanUtils {

	private static final String TAG = "BeanUtils";

	public static <T> T[] convertJsonToBean(JSONArray jsonArray,
			Class resultType) {
		T[] result = (T[]) Array.newInstance(resultType, jsonArray.length());
		Object value;
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				value = resultType.newInstance();
				if (resultType.equals(String.class)) {
					Array.set(result, i, jsonArray.getString(i));
				} else {
					convertJsonToBean(jsonArray.getJSONObject(i), value);
					Array.set(result, i, value);
				}
			}
			return result;
		}

		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void convertJsonToBean(JSONObject jsonObj, Object obj) {
		Class clazz = obj.getClass();

		HashMap<String, Method> methods = new HashMap<String, Method>();
		Class pClazz = clazz;
		do{
		for (Method method : pClazz.getDeclaredMethods()) {
			if (method.getName().startsWith("set")) {
				methods.put(method.getName(), method);
			}
		}}while((pClazz=pClazz.getSuperclass())!=null);

		Iterator<String> keysIterator = jsonObj.keys();
		String key;

		for (; keysIterator.hasNext();) {
			try {
				key = keysIterator.next();
				Method setter = methods.get(getSetterName(key));
				if (setter != null) {
					setProperty(setter, jsonObj, key, obj);
				} else {
					Log.d(TAG, clazz.getName()+" no such feild:" + key);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getSetterName(String fieldName) {
		if (fieldName.length() > 1) {
			return "set" + fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
		} else {
			return "set" + fieldName.toUpperCase();
		}
	}

	private static void setProperty(Method method, JSONObject jsonObj,
			String name, Object target) {
		Class parameterType = method.getParameterTypes()[0];		
		try {
			method.invoke(target, convert(jsonObj, parameterType, name));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Object convert(JSONObject jsonObj, Class parameterType,
			String name) throws IllegalAccessException, InstantiationException,
			JSONException {
		if (parameterType.isArray()) {
			Class componentClazz = parameterType.getComponentType();
			Object obj = jsonObj.get(name);
			if (obj instanceof JSONArray) {
				JSONArray arrayValue = jsonObj.getJSONArray(name);
				Object result = Array.newInstance(componentClazz,
						arrayValue.length());
				Object value;
				for (int i = 0; i < arrayValue.length(); i++) {
					if(componentClazz.isPrimitive()||componentClazz.equals(String.class)){
						value=arrayValue.get(i);
					}else{
						value = componentClazz.newInstance();						
						convertJsonToBean(arrayValue.getJSONObject(i), value);
					}
					Array.set(result, i, value);
				}
				return result;
			} else {
				Log.i(TAG, "expect an JSONArray from response, but get an"
						+ obj.getClass().getName());

				return null;
			}
		}else {
			return convertBean(parameterType, name, jsonObj);
		}
	}

	private static Object convertBean(Class parameterType, String name,
			JSONObject jsonObj) throws JSONException, IllegalAccessException,
			InstantiationException {
		if (jsonObj.isNull(name)) {		
			return null;
		} else if (parameterType.equals(Boolean.TYPE)
				|| parameterType.equals(Boolean.class)) {
			return jsonObj.getBoolean(name);
		} else if (parameterType.equals(Integer.TYPE)
				|| parameterType.equals(Integer.class)) {
			return jsonObj.getInt(name);
		} else if (parameterType.equals(Double.TYPE)
				|| parameterType.equals(Double.class)) {
			return jsonObj.getDouble(name);
		} else if (parameterType.equals(Float.TYPE)
				|| parameterType.equals(Float.class)) {
			return (float) jsonObj.getDouble(name);
		} else if (parameterType.equals(Long.TYPE)
				|| parameterType.equals(Long.class)) {
			return jsonObj.getLong(name);
		} else if (parameterType.equals(String.class)) {
			return jsonObj.getString(name);
		} else if (parameterType.equals(Date.class)) {
			// TODO: fix the date convert.
			return null;
		}

		else {
			Log.d(TAG, parameterType.getName());
			Object value = parameterType.newInstance();
			convertJsonToBean(jsonObj.getJSONObject(name), value);
			return value;
		}
	}	

}
