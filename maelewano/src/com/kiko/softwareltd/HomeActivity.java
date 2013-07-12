package com.kiko.softwareltd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class HomeActivity extends Activity {
	Button exit;
	ImageButton cache;
	Button interpret;
	ImageButton viewlib;
	ImageView addgesture;
	ImageView viewgesture;
	ImageView interpretgesture;
	ImageView settings;
	ImageView exitimg;
	ImageView learn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		addgesture = (ImageView)findViewById(R.id.imageView1);
		addgesture.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	startActivity(new Intent("com.kiko.softwareltd.addgesture"));
	        }
	    });
		
		viewgesture = (ImageView)findViewById(R.id.ImageView04);
		viewgesture.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	startActivity(new Intent("com.kiko.softwareltd.gestures_library"));
	        }
	    });
		
		interpretgesture = (ImageView)findViewById(R.id.ImageView21);
		interpretgesture.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	startActivity(new Intent("com.kiko.softwareltd.camera_layout"));
	        }
	    });
		
		learn = (ImageView)findViewById(R.id.ImageView701);
		learn.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	startActivity(new Intent("com.kiko.softwareltd.learnsign"));
	        }
	    });
		
		settings = (ImageView)findViewById(R.id.ImageView02);
		settings.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	startActivity(new Intent("com.kiko.softwareltd.settings"));
	        }
	    });
		
		exitimg = (ImageView)findViewById(R.id.ImageView03);
		exitimg.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	finish();
	        }
	    });
	}
}
