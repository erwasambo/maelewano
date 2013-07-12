package com.kiko.softwareltd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

public class AddgestureActivity extends Activity{
    private Bitmap bmp = null;
    ProgressDialog pg=null;
    ProgressDialog pg2=null;
    private Preview preview;
    String filename;
    Button ok_button,click_button,try_again_button;
    private byte[] data2=null;
    EditText filenameBox;
    Cursor cursor;
    private DataBaseManager DatabaseHelpers;
    String table = "general";
    

	int red0; int red8 ; int red16 ; int red24 ; 
	int red33 ; int red41 ; int red49 ; int red57 ;
	 int red66 ; int red74 ; int red82 ; int red90 ; 
	int red99 ; int red107 ; int red115 ; int red123 ;
	 int red132 ; int red140 ; int red148 ; int red156 ; 
	int red165 ; int red173 ; int red181 ; int red189 ;
	 int red198 ; int red206 ; int red214 ; int red222 ;
	 int red231 ; int red239 ; int red247 ; int red255; 
	 
	 int   green0 ; int green4 ; int green8 ; int green12 ; int green16 ; 
	int green20 ; int green24 ; int green28 ; int green32 ;
	 int green36 ; int green40 ; int green44 ; int green48 ;
	 int green52 ; int green56 ; int green60 ; int green65 ;
	 int green69 ; int green73 ; int green77 ; int green81 ; 
	int green85 ; int green89 ; int green93 ; int green97 ; 
	int green101 ; int green105 ; int green109 ; 
	int green113 ; int green117 ; int green121 ; int  green125 ; 
	int green130 ; int green134 ; int green138 ; int green142 ;
	 int green146 ; int green150 ; int green154 ; int green158 ; 
	int green162 ; int green166 ; int green170 ; int green174 ; 
	int green178 ; int green182 ; int green186 ; int green190 ;
	 int green195 ; int green199 ; int green203 ; int green207 ; 
	int green211 ; int green215 ; int green219 ; int  green223 ; 
	int green227 ; int green231 ; int green235 ; int green239;
	 int green243 ; int green247 ; int green251 ; int  green255 ;
	 
	 int    blue0 ; int blue8 ; int blue16 ; int blue24 ; int blue33 ;
	 int blue41 ; int blue49 ; int blue57 ; int blue66 ; int blue74 ; 
	int blue82 ; int blue90 ; int blue99 ; int blue107 ; int blue115 ;
	 int blue123 ; int blue132 ; int blue140 ; int blue148 ; int blue156 ; 
	int blue165 ; int blue173 ; int blue181 ; int blue189 ; int blue198 ;
	 int blue206 ; int blue214 ; int blue222 ; int blue231 ; int blue239 ;
	 int blue247 ; int blue255 ;

	
	
