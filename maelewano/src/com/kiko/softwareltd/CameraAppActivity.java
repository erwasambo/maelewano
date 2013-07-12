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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraAppActivity extends Activity{
    private Bitmap bmp=null;
    ProgressDialog pg=null;
    private Preview preview;

    Cursor cursor;
    ArrayList<int[]> drawnHistList;
  


	private DataBaseManager DatabaseHelpers;
	String length;
	String width;
	Thread Timer;
	ImageView startbutton;
	ImageView stopbutton;
	SQLiteDatabase myDataBase;
    String mango;
	EditText interboard;
    //ArrayList<int[]> drawnHistList;
    //private boolean interpreting = false;
	double percentagelow = 0.4;
	double percentagehigh = 1.6;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.camera_layout);
      
        //Create and open the database so we can use it
      	DatabaseHelpers = DataBaseManager.instance();
      	myDataBase = DatabaseHelpers.getWritableDatabase();
      	interboard =  (EditText)findViewById(R.id.editText555);
        preview=new Preview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);
        findViewById(R.id.preview).setVisibility(View.VISIBLE);
        
        startbutton = (ImageView)findViewById(R.id.imageView670);
        startbutton.setVisibility(View.VISIBLE);
        startbutton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	stopbutton.setVisibility(View.VISIBLE);
	        	startbutton.setVisibility(View.GONE);
	        	takepictures();
 
	        }
	    });
        
        stopbutton = (ImageView)findViewById(R.id.ImageView671);
        stopbutton.setVisibility(View.GONE);
        stopbutton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
                findViewById(R.id.preview).setVisibility(View.VISIBLE);
                
                stopbutton.setVisibility(View.GONE);
                //startbutton.setVisibility(View.VISIBLE);
            	//finish();
                preview.camera.release();
                startActivity(new Intent("com.kiko.softwareltd.home"));
	        }
	    });

  
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

                if(pg!=null)
                    pg.dismiss();
                
                startbutton.setVisibility(View.GONE);
                stopbutton.setVisibility(View.VISIBLE);
            }
        }
    };
    /** Handles data for jpeg picture */
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            if(data!=null){
                bmp=BitmapFactory.decodeByteArray(data,0,data.length);
                 
                if(pg!=null)
                    pg.dismiss();
                
                startbutton.setVisibility(View.GONE);
                stopbutton.setVisibility(View.VISIBLE);
                
                File pictureFileDir = getDir();

                if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

                  return;

                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                String date = dateFormat.format(new Date());

                try {
                	drawhisto(bmp);
                	//preloadvalues();
                	//databaseinsert();
                	//updatemaelewanodb();
                	//copyDatabaseToSdCard();

                    		mango = comparefiguresfromdb(drawnHistList);
                    		 
                    		if (mango.length()==1){
                    			interboard.append(mango);
                    		}else{
                    			interboard.append(mango+" ");
                    		}
                	
                } catch (Exception error) {
                  
                }finally{

                	bmp.recycle();
                	preview.camera.startPreview();
                }
            }
        	
        }
    };
    
   
/*    public static Bitmap doGreyscale(Bitmap src) {
        // constant factors
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;
        int max = 0;
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // pixel information
        int A, R, G, B;
        int pixel;
     
        // get image size
        int width = src.getWidth();
        int height = src.getHeight();
     
        // scan through every single pixel
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                //bmOut.setPixel(x, y, Color.argb(A, R, G, B));
                bmOut.setPixel(x, y, Color.rgb(R, G, B));
                
            }
        }
        
        // return final image
        return bmOut;
    }
*/
    private File getDir() {
        File sdDir = Environment.getExternalStorageDirectory();
 
        return new File(sdDir, "Maelewano");
    }
        
    // Return an ArrayList containing histogram values for separate R, G, B channels
    public static ArrayList<int[]> imageHistogram(Bitmap input) {
    	//public static ArrayList<int[]> imageHistogram(Bitmap input) {
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
        
        hist.add(rhistogram);
        hist.add(ghistogram);
        hist.add(bhistogram);
        
        return hist;
     
    }
    
   
    

    public void takepictures() {

    	Timer = new Thread(){
        	public void run(){
        		try{
        			int stopstatus = stopbutton.getVisibility();
        			while(stopstatus == 0){
        			
        			sleep(12000);
        			preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
        			
        			//sleep(7000);

        			}
        			}

        		catch (InterruptedException e){
        			e.printStackTrace();

        		}
        	}
    	};Timer.start();

   	}
    

    public void drawhisto(Bitmap chakala){
		drawnHistList = imageHistogram(chakala);
	   	//Toast.makeText(getApplicationContext(), "histogram drawn", Toast.LENGTH_LONG).show();	
    }

