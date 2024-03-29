package enruta.sistole_engie;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import enruta.sistole_engie.clases.BuscarMedidorMgr;
import enruta.sistole_engie.clases.EmergenciaCallback;
import enruta.sistole_engie.clases.EmergenciaMgr;
import enruta.sistole_engie.clases.Utils;
import enruta.sistole_engie.entities.NoRegistradoEntity;
import enruta.sistole_engie.entities.OperacionResponse;
import enruta.sistole_engie.services.DbLecturasMgr;

public class TomaDeLecturas extends TomaDeLecturasPadre implements
        OnGestureListener {

    int ii_dondeEstaba = 0;


    Timer timer = new Timer();

    String is_anomaliasIngresadasAnteriormente = "";


    double porcentaje = 1.0;
    double porcentajeInfoCliente = 1.0;
    double factorPorcentaje = 0.05;

    int segundoCambiarFuente = 5;
    boolean permiteCambiarFuente = false;
    Timer cambiarFuenteTimer = new Timer();


    private GestureDetector gestureScanner;

    // EditText et_lectura/*, et_presion*/;
    // TextView tv_informacion, tv_lectura, tv_presion;
    private TextView tv_caseta, tv_min, tv_max, tv_mensaje, tv_respuesta/*
     * ,
     * tv_lectura
     */, tv_contador, tv_presion, tv_comentarios, tv_lectura,
            tv_anomalia, tv_contadorOpcional, tv_lecturaAnterior, tv_campo0, tv_campo1, tv_campo2, tv_campo3, tv_campo4,
            label_campo0, label_campo1, label_campo2, label_campo3, label_campo4, tv_advertencia;
    private Button button1, button2, button3, button4, button5, button6, b_repetir_anom;
    private View layout;

    // String globales.is_lectura, globales.is_presion, globales.is_caseta,
    // globales.is_terminacion;

    private LinearLayout ll_limites, ll_linearLayout1, ll_generica, ll_linearLayout2, cuadricula, celda0, celda1, celda2, celda3, celda4;

    boolean filtrarComentarios = false;
    //boolean captureAnomalias=false;

    String is_lectAnt = "", is_comentarios, is_problemas;


//	String ultimaAnomaliaSeleccionada="";
//	String ultimaSubAnomaliaSeleccionada="";

    boolean preguntaSiBorraDatosComodin = false;
    boolean permiteCambiarModoCaptura = true;

    float anomSize, lecturaSize, casetaSize, cialSize, minSize, maxSize,
            nombreSize, comentariosSize, nisradSize, direccionSize,
            contadorOpcionalSize, tipoMedidorSize, sizeGenerico = 14, labelCuadriculaSize, respuestasSize;

    int modoCambiofuente = NINGUNO;
    long secuencialAntesDeInput = 0;
    boolean modoLecturaObligatoria = false;


    private EmergenciaMgr mEmergenciaMgr = null;

    private IntentIntegrator scanIntent;
    private BuscarMedidorMgr mBuscarMedidorMgr = null;
    private AlertDialog mAlertEmergencia = null;
    private DialogoMensaje mDialogoMsg = null;

    @SuppressLint("NewApi")
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Bundle bu_params;

        esconderTeclado();
        boolean permiteTomarFoto = true;

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            mostrarMedidorEscaneado(scanResult.getContents());
        } else {

            switch (requestCode) {
                case FOTOS:
                    procesarFotos();
                    break;
                case LECTURA:
                    permiteTomarFoto = procesarLectura(data, resultCode);
                    break;
                /*
                 * case COMENTARIOS: if (resultCode == Activity.RESULT_OK){ bu_params
                 * =data.getExtras(); is_comentarios=bu_params.getString("input");
                 * setModoCaptura(); tv_comentarios.setText(is_comentarios); }
                 *
                 * break;
                 */
                /*
                 * case PRESION: if (resultCode == Activity.RESULT_OK){ bu_params
                 * =data.getExtras(); globales.is_presion=bu_params.getString("input");
                 * setModoCaptura(); tv_presion.setText("P:" + globales.is_presion); if
                 * (globales.is_presion.trim().equals("")){
                 * tv_presion.setVisibility(View.GONE); } else{
                 * tv_presion.setVisibility(View.VISIBLE); } } break;
                 */

                case ANOMALIA:
                    procesarAnomalia(requestCode, data, resultCode);
                    break;
                case BUSCAR_MEDIDOR:
                    procesarBuscarMedidor(data, resultCode);
                    break;
                case REQUEST_ENABLE_BT:
                    procesarMandarImprimir();
                    break;
                case NO_REGISTADOS:
                    procesarNoRegistrados(data, resultCode);
                    break;
                case CAMBIAR_MEDIDOR:
                    procesarCambiarMedidor(data, resultCode);
                    break;
                case INPUT_CAMPOS_GENERICO_ME:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            bu_params = data.getExtras();
                            globales.tdlg.regresaDeCamposGenericos(bu_params, bu_params.getString("anomalia"));

//					//Escondemos mensaje
//					tv_mensaje.setVisibility(View.GONE);
//					//Restablecemos botones
//					button1.setEnabled(true);
//					button2.setEnabled(true);
//					ll_linearLayout2.setVisibility(View.VISIBLE);
//				 
                            capturar();
                        } catch (Throwable t) {
                            mostrarMensaje("Error", "Error inesperado. Contacte soporte", t.getMessage(), null);
                        }
                    }
                    break;
                case FOTO_NO_REGISTRADO:
                    //procesarFotoNoRegistrado();
                    break;
            }

        }
        if (Build.VERSION.SDK_INT >= 11)
            invalidateOptionsMenu();

    }

    public void setModoCaptura() {
        setModoCaptura(true);

    }

    public void setModoCaptura(boolean esconderBuscar) {
        globales.modoCaptura = true;
        button4.setVisibility(View.INVISIBLE);
        button6.setText(R.string.guardar);
        button6.setEnabled(true);
        // button3.setEnabled(false);
        button3.setEnabled(!esconderBuscar);
        tv_contador.bringToFront();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tomadelecturas);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);

        is_mensaje_direccion = getString(R.string.msj_lecturas_no_hay_mas);
        setTitle("");

        //startListeningGPS();
        mHandler = new Handler();


        globales = ((Globales) getApplicationContext());
        inicializarVariables();


        globales.fechaEnMilisegundos = Calendar.getInstance().getTimeInMillis();

        Bundle bu_params = getIntent().getExtras();
        globales.esSuperUsuario = bu_params.getBoolean("esSuperUsuario");

        globales.is_nombre_Lect = bu_params.getString("nombre");
        bHabilitarImpresion = bu_params.getBoolean("bHabilitarImpresion");


        porcentaje = globales.porcentaje_lectura;
        porcentajeInfoCliente = globales.porcentaje_info;


        globales.ultimoBloqueCapturado = globales.tdlg.ultimoBloqueCapturadoDefault;
        globales.ultimoMedidorCapturado = "";

        // Obtenemos la impresora...
        getImpresora();

        // Filtrado y para empezar editando

        openDatabase();
        Cursor c = db.query("config", null, "key='modo'", null, null, null,
                null);

        if (c.getCount() > 0) {
            c.moveToFirst();

            // Modo correccion segun el su posicion en el arreglo
            if (c.getInt(c.getColumnIndex("value")) == 1) {
                globales.capsModoCorreccion = true;
                globales.bModificar = true;

            }

        }

        c.close();

        // ahora las lecturas forzadas, fotos forzadas y demas

        c = db.query("config", null, "key='modo_config'", null, null, null,
                null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            modo = Integer.parseInt(c.getString(c.getColumnIndex("value")));

            //this.modo = modo;
            switch (modo) {
                case Input.NORMAL:// Normal
                    break;

                case Input.SIN_FOTOS: // Sin fotos
                    globales.validar = false;
                    break;

                case Input.FOTOS: // Fotos
                    globales.fotoForzada = true;
                    break;

                case Input.FOTOS_CC: // Fotos Control de Calidad
                    globales.validar = false;
                    break;
            }
        }

        c.close();
        closeDatabase();
        try {
            globales.tll = new TodasLasLecturas(this);
            if (!globales.tll.hayMasLecturas())
                throw new Throwable();
        } catch (Throwable e) {
//			if (!globales.bModificar) {
//				Toast.makeText(this, R.string.msj_lecturas_no_hay_mas, Toast.LENGTH_LONG)
//						.show();
//				// finish();
//				muere();
//				return;
//			}

        }

        boolean obtenerSiguiente = true;
        gestureScanner = new GestureDetector(this, this);

        globales.il_total = globales.tll.getNumRecords();

        layout = (View) findViewById(R.id.relativeLayoutm);

        iniciaCampos();

        //Datos de la cuadricula que hay
        celda0.setVisibility(globales.ver_celda0 ? View.VISIBLE : View.GONE);
        celda1.setVisibility(globales.ver_celda1 ? View.VISIBLE : View.GONE);
        celda2.setVisibility(globales.ver_celda2 ? View.VISIBLE : View.GONE);
        celda3.setVisibility(globales.ver_celda3 ? View.VISIBLE : View.GONE);
        celda4.setVisibility(globales.ver_celda4 ? View.VISIBLE : View.GONE);


        ViewTreeObserver vto = tv_lectura.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                setPorcentaje();
                ViewTreeObserver obs = tv_lectura.getViewTreeObserver();

                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                // {
                // obs.removeOnGlobalLayoutListener(this);
                // } else {
                obs.removeGlobalOnLayoutListener(this);
                // }
            }
        });

        // if (obtenerSiguiente)
        // getSigLect();
        // else

