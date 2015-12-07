package com.dt.android.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.dt.android.R;

public class VedioWatchActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.vedio_detail);
		VideoView videoView = (VideoView)this.findViewById(R.id.video_view);
		videoView.setMediaController(new MediaController(this));
		Uri uri = Uri.parse(this.getIntent().getStringExtra("uri"));	
		videoView.setVideoURI(uri);
		videoView.start();
		videoView.requestFocus();
	}
	

}