/*    public void copyDatabaseToSdCard() {
  		Log.e("Databasehealper", "********************************");
  		try {
  			File f1 = new File("/data/data/com.kiko.softwareltd/databases/maelewanodb");
  			if (f1.exists()) {

  				File f2 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/pendingwork.db");
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
    */
    public String comparefiguresfromdb(ArrayList<int[]> vdrawnHistList){
    	ArrayList<String> imageids = new ArrayList<String>();
    	String actualmeaning = null;
    	int vred0; int vred8 ; int vred16 ; int vred24 ; 
    	int vred33 ; int vred41 ; int vred49 ; int vred57 ;
    	int vred66 ; int vred74 ; int vred82 ; int vred90 ; 
    	int vred99 ; int vred107 ; int vred115 ; int vred123 ;
    	int vred132 ; int vred140 ; int vred148 ; int vred156 ; 
    	int vred165 ; int vred173 ; int vred181 ; int vred189 ;
    	int vred198 ; int vred206 ; int vred214 ; int vred222 ;
    	int vred231 ; int vred239 ; int vred247 ; int vred255; 


    	int   vgreen0 ; int vgreen4 ; int vgreen8 ; int vgreen12 ; int vgreen16 ; 
    	int vgreen20 ; int vgreen24 ; int vgreen28 ; int vgreen32 ;
    	int vgreen36 ; int vgreen40 ; int vgreen44 ; int vgreen48 ;
    	int vgreen52 ; int vgreen56 ; int vgreen60 ; int vgreen65 ;
    	int vgreen69 ; int vgreen73 ; int vgreen77 ; int vgreen81 ; 
    	int vgreen85 ; int vgreen89 ; int vgreen93 ; int vgreen97 ; 
    	int vgreen101 ; int vgreen105 ; int vgreen109 ; 
    	int vgreen113 ; int vgreen117 ; int vgreen121 ; int  vgreen125 ; 
    	int vgreen130 ; int vgreen134 ; int vgreen138 ; int vgreen142 ;
    	int vgreen146 ; int vgreen150 ; int vgreen154 ; int vgreen158 ; 
    	int vgreen162 ; int vgreen166 ; int vgreen170 ; int vgreen174 ; 
    	int vgreen178 ; int vgreen182 ; int vgreen186 ; int vgreen190 ;
    	int vgreen195 ; int vgreen199 ; int vgreen203 ; int vgreen207 ; 
    	int vgreen211 ; int vgreen215 ; int vgreen219 ; int  vgreen223 ; 
    	int vgreen227 ; int vgreen231 ; int vgreen235 ; int vgreen239;
    	int vgreen243 ; int vgreen247 ; int vgreen251 ; int  vgreen255 ;

    	int vblue0; int vblue8 ; int vblue16 ; int vblue24 ; 
    	int vblue33 ; int vblue41 ; int vblue49 ; int vblue57 ;
    	int vblue66 ; int vblue74 ; int vblue82 ; int vblue90 ; 
    	int vblue99 ; int vblue107 ; int vblue115 ; int vblue123 ;
    	int vblue132 ; int vblue140 ; int vblue148 ; int vblue156 ; 
    	int vblue165 ; int vblue173 ; int vblue181 ; int vblue189 ;
    	int vblue198 ; int vblue206 ; int vblue214 ; int vblue222 ;
    	int vblue231 ; int vblue239 ; int vblue247 ; int vblue255;	

    	 
    	////////////////////////////////////////
          	 int lowvred0 = 0; int lowvred8  = 0; int lowvred16  = 0;
          	 int lowvred24  = 0; int lowvred33  = 0; int lowvred41  = 0;
          	 int lowvred49  = 0; int lowvred57  = 0; int lowvred66  = 0; 
          	 int lowvred74  = 0; int lowvred82  = 0; int lowvred90  = 0; 
          	 int lowvred99  = 0; int lowvred107  = 0; int lowvred115  = 0; int lowvred123  = 0;
          	 int lowvred132  = 0; int lowvred140  = 0; int lowvred148  = 0; int lowvred156  = 0; 
          	 int lowvred165  = 0; int lowvred173  = 0; int lowvred181  = 0; int lowvred189  = 0;
          	 int lowvred198  = 0; int lowvred206  = 0; int lowvred214  = 0; int lowvred222  = 0;
          	 int lowvred231  = 0; int lowvred239  = 0; int lowvred247  = 0; int lowvred255 = 0;

          	 int highvred0 = 0; int highvred8  = 0; int highvred16  = 0;
          	 int highvred24  = 0; int highvred33  = 0; int highvred41  = 0;
          	 int highvred49  = 0; int highvred57  = 0; int highvred66  = 0; 
          	 int highvred74  = 0; int highvred82  = 0; int highvred90  = 0; 
          	 int highvred99  = 0; int highvred107  = 0; int highvred115  = 0; int highvred123  = 0;
          	 int highvred132  = 0; int highvred140  = 0; int highvred148  = 0; int highvred156  = 0; 
          	 int highvred165  = 0; int highvred173  = 0; int highvred181  = 0; int highvred189  = 0;
          	 int highvred198  = 0; int highvred206  = 0; int highvred214  = 0; int highvred222  = 0;
          	 int highvred231  = 0; int highvred239  = 0; int highvred247  = 0; int highvred255 = 0;

    	////////////////////////////////////////////////////////
    	 int   lowvgreen0 = 0; int lowvgreen4 = 0; int lowvgreen8 = 0; int lowvgreen12 = 0; int lowvgreen16 = 0; 
    	 int lowvgreen20 = 0; int lowvgreen24 = 0; int lowvgreen28 = 0; int lowvgreen32 = 0;
    	 int lowvgreen36 = 0; int lowvgreen40 = 0; int lowvgreen44 = 0; int lowvgreen48 = 0;
    	 int lowvgreen52 = 0; int lowvgreen56 = 0; int lowvgreen60 = 0; int lowvgreen65 = 0;
    	 int lowvgreen69 = 0; int lowvgreen73 = 0; int lowvgreen77 = 0; int lowvgreen81 = 0; 
    	 int lowvgreen85 = 0; int lowvgreen89 = 0; int lowvgreen93 = 0; int lowvgreen97 = 0; 
    	 int lowvgreen101 = 0; int lowvgreen105 = 0; int lowvgreen109 = 0; 
    	 int lowvgreen113 = 0; int lowvgreen117 = 0; int lowvgreen121 = 0; int  lowvgreen125 = 0; 
    	 int lowvgreen130 = 0; int lowvgreen134 = 0; int lowvgreen138 = 0; int lowvgreen142 = 0;
    	 int lowvgreen146 = 0; int lowvgreen150 = 0; int lowvgreen154 = 0; int lowvgreen158 = 0; 
    	 int lowvgreen162 = 0; int lowvgreen166 = 0; int lowvgreen170 = 0; int lowvgreen174 = 0; 
    	 int lowvgreen178 = 0; int lowvgreen182 = 0; int lowvgreen186 = 0; int lowvgreen190 = 0;
    	 int lowvgreen195 = 0; int lowvgreen199 = 0; int lowvgreen203 = 0; int lowvgreen207 = 0; 
    	 int lowvgreen211 = 0; int lowvgreen215 = 0; int lowvgreen219 = 0; int  lowvgreen223 = 0; 
    	 int lowvgreen227 = 0; int lowvgreen231 = 0; int lowvgreen235 = 0; int lowvgreen239= 0;
    	 int lowvgreen243 = 0; int lowvgreen247 = 0; int lowvgreen251 = 0; int  lowvgreen255 = 0;

    	 int   highvgreen0 = 0; int highvgreen4 = 0; int highvgreen8 = 0; int highvgreen12 = 0; int highvgreen16 = 0; 
    	 int highvgreen20 = 0; int highvgreen24 = 0; int highvgreen28 = 0; int highvgreen32 = 0;
    	 int highvgreen36 = 0; int highvgreen40 = 0; int highvgreen44 = 0; int highvgreen48 = 0;
    	 int highvgreen52 = 0; int highvgreen56 = 0; int highvgreen60 = 0; int highvgreen65 = 0;
    	 int highvgreen69 = 0; int highvgreen73 = 0; int highvgreen77 = 0; int highvgreen81 = 0; 
    	 int highvgreen85 = 0; int highvgreen89 = 0; int highvgreen93 = 0; int highvgreen97 = 0; 
    	 int highvgreen101 = 0; int highvgreen105 = 0; int highvgreen109 = 0; 
    	 int highvgreen113 = 0; int highvgreen117 = 0; int highvgreen121 = 0; int  highvgreen125 = 0; 
    	 int highvgreen130 = 0; int highvgreen134 = 0; int highvgreen138 = 0; int highvgreen142 = 0;
    	 int highvgreen146 = 0; int highvgreen150 = 0; int highvgreen154 = 0; int highvgreen158 = 0; 
    	 int highvgreen162 = 0; int highvgreen166 = 0; int highvgreen170 = 0; int highvgreen174 = 0; 
    	 int highvgreen178 = 0; int highvgreen182 = 0; int highvgreen186 = 0; int highvgreen190 = 0;
    	 int highvgreen195 = 0; int highvgreen199 = 0; int highvgreen203 = 0; int highvgreen207 = 0; 
    	 int highvgreen211 = 0; int highvgreen215 = 0; int highvgreen219 = 0; int  highvgreen223 = 0; 
    	 int highvgreen227 = 0; int highvgreen231 = 0; int highvgreen235 = 0; int highvgreen239= 0;
    	 int highvgreen243 = 0; int highvgreen247 = 0; int highvgreen251 = 0; int  highvgreen255 = 0;


    	 
    	/////////////////////////////////////////////

	 int lowvblue0 = 0; int lowvblue8  = 0; int lowvblue16  = 0;
	 int lowvblue24  = 0; int lowvblue33  = 0; int lowvblue41  = 0;
	 int lowvblue49  = 0; int lowvblue57  = 0; int lowvblue66  = 0; 
	 int lowvblue74  = 0; int lowvblue82  = 0; int lowvblue90  = 0; 
	 int lowvblue99  = 0; int lowvblue107  = 0; int lowvblue115  = 0; int lowvblue123  = 0;
	 int lowvblue132  = 0; int lowvblue140  = 0; int lowvblue148  = 0; int lowvblue156  = 0; 
	 int lowvblue165  = 0; int lowvblue173  = 0; int lowvblue181  = 0; int lowvblue189  = 0;
	 int lowvblue198  = 0; int lowvblue206  = 0; int lowvblue214  = 0; int lowvblue222  = 0;
	 int lowvblue231  = 0; int lowvblue239  = 0; int lowvblue247  = 0; int lowvblue255 = 0;

	 int highvblue0 = 0; int highvblue8  = 0; int highvblue16  = 0;
	 int highvblue24  = 0; int highvblue33  = 0; int highvblue41  = 0;
	 int highvblue49  = 0; int highvblue57  = 0; int highvblue66  = 0; 
	 int highvblue74  = 0; int highvblue82  = 0; int highvblue90  = 0; 
	 int highvblue99  = 0; int highvblue107  = 0; int highvblue115  = 0; int highvblue123  = 0;
	 int highvblue132  = 0; int highvblue140  = 0; int highvblue148  = 0; int highvblue156  = 0; 
	 int highvblue165  = 0; int highvblue173  = 0; int highvblue181  = 0; int highvblue189  = 0;
	 int highvblue198  = 0; int highvblue206  = 0; int highvblue214  = 0; int highvblue222  = 0;
	 int highvblue231  = 0; int highvblue239  = 0; int highvblue247  = 0; int highvblue255 = 0;

	 ////////////////////////////////////////


	 int[] vredhisto = vdrawnHistList.get(0);
	 int[] vgreenhisto = vdrawnHistList.get(1);
	 int[] vbluehisto = vdrawnHistList.get(2);

	 vred0 = vredhisto[0];vred8 = vredhisto[8]; vred16 = vredhisto[16];vred24 = vredhisto[24];
	 vred33  = vredhisto[33];vred41  = vredhisto[41];vred49  = vredhisto[49];vred57 = vredhisto[57];
	 vred66  = vredhisto[66];vred74  = vredhisto[74];vred82  = vredhisto[82];vred90 = vredhisto[90]; 
	 vred99  = vredhisto[99];vred107  = vredhisto[107];vred115  = vredhisto[115];vred123 = vredhisto[123];
	 vred132  = vredhisto[132];vred140  = vredhisto[140];vred148  = vredhisto[148];vred156 = vredhisto[156]; 
	 vred165  = vredhisto[165];vred173  = vredhisto[173];vred181  = vredhisto[181];vred189 = vredhisto[189];
	 vred198  = vredhisto[198];vred206  = vredhisto[206];vred214  = vredhisto[214];vred222 = vredhisto[222];
	 vred231  = vredhisto[231];vred239  = vredhisto[239];vred247  = vredhisto[247]; vred255  = vredhisto[255];

	 vgreen0 = vgreenhisto[0];  vgreen4  = vgreenhisto[4];vgreen8  = vgreenhisto[8];vgreen12  = vgreenhisto[12];vgreen16 = vgreenhisto[16]; 
	 vgreen20  = vgreenhisto[20];vgreen24  = vgreenhisto[24];vgreen28  = vgreenhisto[28];vgreen32 = vgreenhisto[32];
	 vgreen36  = vgreenhisto[36];vgreen40  = vgreenhisto[40];vgreen44  = vgreenhisto[44];vgreen48 = vgreenhisto[48];
	 vgreen52  = vgreenhisto[52];vgreen56  = vgreenhisto[56];vgreen60  = vgreenhisto[60];vgreen65 = vgreenhisto[65];
	 vgreen69  = vgreenhisto[69];vgreen73  = vgreenhisto[73];vgreen77  = vgreenhisto[77];vgreen81 = vgreenhisto[81]; 
	 vgreen85  = vgreenhisto[85];vgreen89  = vgreenhisto[89];vgreen93  = vgreenhisto[93];vgreen97 = vgreenhisto[97]; 
	 vgreen101  = vgreenhisto[101];vgreen105  = vgreenhisto[105];vgreen109 = vgreenhisto[109]; 
	 vgreen113  = vgreenhisto[113];vgreen117  = vgreenhisto[117];vgreen121  = vgreenhisto[121]; vgreen125 = vgreenhisto[125]; 
	 vgreen130  = vgreenhisto[130];vgreen134  = vgreenhisto[134];vgreen138  = vgreenhisto[138];vgreen142 = vgreenhisto[142];
	 vgreen146  = vgreenhisto[146];vgreen150  = vgreenhisto[150];vgreen154  = vgreenhisto[154];vgreen158 = vgreenhisto[158]; 
	 vgreen162  = vgreenhisto[162];vgreen166  = vgreenhisto[166];vgreen170  = vgreenhisto[170];vgreen174 = vgreenhisto[174]; 
	 vgreen178  = vgreenhisto[178];vgreen182  = vgreenhisto[182];vgreen186  = vgreenhisto[186];vgreen190 = vgreenhisto[190];
	 vgreen195  = vgreenhisto[195];vgreen199  = vgreenhisto[199];vgreen203  = vgreenhisto[203];vgreen207 = vgreenhisto[207]; 
	 vgreen211  = vgreenhisto[211];vgreen215  = vgreenhisto[215];vgreen219  = vgreenhisto[219]; vgreen223 = vgreenhisto[223]; 
	 vgreen227  = vgreenhisto[227];vgreen231  = vgreenhisto[231];vgreen235  = vgreenhisto[235];vgreen239= vgreenhisto[239];
	 vgreen243  = vgreenhisto[243];vgreen247  = vgreenhisto[247];vgreen251  = vgreenhisto[251]; vgreen255 = vgreenhisto[255]; 

	 vblue0  = vbluehisto[0];vblue8  = vbluehisto[8];vblue16  = vbluehisto[16];vblue24  = vbluehisto[24];vblue33 = vbluehisto[33];
	 vblue41  = vbluehisto[41];vblue49  = vbluehisto[49];vblue57  = vbluehisto[57];vblue66  = vbluehisto[66];vblue74 = vbluehisto[74]; 
	 vblue82  = vbluehisto[82];vblue90  = vbluehisto[90];vblue99  = vbluehisto[99];vblue107  = vbluehisto[107];vblue115 = vbluehisto[115];
	 vblue123  = vbluehisto[123];vblue132  = vbluehisto[132];vblue140  = vbluehisto[140];vblue148  = vbluehisto[148];vblue156 = vbluehisto[156]; 
	 vblue165  = vbluehisto[165];vblue173  = vbluehisto[173];vblue181  = vbluehisto[181];vblue189  = vbluehisto[189];vblue198 = vbluehisto[198];
	 vblue206  = vbluehisto[206];vblue214  = vbluehisto[214];vblue222  = vbluehisto[222];vblue231  = vbluehisto[231];vblue239  = vbluehisto[239];
	 vblue247 = vbluehisto[247]; vblue255 = vbluehisto[255]; 

	   	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  lowvred0 = (int)Math.round(percentagelow*vred0);  lowvred8  = (int)Math.round(percentagelow*vred8);  lowvred16  = (int)Math.round(percentagelow*vred16);
	  lowvred24  = (int)Math.round(percentagelow*vred24);  lowvred33  = (int)Math.round(percentagelow*vred33);  lowvred41  = (int)Math.round(percentagelow*vred41);
	  lowvred49  = (int)Math.round(percentagelow*vred49);  lowvred57  = (int)Math.round(percentagelow*vred57);  lowvred66  = (int)Math.round(percentagelow*vred66); 
	  lowvred74  = (int)Math.round(percentagelow*vred74);  lowvred82  = (int)Math.round(percentagelow*vred82);  lowvred90  = (int)Math.round(percentagelow*vred90); 
	  lowvred99  = (int)Math.round(percentagelow*vred99);  lowvred107  = (int)Math.round(percentagelow*vred107);  lowvred115  = (int)Math.round(percentagelow*vred115);  lowvred123  = (int)Math.round(percentagelow*vred123);
	  lowvred132  = (int)Math.round(percentagelow*vred132);  lowvred140  = (int)Math.round(percentagelow*vred140);  lowvred148  = (int)Math.round(percentagelow*vred148);  lowvred156  = (int)Math.round(percentagelow*vred156); 
	  lowvred165  = (int)Math.round(percentagelow*vred165);  lowvred173  = (int)Math.round(percentagelow*vred173);  lowvred181  = (int)Math.round(percentagelow*vred181);  lowvred189  = (int)Math.round(percentagelow*vred189);
	  lowvred198  = (int)Math.round(percentagelow*vred198);  lowvred206  = (int)Math.round(percentagelow*vred206);  lowvred214  = (int)Math.round(percentagelow*vred214);  lowvred222  = (int)Math.round(percentagelow*vred222);
	  lowvred231  = (int)Math.round(percentagelow*vred231);  lowvred239  = (int)Math.round(percentagelow*vred239);  lowvred247  = (int)Math.round(percentagelow*vred247);  lowvred255 = (int)Math.round(percentagelow*vred255);


	  lowvgreen0 = (int)Math.round(percentagelow*vgreen0);  lowvgreen4 = (int)Math.round(percentagelow*vgreen4);  lowvgreen8 = (int)Math.round(percentagelow*vgreen8);  lowvgreen12 = (int)Math.round(percentagelow*vgreen12);  lowvgreen16 = (int)Math.round(percentagelow*vgreen16); 
	  lowvgreen20 = (int)Math.round(percentagelow*vgreen20);  lowvgreen24 = (int)Math.round(percentagelow*vgreen24);  lowvgreen28 = (int)Math.round(percentagelow*vgreen28);  lowvgreen32 = (int)Math.round(percentagelow*vgreen32);
	  lowvgreen36 = (int)Math.round(percentagelow*vgreen36);  lowvgreen40 = (int)Math.round(percentagelow*vgreen40);  lowvgreen44 = (int)Math.round(percentagelow*vgreen44);  lowvgreen48 = (int)Math.round(percentagelow*vgreen48);
	  lowvgreen52 = (int)Math.round(percentagelow*vgreen52);  lowvgreen56 = (int)Math.round(percentagelow*vgreen56);  lowvgreen60 = (int)Math.round(percentagelow*vgreen60);  lowvgreen65 = (int)Math.round(percentagelow*vgreen65);
	  lowvgreen69 = (int)Math.round(percentagelow*vgreen69);  lowvgreen73 = (int)Math.round(percentagelow*vgreen73);  lowvgreen77 = (int)Math.round(percentagelow*vgreen77);  lowvgreen81 = (int)Math.round(percentagelow*vgreen81); 
	  lowvgreen85 = (int)Math.round(percentagelow*vgreen85);  lowvgreen89 = (int)Math.round(percentagelow*vgreen89);  lowvgreen93 = (int)Math.round(percentagelow*vgreen93);  lowvgreen97 = (int)Math.round(percentagelow*vgreen97); 
	  lowvgreen101 = (int)Math.round(percentagelow*vgreen101);  lowvgreen105 = (int)Math.round(percentagelow*vgreen105);  lowvgreen109 = (int)Math.round(percentagelow*vgreen109); 
	  lowvgreen113 = (int)Math.round(percentagelow*vgreen113);  lowvgreen117 = (int)Math.round(percentagelow*vgreen117);  lowvgreen121 = (int)Math.round(percentagelow*vgreen121);   lowvgreen125 = (int)Math.round(percentagelow*vgreen125); 
	  lowvgreen130 = (int)Math.round(percentagelow*vgreen130);  lowvgreen134 = (int)Math.round(percentagelow*vgreen134);  lowvgreen138 = (int)Math.round(percentagelow*vgreen138);  lowvgreen142 = (int)Math.round(percentagelow*vgreen142);
	  lowvgreen146 = (int)Math.round(percentagelow*vgreen146);  lowvgreen150 = (int)Math.round(percentagelow*vgreen150);  lowvgreen154 = (int)Math.round(percentagelow*vgreen154);  lowvgreen158 = (int)Math.round(percentagelow*vgreen158); 
	  lowvgreen162 = (int)Math.round(percentagelow*vgreen162);  lowvgreen166 = (int)Math.round(percentagelow*vgreen166);  lowvgreen170 = (int)Math.round(percentagelow*vgreen170);  lowvgreen174 = (int)Math.round(percentagelow*vgreen174); 
	  lowvgreen178 = (int)Math.round(percentagelow*vgreen178);  lowvgreen182 = (int)Math.round(percentagelow*vgreen182);  lowvgreen186 = (int)Math.round(percentagelow*vgreen186);  lowvgreen190 = (int)Math.round(percentagelow*vgreen190);
	  lowvgreen195 = (int)Math.round(percentagelow*vgreen195);  lowvgreen199 = (int)Math.round(percentagelow*vgreen199);  lowvgreen203 = (int)Math.round(percentagelow*vgreen203);  lowvgreen207 = (int)Math.round(percentagelow*vgreen207); 
	  lowvgreen211 = (int)Math.round(percentagelow*vgreen211);  lowvgreen215 = (int)Math.round(percentagelow*vgreen215);  lowvgreen219 = (int)Math.round(percentagelow*vgreen219);   lowvgreen223 = (int)Math.round(percentagelow*vgreen223); 
	  lowvgreen227 = (int)Math.round(percentagelow*vgreen227);  lowvgreen231 = (int)Math.round(percentagelow*vgreen231);  lowvgreen235 = (int)Math.round(percentagelow*vgreen235);  lowvgreen239= (int)Math.round(percentagelow*vgreen239);
	  lowvgreen243 = (int)Math.round(percentagelow*vgreen243);  lowvgreen247 = (int)Math.round(percentagelow*vgreen247);  lowvgreen251 = (int)Math.round(percentagelow*vgreen251);   lowvgreen255 = (int)Math.round(percentagelow*vgreen255);


	  lowvblue0 = (int)Math.round(percentagelow*vblue0);  lowvblue8  = (int)Math.round(percentagelow*vblue8);  lowvblue16  = (int)Math.round(percentagelow*vblue16);
	  lowvblue24  = (int)Math.round(percentagelow*vblue24);  lowvblue33  = (int)Math.round(percentagelow*vblue33);  lowvblue41  = (int)Math.round(percentagelow*vblue41);
	  lowvblue49  = (int)Math.round(percentagelow*vblue49);  lowvblue57  = (int)Math.round(percentagelow*vblue57);  lowvblue66  = (int)Math.round(percentagelow*vblue66); 
	  lowvblue74  = (int)Math.round(percentagelow*vblue74);  lowvblue82  = (int)Math.round(percentagelow*vblue82);  lowvblue90  = (int)Math.round(percentagelow*vblue90); 
	  lowvblue99  = (int)Math.round(percentagelow*vblue99);  lowvblue107  = (int)Math.round(percentagelow*vblue107);  lowvblue115  = (int)Math.round(percentagelow*vblue115);  lowvblue123  = (int)Math.round(percentagelow*vblue123);
	  lowvblue132  = (int)Math.round(percentagelow*vblue132);  lowvblue140  = (int)Math.round(percentagelow*vblue140);  lowvblue148  = (int)Math.round(percentagelow*vblue148);  lowvblue156  = (int)Math.round(percentagelow*vblue156); 
	  lowvblue165  = (int)Math.round(percentagelow*vblue165);  lowvblue173  = (int)Math.round(percentagelow*vblue173);  lowvblue181  = (int)Math.round(percentagelow*vblue181);  lowvblue189  = (int)Math.round(percentagelow*vblue189);
	  lowvblue198  = (int)Math.round(percentagelow*vblue198);  lowvblue206  = (int)Math.round(percentagelow*vblue206);  lowvblue214  = (int)Math.round(percentagelow*vblue214);  lowvblue222  = (int)Math.round(percentagelow*vblue222);
	  lowvblue231  = (int)Math.round(percentagelow*vblue231);  lowvblue239  = (int)Math.round(percentagelow*vblue239);  lowvblue247  = (int)Math.round(percentagelow*vblue247);  lowvblue255 = (int)Math.round(percentagelow*vblue255);
	  ////////////////////////////////////////////////////////////////////////////////////////////////////
	  highvred0 = (int)Math.round(percentagehigh*vred0);  highvred8  = (int)Math.round(percentagehigh*vred8);  highvred16  = (int)Math.round(percentagehigh*vred16);
	  highvred24  = (int)Math.round(percentagehigh*vred24);  highvred33  = (int)Math.round(percentagehigh*vred33);  highvred41  = (int)Math.round(percentagehigh*vred41);
	  highvred49  = (int)Math.round(percentagehigh*vred49);  highvred57  = (int)Math.round(percentagehigh*vred57);  highvred66  = (int)Math.round(percentagehigh*vred66); 
	  highvred74  = (int)Math.round(percentagehigh*vred74);  highvred82  = (int)Math.round(percentagehigh*vred82);  highvred90  = (int)Math.round(percentagehigh*vred90); 
	  highvred99  = (int)Math.round(percentagehigh*vred99);  highvred107  = (int)Math.round(percentagehigh*vred107);  highvred115  = (int)Math.round(percentagehigh*vred115);  highvred123  = (int)Math.round(percentagehigh*vred123);
	  highvred132  = (int)Math.round(percentagehigh*vred132);  highvred140  = (int)Math.round(percentagehigh*vred140);  highvred148  = (int)Math.round(percentagehigh*vred148);  highvred156  = (int)Math.round(percentagehigh*vred156); 
	  highvred165  = (int)Math.round(percentagehigh*vred165);  highvred173  = (int)Math.round(percentagehigh*vred173);  highvred181  = (int)Math.round(percentagehigh*vred181);  highvred189  = (int)Math.round(percentagehigh*vred189);
	  highvred198  = (int)Math.round(percentagehigh*vred198);  highvred206  = (int)Math.round(percentagehigh*vred206);  highvred214  = (int)Math.round(percentagehigh*vred214);  highvred222  = (int)Math.round(percentagehigh*vred222);
	  highvred231  = (int)Math.round(percentagehigh*vred231);  highvred239  = (int)Math.round(percentagehigh*vred239);  highvred247  = (int)Math.round(percentagehigh*vred247);  highvred255 = (int)Math.round(percentagehigh*vred255);

	  highvgreen0 = (int)Math.round(percentagehigh*vgreen0);  highvgreen4 = (int)Math.round(percentagehigh*vgreen4);  highvgreen8 = (int)Math.round(percentagehigh*vgreen8);  highvgreen12 = (int)Math.round(percentagehigh*vgreen12);  highvgreen16 = (int)Math.round(percentagehigh*vgreen16); 
	  highvgreen20 = (int)Math.round(percentagehigh*vgreen20);  highvgreen24 = (int)Math.round(percentagehigh*vgreen24);  highvgreen28 = (int)Math.round(percentagehigh*vgreen28);  highvgreen32 = (int)Math.round(percentagehigh*vgreen32);
	  highvgreen36 = (int)Math.round(percentagehigh*vgreen36);  highvgreen40 = (int)Math.round(percentagehigh*vgreen40);  highvgreen44 = (int)Math.round(percentagehigh*vgreen44);  highvgreen48 = (int)Math.round(percentagehigh*vgreen48);
	  highvgreen52 = (int)Math.round(percentagehigh*vgreen52);  highvgreen56 = (int)Math.round(percentagehigh*vgreen56);  highvgreen60 = (int)Math.round(percentagehigh*vgreen60);  highvgreen65 = (int)Math.round(percentagehigh*vgreen65);
	  highvgreen69 = (int)Math.round(percentagehigh*vgreen69);  highvgreen73 = (int)Math.round(percentagehigh*vgreen73);  highvgreen77 = (int)Math.round(percentagehigh*vgreen77);  highvgreen81 = (int)Math.round(percentagehigh*vgreen81); 
	  highvgreen85 = (int)Math.round(percentagehigh*vgreen85);  highvgreen89 = (int)Math.round(percentagehigh*vgreen89);  highvgreen93 = (int)Math.round(percentagehigh*vgreen93);  highvgreen97 = (int)Math.round(percentagehigh*vgreen97); 
	  highvgreen101 = (int)Math.round(percentagehigh*vgreen101);  highvgreen105 = (int)Math.round(percentagehigh*vgreen105);  highvgreen109 = (int)Math.round(percentagehigh*vgreen109); 
	  highvgreen113 = (int)Math.round(percentagehigh*vgreen113);  highvgreen117 = (int)Math.round(percentagehigh*vgreen117);  highvgreen121 = (int)Math.round(percentagehigh*vgreen121);   highvgreen125 = (int)Math.round(percentagehigh*vgreen125); 
	  highvgreen130 = (int)Math.round(percentagehigh*vgreen130);  highvgreen134 = (int)Math.round(percentagehigh*vgreen134);  highvgreen138 = (int)Math.round(percentagehigh*vgreen138);  highvgreen142 = (int)Math.round(percentagehigh*vgreen142);
	  highvgreen146 = (int)Math.round(percentagehigh*vgreen146);  highvgreen150 = (int)Math.round(percentagehigh*vgreen150);  highvgreen154 = (int)Math.round(percentagehigh*vgreen154);  highvgreen158 = (int)Math.round(percentagehigh*vgreen158); 
	  highvgreen162 = (int)Math.round(percentagehigh*vgreen162);  highvgreen166 = (int)Math.round(percentagehigh*vgreen166);  highvgreen170 = (int)Math.round(percentagehigh*vgreen170);  highvgreen174 = (int)Math.round(percentagehigh*vgreen174); 
	  highvgreen178 = (int)Math.round(percentagehigh*vgreen178);  highvgreen182 = (int)Math.round(percentagehigh*vgreen182);  highvgreen186 = (int)Math.round(percentagehigh*vgreen186);  highvgreen190 = (int)Math.round(percentagehigh*vgreen190);
	  highvgreen195 = (int)Math.round(percentagehigh*vgreen195);  highvgreen199 = (int)Math.round(percentagehigh*vgreen199);  highvgreen203 = (int)Math.round(percentagehigh*vgreen203);  highvgreen207 = (int)Math.round(percentagehigh*vgreen207); 
	  highvgreen211 = (int)Math.round(percentagehigh*vgreen211);  highvgreen215 = (int)Math.round(percentagehigh*vgreen215);  highvgreen219 = (int)Math.round(percentagehigh*vgreen219);   highvgreen223 = (int)Math.round(percentagehigh*vgreen223); 
	  highvgreen227 = (int)Math.round(percentagehigh*vgreen227);  highvgreen231 = (int)Math.round(percentagehigh*vgreen231);  highvgreen235 = (int)Math.round(percentagehigh*vgreen235);  highvgreen239= (int)Math.round(percentagehigh*vgreen239);
	  highvgreen243 = (int)Math.round(percentagehigh*vgreen243);  highvgreen247 = (int)Math.round(percentagehigh*vgreen247);  highvgreen251 = (int)Math.round(percentagehigh*vgreen251);   highvgreen255 = (int)Math.round(percentagehigh*vgreen255);


	  highvblue0 = (int)Math.round(percentagehigh*vblue0);  highvblue8  = (int)Math.round(percentagehigh*vblue8);  highvblue16  = (int)Math.round(percentagehigh*vblue16);
	  highvblue24  = (int)Math.round(percentagehigh*vblue24);  highvblue33  = (int)Math.round(percentagehigh*vblue33);  highvblue41  = (int)Math.round(percentagehigh*vblue41);
	  highvblue49  = (int)Math.round(percentagehigh*vblue49);  highvblue57  = (int)Math.round(percentagehigh*vblue57);  highvblue66  = (int)Math.round(percentagehigh*vblue66); 
	  highvblue74  = (int)Math.round(percentagehigh*vblue74);  highvblue82  = (int)Math.round(percentagehigh*vblue82);  highvblue90  = (int)Math.round(percentagehigh*vblue90); 
	  highvblue99  = (int)Math.round(percentagehigh*vblue99);  highvblue107  = (int)Math.round(percentagehigh*vblue107);  highvblue115  = (int)Math.round(percentagehigh*vblue115);  highvblue123  = (int)Math.round(percentagehigh*vblue123);
	  highvblue132  = (int)Math.round(percentagehigh*vblue132);  highvblue140  = (int)Math.round(percentagehigh*vblue140);  highvblue148  = (int)Math.round(percentagehigh*vblue148);  highvblue156  = (int)Math.round(percentagehigh*vblue156); 
	  highvblue165  = (int)Math.round(percentagehigh*vblue165);  highvblue173  = (int)Math.round(percentagehigh*vblue173);  highvblue181  = (int)Math.round(percentagehigh*vblue181);  highvblue189  = (int)Math.round(percentagehigh*vblue189);
	  highvblue198  = (int)Math.round(percentagehigh*vblue198);  highvblue206  = (int)Math.round(percentagehigh*vblue206);  highvblue214  = (int)Math.round(percentagehigh*vblue214);  highvblue222  = (int)Math.round(percentagehigh*vblue222);
	  highvblue231  = (int)Math.round(percentagehigh*vblue231);  highvblue239  = (int)Math.round(percentagehigh*vblue239);  highvblue247  = (int)Math.round(percentagehigh*vblue247);  highvblue255 = (int)Math.round(percentagehigh*vblue255);	  

	   	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    	String generaltable = "general";

    	String Sql1 = "SELECT meaning FROM " + generaltable+" WHERE red0 BETWEEN ? AND ?";
    	String Sql2 = "SELECT meaning FROM " + generaltable+" WHERE red8 BETWEEN ? AND ?";
    	String Sql3 = "SELECT meaning FROM " + generaltable+" WHERE red16 BETWEEN ? AND ?";
    	String Sql4 = "SELECT meaning FROM " + generaltable+" WHERE red24 BETWEEN ? AND ?";
    	String Sql5 = "SELECT meaning FROM " + generaltable+" WHERE red33 BETWEEN ? AND ?";
    	String Sql6 = "SELECT meaning FROM " + generaltable+" WHERE red41 BETWEEN ? AND ?";
    	String Sql7 = "SELECT meaning FROM " + generaltable+" WHERE red49 BETWEEN ? AND ?";
    	String Sql8 = "SELECT meaning FROM " + generaltable+" WHERE red57 BETWEEN ? AND ?";
    	String Sql9 = "SELECT meaning FROM " + generaltable+" WHERE red66 BETWEEN ? AND ?";
    	String Sql10 = "SELECT meaning FROM " + generaltable+" WHERE red74 BETWEEN ? AND ?";
    	String Sql11 = "SELECT meaning FROM " + generaltable+" WHERE red82 BETWEEN ? AND ?";
    	String Sql12 = "SELECT meaning FROM " + generaltable+" WHERE red90 BETWEEN ? AND ?";
    	String Sql13 = "SELECT meaning FROM " + generaltable+" WHERE red99 BETWEEN ? AND ?";
    	String Sql14 = "SELECT meaning FROM " + generaltable+" WHERE red107 BETWEEN ? AND ?";
    	String Sql15 = "SELECT meaning FROM " + generaltable+" WHERE red115 BETWEEN ? AND ?";
    	String Sql16 = "SELECT meaning FROM " + generaltable+" WHERE red123 BETWEEN ? AND ?";
    	String Sql17 = "SELECT meaning FROM " + generaltable+" WHERE red132 BETWEEN ? AND ?";
    	String Sql18 = "SELECT meaning FROM " + generaltable+" WHERE red140 BETWEEN ? AND ?";
    	String Sql19 = "SELECT meaning FROM " + generaltable+" WHERE red148 BETWEEN ? AND ?";
    	String Sql20 = "SELECT meaning FROM " + generaltable+" WHERE red156 BETWEEN ? AND ?";
    	String Sql21 = "SELECT meaning FROM " + generaltable+" WHERE red165 BETWEEN ? AND ?";
    	String Sql22 = "SELECT meaning FROM " + generaltable+" WHERE red173 BETWEEN ? AND ?";
    	String Sql23 = "SELECT meaning FROM " + generaltable+" WHERE red181 BETWEEN ? AND ?";
    	String Sql24 = "SELECT meaning FROM " + generaltable+" WHERE red189 BETWEEN ? AND ?";
    	String Sql25 = "SELECT meaning FROM " + generaltable+" WHERE red198 BETWEEN ? AND ?";
    	String Sql26 = "SELECT meaning FROM " + generaltable+" WHERE red206 BETWEEN ? AND ?";
    	String Sql27 = "SELECT meaning FROM " + generaltable+" WHERE red214 BETWEEN ? AND ?";
    	String Sql28 = "SELECT meaning FROM " + generaltable+" WHERE red222 BETWEEN ? AND ?";
    	String Sql29 = "SELECT meaning FROM " + generaltable+" WHERE red231 BETWEEN ? AND ?";
    	String Sql30 = "SELECT meaning FROM " + generaltable+" WHERE red239 BETWEEN ? AND ?";
    	String Sql31 = "SELECT meaning FROM " + generaltable+" WHERE red247 BETWEEN ? AND ?";
    	String Sql32 = "SELECT meaning FROM " + generaltable+" WHERE red255 BETWEEN ? AND ?";
    	
    	
    	
		String Sql51 = "SELECT meaning FROM " + generaltable+" WHERE green0 BETWEEN ? AND ?";     
		String Sql52 = "SELECT meaning FROM " + generaltable+" WHERE green4 BETWEEN ? AND ?";       
		String Sql53 = "SELECT meaning FROM " + generaltable+" WHERE green8 BETWEEN ? AND ?";       
		String Sql54 = "SELECT meaning FROM " + generaltable+" WHERE green12 BETWEEN ? AND ?";       
		String Sql55 = "SELECT meaning FROM " + generaltable+" WHERE green16 BETWEEN ? AND ?";      
		String Sql56 = "SELECT meaning FROM " + generaltable+" WHERE green20 BETWEEN ? AND ?";       
		String Sql57 = "SELECT meaning FROM " + generaltable+" WHERE green24 BETWEEN ? AND ?";        
		String Sql58 = "SELECT meaning FROM " + generaltable+" WHERE green28 BETWEEN ? AND ?";     
		String Sql59 = "SELECT meaning FROM " + generaltable+" WHERE green32 BETWEEN ? AND ?";        
		String Sql60 = "SELECT meaning FROM " + generaltable+" WHERE green36 BETWEEN ? AND ?";        
		String Sql61 = "SELECT meaning FROM " + generaltable+" WHERE green40 BETWEEN ? AND ?";        
		String Sql62 = "SELECT meaning FROM " + generaltable+" WHERE green44 BETWEEN ? AND ?";        
		String Sql63 = "SELECT meaning FROM " + generaltable+" WHERE green48 BETWEEN ? AND ?";        
		String Sql64 = "SELECT meaning FROM " + generaltable+" WHERE green52 BETWEEN ? AND ?";        
		String Sql65 = "SELECT meaning FROM " + generaltable+" WHERE green56 BETWEEN ? AND ?";        
		String Sql66 = "SELECT meaning FROM " + generaltable+" WHERE green60 BETWEEN ? AND ?";        
		String Sql67 = "SELECT meaning FROM " + generaltable+" WHERE green65 BETWEEN ? AND ?";        
		String Sql68 = "SELECT meaning FROM " + generaltable+" WHERE green69 BETWEEN ? AND ?";        
		String Sql69 = "SELECT meaning FROM " + generaltable+" WHERE green73 BETWEEN ? AND ?";        
		String Sql70 = "SELECT meaning FROM " + generaltable+" WHERE green77 BETWEEN ? AND ?";        
		String Sql71 = "SELECT meaning FROM " + generaltable+" WHERE green81 BETWEEN ? AND ?";        
		String Sql72 = "SELECT meaning FROM " + generaltable+" WHERE green85 BETWEEN ? AND ?";           
		String Sql73 = "SELECT meaning FROM " + generaltable+" WHERE green89 BETWEEN ? AND ?";        
		String Sql74 = "SELECT meaning FROM " + generaltable+" WHERE green93 BETWEEN ? AND ?";        
		String Sql75 = "SELECT meaning FROM " + generaltable+" WHERE green97 BETWEEN ? AND ?";        
		String Sql76 = "SELECT meaning FROM " + generaltable+" WHERE green101 BETWEEN ? AND ?";      
		String Sql77 = "SELECT meaning FROM " + generaltable+" WHERE green105 BETWEEN ? AND ?";      
		String Sql78 = "SELECT meaning FROM " + generaltable+" WHERE green109 BETWEEN ? AND ?";      
		String Sql79 = "SELECT meaning FROM " + generaltable+" WHERE green113 BETWEEN ? AND ?";      
		String Sql80 = "SELECT meaning FROM " + generaltable+" WHERE green117 BETWEEN ? AND ?";      
		String Sql81 = "SELECT meaning FROM " + generaltable+" WHERE green121 BETWEEN ? AND ?";      
		String Sql82 = "SELECT meaning FROM " + generaltable+" WHERE green125 BETWEEN ? AND ?";      
		String Sql83 = "SELECT meaning FROM " + generaltable+" WHERE green130 BETWEEN ? AND ?";      
		String Sql84 = "SELECT meaning FROM " + generaltable+" WHERE green134 BETWEEN ? AND ?";      
		String Sql85 = "SELECT meaning FROM " + generaltable+" WHERE green138 BETWEEN ? AND ?";      
		String Sql86 = "SELECT meaning FROM " + generaltable+" WHERE green142 BETWEEN ? AND ?";      
		String Sql87 = "SELECT meaning FROM " + generaltable+" WHERE green146 BETWEEN ? AND ?";      
		String Sql88 = "SELECT meaning FROM " + generaltable+" WHERE green150 BETWEEN ? AND ?";      
		String Sql89 = "SELECT meaning FROM " + generaltable+" WHERE green154 BETWEEN ? AND ?";      
		String Sql90 = "SELECT meaning FROM " + generaltable+" WHERE green158 BETWEEN ? AND ?";      
		String Sql91 = "SELECT meaning FROM " + generaltable+" WHERE green162 BETWEEN ? AND ?";      
		String Sql92 = "SELECT meaning FROM " + generaltable+" WHERE green166 BETWEEN ? AND ?";      
		String Sql93 = "SELECT meaning FROM " + generaltable+" WHERE green170 BETWEEN ? AND ?";        
		String Sql94 = "SELECT meaning FROM " + generaltable+" WHERE green174 BETWEEN ? AND ?";        
		String Sql95 = "SELECT meaning FROM " + generaltable+" WHERE green178 BETWEEN ? AND ?";        
		String Sql96 = "SELECT meaning FROM " + generaltable+" WHERE green182 BETWEEN ? AND ?";        
		String Sql97 = "SELECT meaning FROM " + generaltable+" WHERE green186 BETWEEN ? AND ?";        
		String Sql98 = "SELECT meaning FROM " + generaltable+" WHERE green190 BETWEEN ? AND ?";        
		String Sql99 = "SELECT meaning FROM " + generaltable+" WHERE green195 BETWEEN ? AND ?";        
		String Sql100 = "SELECT meaning FROM " + generaltable+" WHERE green199 BETWEEN ? AND ?";      
		String Sql101 = "SELECT meaning FROM " + generaltable+" WHERE green203 BETWEEN ? AND ?";      
		String Sql102 = "SELECT meaning FROM " + generaltable+" WHERE green207 BETWEEN ? AND ?";      
		String Sql103 = "SELECT meaning FROM " + generaltable+" WHERE green211 BETWEEN ? AND ?";      
		String Sql104 = "SELECT meaning FROM " + generaltable+" WHERE green243 BETWEEN ? AND ?";      
		String Sql105 = "SELECT meaning FROM " + generaltable+" WHERE green247 BETWEEN ? AND ?";      
		String Sql106 = "SELECT meaning FROM " + generaltable+" WHERE green251 BETWEEN ? AND ?";      
		String Sql107 = "SELECT meaning FROM " + generaltable+" WHERE green255 BETWEEN ? AND ?";      
		String Sql108 = "SELECT meaning FROM " + generaltable+" WHERE green215 BETWEEN ? AND ?";      
		String Sql109 = "SELECT meaning FROM " + generaltable+" WHERE green219 BETWEEN ? AND ?";      
		String Sql110 = "SELECT meaning FROM " + generaltable+" WHERE green223 BETWEEN ? AND ?";      
		String Sql111 = "SELECT meaning FROM " + generaltable+" WHERE green227 BETWEEN ? AND ?";      
		String Sql112 = "SELECT meaning FROM " + generaltable+" WHERE green231 BETWEEN ? AND ?";      
		String Sql113 = "SELECT meaning FROM " + generaltable+" WHERE green235 BETWEEN ? AND ?";      
		String Sql114 = "SELECT meaning FROM " + generaltable+" WHERE green239 BETWEEN ? AND ?";      

    	
    	
    	
    	String Sql301 = "SELECT meaning FROM " + generaltable+" WHERE blue0 BETWEEN ? AND ?";
    	String Sql302 = "SELECT meaning FROM " + generaltable+" WHERE blue8 BETWEEN ? AND ?";
    	String Sql303 = "SELECT meaning FROM " + generaltable+" WHERE blue16 BETWEEN ? AND ?";
    	String Sql304 = "SELECT meaning FROM " + generaltable+" WHERE blue24 BETWEEN ? AND ?";
    	String Sql305 = "SELECT meaning FROM " + generaltable+" WHERE blue33 BETWEEN ? AND ?";
    	String Sql306 = "SELECT meaning FROM " + generaltable+" WHERE blue41 BETWEEN ? AND ?";
    	String Sql307 = "SELECT meaning FROM " + generaltable+" WHERE blue49 BETWEEN ? AND ?";
    	String Sql308 = "SELECT meaning FROM " + generaltable+" WHERE blue57 BETWEEN ? AND ?";
    	String Sql309 = "SELECT meaning FROM " + generaltable+" WHERE blue66 BETWEEN ? AND ?";
    	String Sql3010 = "SELECT meaning FROM " + generaltable+" WHERE blue74 BETWEEN ? AND ?";
    	String Sql3011 = "SELECT meaning FROM " + generaltable+" WHERE blue82 BETWEEN ? AND ?";
    	String Sql3012 = "SELECT meaning FROM " + generaltable+" WHERE blue90 BETWEEN ? AND ?";
    	String Sql3013 = "SELECT meaning FROM " + generaltable+" WHERE blue99 BETWEEN ? AND ?";
    	String Sql3014 = "SELECT meaning FROM " + generaltable+" WHERE blue107 BETWEEN ? AND ?";
    	String Sql3015 = "SELECT meaning FROM " + generaltable+" WHERE blue115 BETWEEN ? AND ?";
    	String Sql3016 = "SELECT meaning FROM " + generaltable+" WHERE blue123 BETWEEN ? AND ?";
    	String Sql3017 = "SELECT meaning FROM " + generaltable+" WHERE blue132 BETWEEN ? AND ?";
    	String Sql3018 = "SELECT meaning FROM " + generaltable+" WHERE blue140 BETWEEN ? AND ?";
    	String Sql3019 = "SELECT meaning FROM " + generaltable+" WHERE blue148 BETWEEN ? AND ?";
    	String Sql3020 = "SELECT meaning FROM " + generaltable+" WHERE blue156 BETWEEN ? AND ?";
    	String Sql3021 = "SELECT meaning FROM " + generaltable+" WHERE blue165 BETWEEN ? AND ?";
    	String Sql3022 = "SELECT meaning FROM " + generaltable+" WHERE blue173 BETWEEN ? AND ?";
    	String Sql3023 = "SELECT meaning FROM " + generaltable+" WHERE blue181 BETWEEN ? AND ?";
    	String Sql3024 = "SELECT meaning FROM " + generaltable+" WHERE blue189 BETWEEN ? AND ?";
    	String Sql3025 = "SELECT meaning FROM " + generaltable+" WHERE blue198 BETWEEN ? AND ?";
    	String Sql3026 = "SELECT meaning FROM " + generaltable+" WHERE blue206 BETWEEN ? AND ?";
    	String Sql3027 = "SELECT meaning FROM " + generaltable+" WHERE blue214 BETWEEN ? AND ?";
    	String Sql3028 = "SELECT meaning FROM " + generaltable+" WHERE blue222 BETWEEN ? AND ?";
    	String Sql3029 = "SELECT meaning FROM " + generaltable+" WHERE blue231 BETWEEN ? AND ?";
    	String Sql3030 = "SELECT meaning FROM " + generaltable+" WHERE blue239 BETWEEN ? AND ?";
    	String Sql3031 = "SELECT meaning FROM " + generaltable+" WHERE blue247 BETWEEN ? AND ?";
    	String Sql3032 = "SELECT meaning FROM " + generaltable+" WHERE blue255 BETWEEN ? AND ?";



    	//////////////////////////////////////////////////////////////RED CURSORS ////////////////////////////////////////////////////
    	
    	String[] bindArgs1={Integer.toString(lowvred0), Integer.toString(highvred0)};
    	String[] bindArgs2={Integer.toString(lowvred8 ), Integer.toString(highvred8)};
    	String[] bindArgs3={Integer.toString(lowvred16 ), Integer.toString(highvred16)};
    	String[] bindArgs4={Integer.toString(lowvred24 ), Integer.toString(highvred24)}; 
    	String[] bindArgs5={Integer.toString(lowvred33 ), Integer.toString(highvred33)};
    	String[] bindArgs6={Integer.toString(lowvred41 ), Integer.toString(highvred41)};
    	String[] bindArgs7={Integer.toString(lowvred49 ), Integer.toString(highvred49)}; 
    	String[] bindArgs8={Integer.toString(lowvred57 ), Integer.toString(highvred57)};
    	String[] bindArgs26={Integer.toString(lowvred206), Integer.toString(highvred206)};
    	String[] bindArgs27={Integer.toString(lowvred214), Integer.toString(highvred214)};
    	String[] bindArgs28={Integer.toString(lowvred222), Integer.toString(highvred222)};
    	String[] bindArgs29={Integer.toString(lowvred231), Integer.toString(highvred231)};
    	String[] bindArgs30={Integer.toString(lowvred239), Integer.toString(highvred239)};
    	String[] bindArgs31={Integer.toString(lowvred247), Integer.toString(highvred247)};
    	String[] bindArgs32={Integer.toString(lowvred255), Integer.toString(highvred255)};
 	   	String[] bindArgs9={Integer.toString(lowvred66 ), Integer.toString(highvred66)}; 
    	String[] bindArgs10={Integer.toString(lowvred74 ), Integer.toString(highvred74)};
    	String[] bindArgs11={Integer.toString(lowvred82 ), Integer.toString(highvred82)};
    	String[] bindArgs12={Integer.toString(lowvred90 ), Integer.toString(highvred90)}; 
    	String[] bindArgs13={Integer.toString(lowvred99 ), Integer.toString(highvred99)};
    	String[] bindArgs14={Integer.toString(lowvred107 ), Integer.toString(highvred107)};
    	String[] bindArgs15={Integer.toString(lowvred115 ), Integer.toString(highvred115)};
    	String[] bindArgs16={Integer.toString(lowvred123 ), Integer.toString(highvred123)};
    	String[] bindArgs17={Integer.toString(lowvred132 ), Integer.toString(highvred132)}; 
    	String[] bindArgs18={Integer.toString(lowvred140 ), Integer.toString(highvred140)};
    	String[] bindArgs19={Integer.toString(lowvred148 ), Integer.toString(highvred148)};
    	String[] bindArgs20={Integer.toString(lowvred156 ), Integer.toString(highvred156)}; 
    	String[] bindArgs21={Integer.toString(lowvred165 ), Integer.toString(highvred165)};
    	String[] bindArgs22={Integer.toString(lowvred173 ), Integer.toString(highvred173)};
    	String[] bindArgs23={Integer.toString(lowvred181 ), Integer.toString(highvred181)};
    	String[] bindArgs24={Integer.toString(lowvred189 ), Integer.toString(highvred189)};
    	String[] bindArgs25={Integer.toString(lowvred198 ), Integer.toString(highvred198)};

    	///////////////////////green/////////////////////////////////////////////////////
    	String[] bindArgs51={Integer.toString(lowvgreen0), Integer.toString(highvgreen0)};
    	String[] bindArgs52={Integer.toString(lowvgreen4), Integer.toString(highvgreen4)};
    	String[] bindArgs53={Integer.toString(lowvgreen8), Integer.toString(highvgreen8)};
    	String[] bindArgs54={Integer.toString(lowvgreen12), Integer.toString(highvgreen12)}; 
    	String[] bindArgs55={Integer.toString(lowvgreen16), Integer.toString(highvgreen16)};
    	String[] bindArgs56={Integer.toString(lowvgreen20), Integer.toString(highvgreen20)};
    	String[] bindArgs57={Integer.toString(lowvgreen24), Integer.toString(highvgreen24)}; 
    	String[] bindArgs58={Integer.toString(lowvgreen28), Integer.toString(highvgreen28)};
    	String[] bindArgs59={Integer.toString(lowvgreen32), Integer.toString(highvgreen32)};
    	String[] bindArgs60={Integer.toString(lowvgreen36), Integer.toString(highvgreen36)};
    	String[] bindArgs61={Integer.toString(lowvgreen40), Integer.toString(highvgreen40)};
    	String[] bindArgs62={Integer.toString(lowvgreen44), Integer.toString(highvgreen44)};
    	String[] bindArgs63={Integer.toString(lowvgreen48), Integer.toString(highvgreen48)}; 
    	String[] bindArgs64={Integer.toString(lowvgreen52), Integer.toString(highvgreen52)};
    	String[] bindArgs65={Integer.toString(lowvgreen56), Integer.toString(highvgreen56)};
    	String[] bindArgs66={Integer.toString(lowvgreen60), Integer.toString(highvgreen60)};
    	String[]bindArgs67={Integer.toString(lowvgreen65), Integer.toString(highvgreen65)};
    	String[]bindArgs68={Integer.toString(lowvgreen69), Integer.toString(highvgreen69)};
    	String[]bindArgs69={Integer.toString(lowvgreen73), Integer.toString(highvgreen73)};
    	String[]bindArgs70={Integer.toString(lowvgreen77), Integer.toString(highvgreen77)}; 
    	String[]bindArgs71={Integer.toString(lowvgreen81), Integer.toString(highvgreen81)};
    	String[]bindArgs72={Integer.toString(lowvgreen85), Integer.toString(highvgreen85)};
    	String[]bindArgs73={Integer.toString(lowvgreen89), Integer.toString(highvgreen89)};
    	String[]bindArgs74={Integer.toString(lowvgreen93), Integer.toString(highvgreen93)};
    	String[]bindArgs75={Integer.toString(lowvgreen97), Integer.toString(highvgreen97)}; 
    	String[]bindArgs76={Integer.toString(lowvgreen101), Integer.toString(highvgreen101)};
    	String[]bindArgs77={Integer.toString(lowvgreen105), Integer.toString(highvgreen105)};
    	String[]bindArgs78={Integer.toString(lowvgreen109), Integer.toString(highvgreen109)}; 
    	String[]bindArgs79={Integer.toString(lowvgreen113), Integer.toString(highvgreen113)};
    	String[]bindArgs80={Integer.toString(lowvgreen117), Integer.toString(highvgreen117)};
    	String[]bindArgs81={Integer.toString(lowvgreen121), Integer.toString(highvgreen121)};
    	String[]bindArgs82={Integer.toString(lowvgreen125), Integer.toString(highvgreen125)};
    	String[]bindArgs83={Integer.toString(lowvgreen130), Integer.toString(highvgreen130)};
    	String[]bindArgs84={Integer.toString(lowvgreen134), Integer.toString(highvgreen134)};
    	String[]bindArgs85={Integer.toString(lowvgreen138), Integer.toString(highvgreen138)};
    	String[]bindArgs86={Integer.toString(lowvgreen142), Integer.toString(highvgreen142)};
    	String[]bindArgs87={Integer.toString(lowvgreen146), Integer.toString(highvgreen146)}; 
    	String[]bindArgs88={Integer.toString(lowvgreen150), Integer.toString(highvgreen150)};
    	String[]bindArgs89={Integer.toString(lowvgreen154), Integer.toString(highvgreen154)};
    	String[]bindArgs90={Integer.toString(lowvgreen158), Integer.toString(highvgreen158)}; 
    	String[]bindArgs91={Integer.toString(lowvgreen162), Integer.toString(highvgreen162)};
    	String[]bindArgs92={Integer.toString(lowvgreen166), Integer.toString(highvgreen166)};
    	String[]bindArgs93={Integer.toString(lowvgreen170), Integer.toString(highvgreen170)};
    	String[]bindArgs94={Integer.toString(lowvgreen174), Integer.toString(highvgreen174)};
    	String[]bindArgs95={Integer.toString(lowvgreen178), Integer.toString(highvgreen178)};
    	String[]bindArgs96={Integer.toString(lowvgreen182), Integer.toString(highvgreen182)};
    	String[]bindArgs97={Integer.toString(lowvgreen186), Integer.toString(highvgreen186)};
    	String[]bindArgs98={Integer.toString(lowvgreen190), Integer.toString(highvgreen190)};
    	String[]bindArgs99={Integer.toString(lowvgreen195), Integer.toString(highvgreen195)}; 
    	String[]bindArgs100={Integer.toString(lowvgreen199), Integer.toString(highvgreen199)};
    	String[]bindArgs101={Integer.toString(lowvgreen203), Integer.toString(highvgreen203)};
    	String[]bindArgs102={Integer.toString(lowvgreen207), Integer.toString(highvgreen207)}; 
    	String[]bindArgs103={Integer.toString(lowvgreen211), Integer.toString(highvgreen211)};
    	String[]bindArgs104={Integer.toString(lowvgreen215), Integer.toString(highvgreen215)};
    	String[]bindArgs105={Integer.toString(lowvgreen219), Integer.toString(highvgreen219)};
    	String[]bindArgs106={Integer.toString(lowvgreen219), Integer.toString(highvgreen219)};
    	String[]bindArgs107={Integer.toString(lowvgreen227), Integer.toString(highvgreen227)}; 
    	String[]bindArgs108={Integer.toString(lowvgreen231), Integer.toString(highvgreen231)};
    	String[]bindArgs109={Integer.toString(lowvgreen235), Integer.toString(highvgreen235)};
    	String[]bindArgs110={Integer.toString(lowvgreen239), Integer.toString(highvgreen239)}; 
    	String[]bindArgs111={Integer.toString(lowvgreen243), Integer.toString(highvgreen243)};
    	String[]bindArgs112={Integer.toString(lowvgreen247), Integer.toString(highvgreen247)};
    	String[]bindArgs113={Integer.toString(lowvgreen247), Integer.toString(highvgreen247)};
    	String[]bindArgs114={Integer.toString(lowvgreen255), Integer.toString(highvgreen255)};



    	/////////////////////////////////blue///////////////////////////////////////
    	String[] bindArgs301={Integer.toString(lowvblue0), Integer.toString(highvblue0)};
    	String[] bindArgs302={Integer.toString(lowvblue8 ), Integer.toString(highvblue8)};
    	String[] bindArgs303={Integer.toString(lowvblue16 ), Integer.toString(highvblue16)};
    	String[] bindArgs304={Integer.toString(lowvblue24 ), Integer.toString(highvblue24)}; 
    	String[] bindArgs305={Integer.toString(lowvblue33 ), Integer.toString(highvblue33)};
    	String[] bindArgs306={Integer.toString(lowvblue41 ), Integer.toString(highvblue41)};
    	String[] bindArgs307={Integer.toString(lowvblue49 ), Integer.toString(highvblue49)}; 
    	String[] bindArgs308={Integer.toString(lowvblue57 ), Integer.toString(highvblue57)};
    	String[] bindArgs3026={Integer.toString(lowvblue206), Integer.toString(highvblue206)};
    	String[] bindArgs3027={Integer.toString(lowvblue214), Integer.toString(highvblue214)};
    	String[] bindArgs3028={Integer.toString(lowvblue222), Integer.toString(highvblue222)};
    	String[] bindArgs3029={Integer.toString(lowvblue231), Integer.toString(highvblue231)};
    	String[] bindArgs3030={Integer.toString(lowvblue239), Integer.toString(highvblue239)};
    	String[] bindArgs3031={Integer.toString(lowvblue247), Integer.toString(highvblue247)};
    	String[] bindArgs3032={Integer.toString(lowvblue255), Integer.toString(highvblue255)};
    	String[] bindArgs309={Integer.toString(lowvblue66 ), Integer.toString(highvblue66)}; 
    	String[] bindArgs3010={Integer.toString(lowvblue74 ), Integer.toString(highvblue74)};
    	String[] bindArgs3011={Integer.toString(lowvblue82 ), Integer.toString(highvblue82)};
    	String[] bindArgs3012={Integer.toString(lowvblue90 ), Integer.toString(highvblue90)}; 
    	String[] bindArgs3013={Integer.toString(lowvblue99 ), Integer.toString(highvblue99)};
    	String[] bindArgs3014={Integer.toString(lowvblue107 ), Integer.toString(highvblue107)};
    	String[] bindArgs3015={Integer.toString(lowvblue115 ), Integer.toString(highvblue115)};
    	String[] bindArgs3016={Integer.toString(lowvblue123 ), Integer.toString(highvblue123)};
    	String[] bindArgs3017={Integer.toString(lowvblue132 ), Integer.toString(highvblue132)}; 
    	String[] bindArgs3018={Integer.toString(lowvblue140 ), Integer.toString(highvblue140)};
    	String[] bindArgs3019={Integer.toString(lowvblue148 ), Integer.toString(highvblue148)};
    	String[] bindArgs3020={Integer.toString(lowvblue156 ), Integer.toString(highvblue156)}; 
    	String[] bindArgs3021={Integer.toString(lowvblue165 ), Integer.toString(highvblue165)};
    	String[] bindArgs3022={Integer.toString(lowvblue173 ), Integer.toString(highvblue173)};
    	String[] bindArgs3023={Integer.toString(lowvblue181 ), Integer.toString(highvblue181)};
    	String[] bindArgs3024={Integer.toString(lowvblue189 ), Integer.toString(highvblue189)};
    	String[] bindArgs3025={Integer.toString(lowvblue198 ), Integer.toString(highvblue198)};

    	
    	
    	

    	Cursor cursor1 = myDataBase.rawQuery(Sql1, bindArgs1);
    	Cursor cursor2 = myDataBase.rawQuery(Sql2, bindArgs2);
    	Cursor cursor3 = myDataBase.rawQuery(Sql3, bindArgs3);
    	Cursor cursor4 = myDataBase.rawQuery(Sql4, bindArgs4);
    	Cursor cursor5 = myDataBase.rawQuery(Sql5, bindArgs5);
    	Cursor cursor6 = myDataBase.rawQuery(Sql6, bindArgs6);
    	Cursor cursor7 = myDataBase.rawQuery(Sql7, bindArgs7);
    	Cursor cursor8 = myDataBase.rawQuery(Sql8, bindArgs8);
    	Cursor cursor9 = myDataBase.rawQuery(Sql9, bindArgs9);
    	Cursor cursor10 = myDataBase.rawQuery(Sql10, bindArgs10);
    	Cursor cursor11 = myDataBase.rawQuery(Sql11, bindArgs11);
    	Cursor cursor12 = myDataBase.rawQuery(Sql12, bindArgs12);
    	Cursor cursor13 = myDataBase.rawQuery(Sql13, bindArgs13);
    	Cursor cursor14 = myDataBase.rawQuery(Sql14, bindArgs14);
    	Cursor cursor15 = myDataBase.rawQuery(Sql15, bindArgs15);
    	Cursor cursor16 = myDataBase.rawQuery(Sql16, bindArgs16);
    	Cursor cursor17 = myDataBase.rawQuery(Sql17, bindArgs17);
    	Cursor cursor18 = myDataBase.rawQuery(Sql18, bindArgs18);
    	Cursor cursor19 = myDataBase.rawQuery(Sql19, bindArgs19);
    	Cursor cursor20 = myDataBase.rawQuery(Sql20, bindArgs20);
    	Cursor cursor21 = myDataBase.rawQuery(Sql21, bindArgs21);
    	Cursor cursor22 = myDataBase.rawQuery(Sql22, bindArgs22);
    	Cursor cursor23 = myDataBase.rawQuery(Sql23, bindArgs23);
    	Cursor cursor24 = myDataBase.rawQuery(Sql24, bindArgs24);
    	Cursor cursor25 = myDataBase.rawQuery(Sql25, bindArgs25);
    	Cursor cursor26 = myDataBase.rawQuery(Sql26, bindArgs26);
    	Cursor cursor27 = myDataBase.rawQuery(Sql27, bindArgs27);
    	Cursor cursor28 = myDataBase.rawQuery(Sql28, bindArgs28);
    	Cursor cursor29 = myDataBase.rawQuery(Sql29, bindArgs29);
    	Cursor cursor30 = myDataBase.rawQuery(Sql30, bindArgs30);
    	Cursor cursor31 = myDataBase.rawQuery(Sql31, bindArgs31);
    	Cursor cursor32 = myDataBase.rawQuery(Sql32, bindArgs32);

    	
    	Cursor cursor51 =myDataBase.rawQuery(Sql51, bindArgs51);           
    	Cursor cursor52 =myDataBase.rawQuery(Sql52, bindArgs52);           
    	Cursor cursor53 =myDataBase.rawQuery(Sql53, bindArgs53);           
    	Cursor cursor54 =myDataBase.rawQuery(Sql54, bindArgs54);           
    	Cursor cursor55 =myDataBase.rawQuery(Sql55, bindArgs55);           
    	Cursor cursor56 =myDataBase.rawQuery(Sql56, bindArgs56);           
    	Cursor cursor57 =myDataBase.rawQuery(Sql57, bindArgs57);           
    	Cursor cursor58 =myDataBase.rawQuery(Sql58, bindArgs58);           
    	Cursor cursor59 =myDataBase.rawQuery(Sql59, bindArgs59);           
    	Cursor cursor60 =myDataBase.rawQuery(Sql60, bindArgs60);           
    	Cursor cursor61 =myDataBase.rawQuery(Sql61, bindArgs61);           
    	Cursor cursor62 =myDataBase.rawQuery(Sql62, bindArgs62);           
    	Cursor cursor63 =myDataBase.rawQuery(Sql63, bindArgs63);           
    	Cursor cursor64 =myDataBase.rawQuery(Sql64, bindArgs64);           
    	Cursor cursor65 =myDataBase.rawQuery(Sql65, bindArgs65);           
    	Cursor cursor66 =myDataBase.rawQuery(Sql66, bindArgs66);           
    	Cursor cursor67 =myDataBase.rawQuery(Sql67, bindArgs67);           
    	Cursor cursor68 =myDataBase.rawQuery(Sql68, bindArgs68);           
    	Cursor cursor69 =myDataBase.rawQuery(Sql69, bindArgs69);           
    	Cursor cursor70 =myDataBase.rawQuery(Sql70, bindArgs70);           
    	Cursor cursor71 =myDataBase.rawQuery(Sql71, bindArgs71);           
    	Cursor cursor72 =myDataBase.rawQuery(Sql72, bindArgs72);           
    	Cursor cursor73 =myDataBase.rawQuery(Sql73, bindArgs73);           
    	Cursor cursor74 =myDataBase.rawQuery(Sql74, bindArgs74);           
    	Cursor cursor75 =myDataBase.rawQuery(Sql75, bindArgs75);           
    	Cursor cursor76 =myDataBase.rawQuery(Sql76, bindArgs76);           
    	Cursor cursor77 =myDataBase.rawQuery(Sql77, bindArgs77);           
    	Cursor cursor78 =myDataBase.rawQuery(Sql78, bindArgs78);           
    	Cursor cursor79 =myDataBase.rawQuery(Sql79, bindArgs79);           
    	Cursor cursor80 =myDataBase.rawQuery(Sql80, bindArgs80);           
    	Cursor cursor81 =myDataBase.rawQuery(Sql81, bindArgs81);           
    	Cursor cursor82 =myDataBase.rawQuery(Sql82, bindArgs82);           
    	Cursor cursor83 =myDataBase.rawQuery(Sql83, bindArgs83);           
    	Cursor cursor84 =myDataBase.rawQuery(Sql84, bindArgs84);           
    	Cursor cursor85 =myDataBase.rawQuery(Sql85, bindArgs85);           
    	Cursor cursor86 =myDataBase.rawQuery(Sql86, bindArgs86);           
    	Cursor cursor87 =myDataBase.rawQuery(Sql87, bindArgs87);           
    	Cursor cursor88 =myDataBase.rawQuery(Sql88, bindArgs88);           
    	Cursor cursor89 =myDataBase.rawQuery(Sql89, bindArgs89);           
    	Cursor cursor90 =myDataBase.rawQuery(Sql90, bindArgs90);           
    	Cursor cursor91 =myDataBase.rawQuery(Sql91, bindArgs91);           
    	Cursor cursor92 =myDataBase.rawQuery(Sql92, bindArgs92);           
    	Cursor cursor93 =myDataBase.rawQuery(Sql93, bindArgs93);           
    	Cursor cursor94 =myDataBase.rawQuery(Sql94, bindArgs94);           
    	Cursor cursor95 =myDataBase.rawQuery(Sql95, bindArgs95);           
    	Cursor cursor96 =myDataBase.rawQuery(Sql96, bindArgs96);           
    	Cursor cursor97 =myDataBase.rawQuery(Sql97, bindArgs97);           
    	Cursor cursor98 =myDataBase.rawQuery(Sql98, bindArgs98);           
    	Cursor cursor99 =myDataBase.rawQuery(Sql99, bindArgs99);           
    	Cursor cursor100 =myDataBase.rawQuery(Sql100, bindArgs100);           
    	Cursor cursor101 =myDataBase.rawQuery(Sql101, bindArgs101);           
    	Cursor cursor102 =myDataBase.rawQuery(Sql102, bindArgs102);           
    	Cursor cursor103 =myDataBase.rawQuery(Sql103, bindArgs103);           
    	Cursor cursor104 =myDataBase.rawQuery(Sql104, bindArgs104);           
    	Cursor cursor105 =myDataBase.rawQuery(Sql105, bindArgs105);           
    	Cursor cursor106 =myDataBase.rawQuery(Sql106, bindArgs106);           
    	Cursor cursor107 =myDataBase.rawQuery(Sql107, bindArgs107);           
    	Cursor cursor108 =myDataBase.rawQuery(Sql108, bindArgs108);           
    	Cursor cursor109 =myDataBase.rawQuery(Sql109, bindArgs109);           
    	Cursor cursor110 =myDataBase.rawQuery(Sql110, bindArgs110);           
    	Cursor cursor111 =myDataBase.rawQuery(Sql111, bindArgs111);           
    	Cursor cursor112 =myDataBase.rawQuery(Sql112, bindArgs112);           
    	Cursor cursor113 =myDataBase.rawQuery(Sql113, bindArgs113);           
    	Cursor cursor114 =myDataBase.rawQuery(Sql114, bindArgs114);     

    	
    	Cursor  cursor301 = myDataBase.rawQuery(Sql301, bindArgs301);
    	Cursor  cursor302 = myDataBase.rawQuery(Sql302, bindArgs302);
    	Cursor  cursor303 = myDataBase.rawQuery(Sql303, bindArgs303);
    	Cursor  cursor304 = myDataBase.rawQuery(Sql304, bindArgs304);
    	Cursor  cursor305 = myDataBase.rawQuery(Sql305, bindArgs305);
    	Cursor  cursor306 = myDataBase.rawQuery(Sql306, bindArgs306);
    	Cursor  cursor307 = myDataBase.rawQuery(Sql307, bindArgs307);
    	Cursor  cursor308 = myDataBase.rawQuery(Sql308, bindArgs308);
    	Cursor  cursor309 = myDataBase.rawQuery(Sql309, bindArgs309);
    	Cursor  cursor3010 = myDataBase.rawQuery(Sql3010, bindArgs3010);
    	Cursor  cursor3011 = myDataBase.rawQuery(Sql3011, bindArgs3011);
    	Cursor  cursor3012 = myDataBase.rawQuery(Sql3012, bindArgs3012);
    	Cursor  cursor3013 = myDataBase.rawQuery(Sql3013, bindArgs3013);
    	Cursor  cursor3014 = myDataBase.rawQuery(Sql3014, bindArgs3014);
    	Cursor  cursor3015 = myDataBase.rawQuery(Sql3015, bindArgs3015);
    	Cursor  cursor3016 = myDataBase.rawQuery(Sql3016, bindArgs3016);
    	Cursor  cursor3017 = myDataBase.rawQuery(Sql3017, bindArgs3017);
    	Cursor  cursor3018 = myDataBase.rawQuery(Sql3018, bindArgs3018);
    	Cursor  cursor3019 = myDataBase.rawQuery(Sql3019, bindArgs3019);
    	Cursor  cursor3020 = myDataBase.rawQuery(Sql3020, bindArgs3020);
    	Cursor  cursor3021 = myDataBase.rawQuery(Sql3021, bindArgs3021);
    	Cursor  cursor3022 = myDataBase.rawQuery(Sql3022, bindArgs3022);
    	Cursor  cursor3023 = myDataBase.rawQuery(Sql3023, bindArgs3023);
    	Cursor  cursor3024 = myDataBase.rawQuery(Sql3024, bindArgs3024);
    	Cursor  cursor3025 = myDataBase.rawQuery(Sql3025, bindArgs3025);
    	Cursor  cursor3026 = myDataBase.rawQuery(Sql3026, bindArgs3026);
    	Cursor  cursor3027 = myDataBase.rawQuery(Sql3027, bindArgs3027);
    	Cursor  cursor3028 = myDataBase.rawQuery(Sql3028, bindArgs3028);
    	Cursor  cursor3029 = myDataBase.rawQuery(Sql3029, bindArgs3029);
    	Cursor  cursor3030 = myDataBase.rawQuery(Sql3030, bindArgs3030);
    	Cursor  cursor3031 = myDataBase.rawQuery(Sql3031, bindArgs3031);
    	Cursor  cursor3032 = myDataBase.rawQuery(Sql3032, bindArgs3032);

    	if (cursor1 == null){
			cursor1.close();
		}
		if (cursor1.getCount()==1){
			cursor1.moveToFirst();
			String id = cursor1.getString(0);
			imageids.add(id);
		}else{
		while (cursor1.moveToNext()){
		String id = cursor1.getString(0);
		imageids.add(id);

		}
		}
		cursor1.close();
		////////////////////////////////////////////////////////////////////////
		
		if (cursor2 == null){
			cursor2.close();
		}
		if (cursor2.getCount()==1){
			cursor2.moveToFirst();
			String id = cursor2.getString(0);
			imageids.add(id);
		}else{
		while (cursor2.moveToNext()){
		String id = cursor2.getString(0);
		imageids.add(id);

		}
		}
		cursor2.close();

		////////////////////////////////////////////////////////////////////////
		
		if (cursor3 == null){
			cursor3.close();
		}
		if (cursor3.getCount()==1){
			cursor3.moveToFirst();
			String id = cursor3.getString(0);
			imageids.add(id);
		}else{
		while (cursor3.moveToNext()){
		String id = cursor3.getString(0);
		imageids.add(id);

		}
		}
		cursor3.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor4 == null){
			cursor4.close();
		}
		if (cursor4.getCount()==1){
			cursor4.moveToFirst();
			String id = cursor4.getString(0);
			imageids.add(id);
		}else{
		while (cursor4.moveToNext()){
		String id = cursor4.getString(0);
		imageids.add(id);
		
		}
		}
		cursor4.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor5 == null){
			cursor5.close();
		}
		if (cursor5.getCount()==1){
			cursor5.moveToFirst();
			String id = cursor5.getString(0);
			imageids.add(id);
		}else{
		while (cursor5.moveToNext()){
		String id = cursor5.getString(0);
		imageids.add(id);

		}
		}
		cursor5.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor6 == null){
			cursor6.close();
		}
		if (cursor6.getCount()==1){
			cursor6.moveToFirst();
			String id = cursor6.getString(0);
			imageids.add(id);
		}else{
		while (cursor6.moveToNext()){
		String id = cursor6.getString(0);
		imageids.add(id);

		}
		}
		cursor6.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor7 == null){
			cursor7.close();
		}
		if (cursor7.getCount()==1){
			cursor7.moveToFirst();
			String id = cursor7.getString(0);
			imageids.add(id);
		}else{
		while (cursor7.moveToNext()){
		String id = cursor7.getString(0);
		imageids.add(id);

		}
		}
		cursor7.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor8 == null){
			cursor8.close();
		}
		if (cursor8.getCount()==1){
			cursor8.moveToFirst();
			String id = cursor8.getString(0);
			imageids.add(id);
		}else{
		while (cursor8.moveToNext()){
		String id = cursor8.getString(0);
		imageids.add(id);

		}
		}
		cursor8.close();

		///////////////////////////////////////////////////////////////////////
		if (cursor9 == null){
			cursor9.close();
		}
		if (cursor9.getCount()==1){
			cursor9.moveToFirst();
			String id = cursor9.getString(0);
			imageids.add(id);
		}else{
		while (cursor9.moveToNext()){
		String id = cursor9.getString(0);
		imageids.add(id);

		}
		}
		cursor9.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor10 == null){
			cursor10.close();
		}
		if (cursor10.getCount()==1){
			cursor10.moveToFirst();
			String id = cursor10.getString(0);
			imageids.add(id);
		}else{
		while (cursor10.moveToNext()){
		String id = cursor10.getString(0);
		imageids.add(id);

		}
		}
		cursor10.close();

		//////////////////////////////////////////////////////////////////////
		if (cursor11 == null){
			cursor11.close();
		}
		if (cursor11.getCount()==1){
			cursor11.moveToFirst();
			String id = cursor11.getString(0);
			imageids.add(id);
		}else{
		while (cursor11.moveToNext()){
		String id = cursor11.getString(0);
		imageids.add(id);

		}
		}
		cursor11.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor12 == null){
			cursor12.close();
		}
		if (cursor12.getCount()==1){
			cursor12.moveToFirst();
			String id = cursor12.getString(0);
			imageids.add(id);
		}else{
		while (cursor12.moveToNext()){
		String id = cursor12.getString(0);
		imageids.add(id);

		}
		}
		cursor12.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor13 == null){
			cursor13.close();
		}
		if (cursor13.getCount()==1){
			cursor13.moveToFirst();
			String id = cursor13.getString(0);
			imageids.add(id);
		}else{
		while (cursor13.moveToNext()){
		String id = cursor13.getString(0);
		imageids.add(id);

		}
		}
		cursor13.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor14 == null){
			cursor14.close();
		}
		if (cursor14.getCount()==1){
			cursor14.moveToFirst();
			String id = cursor14.getString(0);
			imageids.add(id);
		}else{
		while (cursor14.moveToNext()){
		String id = cursor14.getString(0);
		imageids.add(id);

		}
		}
		cursor14.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor15 == null){
			cursor15.close();
		}
		if (cursor15.getCount()==1){
			cursor15.moveToFirst();
			String id = cursor15.getString(0);
			imageids.add(id);
		}else{
		while (cursor15.moveToNext()){
		String id = cursor15.getString(0);
		imageids.add(id);

		}
		}
		cursor15.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor16 == null){
			cursor16.close();
		}
		if (cursor16.getCount()==1){
			cursor16.moveToFirst();
			String id = cursor16.getString(0);
			imageids.add(id);
		}else{
		while (cursor16.moveToNext()){
		String id = cursor16.getString(0);
		imageids.add(id);

		}
		}
		cursor16.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor17 == null){
			cursor17.close();
		}
		if (cursor17.getCount()==1){
			cursor17.moveToFirst();
			String id = cursor17.getString(0);
			imageids.add(id);
		}else{
		while (cursor17.moveToNext()){
		String id = cursor17.getString(0);
		imageids.add(id);

		}
		}
		cursor17.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor18 == null){
			cursor18.close();
		}
		if (cursor18.getCount()==1){
			cursor18.moveToFirst();
			String id = cursor18.getString(0);
			imageids.add(id);
		}else{
		while (cursor18.moveToNext()){
		String id = cursor18.getString(0);
		imageids.add(id);

		}
		}
		cursor18.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor19 == null){
			cursor19.close();
		}
		if (cursor19.getCount()==1){
			cursor19.moveToFirst();
			String id = cursor19.getString(0);
			imageids.add(id);
		}else{
		while (cursor19.moveToNext()){
		String id = cursor19.getString(0);
		imageids.add(id);

		}
		}
		cursor19.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor20 == null){
			cursor20.close();
		}
		if (cursor20.getCount()==1){
			cursor20.moveToFirst();
			String id = cursor20.getString(0);
			imageids.add(id);
		}else{
		while (cursor20.moveToNext()){
		String id = cursor20.getString(0);
		imageids.add(id);

		}
		}
		cursor20.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor21 == null){
			cursor21.close();
		}
		if (cursor21.getCount()==1){
			cursor21.moveToFirst();
			String id = cursor21.getString(0);
			imageids.add(id);
		}else{
		while (cursor21.moveToNext()){
		String id = cursor21.getString(0);
		imageids.add(id);

		}
		}
		cursor21.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor22 == null){
			cursor22.close();
		}
		if (cursor22.getCount()==1){
			cursor22.moveToFirst();
			String id = cursor22.getString(0);
			imageids.add(id);
		}else{
		while (cursor22.moveToNext()){
		String id = cursor22.getString(0);
		imageids.add(id);

		}
		}
		cursor22.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor23 == null){
			cursor23.close();
		}
		if (cursor23.getCount()==1){
			cursor23.moveToFirst();
			String id = cursor23.getString(0);
			imageids.add(id);
		}else{
		while (cursor23.moveToNext()){
		String id = cursor23.getString(0);
		imageids.add(id);

		}
		}
		cursor23.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor24 == null){
			cursor24.close();
		}
		if (cursor24.getCount()==1){
			cursor24.moveToFirst();
			String id = cursor24.getString(0);
			imageids.add(id);
		}else{
		while (cursor24.moveToNext()){
		String id = cursor24.getString(0);
		imageids.add(id);

		}
		}
		cursor24.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor25 == null){
			cursor25.close();
		}
		if (cursor25.getCount()==1){
			cursor25.moveToFirst();
			String id = cursor25.getString(0);
			imageids.add(id);
		}else{
		while (cursor25.moveToNext()){
		String id = cursor25.getString(0);
		imageids.add(id);

		}
		}
		cursor25.close();

		//////////////////////////////////////////////////////////////////////
		if (cursor26 == null){
			cursor26.close();
		}
		if (cursor26.getCount()==1){
			cursor26.moveToFirst();
			String id = cursor26.getString(0);
			imageids.add(id);
		}else{
		while (cursor26.moveToNext()){
		String id = cursor26.getString(0);
		imageids.add(id);

		}
		}
		cursor26.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor27 == null){
			cursor27.close();
		}
		if (cursor27.getCount()==1){
			cursor27.moveToFirst();
			String id = cursor27.getString(0);
			imageids.add(id);
		}else{
		while (cursor27.moveToNext()){
		String id = cursor27.getString(0);
		imageids.add(id);

		}
		}
		cursor27.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor28 == null){
			cursor28.close();
		}
		if (cursor28.getCount()==1){
			cursor28.moveToFirst();
			String id = cursor28.getString(0);
			imageids.add(id);
		}else{
		while (cursor28.moveToNext()){
		String id = cursor28.getString(0);
		imageids.add(id);

		}
		}
		cursor28.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor29 == null){
			cursor29.close();
		}
		if (cursor29.getCount()==1){
			cursor29.moveToFirst();
			String id = cursor29.getString(0);
			imageids.add(id);
		}else{
		while (cursor29.moveToNext()){
		String id = cursor29.getString(0);
		imageids.add(id);

		}
		}
		cursor29.close();

		////////////////////////////////////////////////////////////////////////

		////////////////////////////////////////////////////////////////////////
		if (cursor30 == null){
			cursor30.close();
		}
		if (cursor30.getCount()==1){
			cursor30.moveToFirst();
			String id = cursor30.getString(0);
			imageids.add(id);
		}else{
		while (cursor30.moveToNext()){
		String id = cursor30.getString(0);
		imageids.add(id);

		}
		}
		cursor30.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor31 == null){
			cursor31.close();
		}
		if (cursor31.getCount()==1){
			cursor31.moveToFirst();
			String id = cursor31.getString(0);
			imageids.add(id);
		}else{
		while (cursor31.moveToNext()){
		String id = cursor31.getString(0);
		imageids.add(id);

		}
		}
		cursor31.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor32 == null){
			cursor32.close();
		}
		if (cursor32.getCount()==1){
			cursor32.moveToFirst();
			String id = cursor32.getString(0);
			imageids.add(id);
		}else{
		while (cursor32.moveToNext()){
		String id = cursor32.getString(0);
		imageids.add(id);

		}
		}
		cursor32.close();

        ////////////////////////////////////////////////////////////////////////
        
		///////////////////////////////////////////////////////////////GREEN CURSORS ////////////////////////////////////////////////////
		if (cursor51 == null){
			cursor51.close();
		}
		if (cursor51.getCount()==1){
			cursor51.moveToFirst();
			String id = cursor51.getString(0);
			imageids.add(id);
		}else{
		while (cursor51.moveToNext()){
		String id = cursor51.getString(0);
		imageids.add(id);

		}
		}
		cursor51.close();
		////////////////////////////////////////////////////////////////////////
		
		if (cursor52 == null){
			cursor52.close();
		}
		if (cursor52.getCount()==1){
			cursor52.moveToFirst();
			String id = cursor52.getString(0);
			imageids.add(id);
		}else{
		while (cursor52.moveToNext()){
		String id = cursor52.getString(0);
		imageids.add(id);

		}
		}
		cursor52.close();

		////////////////////////////////////////////////////////////////////////
		
		if (cursor53 == null){
			cursor53.close();
		}
		if (cursor53.getCount()==1){
			cursor53.moveToFirst();
			String id = cursor53.getString(0);
			imageids.add(id);
		}else{
		while (cursor53.moveToNext()){
		String id = cursor53.getString(0);
		imageids.add(id);

		}
		}
		cursor53.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor54 == null){
			cursor54.close();
		}
		if (cursor54.getCount()==1){
			cursor54.moveToFirst();
			String id = cursor54.getString(0);
			imageids.add(id);
		}else{
		while (cursor54.moveToNext()){
		String id = cursor54.getString(0);
		imageids.add(id);
		
		}
		}
		cursor54.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor55 == null){
			cursor55.close();
		}
		if (cursor55.getCount()==1){
			cursor55.moveToFirst();
			String id = cursor55.getString(0);
			imageids.add(id);
		}else{
		while (cursor55.moveToNext()){
		String id = cursor55.getString(0);
		imageids.add(id);

		}
		}
		cursor55.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor56 == null){
			cursor56.close();
		}
		if (cursor56.getCount()==1){
			cursor56.moveToFirst();
			String id = cursor56.getString(0);
			imageids.add(id);
		}else{
		while (cursor56.moveToNext()){
		String id = cursor56.getString(0);
		imageids.add(id);

		}
		}
		cursor56.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor57 == null){
			cursor57.close();
		}
		if (cursor57.getCount()==1){
			cursor57.moveToFirst();
			String id = cursor57.getString(0);
			imageids.add(id);
		}else{
		while (cursor57.moveToNext()){
		String id = cursor57.getString(0);
		imageids.add(id);

		}
		}
		cursor57.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor58 == null){
			cursor58.close();
		}
		if (cursor58.getCount()==1){
			cursor58.moveToFirst();
			String id = cursor58.getString(0);
			imageids.add(id);
		}else{
		while (cursor58.moveToNext()){
		String id = cursor58.getString(0);
		imageids.add(id);

		}
		}
		cursor58.close();

		///////////////////////////////////////////////////////////////////////
		if (cursor59 == null){
			cursor59.close();
		}
		if (cursor59.getCount()==1){
			cursor59.moveToFirst();
			String id = cursor59.getString(0);
			imageids.add(id);
		}else{
		while (cursor59.moveToNext()){
		String id = cursor59.getString(0);
		imageids.add(id);

		}
		}
		cursor59.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor60 == null){
			cursor60.close();
		}
		if (cursor60.getCount()==1){
			cursor60.moveToFirst();
			String id = cursor60.getString(0);
			imageids.add(id);
		}else{
		while (cursor60.moveToNext()){
		String id = cursor60.getString(0);
		imageids.add(id);

		}
		}
		cursor60.close();
		////////////////////////////////////////////////////////////////////////
		
