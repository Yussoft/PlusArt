package com.plusart.yus.plusart;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.id;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{

    //Variables para la lista de cuadros
    private ArrayList<String> cuadros;
    private int[] indices;
    private int indice_actual = -1;
    private int max_i = -1;
    public ImageView cuadro;

    //Variables de la interfaz
    private TextView name, date, author;

    boolean goneFlag = false;
    private boolean QRLeido = false;

    //Variables para la cámara
    private Camera myCamera;
    private SurfaceHolder mySH;
    private boolean isCameraOn = false;
    private String contenidoQR;

    //Variables para gesto de swipe
    private float x1,x2,y1,y2;
    static final int MIN_DISTANCE = 150;

    //Logcat
    private static final String TAG = "ActividadCamaraQR";

    final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable() {
        public void run() {
            goneFlag = true;
            //Code for long click
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setupLayout();

        //Iniciamos las variables de los textViews
        name = (TextView)findViewById(R.id.paintingName);
        date = (TextView)findViewById(R.id.paintingDate);
        author = (TextView)findViewById(R.id.paintingAuthor);
        name.setText("");
        date.setText("");
        author.setText("");
        cuadro = (ImageView)findViewById(R.id.cuadroImageView);

        initCuadros();
    }
    /*
        Función que inicializa la camara en el SurfaceView
     */
    private void setupLayout() {

        getWindow().setFormat(PixelFormat.UNKNOWN);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.cameraview);
        mySH = surfaceView.getHolder();
        mySH.addCallback(this);
        mySH.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    /*
        Función que carga una lista de cuadros cuya información va a ser mostrada.
    */
    public void initCuadros(){
        //Inicialización
        cuadros = new ArrayList<String>();
        //Primera pintura
        cuadros.add("Patio granadino,2005,Chema");
        //Segunda pintura
        cuadros.add("Granada,1999,Francisco Saiz-Parto");
        //Tercera pintura
        cuadros.add("Sátiro,1989,Juanjo Guarnido");
        //Cuarta pintura
        cuadros.add("Paisaje desolado,1994,J.Ortuño Trevelez");
        for(int i=0; i < cuadros.size(); i++) {
            Log.d("Cuadro"+i+":",cuadros.get(i));
        }
    }
    /*
        Función que nos permite escanear un código QR y almacenar su contenido para interpretarlo
     */
    public void scanQRCode(View view){
        if(cuadros.size()>0 && QRLeido){
            cuadros.clear();
            name.setVisibility(View.INVISIBLE);
            date.setVisibility(View.INVISIBLE);
            author.setVisibility(View.INVISIBLE);
            QRLeido=false;
        }
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);
        }
    }
    /*
        Función que recoge los datos de la lectura de código QR.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Código establecido por nosotros para controlar de que activity venimos.
        if (requestCode == 0) {
            //Si el resultado ha sido correcto.
            if(resultCode != RESULT_CANCELED){
                String contents = data.getStringExtra("SCAN_RESULT");

                //El contenido es una serie de índices de cuadros que queremos visualizar.
                contenidoQR = contents;
                Log.d(TAG,contenidoQR);  //Debug

                //Dividimos el string en caracteres independientes
                String[] strArray = contents.split(" ");
                indices = null;
                indices = new int[strArray.length];
                for(int i = 0; i < strArray.length; i++) {
                    Log.d(TAG,"Iter:"+i);
                    indices[i] = Integer.parseInt(strArray[i]);
                }
                //Mensaje debug
                //Toast.makeText(this,"QR leido", Toast.LENGTH_SHORT).show ();
                //Una vez leido el QR actualizamos índices
                indice_actual = 0;
                max_i = strArray.length-1;
                Log.d(TAG,"Indice actual="+indice_actual+", max_i="+max_i);
                QRLeido=true;
            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
                //Toast.makeText(this, "Error en la lectura de QR", Toast.LENGTH_SHORT).show ();
            }
        }
    }
    /*

        Función que nos permite reconocer un gesto de swipe hacia derecha o izquierda.

     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(indice_actual != -1 && cuadros.size()>=1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    //Mostramos el cuadro al tocar
                    x1 = event.getX();
                    y1 = event.getY();

                    handler.postDelayed(mLongPressed, 16000);

                    //if(cuadro.getVisibility()==View.INVISIBLE)
                        //cuadro.setVisibility(View.VISIBLE);

                    break;
                case MotionEvent.ACTION_UP:

                    //if(cuadro.getVisibility()==View.VISIBLE)
                        //cuadro.setVisibility(View.INVISIBLE);

                    x2 = event.getX();
                    y2 = event.getY();
                    float deltaX = x1 - x2;
                    float deltaY = y1 - y2;
                    //Log.d("x1,x2:",x1+","+x2);

                    //Si el gesto es de izquierda a derecha
                    if (deltaX > 0 && Math.abs(deltaX) > MIN_DISTANCE) {
                        //Toast.makeText(this, "Cuadro siguiente", Toast.LENGTH_SHORT).show();

                        //Si podemos sumar 1 al índice actual sin pasar del máximo, lo hacemos.
                        if (indice_actual + 1 <= max_i) {
                            indice_actual += 1;
                        }
                        //Si por otro lado superamos el máximo pasamos al principio de los índices.
                        else if (indice_actual + 1 > max_i) {
                            indice_actual = 0;
                        }
                    }
                    //Si el gesto de es derecha a izquierda
                    else if (deltaX < 0 && Math.abs(deltaX) > MIN_DISTANCE) {
                        //Toast.makeText(this, "Cuadro anterior", Toast.LENGTH_SHORT).show();

                        //Si podemos restar 1 al índice sin pasar del mínimo, lo hacemos
                        if (indice_actual - 1 >= 0) {
                            indice_actual -= 1;
                        }
                        //En caso de pasar el mínimo volvemos al final de la lista, como si fuese
                        //circular.
                        else if (indice_actual - 1 < 0) {
                            indice_actual = max_i;
                        }
                    }

                    /*
                    if((deltaY >= 0) && deltaY > MIN_DISTANCE){
                        Toast.makeText(this,"caso1", Toast.LENGTH_SHORT).show();
                    }
                    else if((deltaY < 0)&& (deltaY < MIN_DISTANCE)){
                        Toast.makeText(this,"caso2",Toast.LENGTH_SHORT).show();
                    }
                    */
                    //una vez actualizados los índices actualizamos los valores de la información
                    String info[] = cuadros.get(indice_actual).split(",");
                    name.setText((indice_actual+1)+"."+info[0]);
                    date.setText(info[1]);
                    author.setText(info[2]);

                    //Poner las imágenes traslúcidas
                    int id = getResources().getIdentifier("com.plusart.yus.plusart:drawable/cuadro"+(indice_actual+1), null, null);
                    cuadro.setImageResource(id);
                    cuadro.setAlpha(70);

                    break;
            }
        }
        return super.onTouchEvent(event);
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

    public void showHidePicture(View v){
        if(cuadro.getVisibility()==View.INVISIBLE){
            cuadro.setVisibility(View.VISIBLE);
        }
        else if(cuadro.getVisibility()==View.VISIBLE){
            cuadro.setVisibility(View.INVISIBLE);
        }
    }
}
