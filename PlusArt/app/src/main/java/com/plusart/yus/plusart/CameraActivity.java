package com.plusart.yus.plusart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.R.id.message;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{

    private Camera myCamera;
    private SurfaceHolder mySH;
    private boolean isCameraOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setupLayout();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Intent intent = new Intent(this,CameraActivity.class);
                intent.putExtra("message",contents);
                startActivity(intent);
            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
                
            }
        }
    }

    private void setupLayout() {

        getWindow().setFormat(PixelFormat.UNKNOWN);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.cameraview);
        mySH = surfaceView.getHolder();
        mySH.addCallback(this);
        mySH.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        myCamera = Camera.open();
        myCamera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if(isCameraOn){
            //Si la camara esta encendida se apaga, bool a falso.
            myCamera.stopPreview();
            isCameraOn=false;
        }
        if(myCamera != null){
            try{
                //Si la cámara está instanciada, comenzamos a mostrar la imagen, bool a true.
                myCamera.setPreviewDisplay(mySH);
                myCamera.startPreview();
                isCameraOn=true;
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myCamera.stopPreview();
        myCamera.release();
        myCamera=null;
        isCameraOn=false;
    }
}