if (cursor61 == null){
			cursor61.close();
		}
		if (cursor61.getCount()==1){
			cursor61.moveToFirst();
			String id = cursor61.getString(0);
			imageids.add(id);
		}else{
		while (cursor61.moveToNext()){
		String id = cursor61.getString(0);
		imageids.add(id);

		}
		}
		cursor61.close();
		////////////////////////////////////////////////////////////////////////
		
		if (cursor62 == null){
			cursor62.close();
		}
		if (cursor62.getCount()==1){
			cursor62.moveToFirst();
			String id = cursor62.getString(0);
			imageids.add(id);
		}else{
		while (cursor62.moveToNext()){
		String id = cursor62.getString(0);
		imageids.add(id);

		}
		}
		cursor62.close();

		////////////////////////////////////////////////////////////////////////
		
		if (cursor63 == null){
			cursor63.close();
		}
		if (cursor63.getCount()==1){
			cursor63.moveToFirst();
			String id = cursor63.getString(0);
			imageids.add(id);
		}else{
		while (cursor63.moveToNext()){
		String id = cursor63.getString(0);
		imageids.add(id);

		}
		}
		cursor63.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor64 == null){
			cursor64.close();
		}
		if (cursor64.getCount()==1){
			cursor64.moveToFirst();
			String id = cursor64.getString(0);
			imageids.add(id);
		}else{
		while (cursor64.moveToNext()){
		String id = cursor64.getString(0);
		imageids.add(id);
		
		}
		}
		cursor64.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor65 == null){
			cursor65.close();
		}
		if (cursor65.getCount()==1){
			cursor65.moveToFirst();
			String id = cursor65.getString(0);
			imageids.add(id);
		}else{
		while (cursor65.moveToNext()){
		String id = cursor65.getString(0);
		imageids.add(id);

		}
		}
		cursor65.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor66 == null){
			cursor66.close();
		}
		if (cursor66.getCount()==1){
			cursor66.moveToFirst();
			String id = cursor66.getString(0);
			imageids.add(id);
		}else{
		while (cursor66.moveToNext()){
		String id = cursor66.getString(0);
		imageids.add(id);

		}
		}
		cursor66.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor67 == null){
			cursor67.close();
		}
		if (cursor67.getCount()==1){
			cursor67.moveToFirst();
			String id = cursor67.getString(0);
			imageids.add(id);
		}else{
		while (cursor67.moveToNext()){
		String id = cursor67.getString(0);
		imageids.add(id);

		}
		}
		cursor67.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor68 == null){
			cursor68.close();
		}
		if (cursor68.getCount()==1){
			cursor68.moveToFirst();
			String id = cursor68.getString(0);
			imageids.add(id);
		}else{
		while (cursor68.moveToNext()){
		String id = cursor68.getString(0);
		imageids.add(id);

		}
		}
		cursor68.close();

		///////////////////////////////////////////////////////////////////////
		if (cursor69 == null){
			cursor69.close();
		}
		if (cursor69.getCount()==1){
			cursor69.moveToFirst();
			String id = cursor69.getString(0);
			imageids.add(id);
		}else{
		while (cursor69.moveToNext()){
		String id = cursor69.getString(0);
		imageids.add(id);

		}
		}
		cursor69.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor70 == null){
			cursor70.close();
		}
		if (cursor70.getCount()==1){
			cursor70.moveToFirst();
			String id = cursor70.getString(0);
			imageids.add(id);
		}else{
		while (cursor70.moveToNext()){
		String id = cursor70.getString(0);
		imageids.add(id);

		}
		}
		cursor70.close();
		////////////////////////////////////////////////////////////////////////
		
