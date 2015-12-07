package com.dt.android.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dt.android.view.ListItemViewCreator;

public class ListAdapter<T> extends BaseAdapter {
	private T[] data;
	private ListItemViewCreator<T> viewCreator;
	private Context context;
	
	public ListAdapter(Context context, ListItemViewCreator<T> creator) {
		this.context = context;
		this.viewCreator = creator;
	}

	public int getCount() {
		if (data != null) {
			return data.length;
		}	
		return 0;

	}

	public T getItem(int position) {
		return data[position];
	}

	public long getItemId(int position) {

		return position;
	}

	/**
	 * @return the data
	 */
	public T[] getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(T[] data) {
		
		if(data==null){
//			if(context instanceof LoadingActivity){
//				LoadingActivity activity=(LoadingActivity)context;
//				activity.showToast();
//			}
		}else
		{
			this.data = data;
			this.notifyDataSetChanged();
		
		}
	}
	public View getView(int position, View convertView, ViewGroup parent) {
//		Log.d("ListAdapter","get View for "+position);
		return viewCreator.createOrUpdateView(data[position], convertView,
				position, parent);
	}

}