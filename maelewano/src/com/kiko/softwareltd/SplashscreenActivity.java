package com.kiko.softwareltd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SplashscreenActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_splashscreen);      
        Thread Timer = new Thread(){
        	public void run(){
        		try{
        			sleep(3000);
        			
        			//startActivity(new Intent("com.kiko.softwareltd.maelewano.home2"));
        			startActivity(new Intent("com.kiko.softwareltd.home"));
        			
        		}
        		
        		catch (InterruptedException e){
        			e.printStackTrace();
        		}
        		finally{
        			finish();
        		}
        	}
        };
        Timer.start();
  
	}

	
    
    
}