if (cursor71 == null){
			cursor71.close();
		}
		if (cursor71.getCount()==1){
			cursor71.moveToFirst();
			String id = cursor71.getString(0);
			imageids.add(id);
		}else{
		while (cursor71.moveToNext()){
		String id = cursor71.getString(0);
		imageids.add(id);

		}
		}
		cursor71.close();
		////////////////////////////////////////////////////////////////////////
		
		if (cursor72 == null){
			cursor72.close();
		}
		if (cursor72.getCount()==1){
			cursor72.moveToFirst();
			String id = cursor72.getString(0);
			imageids.add(id);
		}else{
		while (cursor72.moveToNext()){
		String id = cursor72.getString(0);
		imageids.add(id);

		}
		}
		cursor72.close();

		////////////////////////////////////////////////////////////////////////
		
		if (cursor73 == null){
			cursor73.close();
		}
		if (cursor73.getCount()==1){
			cursor73.moveToFirst();
			String id = cursor73.getString(0);
			imageids.add(id);
		}else{
		while (cursor73.moveToNext()){
		String id = cursor73.getString(0);
		imageids.add(id);

		}
		}
		cursor73.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor74 == null){
			cursor74.close();
		}
		if (cursor74.getCount()==1){
			cursor74.moveToFirst();
			String id = cursor74.getString(0);
			imageids.add(id);
		}else{
		while (cursor74.moveToNext()){
		String id = cursor74.getString(0);
		imageids.add(id);
		
		}
		}
		cursor74.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor75 == null){
			cursor75.close();
		}
		if (cursor75.getCount()==1){
			cursor75.moveToFirst();
			String id = cursor75.getString(0);
			imageids.add(id);
		}else{
		while (cursor75.moveToNext()){
		String id = cursor75.getString(0);
		imageids.add(id);

		}
		}
		cursor75.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor76 == null){
			cursor76.close();
		}
		if (cursor76.getCount()==1){
			cursor76.moveToFirst();
			String id = cursor76.getString(0);
			imageids.add(id);
		}else{
		while (cursor76.moveToNext()){
		String id = cursor76.getString(0);
		imageids.add(id);

		}
		}
		cursor76.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor77 == null){
			cursor77.close();
		}
		if (cursor77.getCount()==1){
			cursor77.moveToFirst();
			String id = cursor77.getString(0);
			imageids.add(id);
		}else{
		while (cursor77.moveToNext()){
		String id = cursor77.getString(0);
		imageids.add(id);

		}
		}
		cursor77.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor78 == null){
			cursor78.close();
		}
		if (cursor78.getCount()==1){
			cursor78.moveToFirst();
			String id = cursor78.getString(0);
			imageids.add(id);
		}else{
		while (cursor78.moveToNext()){
		String id = cursor78.getString(0);
		imageids.add(id);

		}
		}
		cursor78.close();

		///////////////////////////////////////////////////////////////////////
		if (cursor79 == null){
			cursor79.close();
		}
		if (cursor79.getCount()==1){
			cursor79.moveToFirst();
			String id = cursor79.getString(0);
			imageids.add(id);
		}else{
		while (cursor79.moveToNext()){
		String id = cursor79.getString(0);
		imageids.add(id);

		}
		}
		cursor79.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor80 == null){
			cursor80.close();
		}
		if (cursor80.getCount()==1){
			cursor80.moveToFirst();
			String id = cursor80.getString(0);
			imageids.add(id);
		}else{
		while (cursor80.moveToNext()){
		String id = cursor80.getString(0);
		imageids.add(id);

		}
		}
		cursor80.close();
		////////////////////////////////////////////////////////////////////////
		
