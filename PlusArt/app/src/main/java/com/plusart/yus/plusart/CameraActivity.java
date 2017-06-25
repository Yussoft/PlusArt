package com.plusart.yus.plusart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{

    //Variables para la lista de cuadros
    private ArrayList<ArrayList<String>> cuadros;
    private int[] indices;
    private int indice_actual = 0;
    private int max_i = 0;

    //Variables de la interfaz
    private TextView name, date, author;

    //Variables para la cámara
    private Camera myCamera;
    private SurfaceHolder mySH;
    private boolean isCameraOn = false;
    private boolean codigoQRleido = false;

    //Variables para gesto de swipe
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setupLayout();

        //Iniciamos la lista de obras de arte
        initCuadros();

        //Iniciamos las variables de los textViews
        name = (TextView)findViewById(R.id.paintingName);
        date = (TextView)findViewById(R.id.paintingDate);
        author = (TextView)findViewById(R.id.paintingAuthor);
    }

    /*

        Función que nos permite reconocer un gesto de swipe hacia derecha o izquierda.

     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x1 - x2;
                //Si el gesto es de izquierda a derecha
                if (deltaX > 0 && Math.abs(deltaX) > MIN_DISTANCE){
                    Toast.makeText(this, "right2left swipe", Toast.LENGTH_SHORT).show ();

                    //Si podemos sumar 1 al índice actual sin pasar del máximo, lo hacemos.
                    if(indice_actual+1 <= max_i){
                        indice_actual+=1;
                    }
                    //Si por otro lado superamos el máximo pasamos al principio de los índices.
                    else if(indice_actual+1 > max_i) {
                        indice_actual = 0;
                    }
                }
                //Si el gesto de es derecha a izquierda
                else if(deltaX < 0 && Math.abs(deltaX) > MIN_DISTANCE){
                    Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show ();

                    //Si podemos restar 1 al índice sin pasar del mínimo, lo hacemos
                    if(indice_actual-1 >= 0){
                        indice_actual-=1;
                    }
                    //En caso de pasar el mínimo volvemos al final de la lista, como si fuese
                    //circular.
                    else if(indice_actual < 0){
                        indice_actual= max_i;
                    }
                }

                name.setText(cuadros.get(indice_actual).get(0));
                date.setText(cuadros.get(indice_actual).get(1));
                author.setText(cuadros.get(indice_actual).get(2));

                break;
        }
        return super.onTouchEvent(event);
    }

    public void initCuadros(){
        //Inicialización
        cuadros = new ArrayList<ArrayList<String>>();
        ArrayList<String> pintura = new ArrayList<String>();

        //Primera pintura
        String nombre = "El brujo";
        String fecha = "1995";
        String autor = "Elka";
        pintura.add(nombre);
        pintura.add(fecha);
        pintura.add(autor);
        cuadros.add(pintura);

        //Segunda pintura
        nombre = "Africa";
        fecha = "2016";
        autor = "Elka";
        pintura.clear();
        pintura.add(nombre);pintura.add(fecha);pintura.add(autor);
        cuadros.add(pintura);

        //Tercera pintura
        nombre = "Trepamuros";
        fecha = "2009";
        autor = "Elka";
        pintura.clear();
        pintura.add(nombre);pintura.add(fecha);pintura.add(autor);
        cuadros.add(pintura);

        //Cuarta pintura
        nombre = "La ninfa";
        fecha = "2007";
        autor = "Elka";
        pintura.clear();
        pintura.add(nombre);pintura.add(fecha);pintura.add(autor);
        cuadros.add(pintura);

    }
    //Función que nos permite escanear un código QR y almacenar su contenido para interpretarlo
    public void scanQRCode(View view){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");

                //Dividimos el string en caracteres independientes
                String[] strArray = contents.split(" ");
                indices = new int[strArray.length];
                for(int i = 0; i < strArray.length; i++) {
                    indices[i] = Integer.parseInt(strArray[i]);
                }

                //Actualizamos el máximo índice que tenemos disponible
                max_i = indices[strArray.length];

                codigoQRleido=true;
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