	String length;
	String width;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addgesture);
		
		//Create and open the database so we can use it
		DatabaseHelpers = DataBaseManager.instance();
		
		preview=new Preview(this);
        ((FrameLayout) findViewById(R.id.preview2)).addView(preview);
        findViewById(R.id.preview2).setVisibility(View.VISIBLE);
        findViewById(R.id.img2).setVisibility(View.GONE);
 
        ButtonListener listener=new ButtonListener();
        click_button=((Button) findViewById(R.id.capture_button2));
        click_button.setVisibility(View.VISIBLE);
        click_button.setOnClickListener(listener);
 
        try_again_button=((Button) findViewById(R.id.recapture_button2));
        try_again_button.setVisibility(View.GONE);
        try_again_button.setOnClickListener(listener);
 
        ok_button=((Button) findViewById(R.id.ok_button2));
        ok_button.setVisibility(View.GONE);
        ok_button.setOnClickListener(listener);
	}
	
	private class ButtonListener implements View.OnClickListener{
		 
        @Override
        public void onClick(View v) {
            if(v.equals(findViewById(R.id.capture_button2))){
                pg=ProgressDialog.show(AddgestureActivity.this, null, "Capturing Image..");
                pg.show();
                preview.camera.takePicture(null, null, jpegCallback);

                
            }else if(v.equals(findViewById(R.id.ok_button2))){

            	//process and save image
                saveImage();
                bmp.recycle();
            }else if(v.equals(findViewById(R.id.recapture_button2))){
            	
                findViewById(R.id.img2).setVisibility(View.GONE);
                findViewById(R.id.preview2).setVisibility(View.VISIBLE);
                bmp.recycle();
                preview.camera.startPreview();
                ok_button.setVisibility(View.GONE);
                try_again_button.setVisibility(View.GONE);
                click_button.setVisibility(View.VISIBLE);

            }
        }
 
    }
 
    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            //Log.d(TAG, "onShutter'd");
            System.out.println("In ShutterCallback");
        }
    };
 
    /** Handles data for raw picture */
    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            if(data!=null){
                bmp=BitmapFactory.decodeByteArray(data,0,data.length);
                findViewById(R.id.img2).setVisibility(View.VISIBLE);
                ((ImageView)findViewById(R.id.img2)).setImageBitmap(bmp);
                findViewById(R.id.preview2).setVisibility(View.GONE);
                
                 if(pg!=null)
                    pg.dismiss();
                ok_button.setVisibility(View.VISIBLE);
                click_button.setVisibility(View.GONE);
                try_again_button.setVisibility(View.VISIBLE);
            }
        }
    };
    /** Handles data for jpeg picture */
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        	//lengthnwidth(bmp);
        	//Toast.makeText(getApplicationContext(), "The image length is : "+length+" and width is: "+width, Toast.LENGTH_LONG).show();
            if(data!=null){
                bmp=BitmapFactory.decodeByteArray(data,0,data.length);
                processimage();
                lengthnwidth(bmp);
            	Toast.makeText(getApplicationContext(), "The image length is : "+length+" and width is: "+width, Toast.LENGTH_LONG).show();
                findViewById(R.id.img2).setVisibility(View.VISIBLE);
                ((ImageView)findViewById(R.id.img2)).setImageBitmap(bmp);
                findViewById(R.id.preview2).setVisibility(View.GONE);
        		
                data2 = data;
                
                if(pg!=null)
                    pg.dismiss();
                ok_button.setVisibility(View.VISIBLE);
                click_button.setVisibility(View.GONE);
                try_again_button.setVisibility(View.VISIBLE);
                
                
                try {
                	
                } catch (Exception error) {
                  
                }finally{
                	
                	
                }
            }
        	
        }
    };
    
    
    private File getDir() {
        File sdDir = Environment.getExternalStorageDirectory();
 
        return new File(sdDir, "Maelewano");
    }
    
    
    public void saveImage(){

    	//Preparing views
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialogue, (ViewGroup) findViewById(R.id.dialog1));

        filenameBox = (EditText) layout.findViewById(R.id.editText1);

        //Building dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //save info where you want it
                confirmedsave();

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        
    	dialog.show();
    	    	        
    }
    
    public void confirmedsave(){
    	
        filename = filenameBox.getText().toString();
    	File pictureFileDir = getDir();
    	    	
        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

          Toast.makeText(getApplicationContext(), "Can't create directory to save image.",
              Toast.LENGTH_LONG).show();
          return;

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());

        String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();

        String interpretImg = ExternalStorageDirectoryPath + "/maelewano/" + filename + ".jpg";
        File imageFile = new File(interpretImg);
        //copyDatabaseToSdCard();
        try {
          if (data2!=null){
          //update the database with calculated values
      	  updatemaelewanodb();
          
          FileOutputStream fos = new FileOutputStream(imageFile);
          fos.write(data2);
          fos.close();

          Toast.makeText(getApplicationContext(), "New Image saved:" + interpretImg,
              Toast.LENGTH_LONG).show();
          

        }

        } catch (Exception error) {
          
        }
        
       
        findViewById(R.id.img2).setVisibility(View.GONE);
        findViewById(R.id.preview2).setVisibility(View.VISIBLE);
        preview.camera.startPreview();
        ok_button.setVisibility(View.GONE);
        try_again_button.setVisibility(View.GONE);
        click_button.setVisibility(View.VISIBLE);
    }
    

    
 // Return an ArrayList containing histogram values for separate R, G, B channels
      public static ArrayList<int[]> imageHistogram(Bitmap input) {
        int[] rhistogram = new int[256];
        int[] ghistogram = new int[256];
        int[] bhistogram = new int[256];
		
		int pixel;
		
        for(int i=0; i<rhistogram.length; i++) rhistogram[i] = 0;
        for(int i=0; i<ghistogram.length; i++) ghistogram[i] = 0;
        for(int i=0; i<bhistogram.length; i++) bhistogram[i] = 0;
     
        for(int i=0; i<input.getWidth(); i++) {
            for(int j=0; j<input.getHeight(); j++) {
				//get one pixel color
				pixel = input.getPixel(i, j);
				
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                
                // Increase the values of colors
                rhistogram[red]++; ghistogram[green]++; bhistogram[blue]++;

            }
        }
        ArrayList<int[]> hist = new ArrayList<int[]>();
         
        hist.add (rhistogram);
        hist.add(ghistogram);
        hist.add(bhistogram);
        
        return hist;
     
    }
    
    
    public void updatemaelewanodb(){

    	   	
    	String column2 = "meaning";
    	String red0column = "red0";
    	String red8column = "red8";
    	String red16column = "red16";
    	String red24column = "red24";
    	String red33column = "red33";
    	String red41column = "red41";
    	String red49column = "red49";
    	String red57column = "red57";
    	String red66column = "red66";
    	String red74column = "red74";
    	String red82column = "red82";
    	String red90column = "red90";
    	String red99column = "red99";
    	String red107column = "red107";
    	String red115column = "red115";
    	String red123column = "red123";
    	String red132column = "red132";
    	String red140column = "red140";
    	String red148column = "red148";
    	String red156column = "red156";
    	String red165column = "red165";
    	String red173column = "red173";
    	String red181column = "red181";
    	String red189column = "red189";
    	String red198column = "red198";
    	String red206column = "red206";
    	String red214column = "red214";
    	String red222column = "red222";
    	String red231column = "red231";
    	String red239column = "red239";
    	String red247column = "red247";
    	String red255column = "red255";

    	
    	String green0column = "green0";
    	String green4column = "green4";
    	String green8column = "green8";
    	String green12column = "green12";
    	String green16column = "green16";
    	String green20column = "green20";
    	String green24column = "green24";
    	String green28column = "green28";
    	String green32column = "green32";
    	String green36column = "green36";
    	String green40column = "green40";
    	String green44column = "green44";
    	String green48column = "green48";
    	String green52column = "green52";
    	String green56column = "green56";
    	String green60column = "green60";
    	String green65column = "green65";
    	String green69column = "green69";
    	String green73column = "green73";
    	String green77column = "green77";
    	String green81column = "green81";
    	String green85column = "green85";
    	String green89column = "green89";
    	String green93column = "green93";
    	String green97column = "green97";
    	String green101column = "green101";
    	String green105column = "green105";
    	String green109column = "green109";
    	String green113column = "green113";
    	String green117column = "green117";
    	String green121column = "green121";
    	String green125column = "green125";
    	String green130column = "green130";
    	String green134column = "green134";
    	String green138column = "green138";
    	String green142column = "green142";
    	String green146column = "green146";
    	String green150column = "green150";
    	String green154column = "green154";
    	String green158column = "green158";
    	String green162column = "green162";
    	String green166column = "green166";
    	String green170column = "green170";
    	String green174column = "green174";
    	String green178column = "green178";
    	String green182column = "green182";
    	String green186column = "green186";
    	String green190column = "green190";
    	String green195column = "green195";
    	String green199column = "green199";
    	String green203column = "green203";
    	String green207column = "green207";
    	String green211column = "green211";
    	String green215column = "green215";
    	String green219column = "green219";
    	String green223column = "green223";
    	String green227column = "green227";
    	String green231column = "green231";
    	String green235column = "green235";
    	String green239column = "green239";
    	String green243column = "green243";
    	String green247column = "green247";
    	String green251column = "green251";
    	String green255column = "green255";


    	String  blue0column = " blue0";
    	String  blue8column = " blue8";
    	String  blue16column = " blue16";
    	String  blue24column = " blue24";
    	String  blue33column = " blue33";
    	String  blue41column = " blue41";
    	String  blue49column = " blue49";
    	String  blue57column = " blue57";
    	String  blue66column = " blue66";
    	String  blue74column = " blue74";
    	String  blue82column = " blue82";
    	String  blue90column = " blue90";
    	String  blue99column = " blue99";
    	String  blue107column = " blue107";
    	String  blue115column = " blue115";
    	String  blue123column = " blue123";
    	String  blue132column = " blue132";
    	String  blue140column = " blue140";
    	String  blue148column = " blue148";
    	String  blue156column = " blue156";
    	String  blue165column = " blue165";
    	String  blue173column = " blue173";
    	String  blue181column = " blue181";
    	String  blue189column = " blue189";
    	String  blue198column = " blue198";
    	String  blue206column = " blue206";
    	String  blue214column = " blue214";
    	String  blue222column = " blue222";
    	String  blue231column = " blue231";
    	String  blue239column = " blue239";
    	String  blue247column = " blue247";
    	String  blue255column = " blue255";

    	// Create a new map of values, where column names are the keys
    	ContentValues maelewanovalues = new ContentValues();

    	maelewanovalues.put(column2 , filename);

    	maelewanovalues.put(red0column, red0);
    	maelewanovalues.put(red8column, red8);
    	maelewanovalues.put(red16column, red16);
    	maelewanovalues.put(red24column, red24);
    	maelewanovalues.put(red33column, red33);
    	maelewanovalues.put(red41column, red41);
    	maelewanovalues.put(red49column, red49);
    	maelewanovalues.put(red57column, red57);
    	maelewanovalues.put(red66column, red66);
    	maelewanovalues.put(red74column, red74);
    	maelewanovalues.put(red82column, red82);
    	maelewanovalues.put(red90column, red90);
    	maelewanovalues.put(red99column, red99);
    	maelewanovalues.put(red107column, red107);
    	maelewanovalues.put(red115column, red115);
    	maelewanovalues.put(red123column, red123);
    	maelewanovalues.put(red132column, red132);
    	maelewanovalues.put(red140column, red140);
    	maelewanovalues.put(red148column, red148);
    	maelewanovalues.put(red156column, red156);
    	maelewanovalues.put(red165column, red165);
    	maelewanovalues.put(red173column, red173);
    	maelewanovalues.put(red181column, red181);
    	maelewanovalues.put(red189column, red189);
    	maelewanovalues.put(red198column, red198);
    	maelewanovalues.put(red206column, red206);
    	maelewanovalues.put(red214column, red214);
    	maelewanovalues.put(red222column, red222);
    	maelewanovalues.put(red231column, red231);
    	maelewanovalues.put(red239column, red239);
    	maelewanovalues.put(red247column, red247);
    	maelewanovalues.put(red255column, red255);


    	maelewanovalues.put(green0column, green0);
    	maelewanovalues.put(green4column, green4);
    	maelewanovalues.put(green8column, green8);
    	maelewanovalues.put(green12column, green12);
    	maelewanovalues.put(green16column, green16);
    	maelewanovalues.put(green20column, green20);
    	maelewanovalues.put(green24column, green24);
    	maelewanovalues.put(green28column, green28);
    	maelewanovalues.put(green32column, green32);
    	maelewanovalues.put(green36column, green36);
    	maelewanovalues.put(green40column, green40);
    	maelewanovalues.put(green44column, green44);
    	maelewanovalues.put(green48column, green48);
    	maelewanovalues.put(green52column, green52);
    	maelewanovalues.put(green56column, green56);
    	maelewanovalues.put(green60column, green60);
    	maelewanovalues.put(green65column, green65);
    	maelewanovalues.put(green69column, green69);
    	maelewanovalues.put(green73column, green73);
    	maelewanovalues.put(green77column, green77);
    	maelewanovalues.put(green81column, green81);
    	maelewanovalues.put(green85column, green85);
    	maelewanovalues.put(green89column, green89);
    	maelewanovalues.put(green93column, green93);
    	maelewanovalues.put(green97column, green97);
    	maelewanovalues.put(green101column, green101);
    	maelewanovalues.put(green105column, green105);
    	maelewanovalues.put(green109column, green109);
    	maelewanovalues.put(green113column, green113);
    	maelewanovalues.put(green117column, green117);
    	maelewanovalues.put(green121column, green121);
    	maelewanovalues.put(green125column, green125);
    	maelewanovalues.put(green130column, green130);
    	maelewanovalues.put(green134column, green134);
    	maelewanovalues.put(green138column, green138);
    	maelewanovalues.put(green142column, green142);
    	maelewanovalues.put(green146column, green146);
    	maelewanovalues.put(green150column, green150);
    	maelewanovalues.put(green154column, green154);
    	maelewanovalues.put(green158column, green158);
    	maelewanovalues.put(green162column, green162);
    	maelewanovalues.put(green166column, green166);
    	maelewanovalues.put(green170column, green170);
    	maelewanovalues.put(green174column, green174);
    	maelewanovalues.put(green178column, green178);
    	maelewanovalues.put(green182column, green182);
    	maelewanovalues.put(green186column, green186);
    	maelewanovalues.put(green190column, green190);
    	maelewanovalues.put(green195column, green195);
    	maelewanovalues.put(green199column, green199);
    	maelewanovalues.put(green203column, green203);
    	maelewanovalues.put(green207column, green207);
    	maelewanovalues.put(green211column, green211);
    	maelewanovalues.put(green215column, green215);
    	maelewanovalues.put(green219column, green219);
    	maelewanovalues.put(green223column, green223);
    	maelewanovalues.put(green227column, green227);
    	maelewanovalues.put(green231column, green231);
    	maelewanovalues.put(green235column, green235);
    	maelewanovalues.put(green239column, green239);
    	maelewanovalues.put(green243column, green243);
    	maelewanovalues.put(green247column, green247);
    	maelewanovalues.put(green251column, green251);
    	maelewanovalues.put(green255column, green255);

    	
    	
    	
    	maelewanovalues.put(blue0column, blue0);
    	maelewanovalues.put(blue8column, blue8);
    	maelewanovalues.put(blue16column, blue16);
    	maelewanovalues.put(blue24column, blue24);
    	maelewanovalues.put(blue33column, blue33);
    	maelewanovalues.put(blue41column, blue41);
    	maelewanovalues.put(blue49column, blue49);
    	maelewanovalues.put(blue57column, blue57);
    	maelewanovalues.put(blue66column, blue66);
    	maelewanovalues.put(blue74column, blue74);
    	maelewanovalues.put(blue82column, blue82);
    	maelewanovalues.put(blue90column, blue90);
    	maelewanovalues.put(blue99column, blue99);
    	maelewanovalues.put(blue107column, blue107);
    	maelewanovalues.put(blue115column, blue115);
    	maelewanovalues.put(blue123column, blue123);
    	maelewanovalues.put(blue132column, blue132);
    	maelewanovalues.put(blue140column, blue140);
    	maelewanovalues.put(blue148column, blue148);
    	maelewanovalues.put(blue156column, blue156);
    	maelewanovalues.put(blue165column, blue165);
    	maelewanovalues.put(blue173column, blue173);
    	maelewanovalues.put(blue181column, blue181);
    	maelewanovalues.put(blue189column, blue189);
    	maelewanovalues.put(blue198column, blue198);
    	maelewanovalues.put(blue206column, blue206);
    	maelewanovalues.put(blue214column, blue214);
    	maelewanovalues.put(blue222column, blue222);
    	maelewanovalues.put(blue231column, blue231);
    	maelewanovalues.put(blue239column, blue239);
    	maelewanovalues.put(blue247column, blue247);
    	maelewanovalues.put(blue255column, blue255);

    	   	
    	DatabaseHelpers.insert(table, maelewanovalues);
    	copyDatabaseToSdCard();
    }
    
    public void lengthnwidth(Bitmap bmp){
    	length = ""+bmp.getHeight();
    	width = ""+bmp.getWidth();
    	
    }
    
    public void processimage(){

   			ArrayList<int[]> drawnHistList = imageHistogram(bmp);
   	   		
   	   		int[] redhisto = drawnHistList.get(0);
   	   		int[] greenhisto = drawnHistList.get(1);
   	   		int[] bluehisto = drawnHistList.get(2);
   	   		
   	   	
   	   	red0 = redhisto[0];red8 = redhisto[8]; red16 = redhisto[16];red24 = redhisto[24];
    	 red33  = redhisto[33];red41  = redhisto[41];red49  = redhisto[49];red57 = redhisto[57];
   	  red66  = redhisto[66];red74  = redhisto[74];red82  = redhisto[82];red90 = redhisto[90]; 
   	 red99  = redhisto[99];red107  = redhisto[107];red115  = redhisto[115];red123 = redhisto[123];
   	  red132  = redhisto[132];red140  = redhisto[140];red148  = redhisto[148];red156 = redhisto[156]; 
   	 red165  = redhisto[165];red173  = redhisto[173];red181  = redhisto[181];red189 = redhisto[189];
   	  red198  = redhisto[198];red206  = redhisto[206];red214  = redhisto[214];red222 = redhisto[222];
   	  red231  = redhisto[231];red239  = redhisto[239];red247  = redhisto[247]; red255  = redhisto[255];

    green0 = greenhisto[0];  green4  = greenhisto[4];green8  = greenhisto[8];green12  = greenhisto[12];green16 = greenhisto[16]; 
   	 green20  = greenhisto[20];green24  = greenhisto[24];green28  = greenhisto[28];green32 = greenhisto[32];
   	  green36  = greenhisto[36];green40  = greenhisto[40];green44  = greenhisto[44];green48 = greenhisto[48];
   	  green52  = greenhisto[52];green56  = greenhisto[56];green60  = greenhisto[60];green65 = greenhisto[65];
   	  green69  = greenhisto[69];green73  = greenhisto[73];green77  = greenhisto[77];green81 = greenhisto[81]; 
   	 green85  = greenhisto[85];green89  = greenhisto[89];green93  = greenhisto[93];green97 = greenhisto[97]; 
   	 green101  = greenhisto[101];green105  = greenhisto[105];green109 = greenhisto[109]; 
   	 green113  = greenhisto[113];green117  = greenhisto[117];green121  = greenhisto[121]; green125 = greenhisto[125]; 
   	 green130  = greenhisto[130];green134  = greenhisto[134];green138  = greenhisto[138];green142 = greenhisto[142];
   	  green146  = greenhisto[146];green150  = greenhisto[150];green154  = greenhisto[154];green158 = greenhisto[158]; 
   	 green162  = greenhisto[162];green166  = greenhisto[166];green170  = greenhisto[170];green174 = greenhisto[174]; 
   	 green178  = greenhisto[178];green182  = greenhisto[182];green186  = greenhisto[186];green190 = greenhisto[190];
   	  green195  = greenhisto[195];green199  = greenhisto[199];green203  = greenhisto[203];green207 = greenhisto[207]; 
   	 green211  = greenhisto[211];green215  = greenhisto[215];green219  = greenhisto[219]; green223 = greenhisto[223]; 
   	 green227  = greenhisto[227];green231  = greenhisto[231];green235  = greenhisto[235];green239= greenhisto[239];
   	  green243  = greenhisto[243];green247  = greenhisto[247];green251  = greenhisto[251]; green255 = greenhisto[255]; 

   blue0  = bluehisto[0];blue8  = bluehisto[8];blue16  = bluehisto[16];blue24  = bluehisto[24];blue33 = bluehisto[33];
   blue41  = bluehisto[41];blue49  = bluehisto[49];blue57  = bluehisto[57];blue66  = bluehisto[66];blue74 = bluehisto[74]; 
   blue82  = bluehisto[82];blue90  = bluehisto[90];blue99  = bluehisto[99];blue107  = bluehisto[107];blue115 = bluehisto[115];
   blue123  = bluehisto[123];blue132  = bluehisto[132];blue140  = bluehisto[140];blue148  = bluehisto[148];blue156 = bluehisto[156]; 
   blue165  = bluehisto[165];blue173  = bluehisto[173];blue181  = bluehisto[181];blue189  = bluehisto[189];blue198 = bluehisto[198];
   blue206  = bluehisto[206];blue214  = bluehisto[214];blue222  = bluehisto[222];blue231  = bluehisto[231];blue239  = bluehisto[239];
   blue247 = bluehisto[247]; blue255 = bluehisto[255];
/*   for (int i=0; i < redhisto.length; ++i){
			int value = redhisto[i];
			
			Log.e("Red value at "+ i, " is " +value);
			}
	for (int i=0; i < greenhisto.length; ++i){
	int value = greenhisto[i];
	
	Log.e("Green value at "+ i, " is " +value);
	}
	for (int i=0; i < bluehisto.length; ++i){
			int value = bluehisto[i];
  		
			Log.e("Blue value at "+ i, " is " +value);
			}*/
}

    public void copyDatabaseToSdCard() {
		Log.e("Databasehealper", "********************************");
		try {
			File f1 = new File("/data/data/com.kiko.softwareltd/databases/maelewanodb");
			if (f1.exists()) {

				File f2 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/My_App_Database.db");
				f2.createNewFile();
				InputStream in = new FileInputStream(f1);
				OutputStream out = new FileOutputStream(f2);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		Log.e("Databasehealper", "********************************");
	}

}