if (cursor81 == null){
			cursor81.close();
		}
		if (cursor81.getCount()==1){
			cursor81.moveToFirst();
			String id = cursor81.getString(0);
			imageids.add(id);
		}else{
		while (cursor81.moveToNext()){
		String id = cursor81.getString(0);
		imageids.add(id);

		}
		}
		cursor81.close();
		////////////////////////////////////////////////////////////////////////
		
		if (cursor82 == null){
			cursor82.close();
		}
		if (cursor82.getCount()==1){
			cursor82.moveToFirst();
			String id = cursor82.getString(0);
			imageids.add(id);
		}else{
		while (cursor82.moveToNext()){
		String id = cursor82.getString(0);
		imageids.add(id);

		}
		}
		cursor82.close();

		////////////////////////////////////////////////////////////////////////
		
		if (cursor83 == null){
			cursor83.close();
		}
		if (cursor83.getCount()==1){
			cursor83.moveToFirst();
			String id = cursor83.getString(0);
			imageids.add(id);
		}else{
		while (cursor83.moveToNext()){
		String id = cursor83.getString(0);
		imageids.add(id);

		}
		}
		cursor83.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor84 == null){
			cursor84.close();
		}
		if (cursor84.getCount()==1){
			cursor84.moveToFirst();
			String id = cursor84.getString(0);
			imageids.add(id);
		}else{
		while (cursor84.moveToNext()){
		String id = cursor84.getString(0);
		imageids.add(id);
		
		}
		}
		cursor84.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor85 == null){
			cursor85.close();
		}
		if (cursor85.getCount()==1){
			cursor85.moveToFirst();
			String id = cursor85.getString(0);
			imageids.add(id);
		}else{
		while (cursor85.moveToNext()){
		String id = cursor85.getString(0);
		imageids.add(id);

		}
		}
		cursor85.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor86 == null){
			cursor86.close();
		}
		if (cursor86.getCount()==1){
			cursor86.moveToFirst();
			String id = cursor86.getString(0);
			imageids.add(id);
		}else{
		while (cursor86.moveToNext()){
		String id = cursor86.getString(0);
		imageids.add(id);

		}
		}
		cursor86.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor87 == null){
			cursor87.close();
		}
		if (cursor87.getCount()==1){
			cursor87.moveToFirst();
			String id = cursor87.getString(0);
			imageids.add(id);
		}else{
		while (cursor87.moveToNext()){
		String id = cursor87.getString(0);
		imageids.add(id);

		}
		}
		cursor87.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor88 == null){
			cursor88.close();
		}
		if (cursor88.getCount()==1){
			cursor88.moveToFirst();
			String id = cursor88.getString(0);
			imageids.add(id);
		}else{
		while (cursor88.moveToNext()){
		String id = cursor88.getString(0);
		imageids.add(id);

		}
		}
		cursor88.close();

		///////////////////////////////////////////////////////////////////////
		if (cursor89 == null){
			cursor89.close();
		}
		if (cursor89.getCount()==1){
			cursor89.moveToFirst();
			String id = cursor89.getString(0);
			imageids.add(id);
		}else{
		while (cursor89.moveToNext()){
		String id = cursor89.getString(0);
		imageids.add(id);

		}
		}
		cursor89.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor90 == null){
			cursor90.close();
		}
		if (cursor90.getCount()==1){
			cursor90.moveToFirst();
			String id = cursor90.getString(0);
			imageids.add(id);
		}else{
		while (cursor90.moveToNext()){
		String id = cursor90.getString(0);
		imageids.add(id);

		}
		}
		cursor90.close();
		////////////////////////////////////////////////////////////////////////
		
