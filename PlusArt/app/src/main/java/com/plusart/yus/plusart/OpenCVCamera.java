package com.plusart.yus.plusart;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;


public class OpenCVCamera extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {


    //Variable de java que nos permite acceder a la cámara y guardar los frames para tratarlos
    private CameraBridgeViewBase mOpenCVCameraView;
    private Mat imagen;
    private int windowHeight;
    private int windowWidth;
    private boolean processFrame = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cvcamera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Inicializamos la cámara de OpenCV
        mOpenCVCameraView = (JavaCameraView)findViewById(R.id.CamaraOpenCV);
        mOpenCVCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCVCameraView.setCvCameraViewListener(this);

    }


    /*
    Método para que la aplicación conecte con OpenCV Manager.
     */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    // Tareas de inicialización tras conectar con OpenCV Manager
                    // Por ejemplo activar un visor de cámara
                    mOpenCVCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //cerramos la cámara al pausar la aplicación
        if(mOpenCVCameraView != null) {
            mOpenCVCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        imagen = new Mat(height,width, CvType.CV_32FC1);
    }

    @Override
    public void onCameraViewStopped() {
        imagen.release();
    }
    /*
    public void imageMatching(Mat frame){

        Log.d("Running","template Matching");

        Mat img = frame;
        Mat template = null;
        try {
            template = Utils.loadResource(this, R.drawable.foto, CvType.CV_32FC1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Mat template = ;Imagen guardada

        //  / Create the result matrix
        int result_cols = img.cols() - template.cols() + 1;
        int result_rows = img.rows() - template.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // / Do the Matching and Normalize
        //Imgproc.TM_CCOEFF is the match method
        Imgproc.matchTemplate(img, template, result, Imgproc.TM_CCOEFF);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        org.opencv.core.Point matchLoc = mmr.minLoc;

        // / Show me what you got
        org.opencv.core.Point p = new org.opencv.core.Point(matchLoc.x+template.cols(),matchLoc.y+template.rows());
        Imgproc.rectangle(img, matchLoc, p, new Scalar(0, 255, 0));

        result.release();
        template.release();
        img.release();
        Log.d("mmr",mmr.toString());
    }
    */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if(processFrame) {
            //Tomamos el frame de la cámara
            Mat mRgba = inputFrame.rgba();
            Mat mRgbaT = mRgba.t();
            Core.flip(mRgba.t(), mRgbaT, 1);
            //Size s = new Size(windowHeight,windowWidth);
            Imgproc.resize(mRgbaT, mRgbaT, new Size(720, 480));
            //imageMatching(mRgbaT);
            return mRgbaT;
        }
        else {
            //Tomamos el frame de la cámara
            Mat mRgba = inputFrame.rgba();
            Mat mRgbaT = mRgba.t();
            Core.flip(mRgba.t(), mRgbaT, 1);
            //Size s = new Size(windowHeight,windowWidth);
            Imgproc.resize(mRgbaT, mRgbaT, new Size(720, 480));
            return mRgbaT;
        }
    }
    public void TMButton(View view){
        processFrame = true;
    }
}