//		if (globales.bModificar) {
//			try {
//				globales.tll.setSecuencialLectura(0);
//			} catch (Throwable e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			setModoModificacion();
//
//			if (!globales.bModificar) {
//				// Aqui entra porque me sacaron del modo de correccion (no hay
//				// nada que corregir), asi que
//				globales.capsModoCorreccion = false;
//				openDatabase();
//				db.execSQL("Update config set value='0' where key='modo'");
//				closeDatabase();
//				// getSigLect();
//			}
//		}

        permiteCerrar();
        if (globales.bcerrar) {
            // iniciarModoCorreccionCAPS();
            rutaFinalizada();
        } else
            setDatos();
        // c.close();
    }

    /*
     * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
     * menu; this adds items to the action bar if it is present.
     * //getMenuInflater().inflate(R.menu.toma_de_lecturas, menu); return true;
     * }
     */

    private void iniciaCampos() {
        // et_lectura=(EditText) findViewById(R.id.et_lectura);
        // et_presion=(EditText) findViewById(R.id.et_presion);

        // tv_informacion= (TextView) findViewById(R.id.tv_informacion);
        // et_lectura= (EditText) findViewById(R.id.et_lectura);
        tv_caseta = (TextView) findViewById(R.id.tv_caseta);
        tv_min = (TextView) findViewById(R.id.tv_min);
        tv_max = (TextView) findViewById(R.id.tv_max);
        tv_mensaje = (TextView) findViewById(R.id.tv_mensaje);
        tv_respuesta = (TextView) findViewById(R.id.tv_respuesta);
        tv_contador = (TextView) findViewById(R.id.tv_contador);
//		tv_presion = (TextView) findViewById(R.id.tv_presion);
        tv_comentarios = (TextView) findViewById(R.id.tv_comentarios);
        tv_lectura = (TextView) findViewById(R.id.tv_lectura);
        tv_anomalia = (TextView) findViewById(R.id.tv_anomalia);
        tv_contadorOpcional = (TextView) findViewById(R.id.tv_contadorOpcional);
        tv_advertencia = (TextView) findViewById(R.id.tv_advertencia);
        tv_lecturaAnterior = (TextView) findViewById(R.id.tv_lecturaAnterior);

        cuadricula = (LinearLayout) findViewById(R.id.cuadricula);

        tv_campo0 = (TextView) findViewById(R.id.campo0);
        tv_campo1 = (TextView) findViewById(R.id.campo1);
        tv_campo2 = (TextView) findViewById(R.id.campo2);
        tv_campo3 = (TextView) findViewById(R.id.campo3);
        tv_campo4 = (TextView) findViewById(R.id.campo4);

        label_campo0 = (TextView) findViewById(R.id.label_campo0);
        label_campo1 = (TextView) findViewById(R.id.label_campo1);
        label_campo2 = (TextView) findViewById(R.id.label_campo2);
        label_campo3 = (TextView) findViewById(R.id.label_campo3);
        label_campo4 = (TextView) findViewById(R.id.label_campo4);


        button1 = (Button) findViewById(R.id.button1);// Lectura
        button2 = (Button) findViewById(R.id.button3); // Presion
        button3 = (Button) findViewById(R.id.button2);// Comentarios
        button4 = (Button) findViewById(R.id.button4); // Anterior
        button5 = (Button) findViewById(R.id.button5);// Fotos
        button6 = (Button) findViewById(R.id.button6);// Siguiente

        b_repetir_anom = (Button) findViewById(R.id.b_repetir_anom);// Siguiente

        ll_limites = (LinearLayout) findViewById(R.id.ll_limites);
        ll_linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        ll_linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        ll_generica = (LinearLayout) findViewById(R.id.ll_generica);

        celda0 = (LinearLayout) findViewById(R.id.celda0);
        celda1 = (LinearLayout) findViewById(R.id.celda1);
        celda2 = (LinearLayout) findViewById(R.id.celda2);
        celda3 = (LinearLayout) findViewById(R.id.celda3);
        celda4 = (LinearLayout) findViewById(R.id.celda4);

        OnGestureListener ogl = new OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }

        };

        // tv_presion=(TextView) findViewById(R.id.tv_presion);
        final TomaDeLecturas parent = this;

        // Vamos a declarar los listeners ya que nos pueden servir mas adelante
        // en otros objetos
        View.OnClickListener clicLectura = new View.OnClickListener() {
            public void onClick(View v) {
                // Capturamos la lectura

                procesarBotonLectura(parent);
            }
        };

        View.OnLongClickListener longClicLectura = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                cancelaTimer();
                // TODO Auto-generated method stub
                if (button1.isEnabled()) {
                    if (globales.bloquearBorrarSiIntento && !globales.tll.getLecturaActual().intento6.equals("")) {
                        return true;
                    }

                    cancelaTimer();
                    globales.is_lectura = "";
                    globales.tdlg.regresaDeBorrarLectura();

                    if (globales.tll.getLecturaActual().anomalias.size() == 0) {
                        globales.modoCaptura = false;
                        salirModoCaptura();
                        // borramos fotos temporales anteriores
                        openDatabase();

                        db.execSQL("delete from fotos where temporal="
                                + CamaraActivity.TEMPORAL);

                        closeDatabase();

                        //tv_lectura.setText(getString(R.string.lbl_tdl_indica_lectura) + globales.is_lectura);
                    }
                    globales.is_presion = globales.tll.getLecturaActual().getAnomaliaAMostrar();
                    tv_lectura.setText(getString(R.string.lbl_tdl_indica_lectura) + globales.is_lectura);
                    tv_anomalia.setText(getString(R.string.lbl_tdl_indica_anomalia) + globales.is_presion);

                    setDatos(false);
                    int requiereLectura = globales.tll.getLecturaActual().requiereLectura();
                    if (!globales.is_lectura.equals("") &&
                            (requiereLectura == Anomalia.LECTURA_AUSENTE))
                        setModoCaptura();
                    else
                        salirModoCaptura();

                    verficarSiPuedoDejarAusente();
                }

                return true;
            }
        };

        View.OnClickListener clicAnomalia = new View.OnClickListener() {

            public void onClick(View v) {
                // mensajeInput(PRESION);
                if (!button2.isEnabled()) {
                    return;
                }
                cancelaTimer();
                Intent anom = new Intent(parent, PantallaAnomalias.class);
                anom.putExtra("secuencial", globales.il_lect_act);
                anom.putExtra("lectura", globales.is_lectura);
                anom.putExtra("anomalia", globales.tll.getLecturaActual().getAnomaliasCapturadas());

                startActivityForResult(anom, ANOMALIA);
                // vengoDeAnomalias = true;

                is_anomaliasIngresadasAnteriormente = globales.is_presion;

            }
            // tv_lectura.setText(getString(R.string.lbl_tdl_indica_lectura) +globales.is_lectura);
        };

        View.OnLongClickListener longClicAnomalia = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                if (!button2.isEnabled()) {
                    return true;
                }

                cancelaTimer();


                if (/*globales.tll.getLecturaActual().anomalias.size()>1 && */globales.tll.getLecturaActual().getAnomaliasABorrar().respuestas.size() > 1) {
                    //Muestra mensaje
                    anomaliasABorrar(globales.tll.getLecturaActual().getAnomaliasABorrar());
                } else if (globales.tll.getLecturaActual().getAnomaliasABorrar().respuestas.size() == 1) {
//					if (globales.tll.getLecturaActual().anomalias.get(0).is_activa.equals("I")){
//						// si es inactiva, ni la toques.
//						return true;
//					}
                    //Solo hay una, asi que la borramos
                    if (globales.tll.getLecturaActual().deleteAnomalia(globales.tll.getLecturaActual().getAnomaliasAIngresadas()))
                        Toast.makeText(parent, R.string.msj_anomalias_borrada, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(parent, R.string.msj_anomalias_error_borrado, Toast.LENGTH_LONG).show();

                }


                regresaDeBorrar();

                verficarSiPuedoDejarAusente();

                return true;
            }
        };

        View.OnClickListener clicMedidor = new View.OnClickListener() {
            public void onClick(View v) {
                // mensajeInput(PRESION);
                /*
                 * intent.putExtra("tipo", Input.COMENTARIOS);
                 * intent.putExtra("comentarios", is_comentarios);
                 * startActivityForResult(intent, COMENTARIOS);
                 */
                if (!button3.isEnabled())
                    return;

                cancelaTimer();
                //Intent intent = new Intent(parent, BuscarMedidor.class);
                buscarMedidor(BuscarMedidor.BUSCAR);
                if (globales.switchBuscarPorMover)
                    globales.moverPosicion = false;
                else
                    globales.moverPosicion = true;

                globales.bEstabaModificando = globales.bModificar;

                globales.tll.guardarDondeEstaba();

            }
        };

        View.OnLongClickListener longClicMedidor = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                // mensajeInput(PRESION);
                /*
                 * intent.putExtra("tipo", Input.COMENTARIOS);
                 * intent.putExtra("comentarios", is_comentarios);
                 * startActivityForResult(intent, COMENTARIOS);
                 */

                if (!button3.isEnabled())
                    return true;

                cancelaTimer();
                //Intent intent = new Intent(parent, BuscarMedidor.class);
                buscarMedidor(BuscarMedidor.MOVER);

                return true;

            }
        };

        tv_anomalia.setClickable(true);
        tv_lectura.setClickable(true);
        tv_caseta.setClickable(true);

        tv_anomalia.setOnClickListener(clicAnomalia);
        tv_lectura.setOnClickListener(clicLectura);
        tv_caseta.setOnClickListener(clicMedidor);
        tv_caseta.setOnLongClickListener(longClicMedidor);

        tv_anomalia.setOnLongClickListener(longClicAnomalia);
        tv_lectura.setOnLongClickListener(longClicLectura);

        button1.setOnClickListener(clicLectura);
        button2.setOnClickListener(clicAnomalia);
        button3.setOnClickListener(clicMedidor);
        button3.setOnLongClickListener(longClicMedidor);

        button3.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(final View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (!globales.modoCaptura) {


                    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

                        /*
                         * Handler handler = new Handler();
                         * handler.postDelayed(new Runnable() { public void
                         * run() {
                         */
                        ((Button) view).setText(R.string.lbl_tdl_mover);
                        /*
                         * } },
                         * android.view.ViewConfiguration.getLongPressTimeout
                         * ());
                         */
                    } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                        // button6.setText(R.string.m_str_siguiente);
                        button3.setText(R.string.str_buscar);

                    }
                }

                return false;
            }

        });

        button4.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(final View view, MotionEvent event) {
                // TODO Auto-generated method stub

                if (!globales.modoCaptura) {
                    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

                        /*
                         * Handler handler = new Handler();
                         * handler.postDelayed(new Runnable() { public void
                         * run() {
                         */
                        ((Button) view).setText(R.string.lbl_tdl_primera);
                        /*
                         * } },
                         * android.view.ViewConfiguration.getLongPressTimeout
                         * ());
                         */
                    } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                        // button6.setText(R.string.m_str_siguiente);
                        button4.setText(R.string.m_str_anterior);

                    }
                }

                return false;
            }

        });

        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                permiteCerrar();
                globales.moverPosicion = false;
                cancelaTimer();
                if (globales.bModificar)
                    is_mensaje_direccion = getString(R.string.msj_tdl_no_mas_lecturas_ingr_antes);
                else
                    is_mensaje_direccion = getString(R.string.msj_tdl_no_mas_lecturas_antes);
                getAntLect();
                // enviarAvance();

            }
        });

        button6.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(final View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (!globales.modoCaptura) {
                    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                        /*
                         * Handler handler = new Handler();
                         * handler.postDelayed(new Runnable() { public void
                         * run() {
                         */
                        ((Button) view).setText(R.string.lbl_tdl_ultima);
                        /*
                         * } },
                         * android.view.ViewConfiguration.getLongPressTimeout
                         * ());
                         */

                    } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                        button6.setText(R.string.m_str_siguiente);

                    }
                }


                return false;
            }

        });
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!globales.modoCaptura) {
                    globales.moverPosicion = false;
                    cancelaTimer();
                    permiteCerrar();
                    if (globales.bModificar)
                        is_mensaje_direccion = getString(R.string.msj_tdl_no_mas_lecturas_ingr_despues);
                    else
                        is_mensaje_direccion = getString(R.string.msj_tdl_no_mas_lecturas_despues);
                    getSigLect();


                    // enviarAvance();
                } else {
                    capturar();
                }

            }
        });

        button4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                cancelaTimer();
                if (!globales.modoCaptura) {
                    permiteCerrar();
                    getPrimLect();
                    // enviarAvance();


                } else {
                    capturar();
                }

                return true;
            }
        });
        button6.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View arg0) {
                cancelaTimer();
                if (!globales.modoCaptura) {
                    permiteCerrar();
                    getUltLect();
                }
                // enviarAvance();
                return true;

            }
        });

        button6.setWidth(button2.getWidth());
        button5.setWidth(button3.getWidth());
        button4.setWidth(button1.getWidth());
    }

    public void buscarMedidor(final int tipo) {

        if (preguntaSiBorraDatos /*&& !globales.bModificar*/) {
            if (!globales.tll.getLecturaActual().getAnomaliasCapturadas().equals("") &&
                    !globales.modoCaptura && !globales.tdlg.esSegundaVisita(globales.tll.getLecturaActual().getAnomaliasCapturadas(), globales.tll.getLecturaActual().is_subAnomalia)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog alert;

                //Si ingresé datos, estoy en modo captura y  me estoy moviendo deberia preguntar
                builder.setMessage(R.string.str_pregunta_guardar_cambios)
                        .setCancelable(false).setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //recepcion();
                                preguntaSiBorraDatos = false;
                                preguntaSiBorraDatosComodin = true;
                                buscarMedidor(tipo);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

                alert = builder.create();
                alert.show();
                return;
            }
        }

        Intent intent = new Intent(this, BuscarMedidor.class);
        intent.putExtra("modificar", globales.bModificar);
        intent.putExtra("tipoDeBusqueda", tipo);
        startActivityForResult(intent, BUSCAR_MEDIDOR);
    }

    public void enviarAvance() {
        Intent lrs = new Intent(this, trasmisionDatos.class);
        lrs.putExtra("tipo", trasmisionDatos.TRANSMISION);
        lrs.putExtra("transmiteFotos", true);
        lrs.putExtra("metodo", TransmisionesPadre.WIFI);
        startActivity(lrs);
    }

    protected void setDatos() {
        setDatos(true);
    }

    @SuppressLint("NewApi")
    protected void setDatos(boolean reiniciaValores) {
        String codigoBarras = "";
        String serieMedidor = "";
        boolean intercambiarConCodigoBarras = false;
        boolean alinearDerechaNumMedidor = false;
        Lectura lecturaActual;

        try {
            if (reiniciaValores) {
                if (globales.tll.getNumRecords() > 0) {
                    if (!globales.tll.hayMasLecturas()
                            || globales.tll.getLecturaActual() == null) {
                        if (!globales.bModificar) {
                            if (globales.permiteDarVuelta && !globales.bcerrar) {
                                irALaPrimeraSinEjecutarAlTerminar();
                                globales.permiteDarVuelta = false;
                                return;
                            } else {
                                if (!globales.permiteDarVuelta)
                                    Toast.makeText(this, is_mensaje_direccion,
                                            Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            if (globales.sonLecturasConsecutivas
                                    && globales.estoyCapturando) {
                                globales.bcerrar = false;
                                globales.bModificar = false;
                                // tv_indica_corr.setText("N");
                                globales.capsModoCorreccion = false;
                                layout.setBackgroundResource(0);
                                // item.setIcon(R.drawable.ic_action_correccion);
                                globales.permiteDarVuelta = false;
                                getSigLect();
                                return;
                            } else {


                                if (globales.permiteDarVuelta) {
                                    irALaPrimeraSinEjecutarAlTerminar();
                                    globales.permiteDarVuelta = false;
                                    return;
                                } else {
                                    Toast.makeText(this, is_mensaje_direccion,
                                            Toast.LENGTH_SHORT).show();
                                }

                            }

                            // globales.bModificar=false;
                        }

                        // Si estoy tomando fotos consecutivas, no puedo cerrar ya que
                        // tengo una actividad hijo que depende de esta...
                        if (globales.bcerrar
                                && /* !estoyTomandoFotosConsecutivas */!globales.sonLecturasConsecutivas && !globales.capsModoCorreccion) {
                            // finish();
                            //muere();
                            //Llegamos a la ultima lectura...  hay que ir al principio y empezar modo correccion
                            //iniciarModoCorreccionCAPS();
                            rutaFinalizada();
                            globales.permiteDarVuelta = false;

//						globales.capsModoCorreccion = true;
//						globales.bModificar = true;
//						layout.setBackgroundResource(R.drawable.correccion_pattern);
//						getPrimLect();
                            return;
                        } else if (globales.bcerrar && globales.sonLecturasConsecutivas) {
                            // Puede darse el caso de que sea la ultima lectura
                            // consecutiva, se decide cerrar, pero el algoritmo
                            // avanza... no debe cerrar
                        } else
                            globales.bcerrar = true;
                    }
                } else {
                    Toast.makeText(this, R.string.msj_lecturas_no_hay_lecturas_cargadas, Toast.LENGTH_SHORT)
                            .show();
                    // finish();
                    muere();
                    return;
                }
            }

            button1.setEnabled(true);
            button2.setEnabled(true);
            ll_linearLayout2.setVisibility(View.VISIBLE);
            tv_mensaje.setVisibility(View.GONE);
            tv_respuesta.setVisibility(View.GONE);

            is_mensaje_direccion = getString(R.string.msj_lecturas_no_hay_mas);

            String ls_comentarios = "";

            globales.permiteDarVuelta = false;
            if (!globales.bModificar) {
                layout.setBackgroundResource(0);
                openDatabase();
                db.execSQL("update encabezado set ultimoSeleccionado=" + globales.tll.getLecturaActual().secuenciaReal);
                closeDatabase();
            }


            // button1.setEnabled(true);


            globales.il_lect_max = globales.tdlg.getLecturaMaxima();
            globales.il_lect_min = globales.tdlg.getLecturaMinima();

            lecturaActual = globales.tll.getLecturaActual();

            globales.il_lect_act = lecturaActual.secuenciaReal;
            is_comentarios = lecturaActual.getDireccion();
            globales.is_caseta = lecturaActual.is_serieMedidor;

            if (reiniciaValores) {
                globales.is_lectura = lecturaActual.getLectura();
                globales.is_presion = lecturaActual.getAnomaliaAMostrar();
                globales.is_terminacion = lecturaActual.terminacion;
                globales.mensaje = lecturaActual.ls_mensaje;
                ultimaAnomaliaSeleccionada = "";
                ultimaSubAnomaliaSeleccionada = "";
                preguntaSiBorraDatos = false;
                globales.ignorarContadorControlCalidad = false;
                preguntaSiBorraDatosComodin = false;
                globales.ignorarGeneracionCalidadOverride = false;
                //voyATomarFoto=false;
                regreseDe = NINGUNA;
                globales.ignorarTomaDeFoto = false;
                globales.contadorIntentos = 0;
                modoLecturaObligatoria = false;
                globales.fotoForzada = false;
                is_anomaliasIngresadasAnteriormente = "";
                //captureAnomalias=false;
            }

            preguntaRepiteAnomalia();

            // if (globales.is_caseta.contains("CF")){
            // setModoCaptura(false);
            // globales.is_lectura="0";
            // tv_caseta.setBackgroundResource(R.color.SteelBlue);
            //
            // }

            globales.is_lectura = globales.is_lectura == null ? ""
                    : globales.is_lectura;
            globales.is_presion = globales.is_presion == null ? ""
                    : globales.is_presion;
            is_comentarios = is_comentarios != null ? is_comentarios : "";

            // Voy a verificar lo de las lecturas consecutivas
            if (globales.tll.hayMasMedidoresIguales(globales.is_caseta)
                    && !globales.is_caseta.trim().equals("0"))
                globales.sonLecturasConsecutivas = true;
            else
                globales.sonLecturasConsecutivas = false;

            globales.requiereGPS = lecturaActual.requiereGPS;

            enciendeGPS();

            // RL, 2022-10-24, Se requiere que para las regionales de Tampico y Guadalajara
            // En el campo donde se muestra la serie del medidor se muestre el código de barras.
            // RL, 2023-01. Ahora la información de si intercambiar o no viene en el archivo TPL

            intercambiarConCodigoBarras = lecturaActual.getIntercambiarSerieMedidor();

            if (!intercambiarConCodigoBarras) {
                serieMedidor = lecturaActual.getSerieMedidorCorregido();
                tv_caseta.setText(serieMedidor);
            } else {
                codigoBarras = lecturaActual.getCodigoBarrasCorregido();
                tv_caseta.setText(codigoBarras);
            }

            alinearDerechaNumMedidor = lecturaActual.getAlinearDerechaNumMedidor();

            if (alinearDerechaNumMedidor)
                tv_caseta.setGravity(Gravity.RIGHT);
            else
                tv_caseta.setGravity(Gravity.LEFT);

            //tv_nombre.setText(globales.tll.getLecturaActual().getNombreCliente());
            //globales.tdlg.getInformacionDelMedidor(ll_generica, globales.tll.getLecturaActual(), sizeGenerico);
            preparaDatosGenericos();
            tv_contador.setText((globales.mostrarRowIdSecuencia ? lecturaActual.secuenciaReal : globales.il_lect_act) + " de " + globales.il_total);
            tv_contadorOpcional.setText(tv_contador.getText().toString());
            tv_min.setText(String.valueOf(globales.il_lect_min));
            tv_max.setText(String.valueOf(globales.il_lect_max));
            tv_lecturaAnterior.setText(String.valueOf(lecturaActual.lecturaAnterior));

            // Queremos que los comentarios sean de la siguiente manera Anomalia: ,
            // SubAnomalia \n(todo lo demas)
//		if (!globales.tll.getLecturaActual().getAnomaliaAMostrar().equals("")) {
//			// Tiene una anomalia
//			ls_comentarios =getString(R.string.str_anomalia)+": "  + globales.is_presion;
//			if (!globales.tll.getLecturaActual().getSubAnomaliaAMostrar().equals("")) {
//				// Tiene una subanomalia
//				ls_comentarios += ", " + getString(R.string.str_subanomalia)+": " 
//						+ globales.tll.getLecturaActual().getSubAnomaliaAMostrar();
//			}
//			ls_comentarios += "\n";
//
//		}
//		
//		tv_comentarios.setText(ls_comentarios
//				+ globales.tll.getLecturaActual().getComentarios());

            tv_comentarios.setVisibility(View.GONE);

            tv_lectura.setText(getString(R.string.lbl_tdl_indica_lectura) + globales.is_lectura);
            tv_anomalia.setText(getString(R.string.lbl_tdl_indica_anomalia) + (globales.is_presion.length() > 3 ? "***" : globales.is_presion));

            if (globales.mostrarCuadriculatdl) {
                cuadricula.setVisibility(View.VISIBLE);
                //llenar los campos
                tv_campo0.setText(globales.tdlg.obtenerContenidoDeEtiqueta("campo0"));
                tv_campo1.setText(globales.tdlg.obtenerContenidoDeEtiqueta("campo1"));
                tv_campo2.setText(globales.tdlg.obtenerContenidoDeEtiqueta("campo2"));
                tv_campo3.setText(globales.tdlg.obtenerContenidoDeEtiqueta("campo3"));
                tv_campo4.setText(globales.tdlg.obtenerContenidoDeEtiqueta("campo4"));

                label_campo0.setText(globales.tdlg.obtenerTituloDeEtiqueta("campo0"));
                label_campo1.setText(globales.tdlg.obtenerTituloDeEtiqueta("campo1"));
                label_campo2.setText(globales.tdlg.obtenerTituloDeEtiqueta("campo2"));
                label_campo3.setText(globales.tdlg.obtenerTituloDeEtiqueta("campo3"));
                label_campo4.setText(globales.tdlg.obtenerTituloDeEtiqueta("campo4"));
            } else {
                cuadricula.setVisibility(View.GONE);
            }

            // if (strEsDemanda.equals("5")) {nNumColorAviso = 8; strTextoEspecial =
            // " - " + tipoMedidor.trim(); }
            // if (strEsDemanda.equals("6") || strEsDemanda.equals("7"))
            // {nNumColorAviso = 9; strTextoEspecial = " - " + tipoMedidor.trim(); }
            //
//		if (globales.tll.getLecturaActual().is_tarifa.endsWith("5")) {
//			// ll_linearLayout1.setBackgroundColor(R.color.Blue);
//			ll_linearLayout1.setBackgroundResource(R.color.Blue);
//		} else if (globales.tll.getLecturaActual().is_tarifa.endsWith("6")
//				|| globales.tll.getLecturaActual().is_tarifa.endsWith("7")) {
//			ll_linearLayout1.setBackgroundResource(R.color.Red);
//		} else {
//			// ll_linearLayout1.setBackgroundResource(R.color.SteelBlue);
//			ll_linearLayout1.setBackgroundResource(R.color.green);
//		}

            /*
             * int secuencial=(int) globales.il_lect_act; button5.setEnabled(false);
             * Cursor c; //Por ahora no tenemos un objeto de donde tomar las fotos
             * asi que haremos esto...
             *
             * openDatabase();
             *
             *
             *
             * c=db.rawQuery(
             * "Select count(*) canti from fotos where cast(secuencial as Integer)="
             * + secuencial,null);
             *
             * c.moveToFirst();
             *
             * if (c.getInt(c.getColumnIndex("canti"))==0){
             * button5.setEnabled(false); } else{ button5.setEnabled(true); }
             *
             * closeDatabase();
             */

            tieneFotos();

            // button5.setEnabled(false);
            // tv_contador.bringToFront();

            if (globales.esSuperUsuario) {
                ll_limites.setVisibility(View.VISIBLE);
            }

            if (!globales.modoCaptura) {
                salirModoCaptura();
            }
            if (Build.VERSION.SDK_INT >= 11)
                invalidateOptionsMenu();

            // button6.setEnabled(true);

            try {
                timer.cancel();
            } catch (Throwable e) {

            }

            timer.purge();
            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mHandler.post(new Runnable() {
                        public void run() {
                            if (!modoLecturaObligatoria)
                                button6.setEnabled(true);
                        }
                    });

                }

            }, 500);

            // Mostrar la sección de acuses

            FormatoDeEtiquetas fde = globales.tdlg.getMensajedeRespuesta();

            if (fde != null) {
                tv_respuesta.setText(fde.texto);
                tv_respuesta.setBackgroundResource(fde.color);
                tv_respuesta.setVisibility(View.VISIBLE);
            }


            String advertencia = globales.tdlg.getMensajedeAdvertencia();
            if (advertencia.equals("")) {
                tv_advertencia.setVisibility(View.GONE);

            } else {
                tv_advertencia.setText(advertencia);
                tv_advertencia.setVisibility(View.VISIBLE);
            }

            this.me = globales.tdlg.getMensaje();
            if (globales.mensaje.equals("") && me != null)
                activaAvisoEspecial(me);
            else if (!globales.mensaje.equals("")) {
                muestraRespuestaSeleccionada(me);
            }
        } catch (Throwable t) {
            mostrarMensaje("Error", "Error inesperado. Pida ayuda a soporte", t.getMessage(), null);
        }
    }

    protected void verficarSiPuedoDejarAusente() {
        if (globales.bModificar) {
            if (globales.is_lectura.equals("") && (globales.is_presion.equals("") || globales.is_presion.endsWith("*"))) {
                setModoCaptura();
            }
        }
    }

    protected void iniciarModoCorreccionCAPS() {
        // TODO Auto-generated method stub
        //setModoModificacion(false);
        //layout.setBackgroundResource(R.drawable.correccion_pattern);
        setFondoCorreccion();
        super.iniciarModoCorreccionCAPS();
    }

    // RL, 2022-10-05, Engie requiere que ya no puedan modificarse las lecturas realizadas.
    protected void rutaFinalizada() {
        mensajeOK(getString(R.string.str_lbl_ruta_finalizada), getString(R.string.msj_tdl_fin_de_ruta),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        muere();
                    }
                });
    }

    private void setFondoCorreccion() {
        layout.setBackgroundResource(R.drawable.correccion_pattern);
    }

    public void salirModoCaptura() {
        globales.modoCaptura = false;
        button4.setEnabled(true);
        button4.setVisibility(View.VISIBLE);
        button6.setText(R.string.m_str_siguiente);
        button6.setEnabled(true);
        button3.setEnabled(true);
        button1.setEnabled(true);
        button2.setEnabled(true);
        tv_contador.bringToFront();
    }

    public void capturar() {

        // int respuesta= globales.tll.capturaLectura(globales.is_lectura,
        // globales.tll.getLecturaActual().getAnomalia());

        try {
            int respuesta = 1;

            globales.estoyCapturando = true;

            Lectura ll_lectura = globales.tll.getLecturaActual();

            if (respuesta > 0) {
                // Siguiente lectura
                try {
                    if (globales.is_lectura.equals("") && globales.requiereLectura) {
                        Toast.makeText(this,
                                R.string.lbl_tdl_requiere_lectura,
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    //Verificamos si la localizacion es nula...
                    if (deboTomarPuntoGPS()) {
                        //No podemos permitir que sea nula sin haber tenido el consentimiento del usuario, ok?...
                        //Nos tendremos que salir de esta rutina e iniciar la rutina de espere gps
                        esperarGPS();
                        return;
                    }

                    seguirConLaCapturaSinPunto = false;

                    // Temporalmente bloqueamos el boton de grabar/siguiente, ya que
                    // nos reportan que se guardan las lecturas consecutivamente
                    button6.setEnabled(false);

                    globales.requiereLectura = false;
//				if (globales.bModificar)
//					globales.tll.getLecturaActual().intentos++;

                    globales.il_ultimoSegReg = globales.il_lect_act;


//				if (captureAnomalias){
//					globales.tdlg.anomaliasARepetir();
//					globales.tdlg.subAnomaliasARepetir();
//					//captureAnomalias=false;
//				}


                    //globales.tdlg.cambiosAnomaliaAntesDeGuardar(globales.is_lectura);

                    globales.tdlg.anomaliasARepetir();
                    globales.tdlg.subAnomaliasARepetir();

                    globales.tll.getLecturaActual().ls_mensaje = globales.mensaje;
                    globales.tll.guardarLectura(globales.is_lectura);
                    //globales.tll.getLecturaActual().setPuntoGPS(globales.location);
                    globales.modoCaptura = false;
                    salirModoCaptura();

                    // Ahora hay que poner que ya no hay temporales
                    // borramos fotos temporales anteriores
                    openDatabase();

                    db.execSQL("update fotos set temporal=0 where temporal="
                            + CamaraActivity.TEMPORAL + " or temporal="
                            + CamaraActivity.ANOMALIA);

                    closeDatabase();

                    globales.idMedidorUltimaLectura = globales.is_caseta;

                    if (!globales.capsModoCorreccion
                            && !(globales.sonLecturasConsecutivas && globales.bModificar))
                        globales.bModificar = false;

                    globales.permiteDarVuelta = true;

                    permiteCerrar();

                    boolean sonLecturasConsecutivas = this.globales.sonLecturasConsecutivas;

                    if (sonLecturasConsecutivas)
                        asignaAnomaliaConsecutiva(globales.idMedidorUltimaLectura,
                                globales.tll.getLecturaActual().getUltimaAnomalia());

                    //Desactivamos el mensaje, la verdad no nos interesa de momento
                    preguntaSiBorraDatos = false;
                    preguntaSiBorraDatosComodin = false;
                    switch (globales.ii_orden) {
                        case ASC:
                            getSigLect();
                            break;
                        case DESC:
                            getAntLect();
                            break;
                    }
                    //Lo volvemos a mostrar si es necesario
                    //preguntaSiBorraDatos=true;

                    if ((globales.sonLecturasConsecutivas && !globales.idMedidorUltimaLectura
                            .equals(globales.is_caseta))
                            || (!globales.tll.hayMasLecturas() && sonLecturasConsecutivas)) {
                        tomaFotosConsecutivas(globales.idMedidorUltimaLectura);

                        // Salimos de correccion
                        if (globales.bModificar) {
                            globales.bcerrar = false;
                            globales.bModificar = false;
                            // tv_indica_corr.setText("N");
                            globales.capsModoCorreccion = false;
                            layout.setBackgroundResource(0);
                            // item.setIcon(R.drawable.ic_action_correccion);
                            getSigLect();
                        }
                    }

                    if (!ll_lectura.getLectura().equals(""))
                        mandarAImprimir(ll_lectura);

                    globales.moverPosicion = false;
                    //Este es el real
//				enviarAvance();

                } catch (Exception e1) {
                    Utils.showMessageLong(this, e1.getMessage());
                } catch (Throwable e) {

                    // En caso de que sea la ultima lectura
                    if (globales.sonLecturasConsecutivas && globales.bModificar) {

                        // Salimos de correccion
                        if (globales.bModificar) {
                            globales.bcerrar = false;
                            globales.bModificar = false;
                            // tv_indica_corr.setText("N");
                            globales.capsModoCorreccion = false;
                            layout.setBackgroundResource(0);
                            // item.setIcon(R.drawable.ic_action_correccion);
                            getSigLect();
                        }
                    } else {
                        // Ya no hay lecturas
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        bEsElFInal = true;
                        if (!ll_lectura.getLectura().equals(""))
                            mandarAImprimir(ll_lectura);
                        // this.finish();
                        muere();
                    }

                }

                globales.estoyCapturando = false;

            } else {
                // Mostramos porque
                switch (respuesta) {
                    case TodasLasLecturas.FUERA_DE_RANGO:
                        Toast.makeText(this, "Lectura fuera de rango, Verifique.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case TodasLasLecturas.ESFERAS_INCORRECTAS:
                        Toast.makeText(this, "No concuerda el número de esferas.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case TodasLasLecturas.INTENTOS_ACABADOS:
                        Toast.makeText(this, "Se han agotado el número de intentos.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case TodasLasLecturas.VACIA:
                        Toast.makeText(this, "La lectura no puede quedar vacia.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case TodasLasLecturas.INTENTANDO:
                        Toast.makeText(this, "Verifique la lectura.",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            globales.estoyCapturando = false;
            globales.location = null;

            if (globales.mostrarPausaActiva) {
                if (Calendar.getInstance().getTimeInMillis() - globales.fechaEnMilisegundos >= globales.tiempoPausaActiva) {
                    mensajeOK("Recuerde realizar sus ejercicios de pausa activa", "Toma de Lecturas");
                    globales.fechaEnMilisegundos = Calendar.getInstance().getTimeInMillis();
                }
            }
            if (globales.mostrarBuscarDespuesDeCapturar && !globales.capsModoCorreccion && !globales.idMedidorUltimaLectura.equals(globales.is_caseta)) {
                buscarMedidor(BuscarMedidor.MOVER);
            }
        } catch (Throwable t) {
            Utils.showMessageLong(this, t.getMessage());
        }
    }


//	private int validaLectura(String ls_lectAct) {
//		long ll_lectAct = Long.parseLong(ls_lectAct);
//
//		if (is_lectAnt.equals("")) {
//			if (globales.il_lect_max < ll_lectAct
//					|| globales.il_lect_min > ll_lectAct) {
//				is_lectAnt = ls_lectAct;
//				return FUERA_DE_RANGO;
//			}
//		} else {
//
//			if (!is_lectAnt.equals(ls_lectAct)) {
//				is_lectAnt = ls_lectAct;
//				return VERIFIQUE;
//			}
//
//		}
//
//		is_lectAnt = "";
//		return CORRECTA;
//	}

//	public void mensajeComentarios(View views) {
//		AlertDialog alert = null;
//		LayoutInflater inflater = this.getLayoutInflater();
//
//		final View view = inflater.inflate(R.layout.comentarios, null);
//		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//		final EditText et_comentario = (EditText) view
//				.findViewById(R.id.et_comentarios);
//		final EditText et_problemas = (EditText) view
//				.findViewById(R.id.et_problemas);
//
//		et_comentario.setText(is_comentarios != null ? is_comentarios : "");
//		et_problemas.setText(is_problemas != null ? is_problemas : "");
//
//		final TomaDeLecturas slda = this;
//		builder.setView(view)
//				.setCancelable(false)
//				.setNegativeButton(R.string.cancelar,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								dialog.cancel();
//								esconderTeclado(et_comentario);
//							}
//
//						})
//				.setPositiveButton(R.string.continuar,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//
//								is_comentarios = et_comentario.getText()
//										.toString();
//								is_problemas = et_problemas.getText()
//										.toString();
//
//								openDatabase();
//								ContentValues cv_datos = new ContentValues(2);
//								String whereClause = "secuencial=?";
//								String[] whereArgs = { String
//										.valueOf(globales.il_lect_act) };
//
//								cv_datos.put("comentarios",
//										is_comentarios.trim());
//								cv_datos.put("problemas", is_problemas.trim());
//
//								db.update("lecturas", cv_datos, whereClause,
//										whereArgs);
//								closeDatabase();
//								dialog.dismiss();
//								esconderTeclado(et_comentario);
//							}
//
//						});
//
//		alert = builder.create();
//		mostrarTeclado(et_comentario);
//		alert.show();
//	}

//	public void mensajeInput(final int tipo) {
//		AlertDialog alert = null;
//		LayoutInflater inflater = this.getLayoutInflater();
//
//		final View view = inflater.inflate(R.layout.layoutgenerico, null);
//		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//		final EditText et_comentario = (EditText) view
//				.findViewById(R.id.et_generico);
//		final TextView tv_label = (TextView) view.findViewById(R.id.tv_label);
//
//		switch (tipo) {
//		case LECTURA:
//			tv_label.setText(R.string.str_lectura);
//			if (is_lectAnt.equals(""))
//				et_comentario.setText(globales.is_lectura.trim());
//			et_comentario.setInputType(InputType.TYPE_CLASS_NUMBER);
//			break;
//		case PRESION:
//			tv_label.setText(R.string.str_presion);
//			et_comentario.setText(globales.is_presion.trim());
//			et_comentario.setInputType(InputType.TYPE_CLASS_TEXT);
//			break;
//		default:
//			return;
//		}
//
//		final TomaDeLecturas slda = this;
//		builder.setView(view)
//				.setCancelable(false)
//				.setNegativeButton(R.string.cancelar,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								esconderTeclado(et_comentario);
//								dialog.cancel();
//							}
//
//						})
//				.setPositiveButton(R.string.continuar,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								String ls_etiqueta;
//
//								String ls_comentario = et_comentario.getText()
//										.toString();
//								switch (tipo) {
//
//								case LECTURA:
//									if (ls_comentario.equals("")) {
//										return;
//									}
//									if (validaLectura(ls_comentario) != CORRECTA) {
//										mensajeErrorInput(tipo);
//										return;
//									}
//									globales.is_lectura = ls_comentario;
//
//									break;
//								case PRESION:
//									globales.is_presion = ls_comentario;
//									break;
//								default:
//									return;
//								}
//								ls_etiqueta = "L: " + globales.is_lectura;
//								ls_etiqueta += "\t P:" + globales.is_presion;
//								// tv_lectura.setText(ls_etiqueta);
//
//								openDatabase();
//								ContentValues cv_datos = new ContentValues(3);
//								String whereClause = "secuencial=?";
//								String[] whereArgs = { String
//										.valueOf(globales.il_lect_act) };
//
//								cv_datos.put(tipo == LECTURA ? "lectact"
//										: "presion", ls_comentario.trim());
//
//								if (tipo == LECTURA) {
//									cv_datos.put("horadelectura",
//											Main.obtieneFecha());
//								}
//								cv_datos.put("envio", NO_ENVIADA);
//
//								if (tipo == LECTURA)
//									tomarFoto(CamaraActivity.TEMPORAL);
//
//								db.update("lecturas", cv_datos, whereClause,
//										whereArgs);
//								closeDatabase();
//								esconderTeclado(et_comentario);
//								dialog.dismiss();
//							}
//
//						});
//
//		alert = builder.create();
//		mostrarTeclado(et_comentario);
//		alert.show();
//
//	}

//	public void mensajeErrorInput(final int tipo) {
//		final TomaDeLecturas slda = this;
//		String ls_mensaje = "";
//		switch (tipo) {
//		case LECTURA:
//			ls_mensaje = "Verifique lectura";
//			break;
//		}
//		AlertDialog.Builder message = new AlertDialog.Builder(slda);
//		message.setMessage(ls_mensaje)
//				.setCancelable(false)
//				.setPositiveButton(R.string.aceptar,
//						new DialogInterface.OnClickListener() {
//
//							public void onClick(DialogInterface dialog,
//									int which) {
//								mensajeInput(tipo);
//
//							}
//
//						});
//		AlertDialog alerta = message.create();
//		alerta.show();
//	}

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toma_de_lecturas, menu);

        manejaEstadosDelMenu(menu);

        return true;
    }

    @SuppressLint("NewApi")
    public boolean onOptionsItemSelected(final MenuItem item) {
        Intent lrs;
        switch (item.getItemId()) {
            /*
             * case R.id.m_anterior: globales.bcerrar=false; getAntLect(); break;
             */
            case R.id.m_correccion:
                if (preguntaSiBorraDatos /*&& !globales.bModificar*/) {
                    if (!globales.tll.getLecturaActual().getAnomaliasCapturadas().equals("") &&
                            !globales.modoCaptura && !globales.tdlg.esSegundaVisita(globales.tll.getLecturaActual().getAnomaliasCapturadas(), globales.tll.getLecturaActual().is_subAnomalia)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        AlertDialog alert;

                        //Si ingresé datos, estoy en modo captura y  me estoy moviendo deberia preguntar
                        builder.setMessage(R.string.str_pregunta_guardar_cambios)
                                .setCancelable(false).setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //recepcion();
                                        preguntaSiBorraDatos = false;
                                        // getSigLect();
                                        onOptionsItemSelected(item);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });

                        alert = builder.create();
                        alert.show();
                        return true;
                    }

                }
                TextView tv_indica_corr = (TextView) findViewById(R.id.tv_indica_corr);
                if (globales.bModificar) {
                    globales.bcerrar = false;
                    globales.bModificar = false;
                    tv_indica_corr.setText("N");
                    globales.capsModoCorreccion = false;
                    layout.setBackgroundResource(0);
                    // item.setIcon(R.drawable.ic_action_correccion);
                    globales.modoCaptura = false;
                    salirModoCaptura();
                    getSigLect();
                } else {
                    // String ls_filtrado= formaCadenaFiltrado();
                    globales.bModificar = true;
                    /*
                     * String[]
                     * ls_selectionArgs={String.valueOf(globales.il_lect_act)};
                     * openDatabase();
                     *
                     * String ls_comentarios="";
                     *
                     * if (filtrarComentarios) ls_comentarios=
                     * " and (lectact<>'' or (comentarios<>'' and comentarios is not null)) "
                     * ; else ls_comentarios=" and lectact<>'' ";
                     *
                     *
                     * Cursor c= db.query("lecturas", null,
                     * "cast(secuencial as integer)< cast (? as integer) " +
                     * ls_comentarios + ls_filtrado, ls_selectionArgs, null, null,
                     * "cast (secuencial as Integer) desc", "1"); c.moveToFirst();
                     */
                    setModoModificacion();
                    globales.bcerrar = false;
                    /*
                     * if (globales.bModificar)
                     * item.setIcon(R.drawable.ic_action_salir_correccion); else
                     * item.setIcon(R.drawable.ic_action_correccion);
                     */
                    setDatos();
                    // closeDatabase();

                }
                break;
            case R.id.m_orden:
                switch (globales.ii_orden) {
                    case ASC:
                        // item.setIcon(R.drawable.ic_action_ascendente);
                        globales.ii_orden = DESC;
                        break;
                    case DESC:
                        // item.setIcon(R.drawable.ic_action_descendente);
                        globales.ii_orden = ASC;
                        break;
                }
                break;
            /*
             * case R.id.m_siguiente: globales.bcerrar=false; getSigLect(); break;
             */

            case R.id.m_noRegistrados:
                if (globales.preguntaSiTieneMedidor) {
                    preguntaSiNo(globales.tdlg.mj_tiene_medidor);
                } else {
                    mostrarVentanaDeNoRegistrados();
                }
                break;
            case R.id.m_cambio_medidor:
                mostrarVentanaDeCambioMedidor();
                break;
            case R.id.m_cambiarFuente:
                final TomaDeLecturas main = this;
                AlertDialog.Builder builder;

                // String ls_opciones[]={"Ninguno", "Info. del Cliente", "Detalle"};
                String ls_opciones[] = {getString(R.string.lbl_informacion), getString(R.string.lbl_area_de_captura)};

                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.msj_tdl_que_ajustar)
                        .setItems(ls_opciones,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        boolean ejecutar = true;
                                        switch (which) {
                                            // case NINGUNO: //Ninguno
                                            // modoCambiofuente= NINGUNO;
                                            // break;
                                            case INFO_CLIENTE: // Info del Cliente
                                                modoCambiofuente = INFO_CLIENTE;
                                                break;
                                            case DETALLE: // detalle
                                                modoCambiofuente = DETALLE;

                                                break;
                                        }

                                        empezarACambiarFuente();

                                    }

                                })
                        .setNegativeButton(R.string.cancelar,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        modoCambiofuente = NINGUNO;
                                    }
                                });
                builder.show();
            case R.id.m_font_areaCaptura:
                modoCambiofuente = DETALLE;
                empezarACambiarFuente();
                break;
            case R.id.m_font_informacion:
                modoCambiofuente = INFO_CLIENTE;
                empezarACambiarFuente();
                break;
            case R.id.m_Impresion:
                bHabilitarImpresion = !bHabilitarImpresion;

                break;
            case R.id.m_SolicitarAyuda:
                solicitarEmergencia();
                break;
            case R.id.m_EntrarSupervisor:
                entrarSupervisor();
                break;
            case R.id.m_UsarEscaner:
                usarEscaner();
                break;
            case R.id.m_MostrarUbicacionGPS:
                mostrarUbicacionGPS();
                break;
        }

        if (Build.VERSION.SDK_INT >= 11)
            invalidateOptionsMenu();
        return true;
    }

    public void setModoModificacion() {
        setModoModificacion(true);
    }

    public void setModoModificacion(boolean prepararModificacion) {
        TextView tv_indica_corr = (TextView) findViewById(R.id.tv_indica_corr);
        try {
            if (prepararModificacion)
                globales.tll.prepararModificar();
            /*
             * if (globales.tll.hayMasLecturas()) throw new Throwable();
             */

            if (!globales.tll.hayMasLecturas())
                throw new Throwable();
            setFondoCorreccion();
            tv_indica_corr.setText("C");
        } catch (Throwable e) {
            globales.bModificar = false;
        }

    }

    public void esconderTeclado(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void esSospechosa() {
        openDatabase();
        ContentValues cv_datos = new ContentValues(1);
        String whereClause = "secuenciaReal=?";
        String[] whereArgs = {String.valueOf(globales.il_lect_act)};

        cv_datos.put("sospechosa", SOSPECHOSA);

        int i = db.update("lecturas", cv_datos, whereClause, whereArgs);
        closeDatabase();
    }

    public void verFotos(View view) {
        // globales.il_lect_act
        cancelaTimer();
        regreseDe = FOTOS;
        Intent intent = new Intent(this, VerFotos.class);
        intent.putExtra("lect_act", globales.il_lect_act);
        startActivity(intent);
    }

    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        if (!globales.modoCaptura /* || globales.is_caseta.contains("CF") */) {
            permiteCerrar();
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // globales.modoCaptura=false;
                    globales.moverPosicion = false;
                    getSigLect();
                    // Left to right swipe
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // globales.modoCaptura=false;
                    globales.moverPosicion = false;
                    getAntLect();
                }
            } catch (Exception e) {
                // nothing
            }
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return gestureScanner.onTouchEvent(ev);
    }

    public void mostrarTeclado(View v) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(v, 0);
    }

    public String formaCadenaFiltrado() {
        String ls_cadena = "";
        TextView tv_indica_filtro = (TextView) findViewById(R.id.tv_indica_filtro);
        openDatabase();
        String ls_tmp;
        Cursor c;

        c = db.query("config", null, "key='ciudad'", null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            ls_tmp = c.getString(c.getColumnIndex("value"));
            if (ls_tmp.trim().length() > 0) {
                ls_cadena += " and upper(municipio) like '%"
                        + ls_tmp.toUpperCase() + "%' ";
            }

        }

        c.close();

        c = db.query("config", null, "key='medidor'", null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            ls_tmp = c.getString(c.getColumnIndex("value"));
            if (ls_tmp.trim().length() > 0) {
                ls_cadena += " and upper(caseta) like '%"
                        + ls_tmp.toUpperCase() + "%' ";
            }

        }

        c.close();

        c = db.query("config", null, "key='cliente'", null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            ls_tmp = c.getString(c.getColumnIndex("value"));
            if (ls_tmp.trim().length() > 0) {
                ls_cadena += " and upper(nombre) like '%"
                        + ls_tmp.toUpperCase() + "%' ";
            }

        }

        c.close();

        c = db.query("config", null, "key='direccion'", null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            ls_tmp = c.getString(c.getColumnIndex("value"));
            if (ls_tmp.trim().length() > 0) {
                ls_cadena += " and upper(direccion) like '%"
                        + ls_tmp.toUpperCase() + "%' ";
            }

        }

        c.close();

        if (ls_cadena.length() == 0)
            tv_indica_filtro.setVisibility(View.GONE);

        c = db.query("config", null, "key='brincarc'", null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            if (c.getInt(c.getColumnIndex("value")) == 1
                    && !globales.bModificar)
                ls_cadena += " and (comentarios='' or comentarios is null) ";
            filtrarComentarios = true;
        }

        c.close();

        closeDatabase();

        return ls_cadena;

    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        // En este metodo se cambian las opciones del menu
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toma_de_lecturas, menu);

        /*
         * MenuItem mi_correcion=menu.findItem(R.id.m_correccion); MenuItem
         * mi_orden= menu.findItem(R.id.m_orden);
         * mi_correcion.setTitle(globales.
         * bModificar?R.string.m_str_salirCorreccion:R.string.m_str_correccion);
         *
         * if (globales.modoCaptura) mi_correcion.setVisible(false); else
         * mi_correcion.setVisible(true) ;
         *
         * if (globales.bModificar){
         * mi_correcion.setIcon(R.drawable.ic_action_salir_correccion); } else{
         * mi_correcion.setIcon(R.drawable.ic_action_correccion); }
         *
         *
         * if(ii_orden==DESC) mi_orden.setIcon(R.drawable.ic_action_ascendente);
         * else mi_orden.setIcon(R.drawable.ic_action_descendente);
         *
         * mi_orden.setTitle(ii_orden==ASC?R.string.m_str_desendente:R.string.
         * m_str_ascendente);
         */

        manejaEstadosDelMenu(menu);

        return super.onPrepareOptionsMenu(menu);
    }

    public void manejaEstadosDelMenu(Menu menu) {
        MenuItem mi_correcion = menu.findItem(R.id.m_correccion);
        MenuItem mi_noRegistrados = menu.findItem(R.id.m_noRegistrados);
        MenuItem mi_orden = menu.findItem(R.id.m_orden);
        MenuItem mi_impresion = menu.findItem(R.id.m_Impresion);
        MenuItem mi_gps = menu.findItem(R.id.m_gps);
        MenuItem mi_info = menu.findItem(R.id.m_font_informacion);
        MenuItem mi_areaCaptura = menu.findItem(R.id.m_font_areaCaptura);
        MenuItem mi_cambioMedidor = menu.findItem(R.id.m_cambio_medidor);

        // mi_cambioMedidor.setVisible(globales.mostrarCambioMedidor);

        mi_correcion
                .setTitle(globales.bModificar ? R.string.m_str_salirCorreccion
                        : R.string.m_str_correccion);
        mi_impresion
                .setTitle(bHabilitarImpresion ? R.string.m_str_desHabilita_imp
                        : R.string.m_str_habilita_Impresion);

        mi_noRegistrados.setIcon(globales.iconoNoRegistrados);

        if (globales.modoCaptura)
            mi_correcion.setVisible(false);
        else
            mi_correcion.setVisible(true);

        if (!globales.capsModoCorreccion) {
            if (globales.bModificar) {
                mi_correcion.setIcon(R.drawable.ic_action_salir_correccion);
                mi_noRegistrados.setVisible(false);
                mi_orden.setVisible(false);


            } else {
                mi_correcion.setIcon(R.drawable.ic_action_correccion);
                if (globales.mostrarNoRegistrados) {
                    mi_noRegistrados.setVisible(true);
                } else {
                    mi_noRegistrados.setVisible(false);
                }

                mi_orden.setVisible(true);
            }
        } else {
            mi_correcion.setVisible(false);

            mi_correcion.setIcon(R.drawable.ic_action_correccion);
            if (globales.mostrarNoRegistrados) {
                mi_noRegistrados.setVisible(true);
            } else {
                mi_noRegistrados.setVisible(false);
            }
        }


        if (globales.mostrarImpresion) {
            if (bHabilitarImpresion) {
                mi_impresion.setIcon(R.drawable.ic_deshabilita_impr);

            } else {
                mi_impresion.setIcon(R.drawable.ic_habilita_impresion);
            }
        } else {
            mi_impresion.setVisible(false);
        }

        if (globales.esSuperUsuario) {
            mi_info.setVisible(true);
            mi_areaCaptura.setVisible(true);
        }


        if (globales.ii_orden == DESC)
            mi_orden.setIcon(R.drawable.ic_action_ascendente);
        else
            mi_orden.setIcon(R.drawable.ic_action_descendente);

        mi_orden.setTitle(globales.ii_orden == ASC ? R.string.m_str_descendente
                : R.string.m_str_ascendente);

        if (globales.gpsEncendido) {
            mi_gps.setVisible(true);
        } else {
            mi_gps.setVisible(false);
        }
    }

    protected void procesarBotonLectura(Context context)
    {
        Lectura lect;
        boolean esAcuseRecibo;

        cancelaTimer();

        if (button1.isEnabled()) {
            cancelaTimer();

            if (globales.bloquearBorrarSiIntento && !globales.tll.getLecturaActual().intento6.equals("")) {
                mensajeOK("Se ha superado la cantidad de veces que se puede ingresar una lectura.", "Toma de Lecturas");
                return;
            }

            Intent intent = new Intent(context, Input.class);
            intent.putExtra("tipo", Input.LECTURA);
            intent.putExtra("min", globales.il_lect_min);
            intent.putExtra("max", globales.il_lect_max);
            intent.putExtra("act", globales.is_lectura);
            intent.putExtra("validar", /*globales.validar*/true); //Siempre va a validar
            intent.putExtra("modo", modo);
            intent.putExtra("secuencia", globales.il_lect_act);

            lect = globales.tll.getLecturaActual();

            secuencialAntesDeInput = lect.secuencia;

            esAcuseRecibo = lect.getEsAcuseRecibo();

//            if (lect.getEsFaunaNociva())
//                procesarFaunaNociva(intent, esAcuseRecibo);
//            else
            procesarBotonLectura2(intent, esAcuseRecibo);
        }
    }

    private void procesarFaunaNociva(Intent intent, boolean esAcuseRecibo){
        mostrarAlerta("Fauna nociva", "Tome sus precauciones. Aviso de fauna nociva", "",
                new DialogoMensaje.Resultado() {
                    @Override
                    public void Aceptar(boolean EsOk) {
                        procesarBotonLectura2(intent, esAcuseRecibo);
                    }
                });
    }

    private void procesarBotonLectura2(Intent intent, boolean esAcuseRecibo) {
        if (!esAcuseRecibo)
            startActivityForResult(intent, LECTURA);
        else
        {
            mostrarMensaje("Acuse de recibo", "El cliente tiene un acuse de recibo. Realice las acciones necesarias y tome foto", "",
                    new DialogoMensaje.Resultado() {
                        @Override
                        public void Aceptar(boolean EsOk) {
                            startActivityForResult(intent, LECTURA);
                        }
                    });
        }
    }

    public void presentacionAnomalias(boolean anomaliaCapturada, String anomalia, String subAnomalia) {
        // if (anomalia.ii_lectura==1 || anomalia.ii_ausente==0 ){
        // requiereLectura=true;
        // button1.setEnabled(true);
        // }
        // else{
        // requiereLectura=false;
        // button1.setEnabled(false);
        // //if (globales.tll.getLecturaActual().anomalia.ii_lectura==0 ||
        // globales.tll.getLecturaActual().anomalia.ii_ausente==4 ){
        // //Si es ausente, tiene que borrar la lectura...
        // globales.is_lectura="";
        // tv_lectura.setText(getString(R.string.lbl_tdl_indica_lectura) +globales.is_lectura);
        // //}
        // }


        int requiereLectura = globales.tll.getLecturaActual().requiereLectura();
        if (requiereLectura == Anomalia.LECTURA_AUSENTE) {
            globales.requiereLectura = false;
            if (permiteCambiarModoCaptura) {
                button1.setEnabled(false);
                globales.is_lectura = "";
                tv_lectura.setText(getString(R.string.lbl_tdl_indica_lectura) + globales.is_lectura);
                setModoCaptura();

            } else {
                permiteCambiarModoCaptura = true;
            }

            setModoCaptura();

        } else if (requiereLectura != Anomalia.SIN_ANOMALIA) {
            //globales.requiereLectura = true;
            button1.setEnabled(true);
            //Requiere lectura... verificamos que la lectura no este vacia
            if (globales.is_lectura.equals("")) {
                salirModoCaptura();
                if (globales.bloquearBotonesLecturaObligatoria && requiereLectura != Anomalia.LECTURA_OBLIGATORIA) {
                    modoLecturaObligatoria = true;
                    modoLecturaObligatoria();
                }

            } else if (!globales.is_lectura.equals("") && !globales.modoCaptura) {
                modoLecturaObligatoria = false;
                setModoCaptura();
            }

//			if (requiereLectura!=Anomalia.LECTURA_OPCIONAL){
//				if(permiteCambiarModoCaptura){
//					modoLecturaObligatoria=false;
//					globales.requiereLectura=false;
//					setModoCaptura();
//				}
//					else{
//						permiteCambiarModoCaptura=true;
//					}
//				
//			}
        } else if (requiereLectura == Anomalia.SIN_ANOMALIA) {
            modoLecturaObligatoria = false;
        }


        //

        // Manejamos si la anomalia que presento es ausente.. hay que recordar
        // que no lleva lectura
        // if (globales.tll.getLecturaActual().anomalia.ii_lectura==0 ||
        // globales.tll.getLecturaActual().anomalia.ii_ausente==4 ){
        // //Si es ausente, tiene que borrar la lectura...
        // globales.is_lectura="";
        // tv_lectura.setText(getString(R.string.lbl_tdl_indica_lectura) +globales.is_lectura);
        // }
        // Manejamos la foto
        if (globales.tll.getLecturaActual().requiereFotoAnomalia() != 0 && anomaliaCapturada) {
            globales.calidadOverride = globales.tdlg.cambiaCalidadSegunTabla(anomalia, subAnomalia);
            tomarFoto(CamaraActivity.ANOMALIA, globales.tll.getLecturaActual().requiereFotoAnomalia(), anomalia);
        } else {
            avanzarDespuesDeAnomalia();
        }
    }

    public void esconderTeclado() {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(tv_lectura.getWindowToken(), 0);
    }

    private void tieneFotos() {
        int secuencial = (int) globales.il_lect_act;
        button5.setEnabled(false);
        Cursor c; // Por ahora no tenemos un objeto de donde tomar las fotos asi
        // que haremos esto...

        openDatabase();

        c = db.rawQuery(
                "Select count(*) canti from fotos where cast(secuencial as Integer)="
                        + secuencial, null);

        c.moveToFirst();

        if (c.getInt(c.getColumnIndex("canti")) == 0) {
            button5.setEnabled(false);
        } else {
            button5.setEnabled(true);
        }

        closeDatabase();

        // button5.setEnabled(false);

        tv_contador.bringToFront();
    }

    @Override
    public void onBackPressed() {


        if (preguntaSiBorraDatos /*&& !globales.bModificar*/) {
            if (!globales.tll.getLecturaActual().getAnomaliasCapturadas().equals("") &&
                    !globales.modoCaptura && !globales.tdlg.esSegundaVisita(globales.tll.getLecturaActual().getAnomaliasCapturadas(), globales.tll.getLecturaActual().is_subAnomalia)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog alert;

                //Si ingresé datos, estoy en modo captura y  me estoy moviendo deberia preguntar
                builder.setMessage(R.string.str_pregunta_guardar_cambios)
                        .setCancelable(false).setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //recepcion();
                                preguntaSiBorraDatos = false;
                                // getSigLect();
                                muere();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

                alert = builder.create();
                alert.show();
                return;
            }

        }

        if (!globales.modoCaptura) {
            muere();
            // finish();
        }


    }

    public void muere() {
        Intent resultado = new Intent();
        resultado.putExtra("bHabilitarImpresion", this.bHabilitarImpresion);
        setResult(Activity.RESULT_OK, resultado);
        //locationManager.removeUpdates(locationListener);
        finish();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (!permiteCambiarFuente) {
                    return super.dispatchKeyEvent(event);
                }
                reiniciaTimer();
                if (action == KeyEvent.ACTION_UP) {

                    switch (modoCambiofuente) {
                        case DETALLE:
                            porcentaje += factorPorcentaje;
                            break;
                        case INFO_CLIENTE:
                            porcentajeInfoCliente += factorPorcentaje;
                            break;
                    }

                    // porcentaje= getFloatValue("porcentaje", porcentaje);

                    setSizes();
                    openDatabase();
                    guardaValor("porcentaje_lectura", porcentaje);
                    guardaValor("porcentaje_info", porcentajeInfoCliente);

                    closeDatabase();
                }

                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (!permiteCambiarFuente) {
                    return super.dispatchKeyEvent(event);
                }

                reiniciaTimer();
                if (action == KeyEvent.ACTION_DOWN) {
                    // TODO

                    switch (modoCambiofuente) {
                        case DETALLE:
                            if ((porcentaje - factorPorcentaje) >= .05f) {
                                porcentaje -= factorPorcentaje;
                                // porcentaje= getFloatValue("porcentaje", porcentaje);
                            }
                            break;
                        case INFO_CLIENTE:
                            if ((porcentajeInfoCliente - factorPorcentaje) >= .05f) {
                                porcentajeInfoCliente -= factorPorcentaje;
                                // porcentaje= getFloatValue("porcentaje", porcentaje);
                            }

                            break;
                    }

                    setSizes();

                    openDatabase();
                    guardaValor("porcentaje_lectura", porcentaje);
                    guardaValor("porcentaje_info", porcentajeInfoCliente);
                    closeDatabase();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public void setSizes() {
        try {
            tv_caseta.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentaje * casetaSize));
            tv_lectura.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentaje * lecturaSize));
            tv_mensaje.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentaje * lecturaSize));
            tv_anomalia.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentaje * anomSize));
            tv_min.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * minSize));
            tv_max.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * maxSize));
            tv_respuesta.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentaje * respuestasSize));

            this.tv_lecturaAnterior.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * maxSize));

            tv_comentarios.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * comentariosSize));

            tv_advertencia.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * comentariosSize));

            tv_campo0.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * comentariosSize));
            tv_campo1.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * comentariosSize));
            tv_campo2.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * comentariosSize));
            tv_campo3.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * comentariosSize));
            tv_campo4.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * comentariosSize));

            label_campo0.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * labelCuadriculaSize));

            label_campo1.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * labelCuadriculaSize));

            label_campo2.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * labelCuadriculaSize));

            label_campo3.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * labelCuadriculaSize));

            label_campo4.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * labelCuadriculaSize));


            sizeGenerico = (float) (porcentajeInfoCliente * comentariosSize);

            tv_contadorOpcional.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    (float) (porcentajeInfoCliente * contadorOpcionalSize));


            //Esto se genera genericamente, asi que hay que rehacerlo
            //globales.tdlg.getInformacionDelMedidor(ll_generica, globales.tll.getLecturaActual(), sizeGenerico);
            preparaDatosGenericos();
        } catch (Throwable t) {
            mostrarMensaje("Error", "Error inesperado. Pida ayuda a soporte técnico", t.getMessage(), null);
        }
    }

    public void setPorcentaje() {
        // openDatabase();
        // db.execSQL("delete from config where key='porcentaje' ");
        // closeDatabase();

        porcentaje = getDoubleValue("porcentaje_lectura", porcentaje);
        porcentajeInfoCliente = getDoubleValue("porcentaje_info",
                porcentajeInfoCliente);

        // porcentaje=1.0f;
        anomSize = tv_anomalia.getTextSize();
        lecturaSize = tv_lectura.getTextSize();
        casetaSize = tv_caseta.getTextSize();

        minSize = tv_min.getTextSize();
        maxSize = tv_max.getTextSize();
        respuestasSize = tv_respuesta.getTextSize();
        comentariosSize = tv_comentarios.getTextSize();
        labelCuadriculaSize = label_campo0.getTextSize();
        contadorOpcionalSize = tv_contadorOpcional.getTextSize();
        //tipoMedidorSize = tv_tipoMedidor.getTextSize();

        setSizes();
    }

    public int getIntValue(String key, int value) {
        openDatabase();

        Cursor c = db.rawQuery("Select * from config where key='" + key + "'",
                null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            value = c.getInt(c.getColumnIndex("value"));
        } else {
            db.execSQL("Insert into config (key, value) values ('" + key
                    + "', " + value + ")");
        }
        c.close();

        closeDatabase();

        return value;
    }

    public double getDoubleValue(String key, double value) {
        openDatabase();

        Cursor c = db.rawQuery("Select * from config where key='" + key + "'",
                null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            value = c.getDouble(c.getColumnIndex("value"));
        } else {
            db.execSQL("Insert into config (key, value) values ('" + key
                    + "', " + value + ")");
        }
        c.close();

        closeDatabase();

        return value;
    }

    public void guardaValor(String key, double value) {
        // openDatabase();
        db.execSQL("Update config  set value=" + value + " where  key='" + key
                + "'");

        // closeDatabase();
    }

    void empezarACambiarFuente() {

        Toast.makeText(
                this,
                R.string.msj_tdl_config_fuente,
                Toast.LENGTH_SHORT).show();
        reiniciaTimer();

    }

    public void reiniciaTimer() {
        final Context contx = this;
        permiteCambiarFuente = true;
        try {
            cambiarFuenteTimer.cancel();
        } catch (Throwable e) {

        }

        cambiarFuenteTimer.purge();
        cambiarFuenteTimer = new Timer();
        cambiarFuenteTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mHandler.post(new Runnable() {
                    public void run() {
                        permiteCambiarFuente = false;
                        modoCambiofuente = NINGUNO;
                        Toast.makeText(contx,
                                R.string.msj_tdl_fin_config_fuente,
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }, segundoCambiarFuente * 1000);
    }

    public void cancelaTimer() {
        permiteCambiarFuente = false;
        try {
            cambiarFuenteTimer.cancel();
        } catch (Throwable e) {

        }

        cambiarFuenteTimer.purge();
    }

    public void inicializarVariables() {
        globales.il_ultimoSegReg = 0; //Ultimo medidor guardado
        globales.idMedidorUltimaLectura = "";
        globales.bModificar = false;
        globales.bcerrar = true;
        globales.moverPosicion = false;
        globales.bEstabaModificando = false;
        globales.capsModoCorreccion = false;
        globales.permiteDarVuelta = false;
        globales.sonLecturasConsecutivas = false;
        globales.estoyTomandoFotosConsecutivas = false;
        globales.il_lect_act = 0;
        globales.ii_orden = TomaDeLecturasPadre.ASC;
        globales.lecturasConFotosPendientes = null;
        globales.ii_foto_cons_act = 0;
        globales.estoyCapturando = false;
        //inputMandaCierre=false;
        globales.requiereLectura = false;

        globales.modoCaptura = false;

        globales.fotoForzada = false; // Siempre tomará foto despues de una lectura
        globales.validar = true; // No se validará la lectura
        globales.location = null;//Variable donde se indica todo del gps
    }

    @Override
    protected void capturaDespuesDelPuntoGPS() {
        // TODO Auto-generated method stub
        //Una vez que se salga verificamos que paso...
        //if (globales.location!=null){

        capturar();
        //	}

    }

    private void preparaDatosGenericos() throws Exception {
        Lectura lect;

        ll_generica.removeAllViews();

        lect = globales.tll.getLecturaActual();

        if (lect != null) {
            Vector<String> datos = globales.tdlg.getInformacionDelMedidor(lect);

            for (String dato : datos) {
                agregarCampo(dato);
            }
        }
    }

    private void agregarCampo(String texto) {
        LayoutParams layout_params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        TextView tv_view = new TextView(this);
        tv_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeGenerico);
        tv_view.setText(texto);
        ll_generica.addView(tv_view, layout_params);
    }

    public void activaAvisoEspecial(final MensajeEspecial me) {
        if (me != null) {
            tv_mensaje.setText(me.descripcion);
            tv_mensaje.setVisibility(View.VISIBLE);

            ll_linearLayout2.setVisibility(View.GONE);

            button1.setEnabled(false);
            button2.setEnabled(false);

            b_repetir_anom.setVisibility(View.GONE);

            switch (me.tipo) {
                case MensajeEspecial.MENSAJE_SI_NO:
                    tv_mensaje.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            preguntaSiNo(me);
                        }

                    });
                    break;

                case MensajeEspecial.OPCION_MULTIPLE:
                    tv_mensaje.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            preguntaOpcionMultiple(me);
                        }

                    });

                    break;

                case MensajeEspecial.MOSTRAR_INPUT_CAMPOS_GENERICO:
                    tv_mensaje.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            preguntaInput(me);
                        }

                    });

            }

        }
    }

    public void guardarRespuestaDialog(int respuesta) {

        //Guardamos
        globales.mensaje = me.regresaValor(respuesta);
        //Escondemos mensaje
        tv_mensaje.setVisibility(View.GONE);
        //Restablecemos botones
        button1.setEnabled(true);
        button2.setEnabled(true);
        ll_linearLayout2.setVisibility(View.VISIBLE);
        muestraRespuestaSeleccionada(me);
    }

    public void muestraRespuestaSeleccionada(final MensajeEspecial me) {
        tv_respuesta.setText(me.regresaDescripcion(globales.mensaje));
        tv_respuesta.setVisibility(View.VISIBLE);

        //Hay que dotarlo de opciones del anterior

        switch (me.tipo) {
            case MensajeEspecial.MENSAJE_SI_NO:
                tv_respuesta.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        preguntaSiNo(me);
                    }

                });
                break;

            case MensajeEspecial.OPCION_MULTIPLE:
                tv_respuesta.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        preguntaOpcionMultiple(me);
                    }

                });

                break;

        }
    }

    public void muestraRespuestaSeleccionadaAutomatica(final MensajeEspecial me) {
        if (me != null) {
            switch (me.tipo) {
                case MensajeEspecial.MENSAJE_SI_NO:
                    preguntaSiNo(me);
                    break;
                case MensajeEspecial.OPCION_MULTIPLE:
                    preguntaOpcionMultiple(me);
                    break;
            }
        }
    }