if (cursor91 == null){
			cursor91.close();
		}
		if (cursor91.getCount()==1){
			cursor91.moveToFirst();
			String id = cursor91.getString(0);
			imageids.add(id);
		}else{
		while (cursor91.moveToNext()){
		String id = cursor91.getString(0);
		imageids.add(id);

		}
		}
		cursor91.close();
		////////////////////////////////////////////////////////////////////////
		
		if (cursor92 == null){
			cursor92.close();
		}
		if (cursor92.getCount()==1){
			cursor92.moveToFirst();
			String id = cursor92.getString(0);
			imageids.add(id);
		}else{
		while (cursor92.moveToNext()){
		String id = cursor92.getString(0);
		imageids.add(id);

		}
		}
		cursor92.close();

		////////////////////////////////////////////////////////////////////////
		
		if (cursor93 == null){
			cursor93.close();
		}
		if (cursor93.getCount()==1){
			cursor93.moveToFirst();
			String id = cursor93.getString(0);
			imageids.add(id);
		}else{
		while (cursor93.moveToNext()){
		String id = cursor93.getString(0);
		imageids.add(id);

		}
		}
		cursor93.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor94 == null){
			cursor94.close();
		}
		if (cursor94.getCount()==1){
			cursor94.moveToFirst();
			String id = cursor94.getString(0);
			imageids.add(id);
		}else{
		while (cursor94.moveToNext()){
		String id = cursor94.getString(0);
		imageids.add(id);
		
		}
		}
		cursor94.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor95 == null){
			cursor95.close();
		}
		if (cursor95.getCount()==1){
			cursor95.moveToFirst();
			String id = cursor95.getString(0);
			imageids.add(id);
		}else{
		while (cursor95.moveToNext()){
		String id = cursor95.getString(0);
		imageids.add(id);

		}
		}
		cursor95.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor96 == null){
			cursor96.close();
		}
		if (cursor96.getCount()==1){
			cursor96.moveToFirst();
			String id = cursor96.getString(0);
			imageids.add(id);
		}else{
		while (cursor96.moveToNext()){
		String id = cursor96.getString(0);
		imageids.add(id);

		}
		}
		cursor96.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor97 == null){
			cursor97.close();
		}
		if (cursor97.getCount()==1){
			cursor97.moveToFirst();
			String id = cursor97.getString(0);
			imageids.add(id);
		}else{
		while (cursor97.moveToNext()){
		String id = cursor97.getString(0);
		imageids.add(id);

		}
		}
		cursor97.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor98 == null){
			cursor98.close();
		}
		if (cursor98.getCount()==1){
			cursor98.moveToFirst();
			String id = cursor98.getString(0);
			imageids.add(id);
		}else{
		while (cursor98.moveToNext()){
		String id = cursor98.getString(0);
		imageids.add(id);

		}
		}
		cursor98.close();

		///////////////////////////////////////////////////////////////////////
		if (cursor99 == null){
			cursor99.close();
		}
		if (cursor99.getCount()==1){
			cursor99.moveToFirst();
			String id = cursor99.getString(0);
			imageids.add(id);
		}else{
		while (cursor99.moveToNext()){
		String id = cursor99.getString(0);
		imageids.add(id);

		}
		}
		cursor99.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor100 == null){
			cursor100.close();
		}
		if (cursor100.getCount()==1){
			cursor100.moveToFirst();
			String id = cursor100.getString(0);
			imageids.add(id);
		}else{
		while (cursor100.moveToNext()){
		String id = cursor100.getString(0);
		imageids.add(id);

		}
		}
		cursor100.close();
		////////////////////////////////////////////////////////////////////////
		
