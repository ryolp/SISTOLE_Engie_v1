package enruta.sistole_engie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CamaraActivity extends Activity {

    public static int TEMPORAL = 1;
    public static int PERMANENTE = 0;
    public static int ANOMALIA = 2;

    /*Estados del Flash*/

    public final static int SIN_FLASH = 0;
    public final static int CON_FLASH = 1;
    public final static int AUTO = 3;

    CamaraActivity ca;

    private Camera mCamera;
    private CamaraPreview mPreview;
    TextView tv_indicador;
    private Button captureButton, backButton, otraButton;
    private FrameLayout fotoPreview, cPreview;
    long secuencial;
    String is_terminacion = "-A", is_anomalia = "";
    ContentValues cv_datos;
    boolean otraFoto = false;
    String ls_nombre, caseta;
    byte[] foto;
    int temporal;
    static String mensajeDeErrorCamera = "";
    Globales globales;
    ImageButton ib_flash;
    boolean tieneFlash = true;
    boolean tieneZoom = true;
    boolean tieneCamaraFrontal = false;
    AlertDialog alert;
    /**
     * Cantidad de fotos
     */
    int cantidad;
    int fotosTomadas = 0;
    Handler mHandler = new Handler();

    String flashMode = Camera.Parameters.FLASH_MODE_OFF;
    int zoomMode = 0;
    String camaraFrontalMode = Camera.Parameters.FLASH_MODE_OFF;

    // RL, 2022-10-04, Botones para aumentar y disminuir resolución, cambiar de cámara y firmar.

    protected ImageButton btnBajarResolucion;
    protected ImageButton btnSubirResolucion;
    protected ImageButton btnCambiarCamara;
    protected ImageButton btnFirmar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camaralayout);
        globales = ((Globales) getApplicationContext());
        Bundle bu_params = getIntent().getExtras();
        secuencial = bu_params.getInt("secuencial");
        caseta = bu_params.getString("caseta");
        is_terminacion = bu_params.getString("terminacion");
        try {
            if (!bu_params.getString("anomalia").equals("")) {
                is_anomalia = bu_params.getString("anomalia");
            }
        } catch (Throwable e) {

        }

        temporal = bu_params.getInt("temporal");
        cantidad = bu_params.getInt("cantidad");

        ca = this;

        tv_indicador = (TextView) findViewById(R.id.tv_indicador);
        captureButton = (Button) findViewById(R.id.camara_b_capture);
        backButton = (Button) findViewById(R.id.camara_b_regresa);
        otraButton = (Button) findViewById(R.id.camara_b_otra);
        ib_flash = (ImageButton) findViewById(R.id.ib_flash);

        btnBajarResolucion = (ImageButton) findViewById(R.id.ib_bajarResolucion);
        btnSubirResolucion = (ImageButton) findViewById(R.id.ib_subirResolucion);
        btnCambiarCamara = (ImageButton) findViewById(R.id.ib_cambiarCamara);
        btnFirmar = (ImageButton) findViewById(R.id.ib_firmar);

        fotoPreview = (FrameLayout) findViewById(R.id.camera_preview_foto);
        cPreview = (FrameLayout) findViewById(R.id.camera_preview);

        tieneFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!tieneFlash) {
            ib_flash.setVisibility(View.GONE);
        } else {
            displayFlashMode();
        }
        if (!tieneZoom) {
            btnBajarResolucion.setVisibility(View.GONE);
            btnSubirResolucion.setVisibility(View.GONE);
        }
        if (is_terminacion.equals("Check")) {
            btnBajarResolucion.setVisibility(View.GONE);
            btnSubirResolucion.setVisibility(View.GONE);
            btnFirmar.setVisibility(View.GONE);
        }
        if (!globales.tomaMultiplesFotos && cantidad > 1) {
            cantidad = 1;
        }
        iniciaCamara();
        mostrarInformacion();
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @SuppressLint("NewApi")
                    public void onClick(View v) {
                        // get an image from the camera
                        if (!otraFoto) {

                            ca.mensajeEspere();
                            captureButton.setEnabled(false);
                            mCamera.autoFocus(mAutoFocusCallback);

                            //mCamera.takePicture(null, null, mPicture);
                            otraFoto = true;
                            //tv_indicador.setVisibility(View.GONE);

                            ib_flash.setVisibility(View.GONE);
                            btnFirmar.setVisibility(View.GONE);
                            btnSubirResolucion.setVisibility(View.GONE);
                            btnBajarResolucion.setVisibility(View.GONE);
                            btnCambiarCamara.setVisibility(View.GONE);
                        } else {
                            iniciaCamara();
                            mostrarInformacion();
                            cPreview.setVisibility(View.VISIBLE);
                            fotoPreview.setVisibility(View.GONE);
                            otraFoto = false;
                            if (tieneFlash) {
                                ib_flash.setVisibility(View.VISIBLE);
                            } else {
                                ib_flash.setVisibility(View.GONE);
                            }
                            if (tieneZoom) {
                                btnBajarResolucion.setVisibility(View.VISIBLE);
                                btnSubirResolucion.setVisibility(View.VISIBLE);
                            }else {
                                btnBajarResolucion.setVisibility(View.GONE);
                                btnSubirResolucion.setVisibility(View.GONE);
                            }
                            if (is_terminacion.equals("Check")) {
                                btnCambiarCamara.setVisibility(View.GONE);
                                btnBajarResolucion.setVisibility(View.GONE);
                                btnSubirResolucion.setVisibility(View.GONE);
                            }
                            else btnCambiarCamara.setVisibility(View.VISIBLE);
                        }

                    }
                }
        );

        backButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // get an image from the camera
                        fotosTomadas++;
                        if (fotosTomadas >= cantidad) {
                            regresar();
                        } else {
                            otraFoto = false;
                            guardarFotoBD();
                            mostrarInformacion();
                            if (tieneFlash) {
                                ib_flash.setVisibility(View.VISIBLE);
                            }else ib_flash.setVisibility(View.GONE);
                            if (tieneZoom) {
                                btnBajarResolucion.setVisibility(View.VISIBLE);
                                btnSubirResolucion.setVisibility(View.VISIBLE);
                            }else {
                                btnBajarResolucion.setVisibility(View.GONE);
                                btnSubirResolucion.setVisibility(View.GONE);
                            }
                            if (is_terminacion.equals("Check")) {
                                btnCambiarCamara.setVisibility(View.GONE);
                                btnBajarResolucion.setVisibility(View.GONE);
                                btnSubirResolucion.setVisibility(View.GONE);
                            }
                            else btnCambiarCamara.setVisibility(View.VISIBLE);
                            iniciaCamara();
                            cPreview.setVisibility(View.VISIBLE);
                            fotoPreview.setVisibility(View.GONE);
                        }
                    }
                }
        );

        otraButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // get an image from the camera

                        otraFoto = false;
                        mostrarInformacion();

                        if (tieneFlash) {
                            ib_flash.setVisibility(View.VISIBLE);
                        } else ib_flash.setVisibility(View.GONE);
                        if (tieneZoom) {
                            btnBajarResolucion.setVisibility(View.VISIBLE);
                            btnSubirResolucion.setVisibility(View.VISIBLE);
                        } else {
                            btnBajarResolucion.setVisibility(View.GONE);
                            btnSubirResolucion.setVisibility(View.GONE);
                        }
                        if (is_terminacion.equals("Check")) {
                            btnCambiarCamara.setVisibility(View.GONE);
                            btnBajarResolucion.setVisibility(View.GONE);
                            btnSubirResolucion.setVisibility(View.GONE);
                        }
                        else btnCambiarCamara.setVisibility(View.VISIBLE);

                        guardarFotoBD();
                        iniciaCamara();
                        cPreview.setVisibility(View.VISIBLE);
                        fotoPreview.setVisibility(View.GONE);
                    }
                }
        );

        btnBajarResolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hacerBajarResolucion();
            }
        });

        btnSubirResolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hacerSubirResolucion();
            }
        });

        btnCambiarCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hacerCambiarCamara();
            }
        });

        btnFirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hacerFirmar();
            }
        });

    }

    public void iniciaCamara() {
        mensajeEspere();
        // Create an instance of Camera
        captureButton.setText("Captura");
        backButton.setVisibility(View.GONE);
        otraButton.setVisibility(View.GONE);
        captureButton.setEnabled(true);
        if (mCamera == null) {
            mCamera = getCameraInstance(globales.camaraFrontal);
            //Si no pudimos habrir la camara, mandamos un lindo mensajito...
            if (mCamera == null) {
                Toast.makeText(this, String.format(getString(R.string.msj_error_descripcion), getString(R.string.msj_camara_obtener)) + mensajeDeErrorCamera, Toast.LENGTH_LONG).show();
                tieneCamaraFrontal = false;
                ca.alert.dismiss();
                this.finish();
                return;
            }
            Camera.Parameters cp = mCamera.getParameters();
//
//    		 if (Build.VERSION.SDK_INT>=8)
//    			 setDisplayOrientation(mCamera,180);
//    		 else
//    		 {
//        		 cp.set("orientation", "portrait");
//        		 cp.set("rotation", 180);
//    		 }
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            cp.getSupportedPictureSizes();
            Cursor c = db.rawQuery("Select cast(value as integer) value from config where key='tam_fotos'", null);
            if (c.getCount() > 0) {
                int n, m;
                c.moveToFirst();
                n = c.getColumnIndex("value");
                m = c.getInt(n);
                if (tieneCamaraFrontal) m = 1;
                Size size = cp.getSupportedPictureSizes().get(m);
                cp.setPictureSize(size.width, size.height);
                //cp.setJpegQuality(70);
                cp.setJpegQuality(/*globales.calidadDeLaFoto*/globales.calidadOverride);
            }
            c.close();
            db.close();
            dbHelper.close();
            if (!tieneCamaraFrontal) {
                if (tieneFlash) {
                    cp.setFlashMode(flashMode);
                }
                tieneZoom = cp.isZoomSupported();
                if (tieneZoom) {
                    cp.setZoom(globales.zoom);
                }
                //cp.setPictureSize(1633, 1225);
                mCamera.setParameters(cp);
            }
        } else {
            try {
                if (!tieneCamaraFrontal) {
                    Camera.Parameters cp = mCamera.getParameters();
                    if (tieneFlash) {
                        cp.setFlashMode(flashMode);
                        mCamera.setParameters(cp);
                    }
                    tieneZoom = cp.isZoomSupported();
                    if (tieneZoom) {
                        cp.setZoom(globales.zoom);
                        mCamera.setParameters(cp);
                    }
                }
                mCamera.reconnect();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                mCamera = null;
                ca.alert.dismiss();
            }
        }
        if (mCamera == null) {
            globales.camaraFrontal = 0;
            tieneCamaraFrontal = false;
            ca.alert.dismiss();
            this.finish();
        }
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CamaraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.removeAllViews();
        preview.addView(mPreview);
        ca.alert.dismiss();
    }

    protected void setDisplayOrientation(Camera camera, int angle) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[]{angle});
        } catch (Exception e1) {
        }
    }

    public static Camera getCameraInstance(int numCamara) {
        Camera c = null;
        try {
            c = Camera.open(numCamara); // attempt to get a Camera instance
            c.setDisplayOrientation(90);
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            mensajeDeErrorCamera = e.getMessage();
        }
        return c; // returns null if camera is unavailable
    }

    private PictureCallback mPicture = new PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {

            captureButton.setText(R.string.msj_camara_volverATomar);
            backButton.setVisibility(View.VISIBLE);
            if (globales.mostrarOtraFoto) {
                otraButton.setVisibility(View.VISIBLE);
            }
            captureButton.setEnabled(true);
            ca.alert.dismiss();
            //alert.dismiss();
            //captureButton.setText("Camara");
            if (data == null) {
              /*  Log.d(TAG, "Error creating media file, check storage permissions: " +
                    e.getMessage());*/
                return;
            } else {
                if (Build.VERSION.SDK_INT >= 11)
                    mCamera.stopPreview();
                guardarFotoTmp(data);
                muestraPreview();
            }
        }
    };
    
  /*  public void muestraPreview(){
    	CamaraPreview cp= new CamaraPreview(this, mCamera);
    }*/

    private void guardarFotoTmp(byte[] foto) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String ls_unicom, ls_nisrad;

