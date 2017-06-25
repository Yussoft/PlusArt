package com.plusart.yus.plusart;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class OpenCVCamera extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {


    //Variable de java que nos permite acceder a la cámara y guardar los frames para tratarlos
    private CameraBridgeViewBase mOpenCVCameraView;
    private Mat imagen;
    private int windowHeight;
    private int windowWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cvcamera);

        //Inicializamos la cámara de OpenCV
        mOpenCVCameraView = (JavaCameraView)findViewById(R.id.CamaraOpenCV);
        mOpenCVCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCVCameraView.setCvCameraViewListener(this);

        //Cogemos medidas de la ventana para ajustar el tamaño de la imagen

        windowHeight = this.getWindow().getDecorView().getHeight();
        windowWidth = this.getWindow().getDecorView().getWidth();
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
        imagen = new Mat(height,width, CvType.CV_8SC4);
    }

    @Override
    public void onCameraViewStopped() {
        imagen.release();
    }

    public Mat flip(Mat source){

        Core.flip(source.t(),source,1);
        Imgproc.resize(source.t(),source,source.size());
        return source;
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat mRgba = inputFrame.rgba();
        Mat mRgbaT = mRgba.t();
        Core.flip(mRgba.t(), mRgbaT, 1);
        Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());
        return mRgbaT;
    }
}
