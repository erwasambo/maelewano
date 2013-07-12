package com.kiko.softwareltd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
public class GesturesLibraryActivity extends Activity{
	//variable for selection intent
	private final int PICKER = 1;
	//variable to store the currently selected image
	
	//private int currentPic = 0;
	private int currentPic = 0;
	//gallery object
	private Gallery picGallery;
	//image view for larger display
	//private ImageView picView;
	//adapter for gallery view
	private PicAdapter imgAdapt;
	String filename;
	
	ArrayList<String> filenames = new ArrayList<String>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.gestures_library);
		
		//get the gallery view
		picGallery = (Gallery) findViewById(R.id.gallery);
		
		//create a new adapter
		imgAdapt = new PicAdapter(this);
		
		
/*		
		Thread Timer = new Thread(){
        	public void run(){
        		      			
        			imgAdapt = new PicAdapter(getApplicationContext());
        			picGallery.setAdapter(imgAdapt);
        	}
        };
        Timer.start();*/
		//set the gallery adapter
		picGallery.setAdapter(imgAdapt);
		
		String currentvalue = filenames.get(0).replace(".jpg", "").toLowerCase();
    	
    	TextView meaningView = (TextView)findViewById(R.id.gesturemeaning2);
    	meaningView.setText(currentvalue);
    	
    	picGallery.setOnItemClickListener(new OnItemClickListener() {
		    //handle clicks
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		        //set the larger image view to display the chosen bitmap calling method of adapter class
		    	imgAdapt.getView(position, v, parent);
		    	
		    	String currentvalue = filenames.get(position).replace(".jpg", "").toLowerCase();
		    	
		    	TextView meaningView = (TextView)findViewById(R.id.gesturemeaning2);
		    	meaningView.setText(currentvalue);
		    	//picView.setImageBitmap(imgAdapt.getPic(position));
		    }
		});
    			
	}
	
	public class PicAdapter extends BaseAdapter {
		//use the default gallery background image
		int defaultItemBackground;
		//gallery context
		private Context galleryContext;
		//array to store bitmaps to display
		private Bitmap[] imageBitmaps;
		//placeholder bitmap for empty spaces in gallery
		Bitmap placeholder;
		
		
		public PicAdapter(Context c) {
		    //instantiate context
		    galleryContext = c;
		    
		    //path name
		    String imagepath = "/sdcard/maelewano/";
		    File directory = new File(imagepath);

		    File [] allphotos = directory.listFiles();

		    int nooffiles = allphotos.length;

		    if (directory.isDirectory()){
		    	String photofiles []= directory.list();
		    	//create bitmap array
			    imageBitmaps  = new Bitmap[nooffiles];
			    //for (int i = 0; i<photofiles.length; i++){
		    	for (int i = 0; i<nooffiles; i++){
		    		try {
						filename = photofiles[i];
						placeholder = BitmapFactory.decodeFile(imagepath
								+ filename);
						filenames.add(filename);
						
						imageBitmaps[i] = placeholder;
					} catch (Exception e) {
						
					}finally{
						//placeholder.recycle();
					}
		    		
		    	}
		    	
		    }

		    //get the styling attributes - use default Andorid system resources
		    TypedArray styleAttrs = galleryContext.obtainStyledAttributes(R.styleable.PicGallery);
		    //get the background resource
		    defaultItemBackground = styleAttrs.getResourceId(
		        R.styleable.PicGallery_android_galleryItemBackground, 0);
		    //recycle attributes
		    styleAttrs.recycle();
		    
		    
		}
		
		@Override
		public int getCount() {
			return imageBitmaps.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		//get view specifies layout and display options for each thumbnail in the gallery
		public View getView(int position, View convertView, ViewGroup parent) {
		    //create the view
		    ImageView imageView = new ImageView(galleryContext);
		    //specify the bitmap at this position in the array
		    imageView.setImageBitmap(imageBitmaps[position]);
		    //set layout options
		    imageView.setLayoutParams(new Gallery.LayoutParams(300, 200));
		    //imageView.setLayoutParams(new Gallery.LayoutParams(600, 400));
		    //scale type within view area
		    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		    //set default gallery item background
		    imageView.setBackgroundResource(defaultItemBackground);
		    //return the view
		    return imageView;
		}
		
		//helper method to add a bitmap to the gallery when the user chooses one
		public void addPic(Bitmap newPic){
		    //set at currently selected index
		    imageBitmaps[currentPic] = newPic;
		}
		
		//return bitmap at specified position for larger display
		public Bitmap getPic(int posn)
		{
		    //return bitmap at posn index
		    return imageBitmaps[posn];
		}
		
	}
}