//    	Cursor c= db.rawQuery("Select registro from encabezado", null);
//    	
//    	c.moveToFirst();
//    	ls_unicom= new String (c.getBlob(c.getColumnIndex("registro")), getResources().getInteger(R.integer.POS_DATOS_UNICOM), getResources().getInteger(R.integer.LONG_CAMPO_UNICOM));
//    	
//    	ls_nombre= ls_unicom.substring(0, 4)+"_" +ls_unicom.substring(4, 6)+"_"+ls_unicom.substring(6,10)+"_"+ls_unicom.substring(10, 12);
//    	c.close();
//    	
//    	//Quiero su nis_rad
//    	
//    	c= db.rawQuery("Select nisRad from ruta where cast(secuencia as Integer) ="+secuencial, null);
//    	c.moveToFirst();
//    	
//    	ls_nombre+="_"+ Main.rellenaString(c.getString(c.getColumnIndex("nisRad")), "0", getResources().getInteger(R.integer.LONG_CAMPO_POLIZA), true);
//
//    	c.close();
//    	db.close();
//    	dbHelper.close();
//    	//ls_nombre=caseta+ "_"+ secuencial + "_" + Main.obtieneFecha()+".jpg";
//    	
//    	ls_nombre+="_"+ Main.obtieneFecha("ymd_his");
//    	//Hay que preguntar por la terminacion
//    	ls_nombre+= "_" + is_terminacion +".jpg";

        if (is_terminacion.equals("Check")) {
            ls_nombre = Main.rellenaString(is_terminacion, "x", 10, true) + "-";
            ls_nombre += Main.rellenaString(caseta, "0", 20, true) + "-";
            ls_nombre += Main.obtieneFecha("ymd");
            ls_nombre += Main.obtieneFecha("his");
            ls_nombre += ".JPG";
        }else{
            ls_nombre = globales.tdlg.getNombreFoto(globales, db, secuencial, is_terminacion, is_anomalia);
        }
        db.close();
        dbHelper.close();

        cv_datos = new ContentValues(4);

        ByteArrayInputStream imageStream = new ByteArrayInputStream(foto);
        Bitmap theImage = rotateImage(imageStream);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        theImage.compress(Bitmap.CompressFormat.JPEG, 100, out);

        byte[] fotoAGuardar = out.toByteArray();

        cv_datos.put("secuencial", secuencial);
        cv_datos.put("nombre", ls_nombre);
        cv_datos.put("foto", fotoAGuardar);
        cv_datos.put("envio", TomaDeLecturas.NO_ENVIADA);
        cv_datos.put("temporal", temporal);
        this.foto = foto;
    }

    @Override
    public void onBackPressed() {
        if (globales.puedoCancelarFotos && !otraFoto) {

            mCamera.stopPreview();
            mPreview.setCamera(null);

            globales.camaraFrontal = 0;
            tieneCamaraFrontal = false;
            if (mCamera != null) {
                globales.camaraFrontal = findBackCamera();
                mCamera.release();
                mCamera = null;
            }
            finish();
        }
    }

    public void regresar() {
        guardarFotoBD();
        globales.camaraFrontal = 0;
        tieneCamaraFrontal = false;
// CE, 10/10/22, Estoy cerrando la camara aqui para evitar un problema al volverla a abrir
        if (mCamera != null) {
            globales.camaraFrontal = findBackCamera();
            mCamera.release();
            mCamera = null;
        }
        this.finish();
    }

    public void guardarFotoBD() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        db.insert("fotos", null, cv_datos);

        //Guardar las fotos en la memoria del telefono, si me piden esto despues lo habilito pero por mientras vamos a quitarlo, ya que no tenemos control de esto.
    	/*File pictureFile = getOutputMediaFile(1, ls_nombre);
        if (pictureFile == null){
            //Log.d(TAG, "Error creating media file, check storage permissions: " +
              //  e.getMessage());
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(foto);
            fos.close();
        } catch (Throwable e) {
            
        }*/

        db.close();
        dbHelper.close();
    }

    @Override
    protected void onDestroy() {
        tieneCamaraFrontal = false;
        if (mCamera != null) {
            globales.camaraFrontal = findBackCamera();
            mCamera.release();
            mCamera = null;
        }
        super.onDestroy();
    }

    private static File getOutputMediaFile(int type, String ls_nombre) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                // Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    ls_nombre);
        } else if (type == 2) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            camera.takePicture(null, null, mPicture);
        }
    };

    public void muestraPreview() {
        ImageView imageView = new ImageView(this);
        int padding = /*context.getResources().getDimensionPixelSize(R.dimen.padding_medium)*/0;
        imageView.setPadding(padding, padding, padding, padding);
        //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ByteArrayInputStream imageStream = new ByteArrayInputStream(foto);
        Bitmap theImage = rotateImage(imageStream);
        //theImage =resizeImage(imageStream);
        imageView.setImageBitmap(theImage);

        fotoPreview.removeAllViews();
        fotoPreview.addView(imageView);

        fotoPreview.setVisibility(View.VISIBLE);
        cPreview.setVisibility(View.GONE);

        mCamera.release();
        mCamera = null;


    }

