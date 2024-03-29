package com.dt.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.domainobject.Skill;
import com.dt.android.utils.ListAdapter;
import com.dt.android.view.ListItemViewCreator;

public class SkillActivity extends BackBaseActivity implements
		ListItemViewCreator<Skill>, OnItemClickListener {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skill);
		this.rightBtnIsCarType=false;
		this.findViewById(R.id.rightbtn).setVisibility(View.GONE);
		// findViewById(R.id.titleText).setVisibility(View.VISIBLE);
		ImageView img = (ImageView) findViewById(R.id.titleImg);
		img.setImageResource(R.drawable.skill);
		ListView v = (ListView) findViewById(R.id.skillList);
		ListAdapter<Skill> adapter = new ListAdapter<Skill>(this, this);	
		
		adapter.setData(dbadapter.getSkillsDAO().getSkills().toArray(new Skill[0]));
		v.setAdapter(adapter);
		
		v.setOnItemClickListener(this);
	}

	public View createOrUpdateView(Skill item, View convertView, int position,ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.skill_item, parent,false);
		TextView t = (TextView) result.findViewById(R.id.skillItemTxt);
		t.setText(item.getName());

		return result;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Skill skill=(Skill)arg0.getAdapter().getItem(arg2);
		Intent i=new Intent(this,SkillDetailActivity.class);
		i.putExtra("file_name", skill.getFileName());
		this.startActivity(i);
	}
}
