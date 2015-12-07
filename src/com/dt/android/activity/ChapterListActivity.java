package com.dt.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.db.Constants;
import com.dt.android.db.dao.ChapterDAO;
import com.dt.android.domainobject.AppData;
import com.dt.android.domainobject.Chapter;
import com.dt.android.utils.ListAdapter;
import com.dt.android.view.ListItemViewCreator;

public class ChapterListActivity extends BackBaseActivity implements
		ListItemViewCreator<Chapter>, OnItemClickListener, OnClickListener {

	private Integer title;

	private ChapterDAO chapterDao;

	private int type;
	private ListAdapter<Chapter> adapter;
	
	private Integer pId;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		chapterDao = dbadapter.getChapterDAO();
		setContentView(R.layout.list);
		// findViewById(R.id.titleText).setVisibility(View.VISIBLE);
		ImageView img = (ImageView) findViewById(R.id.titleImg);
		img.setVisibility(View.GONE);
		title = getIntent().getIntExtra("title", R.string.practice_cha);
		
		pId  = getIntent().getIntExtra("pId", 0);
		TextView titleText = (TextView) findViewById(R.id.titletext);
		if (title != null) {
			titleText.setText(title);
		} else {
			titleText.setText(R.string.practice_cha);
		}
		titleText.setVisibility(View.VISIBLE);
		ListView v = (ListView) findViewById(R.id.lists);
		adapter = new ListAdapter<Chapter>(this, this);
		type = this.getIntent().getIntExtra("type", 0);
		Button clearBtn = (Button) this.findViewById(R.id.btn_clear);
		clearBtn.setOnClickListener(this);
		if (type == Constants.STARED) {
			clearBtn.setVisibility(View.VISIBLE);
			clearBtn.setText(R.string.clear_stared);
		} else if (type == Constants.ERRORED) {
			clearBtn.setVisibility(View.VISIBLE);
			clearBtn.setText(R.string.clear_errors);
		}
		v.setAdapter(adapter);
		v.setOnItemClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dt.android.activity.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		adapter.setData(getData());
	}

	private Chapter[] getData() {
		Chapter[] result = null;
		switch (type) {
		case 0:
			// 0
			result = chapterDao.getChapters(pId).toArray(new Chapter[0]);
			break;
		case 1:
			// stared
			result = chapterDao.getChaptersForStared(pId).toArray(new Chapter[0]);
			break;
		case 2:
			// errored
			result = chapterDao.getChaptersForError(pId).toArray(new Chapter[0]);
			break;
		}
		boolean removeTruck=true;
		boolean removeBus =true;
		switch (AppData.getInstance().getCarType()) {		
		case CAR_TYPE_BUS:
			removeBus=false;
			break;
		case CAR_TYPE_TRUCK:
			removeTruck = false;
			break;
		}
		List<Chapter>l=new ArrayList<Chapter>();
		for(Chapter ch:result){
			if(ch.getChapterId()==12&&removeTruck){
				continue;
			}
			if(ch.getChapterId()==13&&removeBus){
				continue;
			}
			l.add(ch);
		}
		return l.toArray(new Chapter[0]);
	}

	public View createOrUpdateView(Chapter item, View convertView, int position,ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.listitem_textwithcount, null);
		TextView t = (TextView) result.findViewById(R.id.title);
		t.setText(item.getChapter());
		TextView c = (TextView) result.findViewById(R.id.count);
		if(!item.isHasChildren()){
			c.setText("共" + item.getQuesitonCount() + "题");
		}else{
			c.setVisibility(View.GONE);
		}
		return result;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Chapter ch = (Chapter) arg0.getAdapter().getItem(arg2);
		if(ch.isHasChildren()){
			Intent i = new Intent(this, ChapterListActivity.class);
			i.putExtra("title", title);
			i.putExtra("type", type);
			i.putExtra("pId", ch.getChapterId());
			startActivity(i);
		}else if (ch.getQuesitonCount() > 0) {
			Intent i = new Intent(this, QuestionActivity.class);
			i.putExtra("title", title);
			i.putExtra("type", type);
			i.putExtra("chapter", ch);
			startActivity(i);
		} else {
			this.showToast(getMessage(ch));
		}
	}

	private String getMessage(Chapter ch) {
		if (type == 0) {
			return ch.getChapter() + "的问题为空";
		} else if (ch.getChapterId() == null) {
			return ch.getChapter() + "为空";
		} else {
			if (type == Constants.STARED) {
				return ch.getChapter() + "的收藏为空";
			} else  {
				return ch.getChapter() + "的错题为空";
			}
		}
	}

	protected String [] getClearMessage(){
		if(type==1){
			return new String[]{"清空收藏","确认要清空我的收藏？"};
		}else if(type==Constants.ERRORED){
			return new String[]{"清空错题","确认要清空我的错题？"};
		}else{
			return new String[]{"清空收藏","确认要清空我的收藏？"};
		}
	};	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_clear:
			showDialog(CLEAR_STARED);
			break;
		}

	}
	
	protected void doClear(){
		dbadapter.getQuestionDAO().clearFlag(type);
		adapter.setData(getData());
	}

}