//	@SuppressLint("NewApi")
//	public Bitmap rotateImage(ByteArrayInputStream imageStream ){
//		Bitmap theImage = BitmapFactory.decodeStream(imageStream);
//	
//		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
//		
//		Display display = wm.getDefaultDisplay();
//		Point size = new Point();
//	
//	
//		int swidth ;
//		int sheight;
//		 int width = theImage.getWidth();
//	     int height = theImage.getHeight();
//		
//		try { 
//			display.getSize(size); 
//			swidth = size.y; 
//			} catch (NoSuchMethodError e) {
//				 swidth = display.getHeight(); 
//				} 
//		
//		sheight= (height * swidth) / width;
//		
//		
//	    
//	     int newWidth = swidth -10 ;
//	     int newHeight = sheight -10;
//	
//	     // calculate the scale - in this case = 0.4f
//	     float scaleWidth = ((float) newWidth) / width;
//	     float scaleHeight = ((float) newHeight) / height;
//	
//	     // createa matrix for the manipulation
//	     Matrix matrix = new Matrix();
//	     // resize the bit map
//	     matrix.postScale(scaleWidth, scaleHeight);
//	     // rotate the Bitmap
//	     matrix.postRotate(90);
//
//	     // recreate the new Bitmap
//	     Bitmap resizedBitmap = Bitmap.createBitmap(theImage, 0, 0,
//	                       width, height, matrix, true);
//	     
//	     return resizedBitmap;
//	}

    @SuppressLint("NewApi")
    public Bitmap rotateImage(ByteArrayInputStream imageStream) {
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();
        Point size = new Point();


        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // rotate the Bitmap

        int numRotation= 0;
        if (tieneCamaraFrontal) {
            numRotation = 270;
        } else {
            numRotation = 90;
        }
        matrix.postRotate(numRotation);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(theImage, 0, 0,
                theImage.getWidth(), theImage.getHeight(), matrix, true);

        return resizedBitmap;
    }

    @SuppressLint("NewApi")
    public Bitmap resizeImage(ByteArrayInputStream imageStream) {
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();
        Point size = new Point();


        int swidth;
        int sheight;
        int width = theImage.getWidth();
        int height = theImage.getHeight();

        try {
            display.getSize(size);
            swidth = size.y;
        } catch (NoSuchMethodError e) {
            swidth = display.getHeight();
        }

        sheight = (height * swidth) / width;


        int newWidth = swidth - 10;
        int newHeight = sheight - 10;

        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);


        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(theImage, 0, 0,
                width, height, matrix, true);

        return resizedBitmap;
    }


    public void mensajeEspere() {


        final LayoutInflater inflater = this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.wait_messagebox, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setView(view);


        builder
                .setCancelable(false);

        alert = builder.create();

        alert.show();
    }

    public void mostrarInformacion() {
        if (cantidad > 1) {
            //Toast.makeText(this,String.format(getString(R.string.msj_fotos_cantidad_a_tomar), cantidad), Toast.LENGTH_LONG).show();
            tv_indicador.setVisibility(View.VISIBLE);
            tv_indicador.setText((fotosTomadas + 1) + " " + getString(R.string.de) + " " + cantidad + " " + getString(R.string.msj_fotos));
        }
    }

    public void flashMode(View view) {
        //Verificamos primero el modo actual, y luego cambiamos al siguiente...como un carrusel
        switch (globales.flash) {
            case SIN_FLASH:
                globales.flash = CON_FLASH;
                break;
            case CON_FLASH:
                globales.flash = AUTO;
                break;
            case AUTO:
                globales.flash = SIN_FLASH;
                break;
        }
        displayFlashMode();
        //tenemos que detener la camara y volverla a iniciar
        if (Build.VERSION.SDK_INT >= 11)
            mCamera.stopPreview();
        iniciaCamara();
    }

    public void zoomMode(boolean bSubir) {
        //Verificamos primero el modo actual, y luego cambiamos al siguiente...como un carrusel
        Camera.Parameters cp = mCamera.getParameters();
        if (bSubir) {
            globales.zoom = globales.zoom + 10;
            if (globales.zoom > cp.getMaxZoom())
                globales.zoom = cp.getMaxZoom();
        }else {
            globales.zoom = globales.zoom - 10;
            if (globales.zoom < 0)
                globales.zoom = 0;
        }
        cp.setZoom(globales.zoom);
        if (!tieneCamaraFrontal) {
            mCamera.setParameters(cp);
        }
    }

    public void camaraFrontalMode() {
        //tenemos que detener la camara y volverla a iniciar
        if (!tieneCamaraFrontal) {
            tieneCamaraFrontal = true;
            globales.camaraFrontal = findFrontCamera();
        }else {
            tieneCamaraFrontal = false;
            globales.camaraFrontal = findBackCamera();
        }
        if (Build.VERSION.SDK_INT >= 11)
            mCamera.stopPreview();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        iniciaCamara();
    }

    private int findFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return camIdx;
            }
        }
        return -1;
    }

    private int findBackCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return camIdx;
            }
        }
        return -1;
    }

    public void displayFlashMode() {
        switch (globales.flash) {
            case SIN_FLASH:
                flashMode = Camera.Parameters.FLASH_MODE_OFF;
                ib_flash.setImageResource(R.drawable.ic_sin_flash);
                break;
            case CON_FLASH:
                flashMode = Camera.Parameters.FLASH_MODE_ON;
                ib_flash.setImageResource(R.drawable.ic_con_flash);
                break;
            case AUTO:
                flashMode = Camera.Parameters.FLASH_MODE_AUTO;
                ib_flash.setImageResource(R.drawable.ic_auto);
                break;
        }
    }

    protected void hacerBajarResolucion(){
        // Sustituir por el código que permita bajar la resolución
        zoomMode(false);
//        Utils.showMessageLong(this, "Bajar resolución");
    }

    protected  void hacerSubirResolucion() {
        // Sustituir por el código que permita subir  la resolución
        zoomMode(true);
//        Utils.showMessageLong(this, "Subir resolución");
    }

    protected  void hacerCambiarCamara() {
        // Sustituir por el código que permita intercambiar las cámaras
        camaraFrontalMode();
//        Utils.showMessageLong(this, "Cambiar cámara");
    }

    protected void hacerFirmar() {
        // Sustituir por el código que permita llamar el Activity para firmar

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ls_nombre = globales.tdlg.getNombreFoto(globales, db, secuencial, is_terminacion, is_anomalia);
        db.close();
        dbHelper.close();

        Intent padParaFirmar = new Intent(this, SignaturePadActivity.class);
        padParaFirmar.putExtra("secuencial", secuencial);
        padParaFirmar.putExtra("caseta", caseta);
        padParaFirmar.putExtra("terminacion", is_terminacion);
        padParaFirmar.putExtra("temporal", temporal);
        padParaFirmar.putExtra("cantidad", cantidad);
        padParaFirmar.putExtra("anomalia", is_anomalia);
        padParaFirmar.putExtra("ls_nombre", ls_nombre);
        // vengoDeFotos = true;
        startActivityForResult(padParaFirmar, 1);
//        startActivity(padParaFirmar);

        //        Utils.showMessageLong(this, "Firmar");
    }

    @Override
    protected void onResume() {
        //Ahora si abrimos
        if (globales.tdlg == null) {
            super.onResume();
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            System.exit(0);
            return;
        }
        super.onResume();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bu_params;
//        switch (requestCode) {
//            case FIRMA:
                if (data == null) {
//                    mensajeOK(getString(R.string.msj_main_operacion_cancelada));
                    return;
                }
                if (resultCode == Activity.RESULT_OK) {
//                    if (bu_params.getString("mensaje").length() > 0)
//                        mensajeOK(bu_params.getString("mensaje"));
                } else {
//                    mensajeOK(getString(R.string.msj_main_operacion_cancelada));
                }
//                break;
//        }
    }
}