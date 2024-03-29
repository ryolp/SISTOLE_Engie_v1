package enruta.sistole_engie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import enruta.sistole_engie.clases.DescargarLecturasProcesoMgr;

public class DescargarLecturasActivity extends Activity {

    private TextView txtProgreso= null;
    private TextView txtIndicador= null;
    private TextView txtDetalle= null;
    private Button btnAceptar= null;
    private Button btnCancelar= null;
    private Button btnRegresar= null;
    private ProgressBar barraProgreso= null;;
    private DescargarLecturasProcesoMgr mProcesadorLecturas = null;
    private Globales mGlobales= null;
    private DialogoMensaje mDialogo = null;
    private String mResultado = "";
    private boolean mResultadoOk = false;

    /*
        Función para la creación del activity
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descargar_lecturas);

        inicializarControles();
        inicializarEventos();
        descargarLecturas();
    }

    /*
        Evitar que el usuario haga el botón back.
    */

    @Override
    public void onBackPressed() {
    }

    /*
        Función para inicializar las referencias a los controles y variables globales
     */

    private void inicializarControles() {
        txtProgreso = (TextView)findViewById(R.id.txtProgreso);
        txtIndicador = (TextView)findViewById(R.id.txtIndicador);
        txtDetalle = (TextView)findViewById(R.id.txtDetalle);
        barraProgreso = (ProgressBar)findViewById(R.id.ep_gauge);
        btnAceptar = (Button)findViewById(R.id.btnAceptar);
        btnCancelar = (Button)findViewById(R.id.btnCancelar);
        btnRegresar = (Button)findViewById(R.id.btnRegresar);
        mGlobales = ((Globales) getApplicationContext());

        btnAceptar.setVisibility(View.GONE);
        btnCancelar.setVisibility(View.GONE);
        btnRegresar.setVisibility(View.GONE);

        txtProgreso.setText("");
        txtIndicador.setText("");
        txtDetalle.setText("");
        txtDetalle.setVisibility(View.GONE);
    }

    private void inicializarEventos()
    {
        if (btnAceptar != null)
        {
            btnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalizarActivity();
                }
            });
        }

        if (btnRegresar != null)
        {
            btnRegresar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalizarActivity();
                }
            });
        }

        if (btnCancelar != null)
        {
            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalizarActivity();
                }
            });
        }

        if (txtIndicador != null) {
            txtIndicador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String detalle;

                    detalle = txtDetalle.getText().toString();
                    if (!detalle.trim().equals(""))
                        txtDetalle.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /*
        Función para iniciar el proceso para descargar las lecturas
     */

    private void descargarLecturas() {
        if (mProcesadorLecturas == null) {
            mProcesadorLecturas = new DescargarLecturasProcesoMgr(this, mGlobales);
        }

        if (!mProcesadorLecturas.puedoCargar()) {
            setResultado(getString(R.string.msj_trans_ruta_no_descargada), false, btnRegresar);
            finalizarActivity();
            return;
        }

        preguntarSiBorrar();
    }

    /*
        Función para preguntar si borra y continua o cancela el proceso
     */

    private void preguntarSiBorrar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msj_warning_importar)
                .setCancelable(false)
                .setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        continuarDescargarLecturas();
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        setResultado("Operación cancelada", false, null);
                        finalizarActivity();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /*
        Función que iniciará los procesos de:
            Descargar el archivo de las lecturas de manera asíncrona
            Procesar las lecturas recibidas en un thread para no bloquear el thread principal (UI = User Interface)
     */

    private void continuarDescargarLecturas() {

        // Inicializar el thread o proceso
        mProcesadorLecturas.setEnCallback(new DescargarLecturasProcesoMgr.EnCallback() {
            @Override
            public void enMensaje(String mensaje) {
                if (txtIndicador != null)
                    txtIndicador.setText(mensaje);
            }

            @Override
            public void enProgreso(String progreso, int porcentaje) {
                if (txtProgreso != null)
                    txtProgreso.setText(progreso);

                if (barraProgreso != null)
                    barraProgreso.setProgress(porcentaje);
            }

            @Override
            public void enExito() {
                descargaFinalizada();
            }

            @Override
            public void enFallo(String mensaje) {
                if (txtIndicador != null)
                    txtIndicador.setText(mensaje);

                setResultado(mensaje, false, btnRegresar);
            }


            @Override
            public void enError(int numError, String mensajeError, String detalleError) {
                if (txtIndicador != null)
                    txtIndicador.setText(mensajeError);

                if (txtDetalle != null)
                    txtDetalle.setText(detalleError);

                setResultado("Operación cancelada por un error", false, btnCancelar);
            }
        });

        mProcesadorLecturas.descargarLecturas();
    }

    private void descargaFinalizada() {
        String msg;

        mProcesadorLecturas.finalize();
        mProcesadorLecturas = null;

        msg = "Todos los datos fueron descargados";

        txtIndicador.setText(msg);

        setResultado(msg, true, btnRegresar);
    }

        /*
        Función para mostrar algún resultado de la recepción de lecturas
     */

//    private void mostrarResultado(String titulo, String mensaje, String mensajeDetalle) {
//        if (mDialogo == null) {
//            mDialogo = new DialogoMensaje(this);
//
//            mDialogo.setOnResultado(new DialogoMensaje.Resultado() {
//                @Override
//                public void Aceptar(boolean EsOk) {
//                    finalizarActivity("Operación cancelada");
//                }
//            });
//        }
//
//        mDialogo.mostrarMensaje(titulo, mensaje, mensajeDetalle);
//    }

    private void setResultado(String mensaje, boolean resultado, Button btn)
    {
        mResultado = mensaje;
        mResultadoOk = resultado;

        btnAceptar.setVisibility(View.GONE);
        btnRegresar.setVisibility(View.GONE);
        btnCancelar.setVisibility(View.GONE);

        if (btn != null)
            btn.setVisibility(View.VISIBLE);
    }

    private void finalizarActivity() {
        Intent resultado = new Intent();
        resultado.putExtra("mensaje", mResultado);

        if (mProcesadorLecturas != null) {
            mProcesadorLecturas.finalize();
        }

        if (mResultadoOk)
            setResult(Activity.RESULT_OK, resultado);
        else
            setResult(Activity.RESULT_CANCELED, resultado);

        finish();
    }
}