//	public void preguntaSiNo(MensajeEspecial me){
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle(me.descripcion)
//		.setPositiveButton(R.string.No,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int id) {
//								guardarRespuestaDialog(MensajeEspecial.NO);
//							}
//						})
//				.setNegativeButton(R.string.Si,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int id) {
//								guardarRespuestaDialog(MensajeEspecial.SI);
//							}
//						});
//		builder.show();
//	}
//	
//	public void preguntaOpcionMultiple(MensajeEspecial me){
//		
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle(me.descripcion).setItems(me.getArregloDeRespuestas(), 
//				new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int id) {
//								guardarRespuestaDialog(id);
//							}
//						});
//		builder.show();
//	}

    public void anomaliasABorrar(final MensajeEspecial me) {

        final TomaDeLecturas tdl = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(me.descripcion).setItems(me.getArregloDeRespuestas(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        if (globales.tll.getLecturaActual().deleteAnomalia(me.regresaValor(id)))
                            Toast.makeText(tdl, R.string.msj_anomalias_borrada, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(tdl, R.string.msj_anomalias_error_borrado, Toast.LENGTH_LONG).show();

                        regresaDeBorrar();
                    }
                });
        builder.show();
    }

    public void regresaDeBorrar() {
        String ls_comentarios = "";
        globales.is_presion = globales.tll.getLecturaActual().getAnomaliaAMostrar();
        if (globales.is_lectura.length() == 0 && globales.tll.getLecturaActual().anomalias.size() == 0) {
            globales.modoCaptura = false;
            salirModoCaptura();
            // borramos fotos temporales anteriores
            openDatabase();

            db.execSQL("delete from fotos where temporal="
                    + CamaraActivity.TEMPORAL);

            closeDatabase();
        } else if (globales.is_lectura.length() == 0 && globales.tll.getLecturaActual().anomalias.size() > 0) {
            presentacionAnomalias(false, "", "");
        } else {
            globales.tll.getLecturaActual().borrarLecturasAusentes();
            globales.is_presion = globales.tll.getLecturaActual().getAnomaliaAMostrar();
            setModoCaptura();
        }

        //No tiene caso poner esto
//		tv_anomalia.setText(getString(R.string.lbl_tdl_indica_anomalia) + (globales.is_presion.length()>3?"***":globales.is_presion));
//		if (globales.tll.getLecturaActual().anomalias.size() != 0) {
//			// Tiene una anomalia
//			ls_comentarios = getString(R.string.str_anomalia)+": "  + globales.tll.getLecturaActual().getAnomaliaAMostrar();
//			if (globales.tll.getLecturaActual().subAnomalias.size() != 0) {
//				// Tiene una subanomalia
//				ls_comentarios += ", "
//						+ getString(R.string.str_subanomalia)+": " 
//						+ globales.tll.getLecturaActual().getSubAnomaliaAMostrar();
//			}
//			ls_comentarios += "\n";


        // //Hay que verificar si la anomalia es ausente
        // if
        // (globales.tll.getLecturaActual().anomalia.ii_lectura==0
        // || globales.tll.getLecturaActual().anomalia.ii_ausente==4
        // ){
        // //Si es ausente, tiene que borrar la lectura...
        // globales.is_lectura="";
        // tv_lectura.setText(getString(R.string.lbl_tdl_indica_lectura) +globales.is_lectura);
        // }
        //	}

        setDatos(false);
//		tv_comentarios.setText(ls_comentarios
//				+ globales.tll.getLecturaActual().getComentarios());
    }

    public void preguntaRepiteAnomalia() {
        if (!globales.repiteAnomalias)
            return;

        if (globales.anomaliaARepetir.equals("") || globales.bModificar || !globales.tdlg.puedoRepetirAnomalia()) {
            b_repetir_anom.setVisibility(View.GONE);
            return;
        }
        if (globales.tll.getLecturaActual().getAnomaliasCapturadas().contains(globales.anomaliaARepetir)) {
            b_repetir_anom.setVisibility(View.GONE);
            return;
        }


        b_repetir_anom.setText(getString(R.string.lbl_tdl_repetirAnomalia) + " (" + globales.anomaliaARepetir + ")");
        b_repetir_anom.setVisibility(View.VISIBLE);

        b_repetir_anom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Hay que agregar la anomalia...
                globales.tdlg.cambiosAnomalia(globales.anomaliaARepetir);
                globales.tll.getLecturaActual().setAnomalia(globales.anomaliaARepetir);
                globales.tll.getLecturaActual().setSubAnomalia(globales.subAnomaliaARepetir);
                globales.tdlg.repetirAnomalias();
                if (globales.tdlg.esSegundaVisita(globales.anomaliaARepetir, globales.subAnomaliaARepetir)) {
                    //grabamos
                    capturar();
                } else {
                    b_repetir_anom.setVisibility(View.GONE);
                    globales.is_presion = globales.tll.getLecturaActual().getAnomalia();
                    preguntaSiBorraDatos = true;
                    setDatos(false);
                    presentacionAnomalias(false, globales.anomaliaARepetir.substring(globales.anomaliaARepetir.length() - 1, globales.anomaliaARepetir.length()), "");
                }


            }

        });
    }

    @Override
    protected void regresaDeMensaje(MensajeEspecial me, int respuesta) throws Exception {

        // TODO Auto-generated method stub
        regreseDe = NINGUNA;
        if (me.respondeA == TomaDeLecturasGenerica.PREGUNTAS_UBICACION_VACIA) {
            globales.is_presion = globales.tll.getLecturaActual().getAnomaliaAMostrar();
            setDatos(false);
            preguntaSiBorraDatos = true;
            presentacionAnomalias(true, me.regresaValor(respuesta).substring(0, 1), me.regresaValor(respuesta));
        } else if (me.respondeA == TomaDeLecturasGenerica.ANOMALIA_SEIS) {
            globales.is_presion = globales.tll.getLecturaActual().getAnomaliaAMostrar();
            setDatos(false);
            presentacionAnomalias(true, me.regresaValor(respuesta), "");
        } else if (me.respondeA == TomaDeLecturasGenerica.TIENE_MEDIDOR) {

            if (respuesta == 1) {
                //No tiene medidor
//			Bundle bu_params= new  Bundle();
//			
//			bu_params.putString(TomaDeLecturasGenerica., value)
                globales.tdlg.noRegistradosinMedidor();
            } else {
                mostrarVentanaDeNoRegistrados();
            }
        } else {
            preguntaSiBorraDatos = true;
            guardarRespuestaDialog(respuesta);
        }

        preguntaRepiteAnomalia();

    }

    protected void avanzarDespuesDeAnomalia() {
        if (globales.tdlg.avanzarDespuesDeAnomalia(ultimaAnomaliaSeleccionada, ultimaSubAnomaliaSeleccionada, true)) {
            salirModoCaptura();
            //if (captureAnomalias){
            globales.tdlg.anomaliasARepetir();
            globales.tdlg.subAnomaliasARepetir();
            //captureAnomalias=false;
            //}
            preguntaSiBorraDatos = false;
            switch (globales.ii_orden) {
                case ASC:
                    getSigLect();
                    break;
                case DESC:
                    getAntLect();
                    break;
            }
            //preguntaSiBorraDatos=true;
        }
    }

    void modoLecturaObligatoria() {
        button4.setEnabled(false);
        button3.setEnabled(false);
        button2.setEnabled(false);
        button6.setEnabled(false);

    }

    /*
        Muestra la ventana (Activity) donde se registran los medidores que no están registrados...
        ... en la lista de medidores que trae el celular.
     */

    void mostrarVentanaDeNoRegistrados() {
        Intent intent = new Intent(this, InputCamposGenerico.class);
        intent.putExtra("campos", globales.tdlg.getCamposGenerico("noregistrados"));
        intent.putExtra("label", "");
        intent.putExtra("anomalia", "noregistrados");
        intent.putExtra("titulo", globales.textoNoRegistrados);
        intent.putExtra("boton", "Grabar");
        intent.putExtra("puedoCerrar", true);
        startActivityForResult(intent, NO_REGISTADOS);
    }

    void mostrarVentanaDeCambioMedidor() {
        Intent intent = new Intent(this, InputCamposGenerico.class);
        intent.putExtra("campos", globales.tdlg.getCamposGenerico("cambiomedidor"));
        intent.putExtra("label", "");
        intent.putExtra("anomalia", "cambiomedidor");
        intent.putExtra("titulo", "Cambio de Medidor");
        intent.putExtra("boton", "Grabar");
        intent.putExtra("puedoCerrar", true);
        startActivityForResult(intent, CAMBIAR_MEDIDOR);
    }

    private void solicitarEmergencia() {
        enviarSolicitudEmergencia(EmergenciaMgr.EMERGENCIA_PRELIMINAR);
    }