if (cursor101 == null){
			cursor101.close();
		}
		if (cursor101.getCount()==1){
			cursor101.moveToFirst();
			String id = cursor101.getString(0);
			imageids.add(id);
		}else{
		while (cursor101.moveToNext()){
		String id = cursor101.getString(0);
		imageids.add(id);

		}
		}
		cursor101.close();
		////////////////////////////////////////////////////////////////////////
		
		if (cursor102 == null){
			cursor102.close();
		}
		if (cursor102.getCount()==1){
			cursor102.moveToFirst();
			String id = cursor102.getString(0);
			imageids.add(id);
		}else{
		while (cursor102.moveToNext()){
		String id = cursor102.getString(0);
		imageids.add(id);

		}
		}
		cursor102.close();

		////////////////////////////////////////////////////////////////////////
		
		if (cursor103 == null){
			cursor103.close();
		}
		if (cursor103.getCount()==1){
			cursor103.moveToFirst();
			String id = cursor103.getString(0);
			imageids.add(id);
		}else{
		while (cursor103.moveToNext()){
		String id = cursor103.getString(0);
		imageids.add(id);

		}
		}
		cursor103.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor104 == null){
			cursor104.close();
		}
		if (cursor104.getCount()==1){
			cursor104.moveToFirst();
			String id = cursor104.getString(0);
			imageids.add(id);
		}else{
		while (cursor104.moveToNext()){
		String id = cursor104.getString(0);
		imageids.add(id);
		
		}
		}
		cursor104.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor105 == null){
			cursor105.close();
		}
		if (cursor105.getCount()==1){
			cursor105.moveToFirst();
			String id = cursor105.getString(0);
			imageids.add(id);
		}else{
		while (cursor105.moveToNext()){
		String id = cursor105.getString(0);
		imageids.add(id);

		}
		}
		cursor105.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor106 == null){
			cursor106.close();
		}
		if (cursor106.getCount()==1){
			cursor106.moveToFirst();
			String id = cursor106.getString(0);
			imageids.add(id);
		}else{
		while (cursor106.moveToNext()){
		String id = cursor106.getString(0);
		imageids.add(id);

		}
		}
		cursor106.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor107 == null){
			cursor107.close();
		}
		if (cursor107.getCount()==1){
			cursor107.moveToFirst();
			String id = cursor107.getString(0);
			imageids.add(id);
		}else{
		while (cursor107.moveToNext()){
		String id = cursor107.getString(0);
		imageids.add(id);

		}
		}
		cursor107.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor108 == null){
			cursor108.close();
		}
		if (cursor108.getCount()==1){
			cursor108.moveToFirst();
			String id = cursor108.getString(0);
			imageids.add(id);
		}else{
		while (cursor108.moveToNext()){
		String id = cursor108.getString(0);
		imageids.add(id);

		}
		}
		cursor108.close();

		///////////////////////////////////////////////////////////////////////
		if (cursor109 == null){
			cursor109.close();
		}
		if (cursor109.getCount()==1){
			cursor109.moveToFirst();
			String id = cursor109.getString(0);
			imageids.add(id);
		}else{
		while (cursor109.moveToNext()){
		String id = cursor109.getString(0);
		imageids.add(id);

		}
		}
		cursor109.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor110 == null){
			cursor110.close();
		}
		if (cursor110.getCount()==1){
			cursor110.moveToFirst();
			String id = cursor110.getString(0);
			imageids.add(id);
		}else{
		while (cursor110.moveToNext()){
		String id = cursor110.getString(0);
		imageids.add(id);

		}
		}
		cursor110.close();
		////////////////////////////////////////////////////////////////////////
		
