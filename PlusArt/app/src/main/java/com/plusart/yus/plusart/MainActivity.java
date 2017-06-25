package com.plusart.yus.plusart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
    Crear nueva actividad: https://developer.android.com/training/basics/firstapp/starting-activity.html
     */
    public void startCameraActivity(View view) {
        //Crear un intent para abrir la nueva actividad.
        Intent intent= new Intent(this,CameraActivity.class);
        startActivity(intent);
    }

    public void startOpenCVActivity(View view){

        //Creamos un intent para abrir la actividad de OpenCV
        Intent intent = new Intent(this, OpenCVCamera.class);
        startActivity(intent);
    }
}
