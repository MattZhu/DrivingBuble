package com.dt.android.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.domainobject.AppData;
import com.dt.android.domainobject.CountyCode;
import com.dt.android.serverapi.JsonApi;
import com.dt.android.serverapi.message.ParamterNames;
import com.dt.android.serverapi.message.RequestData;
import com.dt.android.serverapi.message.Response;
import com.dt.android.serverapi.message.School;
import com.dt.android.serverapi.message.SchoolResponse;
import com.dt.android.utils.AsyncTaskEx;
import com.dt.android.utils.ListAdapter;
import com.dt.android.view.ListItemViewCreator;

public class SchoolQueryActivity extends BackBaseActivity implements
		OnClickListener, ListItemViewCreator<School>, OnItemClickListener,
		OnScrollListener, ParamterNames {

	private static final String ST_PRICE = "price";
	private static final String ST_DATE = "date";
	private static final String ST_DISTRICT = "district";
	private static final String ORDER_ASC = "ASC";
	private static final String ORDER_DESC = "DESC";

	private View priceBtn;

	private View dateBtn;

	private View areaBtn;

	private TextView priceTxt;

	private TextView dateTxt;

	private TextView areaTxt;

	private boolean priceUp = true;

	private boolean dateUp = true;

	private Integer selectArea = 0;

	private RequestData parameter;

	private int selectAreaID = 0;

	private JsonApi<SchoolResponse> api;

	private ListAdapter<School> adapter;

	private boolean hasMore = true;
	private boolean loading = false;
	private View[] buttons = new View[3];
	private ImageView priceArrow;
	private ImageView dateArrow;
	private int tg=0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.school_query);
		findViewById(R.id.titleImg).setVisibility(View.GONE);

		TextView title = (TextView) findViewById(R.id.titletext);
		title.setVisibility(View.VISIBLE);
		title.setText(R.string.school_query);
		this.rightBtnIsCarType = false;

		Button rightBtn = (Button) this.findViewById(R.id.rightbtn);
		rightBtn.setText("");
		rightBtn.setBackgroundResource(R.drawable.query_btn);
		int width = (int) (34 * this.getResources().getDisplayMetrics().density);
		LayoutParams params = rightBtn.getLayoutParams();
		params.height = width;
		params.width = width;
		rightBtn.setLayoutParams(params);
		rightBtn.setOnClickListener(this);

		priceBtn = findViewById(R.id.byPrice);
		priceBtn.setOnClickListener(this);
		priceArrow = (ImageView) findViewById(R.id.byPriceArrow);
		priceBtn.setSelected(true);
		buttons[0] = priceBtn;

		dateBtn = findViewById(R.id.byDate);
		dateBtn.setOnClickListener(this);
		dateArrow = (ImageView) findViewById(R.id.byDateArrow);
		buttons[1] = dateBtn;
		areaBtn = findViewById(R.id.byArea);
		areaBtn.setOnClickListener(this);
		buttons[2] = areaBtn;
		Button b = (Button) findViewById(R.id.registerBtn);
		b.setOnClickListener(this);
		priceTxt = (TextView) findViewById(R.id.byPriceTxt);
		dateTxt = (TextView) findViewById(R.id.byDateTxt);

		areaTxt = (TextView) areaBtn.findViewById(R.id.area_id);
		selectAreaID = AppData.getInstance().getCurrentLocation().getId();

		ListView list = (ListView) findViewById(R.id.schoolList);
		adapter = new ListAdapter<School>(this, this);
		api = new JsonApi<SchoolResponse>(SchoolResponse.class);
		parameter = new RequestData();
		if(this.getIntent().getIntExtra("queryschool", 1)==1){
			parameter.set("api_name", "get_school");
			title.setText(R.string.school_query);			
		}
		else{
			tg=1;
			b.setVisibility(View.GONE);		
			title.setText("团购驾校");
			parameter.set("api_name", "get_tuangou");
		}
		parameter.set(SEARCH_TYPE, ST_PRICE);
		parameter.set(ORDER_TYPE, ORDER_ASC);
		parameter.set(CITY_ID, selectAreaID);
		parameter.set(PAGE_SIZE, 60);
		parameter.set(PAGE_NUM, 0);
		querySchool();

		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnScrollListener(this);
	}

	private void querySchool() {
		showProgressDialog(PROGRESS_DIALOG);
		task = new AsyncTaskEx<String, Void, SchoolResponse>() {

			@Override
			protected SchoolResponse doInBackground(String... params) {

				return api.post(parameter);
			}

			@Override
			protected void onPostExecute(SchoolResponse result) {
				if (!dialogCanceled) {
					dismissDialog(PROGRESS_DIALOG);

					if (result != null) {
						if (result.getStatus_code() != null
								&& result.getStatus_code().equals(1)) {
							if (adapter.getData() != null
									&& adapter.getData().length > 0
									&& result.getContent() != null
									&& result.getContent().length > 0) {
								School[] data = new School[adapter.getData().length
										+ result.getContent().length];
								System.arraycopy(adapter.getData(), 0, data, 0,
										adapter.getData().length);
								System.arraycopy(result.getContent(), 0, data,
										adapter.getData().length,
										result.getContent().length);
								adapter.setData(data);
							} else if (adapter.getData() == null
									|| adapter.getData().length == 0) {
								adapter.setData(result.getContent());
							} else {
								hasMore = false;
							}
						} else {
							hasMore = false;
						}
					} else {
						showToast("驾校查询出错，请检查你是否联网。");
					}
				}
				loading = false;
			}

		}.execute();
	}

	private void updateBtn(int i) {
		if (priceUp) {
			priceArrow.setImageResource(R.drawable.up);
		} else {
			priceArrow.setImageResource(R.drawable.down);
		}

		if (dateUp) {
			dateArrow.setImageResource(R.drawable.up);
		} else {
			dateArrow.setImageResource(R.drawable.down);
		}
		for (int j = 0; j < buttons.length; j++) {
			if (i == j) {
				buttons[j].setSelected(true);
			} else {
				buttons[j].setSelected(false);
			}
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.byPrice:
			priceUp = !priceUp;
			updateBtn(0);
			parameter.set(SEARCH_TYPE, ST_PRICE);
			parameter.set(PAGE_NUM, 0);
			parameter.set(ORDER_TYPE, priceUp ? ORDER_ASC : ORDER_DESC);
			hasMore = true;
			adapter.setData(new School[0]);
			querySchool();
			break;
		case R.id.byDate:
			dateUp = !dateUp;
			updateBtn(1);
			parameter.set(SEARCH_TYPE, ST_DATE);
			parameter.set(PAGE_NUM, 0);
			parameter.set(ORDER_TYPE, dateUp ? ORDER_ASC : ORDER_DESC);
			hasMore = true;
			adapter.setData(new School[0]);
			querySchool();
			break;
		case R.id.byArea:
			updateBtn(2);
			showDialog(1);
			break;
		case R.id.registerBtn:
			Intent i = new Intent(this, SignupActivity.class);
			this.startActivity(i);
			break;
		case R.id.rightbtn:
			Intent ic = new Intent(this, SchoolQueryInputActivity.class);
			this.startActivityForResult(ic, 1);
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				String keyword = data.getStringExtra("search.keyword");
				this.parameter.set(SEARCH_KEY, keyword);
				this.adapter.setData(new School[0]);
				this.querySchool();
				Log.d("SchoolQueryActivity", "keyword =" + keyword);
			}
			break;
		default:

			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public View createOrUpdateView(final School item, View convertView, int position,
			ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.school_item, null);
		TextView schoolname = (TextView) result.findViewById(R.id.schoolname);
		schoolname.setText(item.getName());
		TextView price = (TextView) result.findViewById(R.id.school_price);
		if (item.getPrice() != null & item.getPrice().length() > 0) {
			price.setText("￥" + item.getPrice());
		} else {
			price.setText("");
		}
		View zizhiwarp= result.findViewById(R.id.zizhiwarp);
		TextView zizhiStatus=(TextView) result.findViewById(R.id.zizhi_status);
		if(item.getZizhi_status()!=null&&item.getZizhi_status().trim().length()>0){
			zizhiStatus.setText(item.getZizhi_status());
		}else{
			zizhiwarp.setVisibility(View.GONE);
		}
		View changdi= result.findViewById(R.id.changdiwarp);
		TextView changdiStatus=(TextView) result.findViewById(R.id.changdi_status);
		if(item.getChangdi_status()!=null&&item.getChangdi_status().trim().length()>0){
			changdiStatus.setText(item.getChangdi_status());
		}else{
			changdi.setVisibility(View.GONE);
		}
		TextView phone = (TextView) result.findViewById(R.id.phone_num);
		phone.setText(item.getFphone() + " " + item.getMphone());
		TextView address = (TextView) result.findViewById(R.id.address);
		address.setText(item.getAddress());
		Button submit1=(Button)result.findViewById(R.id.submit_tg1);
		Button submit=(Button)result.findViewById(R.id.submit_tg);
		if(tg==1)
		{
			submit1.setVisibility(View.VISIBLE);
			submit.setVisibility(View.VISIBLE);
			OnClickListener l=new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent i = new Intent(SchoolQueryActivity.this, SignupActivity.class);
					i.putExtra("school",item);
					i.putExtra("tg", tg);
					startActivity(i);
				}
			};
			submit.setOnClickListener(l);
		}else{
			submit1.setVisibility(View.GONE);
			submit.setVisibility(View.GONE);
		}
		return result;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(this.tg==1){
			
		}else{
		Intent i = new Intent(this, SchoolDetailActivity.class);
		i.putExtra("school", (School) arg0.getItemAtPosition(arg2));
		startActivity(i);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dt.android.activity.BaseActivity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:// create select area dialog
			return createAreaDialog();
		}
		return super.onCreateDialog(id);
	}

	private Dialog createAreaDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_area);
		List<CountyCode> countyCodes = dbadapter.getLocationsDAO()
				.getCountyCodeByParent(
						AppData.getInstance().getCurrentLocation());
		final String area[] = new String[countyCodes.size() + 1];
		final Integer areaId[] = new Integer[countyCodes.size() + 1];
		area[0] = "全部区县";
		areaId[0] = AppData.getInstance().getCurrentLocation().getId();
		int i = 1;
		for (CountyCode c : countyCodes) {
			area[i] = c.getName();
			areaId[i++] = c.getId();
		}
		builder.setSingleChoiceItems(area, selectArea,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						selectAreaID = areaId[which];
						selectArea = which;
						removeDialog(1);
						parameter.set(CITY_ID, selectAreaID);
						parameter.set(SEARCH_TYPE, ST_DISTRICT);
						parameter.set(PAGE_NUM, 0);
						hasMore = true;
						adapter.setData(new School[0]);
						areaTxt.setText(area[which]);
						querySchool();
					}

				});

		return builder.create();
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;
		if ((lastInScreen == totalItemCount && totalItemCount != 0 && hasMore && !loading)) {
			this.parameter.set(PAGE_NUM, (Integer) parameter.get(PAGE_NUM) + 1);
			loading = true;
			this.querySchool();
		}

	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dt.android.activity.BaseActivity#getProgressMessage()
	 */
	@Override
	protected String getProgressMessage() {
		return "驾校加载中，请稍等.";
	}

}
