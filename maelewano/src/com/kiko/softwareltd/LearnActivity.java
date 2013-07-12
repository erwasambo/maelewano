package com.kiko.softwareltd;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class LearnActivity extends Activity{

	private int currentPic = 0;
	//gallery object
	private Gallery picGallery;

	Integer[] pics = {
			R.drawable.a,
			R.drawable.b,
			R.drawable.c,
			R.drawable.d,
			R.drawable.e,
			R.drawable.f,
			R.drawable.g,
			R.drawable.h,
			R.drawable.i,
			R.drawable.j,
			R.drawable.k,
			R.drawable.l,
			R.drawable.m,
			R.drawable.n,
			R.drawable.o,
			R.drawable.p,
			R.drawable.q,
			R.drawable.r,
			R.drawable.s,
			R.drawable.t,
			R.drawable.u,
			R.drawable.v,
			R.drawable.w,
			R.drawable.x,
			R.drawable.y,
			R.drawable.z,
			R.drawable.zero,
			R.drawable.one,
			R.drawable.two,
			R.drawable.three,
			R.drawable.four,
			R.drawable.five,
			R.drawable.six,
			R.drawable.seven,
			R.drawable.eight,
			R.drawable.nine,
			R.drawable.ten,
			R.drawable.nyama,
			R.drawable.uji,
			R.drawable.chai,
			R.drawable.mwalimu,
			R.drawable.kushiba,
			R.drawable.ugali,
			R.drawable.milk,
			R.drawable.maji,
			R.drawable.beans,
			R.drawable.njaa,
			R.drawable.mchele,
			R.drawable.kuku,			R.drawable.m,
			R.drawable.kuni,
			R.drawable.kupika,
			R.drawable.mbaya,
			R.drawable.mahindi,
			R.drawable.chafu,
			R.drawable.shule,
			R.drawable.safai,
			R.drawable.seremala,
			R.drawable.cow,
			R.drawable.good,
			R.drawable.tailor,
			R.drawable.nyumbani,
			R.drawable.kaka,
			R.drawable.jamii,
			R.drawable.baba,
			R.drawable.luelewa,
			R.drawable.dada,
			R.drawable.jizuie,
			R.drawable.ukimwi,
			R.drawable.damu,
			R.drawable.mama,
			R.drawable.lini,
			R.drawable.kuhara,
			R.drawable.huzuni,
			R.drawable.choo,
			R.drawable.tumbo,
			R.drawable.kifo,
			R.drawable.dawa,
			R.drawable.furaha,
			R.drawable.maumivu,
			R.drawable.nini,
			R.drawable.kwanini,
			R.drawable.malaria,
			R.drawable.afya,
			R.drawable.mgonjwa

			};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learnsign);
		
		picGallery = (Gallery)findViewById(R.id.gallery909);
		picGallery.setAdapter(new ImageAdapter(this));
		//get the gallery view

	}
	public class ImageAdapter extends BaseAdapter {


		private Context ctx;
		int imageBackground;


		public ImageAdapter(Context c) {
		    ctx = c;
		    TypedArray ta = obtainStyledAttributes(R.styleable.PicGallery);
		    imageBackground = ta.getResourceId(R.styleable.PicGallery_android_galleryItemBackground, 1);
		    ta.recycle();
		}


		@Override
		public int getCount() {

			return pics.length;
		}


		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}


		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = new ImageView(ctx);
		    iv.setImageResource(pics[position]);
		    iv.setScaleType(ImageView.ScaleType.FIT_XY);
		    iv.setLayoutParams(new Gallery.LayoutParams(400,320));
		    iv.setBackgroundResource(imageBackground);
			return iv;
		}
		

	}


}
