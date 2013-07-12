package com.kiko.softwareltd;

import java.io.IOException;
import java.util.List;
 
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
 
public class Preview extends SurfaceView implements SurfaceHolder.Callback{
 
    SurfaceHolder mHolder;
    public Camera camera;
 
    public Preview(Context context) {
        super(context);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
    }
 
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        camera.startPreview();
    }
 
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters=camera.getParameters();
            List<Size> sizes=parameters.getSupportedPictureSizes();
            parameters.setPictureSize(sizes.get(0).width, sizes.get(0).height);
            //parameters.setRotation(90);
            camera.setParameters(parameters);
            camera.startPreview();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        camera.stopPreview();
        camera.release();
        camera = null;
    }
 
}