if (cursor111 == null){
			cursor111.close();
		}
		if (cursor111.getCount()==1){
			cursor111.moveToFirst();
			String id = cursor111.getString(0);
			imageids.add(id);
		}else{
		while (cursor111.moveToNext()){
		String id = cursor111.getString(0);
		imageids.add(id);

		}
		}
		cursor111.close();
		////////////////////////////////////////////////////////////////////////
		
		if (cursor112 == null){
			cursor112.close();
		}
		if (cursor112.getCount()==1){
			cursor112.moveToFirst();
			String id = cursor112.getString(0);
			imageids.add(id);
		}else{
		while (cursor112.moveToNext()){
		String id = cursor112.getString(0);
		imageids.add(id);

		}
		}
		cursor112.close();

		////////////////////////////////////////////////////////////////////////
		
		if (cursor113 == null){
			cursor113.close();
		}
		if (cursor113.getCount()==1){
			cursor113.moveToFirst();
			String id = cursor113.getString(0);
			imageids.add(id);
		}else{
		while (cursor113.moveToNext()){
		String id = cursor113.getString(0);
		imageids.add(id);

		}
		}
		cursor113.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor114 == null){
			cursor114.close();
		}
		if (cursor114.getCount()==1){
			cursor114.moveToFirst();
			String id = cursor114.getString(0);
			imageids.add(id);
		}else{
		while (cursor114.moveToNext()){
		String id = cursor114.getString(0);
		imageids.add(id);
		
		}
		}
		cursor114.close();

		
		////////////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////BLUE CURSORS ////////////////////////////////////////////////////
		if (cursor301 == null){
			cursor301.close();
		}
		if (cursor301.getCount()==1){
			cursor301.moveToFirst();
			String id = cursor301.getString(0);
			imageids.add(id);
		}else{
		while (cursor301.moveToNext()){
		String id = cursor301.getString(0);
		imageids.add(id);

		}
		}
		cursor301.close();
		////////////////////////////////////////////////////////////////////////
		
		if (cursor302 == null){
			cursor302.close();
		}
		if (cursor302.getCount()==1){
			cursor302.moveToFirst();
			String id = cursor302.getString(0);
			imageids.add(id);
		}else{
		while (cursor302.moveToNext()){
		String id = cursor302.getString(0);
		imageids.add(id);

		}
		}
		cursor302.close();

		////////////////////////////////////////////////////////////////////////
		
		if (cursor303 == null){
			cursor303.close();
		}
		if (cursor303.getCount()==1){
			cursor303.moveToFirst();
			String id = cursor303.getString(0);
			imageids.add(id);
		}else{
		while (cursor303.moveToNext()){
		String id = cursor303.getString(0);
		imageids.add(id);

		}
		}
		cursor303.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor304 == null){
			cursor304.close();
		}
		if (cursor304.getCount()==1){
			cursor304.moveToFirst();
			String id = cursor304.getString(0);
			imageids.add(id);
		}else{
		while (cursor304.moveToNext()){
		String id = cursor304.getString(0);
		imageids.add(id);
		
		}
		}
		cursor304.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor305 == null){
			cursor305.close();
		}
		if (cursor305.getCount()==1){
			cursor305.moveToFirst();
			String id = cursor305.getString(0);
			imageids.add(id);
		}else{
		while (cursor305.moveToNext()){
		String id = cursor305.getString(0);
		imageids.add(id);

		}
		}
		cursor305.close();

		
		////////////////////////////////////////////////////////////////////////
		
		if (cursor306 == null){
			cursor306.close();
		}
		if (cursor306.getCount()==1){
			cursor306.moveToFirst();
			String id = cursor306.getString(0);
			imageids.add(id);
		}else{
		while (cursor306.moveToNext()){
		String id = cursor306.getString(0);
		imageids.add(id);

		}
		}
		cursor306.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor307 == null){
			cursor307.close();
		}
		if (cursor307.getCount()==1){
			cursor307.moveToFirst();
			String id = cursor307.getString(0);
			imageids.add(id);
		}else{
		while (cursor307.moveToNext()){
		String id = cursor307.getString(0);
		imageids.add(id);

		}
		}
		cursor307.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor308 == null){
			cursor308.close();
		}
		if (cursor308.getCount()==1){
			cursor308.moveToFirst();
			String id = cursor308.getString(0);
			imageids.add(id);
		}else{
		while (cursor308.moveToNext()){
		String id = cursor308.getString(0);
		imageids.add(id);

		}
		}
		cursor308.close();

		///////////////////////////////////////////////////////////////////////
		if (cursor309 == null){
			cursor309.close();
		}
		if (cursor309.getCount()==1){
			cursor309.moveToFirst();
			String id = cursor309.getString(0);
			imageids.add(id);
		}else{
		while (cursor309.moveToNext()){
		String id = cursor309.getString(0);
		imageids.add(id);

		}
		}
		cursor309.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3010 == null){
			cursor3010.close();
		}
		if (cursor3010.getCount()==1){
			cursor3010.moveToFirst();
			String id = cursor3010.getString(0);
			imageids.add(id);
		}else{
		while (cursor3010.moveToNext()){
		String id = cursor3010.getString(0);
		imageids.add(id);

		}
		}
		cursor3010.close();

		//////////////////////////////////////////////////////////////////////
		if (cursor3011 == null){
			cursor3011.close();
		}
		if (cursor3011.getCount()==1){
			cursor3011.moveToFirst();
			String id = cursor3011.getString(0);
			imageids.add(id);
		}else{
		while (cursor3011.moveToNext()){
		String id = cursor3011.getString(0);
		imageids.add(id);

		}
		}
		cursor3011.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3012 == null){
			cursor3012.close();
		}
		if (cursor3012.getCount()==1){
			cursor3012.moveToFirst();
			String id = cursor3012.getString(0);
			imageids.add(id);
		}else{
		while (cursor3012.moveToNext()){
		String id = cursor3012.getString(0);
		imageids.add(id);

		}
		}
		cursor3012.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3013 == null){
			cursor3013.close();
		}
		if (cursor3013.getCount()==1){
			cursor3013.moveToFirst();
			String id = cursor3013.getString(0);
			imageids.add(id);
		}else{
		while (cursor3013.moveToNext()){
		String id = cursor3013.getString(0);
		imageids.add(id);

		}
		}
		cursor3013.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3014 == null){
			cursor3014.close();
		}
		if (cursor3014.getCount()==1){
			cursor3014.moveToFirst();
			String id = cursor3014.getString(0);
			imageids.add(id);
		}else{
		while (cursor3014.moveToNext()){
		String id = cursor3014.getString(0);
		imageids.add(id);

		}
		}
		cursor3014.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3015 == null){
			cursor3015.close();
		}
		if (cursor3015.getCount()==1){
			cursor3015.moveToFirst();
			String id = cursor3015.getString(0);
			imageids.add(id);
		}else{
		while (cursor3015.moveToNext()){
		String id = cursor3015.getString(0);
		imageids.add(id);

		}
		}
		cursor3015.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3016 == null){
			cursor3016.close();
		}
		if (cursor3016.getCount()==1){
			cursor3016.moveToFirst();
			String id = cursor3016.getString(0);
			imageids.add(id);
		}else{
		while (cursor3016.moveToNext()){
		String id = cursor3016.getString(0);
		imageids.add(id);

		}
		}
		cursor3016.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3017 == null){
			cursor3017.close();
		}
		if (cursor3017.getCount()==1){
			cursor3017.moveToFirst();
			String id = cursor3017.getString(0);
			imageids.add(id);
		}else{
		while (cursor3017.moveToNext()){
		String id = cursor3017.getString(0);
		imageids.add(id);

		}
		}
		cursor3017.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3018 == null){
			cursor3018.close();
		}
		if (cursor3018.getCount()==1){
			cursor3018.moveToFirst();
			String id = cursor3018.getString(0);
			imageids.add(id);
		}else{
		while (cursor3018.moveToNext()){
		String id = cursor3018.getString(0);
		imageids.add(id);

		}
		}
		cursor3018.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3019 == null){
			cursor3019.close();
		}
		if (cursor3019.getCount()==1){
			cursor3019.moveToFirst();
			String id = cursor3019.getString(0);
			imageids.add(id);
		}else{
		while (cursor3019.moveToNext()){
		String id = cursor3019.getString(0);
		imageids.add(id);

		}
		}
		cursor3019.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3020 == null){
			cursor3020.close();
		}
		if (cursor3020.getCount()==1){
			cursor3020.moveToFirst();
			String id = cursor3020.getString(0);
			imageids.add(id);
		}else{
		while (cursor3020.moveToNext()){
		String id = cursor3020.getString(0);
		imageids.add(id);

		}
		}
		cursor3020.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3021 == null){
			cursor3021.close();
		}
		if (cursor3021.getCount()==1){
			cursor3021.moveToFirst();
			String id = cursor3021.getString(0);
			imageids.add(id);
		}else{
		while (cursor3021.moveToNext()){
		String id = cursor3021.getString(0);
		imageids.add(id);

		}
		}
		cursor3021.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3022 == null){
			cursor3022.close();
		}
		if (cursor3022.getCount()==1){
			cursor3022.moveToFirst();
			String id = cursor3022.getString(0);
			imageids.add(id);
		}else{
		while (cursor3022.moveToNext()){
		String id = cursor3022.getString(0);
		imageids.add(id);

		}
		}
		cursor3022.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3023 == null){
			cursor3023.close();
		}
		if (cursor3023.getCount()==1){
			cursor3023.moveToFirst();
			String id = cursor3023.getString(0);
			imageids.add(id);
		}else{
		while (cursor3023.moveToNext()){
		String id = cursor3023.getString(0);
		imageids.add(id);

		}
		}
		cursor3023.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3024 == null){
			cursor3024.close();
		}
		if (cursor3024.getCount()==1){
			cursor3024.moveToFirst();
			String id = cursor3024.getString(0);
			imageids.add(id);
		}else{
		while (cursor3024.moveToNext()){
		String id = cursor3024.getString(0);
		imageids.add(id);

		}
		}
		cursor3024.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3025 == null){
			cursor3025.close();
		}
		if (cursor3025.getCount()==1){
			cursor3025.moveToFirst();
			String id = cursor3025.getString(0);
			imageids.add(id);
		}else{
		while (cursor3025.moveToNext()){
		String id = cursor3025.getString(0);
		imageids.add(id);

		}
		}
		cursor3025.close();

		//////////////////////////////////////////////////////////////////////
		if (cursor3026 == null){
			cursor3026.close();
		}
		if (cursor3026.getCount()==1){
			cursor3026.moveToFirst();
			String id = cursor3026.getString(0);
			imageids.add(id);
		}else{
		while (cursor3026.moveToNext()){
		String id = cursor3026.getString(0);
		imageids.add(id);

		}
		}
		cursor3026.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3027 == null){
			cursor3027.close();
		}
		if (cursor3027.getCount()==1){
			cursor3027.moveToFirst();
			String id = cursor3027.getString(0);
			imageids.add(id);
		}else{
		while (cursor3027.moveToNext()){
		String id = cursor3027.getString(0);
		imageids.add(id);

		}
		}
		cursor3027.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3028 == null){
			cursor3028.close();
		}
		if (cursor3028.getCount()==1){
			cursor3028.moveToFirst();
			String id = cursor3028.getString(0);
			imageids.add(id);
		}else{
		while (cursor3028.moveToNext()){
		String id = cursor3028.getString(0);
		imageids.add(id);

		}
		}
		cursor3028.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3029 == null){
			cursor3029.close();
		}
		if (cursor3029.getCount()==1){
			cursor3029.moveToFirst();
			String id = cursor3029.getString(0);
			imageids.add(id);
		}else{
		while (cursor3029.moveToNext()){
		String id = cursor3029.getString(0);
		imageids.add(id);

		}
		}
		cursor3029.close();

		////////////////////////////////////////////////////////////////////////

		////////////////////////////////////////////////////////////////////////
		if (cursor3030 == null){
			cursor3030.close();
		}
		if (cursor3030.getCount()==1){
			cursor3030.moveToFirst();
			String id = cursor3030.getString(0);
			imageids.add(id);
		}else{
		while (cursor3030.moveToNext()){
		String id = cursor3030.getString(0);
		imageids.add(id);

		}
		}
		cursor3030.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3031 == null){
			cursor3031.close();
		}
		if (cursor3031.getCount()==1){
			cursor3031.moveToFirst();
			String id = cursor3031.getString(0);
			imageids.add(id);
		}else{
		while (cursor3031.moveToNext()){
		String id = cursor3031.getString(0);
		imageids.add(id);

		}
		}
		cursor3031.close();

		////////////////////////////////////////////////////////////////////////
		if (cursor3032 == null){
			cursor3032.close();
		}
		if (cursor3032.getCount()==1){
			cursor3032.moveToFirst();
			String id = cursor3032.getString(0);
			imageids.add(id);
		}else{
		while (cursor3032.moveToNext()){
		String id = cursor3032.getString(0);
		imageids.add(id);

		}
		}
		cursor3032.close();


	

		Map<String,Integer> charCounter=new HashMap<String,Integer>();
        for(int i=0;i<imageids.size();i++)
        {
        	String fetch = imageids.get(i);
            if(charCounter.containsKey(fetch)){
                charCounter.put(fetch, charCounter.get(fetch)+1);
            } 
            else
            {
                charCounter.put(fetch, 1);
            }
       }
        

        int mostvotes = 0;
        String imagemeaning = null;
        for(String key:charCounter.keySet()){
        	int  mbalue = charCounter.get(key);

        	if(mostvotes < mbalue){
        	mostvotes = mbalue;
        	imagemeaning = key;

        	}

        }
        //System.out.println("the highest votes is: "+ mostvotes+ "image meaning is : "+ imagemeaning);
        Toast.makeText(getApplicationContext(), "the highest votes is: "+ mostvotes+ "image meaning is : "+ imagemeaning, Toast.LENGTH_LONG).show();
        /*Cursor finalcur = DatabaseHelpers.select("SELECT meaning FROM " + generaltable+" WHERE imageid = "+ imagenumber);
        
        if (finalcur == null){
        	finalcur.close();
    	}
    		
    		while (finalcur.moveToNext()){
    		int threshold = 4;
    		if(max>threshold){
    		actualmeaning = finalcur.getString(0);
    		}else{
    			actualmeaning = "";
    		}
    		  		 
    		System.out.println("the meaning is : "+ actualmeaning+ " image id is : "+ imagenumber);
    		}
    		//here we close the cursor30 because we do not longer need it
    		finalcur.close();*/
    return imagemeaning;
    }
    @Override
  	protected void onDestroy() {
    	//myDataBase = DatabaseHelpers.close();
  		super.onDestroy();
  	}

  	@Override
  	protected void onPause() {
  		// TODO Auto-generated method stub
  		super.onPause();
  	}

  	@Override
  	protected void onResume() {
  		// TODO Auto-generated method stub
  		super.onResume();
  	}

  	@Override
  	protected void onStop() {
  		// TODO Auto-generated method stub
  		super.onStop();
  	}

}