//    private boolean requiereConfirmarEmergencia(){
//        if (!mDialogoConfirmarAyuda)
//            return true;
//
//
//    }

    protected void confirmarEmergencia() {
        if (mAlertEmergencia == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirmar ayuda");
            builder.setMessage("¿Está seguro de la ayuda?");
            builder.setCancelable(false);

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    enviarSolicitudEmergencia(EmergenciaMgr.EMERGENCIA_CONFIRMADA);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    enviarSolicitudEmergencia(EmergenciaMgr.EMERGENCIA_CANCELADA);
                    dialog.dismiss();
                    mEmergenciaMgr = null;
                }
            });

            mAlertEmergencia = builder.create();
        }
        mAlertEmergencia.show();
    }

    protected void enviarSolicitudEmergencia(int solicitudEmergencia) {
        if (globales == null) {
            Utils.showMessageLong(this, "Error al solicitar ayuda. Contacte soporte técnico.");
            return;
        }

        if (globales.sesionEntity == null) {
            Utils.showMessageLong(this, "No se ha autenticado en la aplicación");
            return;
        }

        if (globales.sesionEntity.empleado == null) {
            Utils.showMessageLong(this, "No se ha autenticado en la aplicación");
            return;
        }

        if (mEmergenciaMgr == null) {
            mEmergenciaMgr = new EmergenciaMgr(this);

            mEmergenciaMgr.setEmergenciaCallback(new EmergenciaCallback() {
                @Override
                public void enExito(OperacionResponse resp, int solicitudEmergenciaResultado) {
                    if (solicitudEmergenciaResultado == EmergenciaMgr.EMERGENCIA_PRELIMINAR)
                        confirmarEmergencia();
                }

                @Override
                public void enFallo(OperacionResponse resp) {
                    Utils.showMessageLong(TomaDeLecturas.this, resp.MensajeError);
                }
            });
        }

        switch (solicitudEmergencia) {
            case EmergenciaMgr.EMERGENCIA_PRELIMINAR:
                Utils.showMessageLong(TomaDeLecturas.this, "Fue enviada la solicitud de emergencia");
                break;
            case EmergenciaMgr.EMERGENCIA_CONFIRMADA:
                Utils.showMessageLong(TomaDeLecturas.this, "Fue enviada la confirmación de emergencia");
                break;
            case EmergenciaMgr.EMERGENCIA_CANCELADA:
                Utils.showMessageLong(TomaDeLecturas.this, "Fue enviada la cancelación de emergencia");
                break;
        }

        mEmergenciaMgr.enviarSolicitudEmergencia(globales.sesionEntity, globales.location, solicitudEmergencia);
    }

    protected void entrarSupervisor() {
        try {
            Intent entrarSupervisor = new Intent(TomaDeLecturas.this, SupervisorLoginActivity.class);

            startActivityForResult(entrarSupervisor, RESULTADO_ACTIVITY_ENTRAR_SUPERVISOR);
        } catch (Exception e) {
            Log.e("CPL", "entrarSupervisor: ", e);
            Utils.showMessageLong(this, "Hubo un error al iniciar la pantalla :" + e.getMessage());
        }
    }

    protected void usarEscaner() {
        scanIntent = new IntentIntegrator(this);
        scanIntent.setCaptureActivity(ScanActivity.class);
        scanIntent.setBeepEnabled(true);
        scanIntent.setPrompt(getString(R.string.str_lbl_instruccion_escaner));
        scanIntent.initiateScan();
    }

    protected void mostrarMedidorEscaneado(String codigo) {
        if (codigo == null)
            return;

        if (codigo.equals(""))
            return;

//        Utils.mostrarAlerta(this, "Medidor escaneado", "Se buscará el medidor con código: " + codigo,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        buscarMedidorEscaneado(codigo);
//                    }
//                });

        Utils.showMessageShort(TomaDeLecturas.this, "Buscando medidor: " + codigo);
        buscarMedidorEscaneado(codigo);
    }

    protected void buscarMedidorEscaneado(String codigo) {
        int secuencia;

        if (codigo == null)
            return;

        if (codigo.equals(""))
            return;

        if (mBuscarMedidorMgr == null)
            mBuscarMedidorMgr = new BuscarMedidorMgr(this);

        mBuscarMedidorMgr.setOnBuscarMedidorListener(new BuscarMedidorMgr.BuscarMedidorCallback() {
            @Override
            public void enExito(String codigo, int secuencia) {
                if (secuencia > 0)
                    seleccionarMedidor(secuencia);
                else
                    Utils.showMessageLong(TomaDeLecturas.this, "No se encontró el medidor");
            }

            @Override
            public void enFallo(String codigo, String mensajeError) {
                Utils.showMessageLong(TomaDeLecturas.this, "Error en le búsqueda del medidor :" + mensajeError);
            }
        });

        mBuscarMedidorMgr.buscarMedidorLocal(codigo);
    }


    protected void seleccionarMedidor(int secuencia) {
        try {
            globales.tll.setSecuencialLectura(secuencia);
            // Si estamos modificando y no tiene lectura o anomalia
            // debemos romper el modo de modificacion

            if (globales.bModificar) {
                if ((globales.tll.getLecturaActual().getLectura()
                        .equals("") && globales.tll.getLecturaActual()
                        .getAnomalia().equals(""))) {
                    globales.bcerrar = false;
                    globales.bModificar = false;
                    // tv_indica_corr.setText("N");
                }
            }
            globales.modoCaptura = false;
            this.salirModoCaptura();
            setDatos();

            // Si selecciono del listado un medidor con lectura o
            // anomalia, debe detectar que esta en modo de correccion
            if (!globales.bModificar) {
                if (!globales.tll.getLecturaActual().is_tipoLectura.trim().equals("")) {
                    globales.bModificar = true;
                    setModoModificacion(false);
                }
            }

        } catch (Throwable t) {
            mostrarMensaje("Error", "Error inesperado. Contacte soporte", t.getMessage(), null);
            t.printStackTrace();
        }
    }

     /*
        Procesa el resultado de regresar del Activity BuscarMedidor y el resultCode = BUSCAR_MEDIDOR
     */

    protected void procesarBuscarMedidor(final Intent data, final int resultCode) {
        Bundle bu_params;

        try {
            if (data == null)
                return;

            if (resultCode == Activity.RESULT_OK) {
                bu_params = data.getExtras();

                globales.tll.setSecuencialLectura(bu_params
                        .getInt("secuencia"));
                // Si estamos modificando y no tiene lectura o anomalia
                // debemos romper el modo de modificacion

                if (globales.bModificar) {
                    if ((globales.tll.getLecturaActual().getLectura()
                            .equals("") && globales.tll.getLecturaActual()
                            .getAnomalia().equals(""))) {
                        globales.bcerrar = false;
                        globales.bModificar = false;
                        // tv_indica_corr.setText("N");
                    }
                }
                globales.modoCaptura = false;
                this.salirModoCaptura();
                setDatos();

                // Si selecciono del listado un medidor con lectura o
                // anomalia, debe detectar que esta en modo de correccion
                if (!globales.bModificar) {
                    if (!globales.tll.getLecturaActual().is_tipoLectura.trim().equals("")) {
                        globales.bModificar = true;
                        setModoModificacion(false);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                globales.moverPosicion = false;
                preguntaSiBorraDatos = preguntaSiBorraDatosComodin;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            mostrarMensaje("Error", "Error inesperado. Contacte soporte", t.getMessage(), null);
        }
    }

    /*
        Procesa el resultado de regresar del Activity InputCamposGenerico y el resultCode =  NO_REGISTADOS
     */

    protected void procesarNoRegistrados(final Intent data, final int resultCode) {
        Bundle bu_params;
        long idNoRegistrado;
        NoRegistradoEntity reg;

        try {
            if (data == null)
                return;

            bu_params = data.getExtras();

            if (resultCode == Activity.RESULT_OK) {
                idNoRegistrado = globales.tdlg.regresaDeCamposGenericos(bu_params, "noregistrados");

                if (idNoRegistrado != 0) {
                    reg = DbLecturasMgr.getInstance().getNoRegistrado(this, idNoRegistrado);

                    if (reg != null)
                        fotoNoRegistrado(reg);
                }

                // if (globales.tomarFotoNoRegistrados)
                //this.tomarFoto(0, 1); no puedo
            }
        } catch (Throwable t) {
            t.printStackTrace();
            mostrarMensaje("Error", "Error inesperado. Contacte soporte", t.getMessage(), null);
        }
    }

    public void fotoNoRegistrado(NoRegistradoEntity reg) {
        Intent camara = new Intent(this, CamaraActivity.class);
        camara.putExtra("secuencial", reg.idLectura);
        camara.putExtra("caseta", String.valueOf(reg.idNoRegistrado));
        camara.putExtra("terminacion", "NoReg");
        camara.putExtra("temporal", CamaraActivity.ANOMALIA);
        camara.putExtra("cantidad", 1);
        camara.putExtra("anomalia", "SinAnomalia");
        camara.putExtra("TipoFoto", CamaraActivity.TIPO_FOTO_MEDIDOR_NO_REGISTRADO);
        // vengoDeFotos = true;
        startActivityForResult(camara, FOTO_NO_REGISTRADO);
    }

    private boolean procesarLectura(final Intent data, final int resultCode) {
        Bundle bu_params;

        boolean cambiandoDeLectura = false;
        boolean permiteTomarFoto = true;
//        int frecuenciaFotosCalidad = 0;
        Lectura lect;

        try {
            regreseDe = LECTURA;
            voyATomarFoto = false;

            lect = globales.tll.getLecturaActual();

            if (resultCode == Activity.RESULT_OK) {
                bu_params = data.getExtras();
                globales.is_lectura = bu_params.getString("input");

                globales.tdlg.setConsumo();

                if (globales.is_lectura.equals("")) {
                    globales.tdlg.regresaDeBorrarLectura();
                }

                regresaDeBorrar();
                if (globales.is_lectura.trim().length() > 0 || lect.anomalias.size() > 0) {
                    lect.sospechosa = String.valueOf(bu_params.getInt("confirmada"));
                    lect.guardarSospechosa();

                    int requiereLectura = lect.requiereLectura();

                    if (!globales.is_lectura.equals("") ||
                            requiereLectura == Anomalia.LECTURA_AUSENTE)
                        setModoCaptura();
                    else
                        salirModoCaptura();

                } else {
                    globales.modoCaptura = false;
                    salirModoCaptura();
                    permiteTomarFoto = false;
                }


                if (globales.ignorarTomaDeFoto && !globales.bModificar) {
                    permiteTomarFoto = false;
                }

                if (globales.bModificar && !globales.tdlg.tomarFotoModificar()) {
                    permiteTomarFoto = false;
                }


                // borramos fotos temporales anteriores
                openDatabase();

                db.execSQL("delete from fotos where temporal="
                        + CamaraActivity.TEMPORAL);

                closeDatabase();

                globales.is_terminacion = bu_params.getString("terminacion");
                lect.setTerminacion(globales.is_terminacion);

                tv_lectura.setText(getString(R.string.lbl_tdl_indica_lectura) + globales.is_lectura);
                tv_anomalia.setText(getString(R.string.lbl_tdl_indica_anomalia) + (globales.is_presion.length() > 3 ? "***" : globales.is_presion));

                if ((bu_params.getBoolean("sospechosa") || globales.fotoForzada || globales.bModificar)
                        && permiteTomarFoto && globales.is_lectura.length() > 0) {

                    if (modo == Input.FOTOS) {
                        globales.ignorarContadorControlCalidad = true;
                    }

//					if (!globales.ignorarGeneracionCalidadOverride){
                    globales.calidadOverride = globales.tdlg.cambiaCalidadSegunTabla("", "");
//					}
//					else{
//						globales.ignorarGeneracionCalidadOverride=false;
//					}

                    if (globales.ignorarContadorControlCalidad) {
                        tomarFoto(CamaraActivity.TEMPORAL, 1);
                        globales.ignorarContadorControlCalidad = false;
                    }
//                    else {
//                        if (contadorControlCalidadFotos == 0) {
//                            tomarFoto(CamaraActivity.TEMPORAL, 1);
//                            contadorControlCalidadFotos++;
//                        } else {
//                            contadorControlCalidadFotos++;
//                            if (contadorControlCalidadFotos >= globales.controlCalidadFotos) {
//                                contadorControlCalidadFotos = 0;
//                            }
//                        }
//                    }
                } else if (!lect.getmMotivoLectura().equals("01") && globales.getTomarFotoCambioMedidor()) {
                    tomarFoto(CamaraActivity.TEMPORAL, 1);
                }
//                else {
//                    frecuenciaFotosCalidad = globales.getFrecuenciaFotoCalidad();
//                    if (frecuenciaFotosCalidad > 0) {
//                        if (contadorControlCalidadFotos > frecuenciaFotosCalidad) {
//                            contadorControlCalidadFotos = 0;
//                            tomarFoto(CamaraActivity.TEMPORAL, 1);
//                        }
//
//                        contadorControlCalidadFotos++;
//                    }
//                }


                if (bu_params.getBoolean("sospechosa")) {
                    lect.sospechosa = String.valueOf(bu_params.getInt("confirmada"));
                    lect.guardarSospechosa();
                }

                boolean mostrarVentanaDeSellos = globales.tdlg.mostrarVentanaDeSellos();

                if (!voyATomarFoto && globales.legacyCaptura && !mostrarVentanaDeSellos && !globales.is_lectura.equals("")) {
                    capturar();
                    cambiandoDeLectura = true;
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                setDatos(false);
                if (globales.inputMandaCierre) {
                    globales.inputMandaCierre = false;
                    muere();

                    return false;
                }

                bu_params = data.getExtras();
                if (bu_params.getBoolean("sospechosa")) {
                    lect.sospechosa = String.valueOf(bu_params.getInt("confirmada"));
                    lect.guardarSospechosa();
                }
                if (globales.bModificar) {
                    //establcemos el fondo de correccion
                    setFondoCorreccion();
                } else {
                    layout.setBackgroundResource(0);
                }

                if (secuencialAntesDeInput != lect.secuencia)
                    setDatos();
            }
            voyATomarFoto = false;

            //checamos si va a tomar sellos
            if (globales.tdlg.mostrarVentanaDeSellos() && !cambiandoDeLectura) {
                Intent intent = new Intent(this, InputCamposGenerico.class);
                intent.putExtra("campos", globales.tdlg.getCamposGenerico("sellos"));
                intent.putExtra("label", "");
                intent.putExtra("anomalia", "sellos");
                intent.putExtra("titulo", "Retirar Sellos");
                intent.putExtra("boton", "Grabar");
                intent.putExtra("puedoCerrar", false);
                startActivityForResult(intent, INPUT_CAMPOS_GENERICO_ME);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return permiteTomarFoto;
    }

    private void procesarFotos() {
        Lectura lect;

        if (globales.estoyTomandoFotosConsecutivas) {
            tomaFotosConsecutivas(globales.idMedidorUltimaLectura);
        } else {
            tieneFotos();
            //avanzarDespuesDeAnomalia();
        }

        if (regreseDe == ANOMALIA && globales.legacyCaptura) {
            lect = globales.tll.getLecturaActual();

            if (lect.requiereLectura() == Anomalia.LECTURA_AUSENTE && !globales.tdlg.avanzarDespuesDeAnomalia(ultimaAnomaliaSeleccionada, ultimaSubAnomaliaSeleccionada, false)) {
                capturar();
            } else if (globales.tdlg.avanzarDespuesDeAnomalia(ultimaAnomaliaSeleccionada, ultimaSubAnomaliaSeleccionada, false)) {
                avanzarDespuesDeAnomalia();
            }
        } else if (globales.legacyCaptura && regreseDe == LECTURA) {
            capturar();
        }
        //regreseDe=FOTOS;

        voyATomarFoto = false;
    }

    private void procesarAnomalia(final int requestCode, final Intent data, final int resultCode) {
        Bundle bu_params;

        regreseDe = ANOMALIA;
        voyATomarFoto = false;
        //Si se hicieron cambois en el nombre del usuario se verán
        setDatos(false);
        String ls_comentarios = "";
        boolean anomaliaCapturada = true;
        String ls_anomalia = "", ls_subAnomalia = "";

        Anomalia anom = null;

        if (resultCode == Activity.RESULT_OK) {
            bu_params = data.getExtras();
            ls_anomalia = bu_params.getString("anomalia");
            ls_subAnomalia = bu_params.getString("subAnomalia");


            if (!ls_subAnomalia.equals("")) {
                anom = new Anomalia(this, ls_subAnomalia, ls_anomalia, true);
            } else {
                anom = new Anomalia(this, ls_anomalia, "", false);
            }

            if (preguntaSiBorrarEnAnomaliaAusentes && !globales.is_lectura.equals("")/*&& !globales.bModificar*/) {
                if (anom.requiereLectura() == Anomalia.LECTURA_AUSENTE) {
                    if (globales.bloquearBorrarSiIntento && !globales.tll.getLecturaActual().intento6.equals("")) {
                        mensajeOK("La lectura para la anomalia seleccionada es 'Ausente' y se ha superado la cantidad de veces que se puede ingresar una lectura. No se ingresará la anomalia.", "Toma de Lecturas");
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    AlertDialog alert;

                    //Si ingresé datos, estoy en modo captura y  me estoy moviendo deberia preguntar
                    builder.setMessage(R.string.str_pregunta_guardar_cambios_anomalia_ausente)
                            .setCancelable(false).setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //recepcion();
                                    preguntaSiBorrarEnAnomaliaAusentes = false;
                                    onActivityResult(requestCode, resultCode, data);


                                    dialog.dismiss();

                                }
                            })
                            .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

                    alert = builder.create();
                    alert.show();
                    return;
                }
            }

            preguntaSiBorrarEnAnomaliaAusentes = true;

            //Realizamos los cambios necesarios en la anomalia
            globales.tdlg.cambiosAnomalia(bu_params.getString("anomalia"));

            // Tomamos la anomalia y la subAnomalia en caso de requerir
            globales.tll.getLecturaActual().setAnomalia(
                    bu_params.getString("anomalia"));

            globales.tll.getLecturaActual().setSubAnomalia(
                    bu_params.getString("subAnomalia"));

//				globales.tll.getLecturaActual().setComentarios(
//						bu_params.getString("comentarios"));

            ultimaAnomaliaSeleccionada = bu_params.getString("anomalia");
            ultimaSubAnomaliaSeleccionada = bu_params.getString("subAnomalia");

            postProcesarAnomalias(globales.tll.getLecturaActual(), ls_anomalia);

            //Aqui guardamos si debe repetir anomalia o no
//				String ls_anomalia=globales.tll.getLecturaActual().getAnomaliaAMostrar();
//				if (ls_anomalia.endsWith("A") || ls_anomalia.endsWith("AC") || ls_anomalia.endsWith("CA")|| ls_anomalia.endsWith("R")|| ls_anomalia.endsWith("Z"))
//					globales.anomaliaARepetir=globales.convertirAnomalias?
//							globales.tll.getLecturaActual().getUltimaAnomalia().is_conv:
//								globales.tll.getLecturaActual().getUltimaAnomalia().is_anomalia;
//				else
//					globales.anomaliaARepetir="";
            //captureAnomalias=true;
            preguntaSiBorraDatos = true;
            if (bu_params.getString("subAnomalia").equals(""))
                muestraRespuestaSeleccionadaAutomatica(globales.tdlg.regresaDeAnomalias(bu_params.getString("anomalia")));
            else
                muestraRespuestaSeleccionadaAutomatica(globales.tdlg.regresaDeAnomalias(bu_params.getString("subAnomalia"), false));


        } else if (resultCode == Activity.RESULT_CANCELED) {
            anomaliaCapturada = false;
        }


        globales.is_presion = globales.tll.getLecturaActual().getAnomaliaAMostrar();

//				tv_anomalia.setText(getString(R.string.lbl_tdl_indica_anomalia) + (globales.is_presion.length()>3?"***":globales.is_presion));
//
//				if (globales.tll.getLecturaActual().anomalias.size() != 0) {
//					// Tiene una anomalia
//					ls_comentarios = getString(R.string.str_anomalia)+": "  + globales.tll.getLecturaActual().getAnomaliaAMostrar();
//					if (globales.tll.getLecturaActual().subAnomalias.size() != 0) {
//						// Tiene una subanomalia
//						ls_comentarios += ", "
//								+ getString(R.string.str_subanomalia)+": "
//								+ globales.tll.getLecturaActual().getSubAnomaliaAMostrar();
//					}
//					ls_comentarios += "\n";
//
//					// //Hay que verificar si la anomalia es ausente
//					// if
//					// (globales.tll.getLecturaActual().anomalia.ii_lectura==0
//					// || globales.tll.getLecturaActual().anomalia.ii_ausente==4
//					// ){
//					// //Si es ausente, tiene que borrar la lectura...
//					// globales.is_lectura="";
//					// tv_lectura.setText(getString(R.string.lbl_tdl_indica_lectura) +globales.is_lectura);
//					// }
//				}
//
//				tv_comentarios.setText(ls_comentarios
//						+ globales.tll.getLecturaActual().getComentarios());

        setDatos(false);
        //setModoCaptura();


        //Fotos tomadas con las anomalias se almacenan sin importar que se borren, just like the nokia does it
//				openDatabase();
//				db.execSQL("delete from fotos where temporal="
//						+ CamaraActivity.ANOMALIA);
//				closeDatabase();

        permiteCambiarModoCaptura = !is_anomaliasIngresadasAnteriormente.equals(globales.is_presion);

        // Aqui manejamos el si requiere lectura o no
        //if (!bu_params.getString("anomalia").equals("")) {
        if (globales.tll.getLecturaActual().anomalias.size() != 0) {
//					if (globales.tll.getLecturaActual().subAnomalias.size() != 0) {
//						presentacionAnomalias();
            // Hay que verificar si la anomalia es ausente

            //} else if (globales.tll.getLecturaActual().anomalias.size() != 0) {


            presentacionAnomalias(anomaliaCapturada, ls_anomalia, ls_subAnomalia);
            //}
        } else {
            // Hay que verificar si aun hay que seguir en modo de
            // captura...
            if (globales.is_lectura.equals("")) {
                globales.modoCaptura = false;
                salirModoCaptura();
            }
            button1.setEnabled(true);

        }

        if (anom != null) {
            if (anom.requiereLectura() == Anomalia.LECTURA_AUSENTE && !voyATomarFoto && !globales.tdlg.avanzarDespuesDeAnomalia(ultimaAnomaliaSeleccionada, ultimaSubAnomaliaSeleccionada, false)) {
                capturar();

            }
        }

        voyATomarFoto = false;
    }

    protected void postProcesarAnomalias(Lectura lect, String anomalia) {
        if (lect == null)
            return;

//        if (anomalia.equals("002")) {
//            procesarAnomaliaCambioMedidor(lect);
//        }
    }

//    protected void procesarAnomaliaCambioMedidor(Lectura lect)
//    {
//        ContentValues cv_datos = new ContentValues();
//        long idValorInsertado = 0;
//
//        cv_datos.put("envio", 1);
//        cv_datos.put("idArchivo", lect.idArchivo);
//        cv_datos.put("idLectura", lect.poliza);
//        cv_datos.put("idUnidadLect", lect.mIdUnidadLect);
//        cv_datos.put("Calle", lect.getDireccion());
//        cv_datos.put("Colonia", lect.getColonia());
//        cv_datos.put("NumMedidor", "");
//        cv_datos.put("TipoRegistro", "CM");
//
//        openDatabase();
//
//        idValorInsertado = db.insert("noRegistrados", null, cv_datos);
//
//        closeDatabase();
//    }

    /*
    Procesa el resultado de regresar del Activity InputCamposGenerico y el resultCode = CAMBIAR_MEDIDOR
 */
    protected void procesarCambiarMedidor(final Intent data, final int resultCode) {
        Bundle bu_params;

        try {
            if (resultCode == Activity.RESULT_OK) {
                bu_params = data.getExtras();
                globales.tdlg.regresaDeCamposGenericos(bu_params, "cambiomedidor");
                this.tomarFoto(0, 1, CamaraActivity.TIPO_FOTO_MEDIDOR_CAMBIO_MEDIDOR);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            mostrarMensaje("Error", "Error inesperado. Contacte soporte", t.getMessage(), null);
        }
    }

    private void procesarMandarImprimir() {
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                    .getDefaultAdapter();
            if (mBluetoothAdapter.isEnabled()) {
                // Solo si esta encendido volvemos a llamar esta función
                mandarAImprimir(ilec_lectura);
            }
        } catch (Throwable t) {
            mostrarMensaje("Error", "Error inesperado. Contacte soporte", t.getMessage(), null);
        }
    }

    /*
        Muestra la ubicación del medidor, de acuerdo con la Latitud, Longitud que se tiene guardada que se envió en el archivo TPL
    */

    private void mostrarUbicacionGPS() {
        Lectura lectura;
        String miLatitud = "";
        String miLongitud = "";
        String serieMedidor = "";
        String codigoBarras = "";
        boolean intercambiarSerieMedidor = false;
        String uri = "";

        try {
            if (globales == null)
                return;

            if (globales.tll == null)
                return;

            lectura = globales.tll.getLecturaActual();

            if (lectura == null)
                return;

            miLatitud = lectura.getMiLatitud();
            miLongitud = lectura.getMiLongitud();

            if (miLatitud.trim().equals("") || miLongitud.trim().equals(""))
                return;

            serieMedidor = lectura.getSerieMedidor();
            codigoBarras = lectura.getCodigoBarras();
            intercambiarSerieMedidor = lectura.getIntercambiarSerieMedidor();

            //uri = "https://maps.google.com/maps/@?api=1&map_action=map&center=" + miLatitud + "," + miLongitud + "&zoom=20";

            if (!intercambiarSerieMedidor)
                uri = "geo:" + miLatitud + "," + miLongitud + "?q=" + miLatitud + "," + miLongitud + "(" + serieMedidor + ")&z=24";
            else
                uri = "geo:" + miLatitud + "," + miLongitud + "?q=" + miLatitud + "," + miLongitud + "(" + codigoBarras + ")&z=24";


            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            this.startActivity(intent);
        } catch (Throwable t) {
            Utils.showMessageLong(this, t.getMessage());
        }
    }


       /* -------------------------------------------------------------------------------------------
    Muestra el diálogo o ventana para mostrar mensajes diversos o de error.
    El detalle del error está oculto hasta que se hace click en el mensaje.
    ------------------------------------------------------------------------------------------- */

    private void mostrarMensaje(String titulo, String mensaje, String detalleError, DialogoMensaje.Resultado resultado) {
        if (mDialogoMsg == null) {
            mDialogoMsg = new DialogoMensaje(this);
        }

        mDialogoMsg.setIdColorFondo(R.color.White);
        mDialogoMsg.setOnResultado(resultado);
        mDialogoMsg.mostrarMensaje(titulo, mensaje, detalleError);
    }

    private void mostrarAlerta(String titulo, String mensaje, String detalleError, DialogoMensaje.Resultado resultado) {
        if (mDialogoMsg == null) {
            mDialogoMsg = new DialogoMensaje(this);
        }

        mDialogoMsg.setIdColorFondo(R.color.Pink);
        mDialogoMsg.setOnResultado(resultado);
        mDialogoMsg.mostrarMensaje(titulo, mensaje, detalleError);
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        mostrarMensaje(titulo, mensaje, "", null);
    }

}
