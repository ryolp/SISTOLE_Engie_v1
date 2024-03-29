package enruta.sistole_engie;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class TransmisionesPadre extends Activity {
    TextView tv_progreso, tv_indicador;
    ProgressBar pb_progress;
    Handler mHandler;

    boolean transmitirLecturas = true;

    DBHelper dbHelper;
    SQLiteDatabase db;

    public final static int TRANSMISION = 0;
    public final static int RECEPCION = 1;

    public final static int BLUETOOTH = 0;
    public final static int WIFI = 1;
    public final static int GPRS = 2;

    final static int PROGRESO = 0;
    final static int MENSAJE = 1;
    final static int BARRA = 2;
    final static int TOPE = 3;


    public final static int ERROR = -99;
    public final static int CANCELADA_POR_USUARIO = -98;

    boolean yaAcabo = false;
    boolean mostrarAlerta = true;
    boolean transmitirTodo = false;

    String ls_categoria = "", ls_servidor = "", ls_subCarpeta;
    String ls_carpeta = "uploads/activos";
    String ls_extension = "tpl";

    Vector<String> vLecturas;
    boolean transmiteFotos = true;
    Resources resources;
    TodasLasLecturas tll;

    boolean puedoCerrar = true;
    boolean cancelar = false;
    boolean algunError = false;

    Thread hilo;
    Bundle bu_params;

    Globales globales;

    public boolean puedoCargar() {
        boolean puedo = false;
        openDatabase();


        Cursor c = db.rawQuery("Select descargada from encabezado", null);

        c.moveToFirst();

        if (c.getCount() > 0) {
            if (c.getInt(c.getColumnIndex("descargada")) == 0) {
                //Si no ha sido descargada, habrá que verificar si ya hay una lectura ingresada
                c.close();
                c = db.rawQuery("Select count(*) canti from Ruta where trim(tipoLectura)<>''"
                        + "order by cast(secuencia as Integer) asc limit 1", null);
                c.moveToFirst();
                puedo = c.getInt(c.getColumnIndex("canti")) == 0;
            } else
                puedo = true;
        } else
            puedo = true;

        c.close();
        closeDatabase();

        return puedo;
    }

    protected void seleccion() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alert;
        switch (bu_params.getInt("tipo")) {
            case TRANSMISION:

                DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface arg0) {
                        // TODO Auto-generated method stub
                        muere(false, "");
                    }
                };

                if (globales.elegirQueDescargar) {
                    String[] ls_opciones = {"Solo lecturas", "Solo fotos", "Ambas"};
                    builder = new AlertDialog.Builder(this);
                    builder.setOnCancelListener(cancelListener).setTitle(R.string.msj_main_select_metodo_trans)
                            .setItems(ls_opciones, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean ejecutar = true;
                                    switch (which) {
                                        case 0: //Solo lecturas
                                            transmitirLecturas = true;
                                            transmiteFotos = false;
                                            break;
                                        case 1://Solo Fotos
                                            transmitirLecturas = false;
                                            transmiteFotos = true;
                                            break;
                                        case 2: //Ambas
                                            transmitirLecturas = true;
                                            transmiteFotos = true;
                                            break;
                                    }

                                    procesosDeTransmision();


                                }

                                public void onCancel(DialogInterface dialog) {
                                    // TODO Auto-generated method stub
                                    finish();
                                }
                            });
                    builder.show();

                } else {
                    procesosDeTransmision();
                }


                break;
            case RECEPCION:
                //recepcion();

                if (!puedoCargar()) {
                    muere(true, getString(R.string.msj_trans_ruta_no_descargada));
                    return;
                }

                builder.setMessage(R.string.msj_warning_importar)
                        .setCancelable(false).setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                recepcion();
                                // preguntaArchivo();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                mostrarAlerta = false;
                                muere(true, "");

                            }
                        });

                alert = builder.create();
                alert.show();
                break;
        }
    }

    public void openDatabase() {
        dbHelper = new DBHelper(this);

        db = dbHelper.getReadableDatabase();
    }

    public void closeDatabase() {
        if (db.isOpen()) {
            db.close();
            dbHelper.close();
        }

    }

    public void muere(boolean yaAcabo, String mensaje) {
        Intent resultado = new Intent();
        resultado.putExtra("mensaje", mensaje);

        //Parece que aveces cuando salimos no cerramos correctamente la base de datos, asi que la vamos a cerrar aqui si aun no esta cerrada
        if (db != null) {
            if (db.isOpen()) {
                try {
                    db.endTransaction();
                } catch (Throwable e) {
                }

                closeDatabase();
            }
        }


        if (yaAcabo) {
            setResult(Activity.RESULT_OK, resultado);
            finish();
        } else {
            setResult(ERROR, resultado);
            finish();
        }

    }

    protected abstract void transmitir();

    protected abstract void recepcion();

    public void esconderTeclado(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if (puedoCerrar) {

            if (hilo != null)
                hilo.interrupt();
	    	
	    	/*if (db.isOpen()){
	    		try{
	    			db.endTransaction();
	    		}catch(Throwable e)
	    		{}
	    		
	 	    	closeDatabase();
	    	 }*/


            //setResult(Activity.RESULT_CANCELED);
            //super.onBackPressed();

        }

        cancelar = true;

    }

    protected void stop() throws Throwable {

        if (cancelar) {
            puedoCerrar = true;
            onBackPressed();
            throw new Throwable(getString(R.string.msj_trans_cancelada_por_usuario));
        }


    }

    protected void onDestroy() {

        super.onDestroy();

    }

    public boolean validaCampoDeConfig(Cursor c, String ls_mensaje) {
        String ls_valor;
        if (c.getCount() == 0) {

            //muere(true, "No se ha configurado algún servidor.\nConfigure el servidor en la pantalla 'Configuración' que se en cuentra en el menu de la 'Categoria" +
            //		" Principal'.");
		   
		   /*db.execSQL("insert into config(key, value) values ('servidor', 'http://www.espinosacarlos.com')");
		   c=db.rawQuery("Select value from config where key='servidor'", null);
		   c.moveToFirst();
			  // return;
		   ls_servidor=c.getString(c.getColumnIndex("value"));*/
            muere(true, ls_mensaje);
            return false;


        } else {
            ls_valor = c.getString(c.getColumnIndex("value"));
            if (ls_valor.equals("")) {
                muere(true, ls_mensaje);
                return false;
            }

        }

        return true;
    }

    public boolean validaCampoDeConfig(String ls_mensaje) {
        String ls_valor;
        if (ls_mensaje.length() != 0) {

            muere(true, ls_mensaje);
            return false;


        }

        return true;
    }

    public static void borrarRuta(SQLiteDatabase db) {
        db.execSQL("delete from ruta ");
        db.execSQL("delete from fotos ");
        db.execSQL("delete from Anomalia ");
        db.execSQL("delete from encabezado ");
        db.execSQL("delete from NoRegistrados ");
        db.execSQL("delete from usuarios ");
    }

    protected void marcarComoDescargada() {
        openDatabase();

        db.execSQL("update encabezado set descargada=1");

        closeDatabase();

    }

    protected void procesosDeTransmision() {
        try {
            int li_datos = 0, li_fotos;
            //transmitir();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog alert;


            openDatabase();
            Cursor c;


            c = db.rawQuery("select * from ruta", null);
            li_datos = c.getCount();
            c.close();
            c = db.rawQuery("select * from fotos", null);
            li_fotos = c.getCount();
            c.close();

            if (li_datos == 0 && li_fotos == 0) {
                muere(true, getString(R.string.msj_trans_no_hay_datos));
                return;
            }
//		c= db.rawQuery("select value from config where key='cpl'", null);
//		
//		if (c.getCount()==0){
//				muere(true, String.format(getString(R.string.msj_config_no_guardada), getString(R.string.info_CPL)));
//				return;
//			}
//		c.moveToFirst();
//		if (c.getString(c.getColumnIndex("value")).trim().equals("")){
//			muere(true, String.format(getString(R.string.msj_config_no_guardada), getString(R.string.info_CPL)));
//				return;
//		}
//		ls_categoria=c.getString(c.getColumnIndex("value"))+"."+ls_extension;
//		c.close();
//		closeDatabase();

            ls_categoria = globales.tdlg.getNombreArchivo(TomaDeLecturasGenerica.SALIDA);

            if (ls_categoria.trim().equals("")) {
                muere(true, String.format(getString(R.string.msj_config_no_guardada), getString(R.string.info_CPL)));
                return;
            }

            //hacemos un query para buscar si ya se descargo >:|

            if (globales.preguntarSiSegundaVezDescargada) {
                c = db.rawQuery("select descargada from encabezado", null);

                c.moveToFirst();

                int descargada = c.getInt(c.getColumnIndex("descargada"));

                c.close();

                if (descargada == 1) {
                    //preguntar si deseo descargar

                    builder.setMessage("El itinerario ya ha sido descargado anteriormente, ¿Desea sobrescribir con los datos actuales?")
                            .setCancelable(false).setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //recepcion();
                                    procesosCierreDeLecturas();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    mostrarAlerta = false;
                                    muere(true, "");

                                }
                            });

                    alert = builder.create();
                    alert.show();
                } else {
                    procesosCierreDeLecturas();
                }
            } else {
                procesosCierreDeLecturas();
            }
        } catch (Throwable t) {
            muere(true, t.getMessage());
        }
    }

    public void procesosCierreDeLecturas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alert;
        if (tll.hayPendientes()) {
            switch (globales.modoDeCierreDeLecturas) {
                case Globales.FORZADO:
                    builder.setMessage(R.string.msj_warning_pendientes)
                            .setCancelable(false).setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //recepcion();
                                    transmitir();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    mostrarAlerta = false;
                                    muere(true, "");

                                }
                            });

                    alert = builder.create();
                    alert.show();
                    break;

                case Globales.RUTA_COMPLETA:
                    muere(true, "No se pueden transmitir los datos porque hay lecturas pendientes.");
                    break;
                case Globales.SOLO_TRANSMITIR_PENDIENTES:
                    transmitir();
                    break;
                default:
                    builder.setMessage(R.string.msj_warning_pendientes_continuar)
                            .setCancelable(false).setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //recepcion();
                                    transmitir();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    mostrarAlerta = false;
                                    muere(true, "");

                                }
                            });

                    alert = builder.create();
                    alert.show();

            }

        } else {
            transmitir();
        }
    }

}
