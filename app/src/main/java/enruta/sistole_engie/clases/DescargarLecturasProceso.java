package enruta.sistole_engie.clases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import enruta.sistole_engie.Anomalia;
import enruta.sistole_engie.DBHelper;
import enruta.sistole_engie.Globales;
import enruta.sistole_engie.Lectura;
import enruta.sistole_engie.R;
import enruta.sistole_engie.TodasLasLecturas;
import enruta.sistole_engie.Usuario;
import enruta.sistole_engie.entities.ResumenEntity;
import enruta.sistole_engie.services.DbConfigMgr;
import enruta.sistole_engie.services.DbLecturasMgr;

/*
    DescargarLecturasProceso().

    Clase que agrupa el proceso que lee las lecturas recibidas del servidor y las registra
    en la base de datos del celular.
    La instancia de esta clase se ejecuta en un thread diferente al principal.
*/

public class DescargarLecturasProceso implements Runnable {
    protected Context mContext = null;
    protected TodasLasLecturas mTll;
    protected Vector<Lectura> mLecturas;
    protected Handler mHandler = null;
    protected DBHelper mDbHelper = null;
    protected SQLiteDatabase mDb = null;
    protected Globales mGlobales = null;
    protected String mContenido = "";
    protected ArchivosLectMgr mArchivosLectMgr = null;
    protected DbLecturasMgr mDbLectMgr = null;
    protected DescargarLecturasProceso.EnNotificaciones mNotificaciones = null;

    /*
        EnNotificaciones().

        Interface para el envío de las notificaciones al proceso que llama a la instancia de esta clase.
    */
    public interface EnNotificaciones {
        public void enMensaje(String mensaje);

        public void enProgreso(String progreso, int porcentaje);

        public void enError(String mensajeError, String detalleError);

        public void enFinalizado();
    }

    /*
        DescargarLecturasProceso().

        Constructor de la clase.
    */

    public DescargarLecturasProceso(Context context, Globales globales, Handler handler, String contenido) {
        mContext = context;
        mGlobales = globales;
        mHandler = handler;
        mContenido = contenido;
    }

    /*
        setEnNotificaciones().

        Metodo para guardar una referencia al método del proceso que llama a la instancia de esta clase,
        el cual recibirá las notificaciones de este proceso.
    */

    public void setEnNotificaciones(DescargarLecturasProceso.EnNotificaciones progreso) {
        mNotificaciones = progreso;
    }

    /*
        run().

        Entrada principal para que un thread ejecute el proceso definido en esta clase.
    */

    public void run() {
        boolean resultado;

        try {
            procesarLecturas(mContenido);

            if (mNotificaciones != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        notificarFinalizado();
                    }
                });
            }
        } catch (AppException e1) {
            notificarError(e1.getMessage(), "");
        } catch (Throwable e2) {
            notificarError("Error inesperado", e2.getMessage());
        }
    }

    /*
        notificarMensaje().

        Función para notificar al thread principal de un evento.
    */

    private void notificarMensaje(String mensaje) {
        if (mNotificaciones != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mNotificaciones.enMensaje(mensaje);
                }
            });
        }
    }

    /*
        notificarProgreso().

        Función para notificar al thread principal del avance de la ejecución del proceso de descargar...
        ... las lecturas del servidor.
    */

    private void notificarProgreso(int valorActual, int cantidadTotal) {

        if (mNotificaciones != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    int porcentaje;
                    String mensajeProgreso;

                    if (cantidadTotal > 0)
                        porcentaje = (valorActual * 100) / cantidadTotal;
                    else
                        porcentaje = 0;

                    mensajeProgreso = valorActual + " " + mContext.getString(R.string.de) + " " + cantidadTotal
                            + " " + mContext.getString(R.string.registros) + "\n"
                            + String.valueOf(porcentaje) + "%";

                    mNotificaciones.enProgreso(mensajeProgreso, porcentaje);
                }
            });
        }
    }

    /*
        notificarError().
        Función para notificar al thread principal de un error.
    */

    private void notificarError(String mensaje, String detalleError) {
        if (mNotificaciones != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mNotificaciones.enError(mensaje, detalleError);
                }
            });
        }
    }

    private void notificarFinalizado(){
        ResumenEntity resumen;

        resumen = DbLecturasMgr.getInstance().getResumen(mContext);
        mNotificaciones.enFinalizado();
    }


    /*
        openDatabase().
        Función para abrir la base de datos.
    */
    private void openDatabase() {
        if (mDbHelper == null)
            mDbHelper = new DBHelper(mContext);

        if (mDb == null)
            mDb = mDbHelper.getReadableDatabase();
    }

    /*
        closeDatabase().
        Función para cerrar la base de datos.
    */
    private void closeDatabase() {
        if (mDb != null)
            if (mDb.isOpen()) {
                mDb.close();
                mDbHelper.close();
            }
    }

    /*
        procesarLecturas().

        Este método recibe un string con el listado de lecturas. El string está separado en renglones por un saldo de línea. Cada renglón está separado...
         ... en columnas por un pipe (|). Registra en la base de datos las lecturas recibidas y los parámetros.
         Los renglones que empiezan con L son las lecturas.
         Los renglones que empiezan con P con los parametros.
         Los renglones que empiezan con # son el catálogo de anomalías.
     */

    private void procesarLecturas(String contenido) throws Exception {
        Vector<String> vLecturas;
        String[] lineas;
        int i = 0;
        int secuenciaReal = 0;
        long idArchivo;
        int cantRegistros = 0;
        String mensajeProgreso;
        boolean resultado = false;

        openDatabase();

        vLecturas = new Vector<String>();

        lineas = contenido.split("\\r?\\n", -1);

        try {
            borrarRuta(mDb);

            mDb.beginTransaction();

            cantRegistros = lineas.length;

            for (String ls_linea : lineas) {
                if (i != 0 && ls_linea.startsWith("L")) {
                    secuenciaReal++;
                    idArchivo = mGlobales.tlc.strToBD(mDb, ls_linea, secuenciaReal);// Esta

                    if (idArchivo == 0)    // Si es cero significa que el renglón no trae el mínimo de columnas
                        throw new AppException(mContext.getString(R.string.msj_trans_file_doesnt_match));
//                    else
//                        mArchivosLectMgr.agregarArchivoLect(idArchivo);

                    // clase
                    // ahora
                    // guarda
                    // new Lectura(context,
                    // ls_linea.getBytes("ISO-8859-1"), db);
                }
                else if (i != 0 && ls_linea.startsWith("P")) {
                    actualizarParametros(ls_linea);
                }
                else if (ls_linea.startsWith("#")) {// Esto indica que
                    // es una
                    // anomalia
                    new Anomalia(mContext, ls_linea.getBytes("ISO-8859-1"), mDb);
                } else if (ls_linea.startsWith("!")) { // un usuario
                    new Usuario(mContext, ls_linea.getBytes("ISO-8859-1"), mDb);
                } else if (mGlobales.tdlg.esUnRegistroRaro(ls_linea) && i != 0) {
                    mGlobales.tdlg.agregarRegistroRaro(mDb, ls_linea);
                } else if (i == 0) {
                    // la primera
                    ContentValues cv = new ContentValues();
                    cv.put("registro", ls_linea.getBytes());

                    mDb.insert("encabezado", null, cv);
                }

                i++;
                notificarProgreso(i, cantRegistros);
            }

            notificarProgreso(i, cantRegistros);

            mGlobales.tdlg.AgregarAnomaliasManualmente(mDb);

            mGlobales.tdlg.accionesDespuesDeCargarArchivo(mDb);

            mDb.setTransactionSuccessful();

            mDb.endTransaction();

            closeDatabase();

            resultado = true;

            notificarMensaje("Descarga finalizada");
        } catch (AppException e1) {
            mDb.execSQL("delete from Lecturas ");
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            closeDatabase();
            throw new AppException(e1.getMessage());
        } catch (Exception e2) {
            mDb.execSQL("delete from Lecturas ");
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            closeDatabase();
            throw e2;
        }
    }

    /*
        borrarRuta().

        Este método borra el contenido de varias tablas para que puedan recibir la información que descarga.
     */
    private static void borrarRuta(SQLiteDatabase db) {
        db.execSQL("delete from ruta ");
        db.execSQL("delete from fotos ");
        db.execSQL("delete from Anomalia ");
        db.execSQL("delete from encabezado ");
        db.execSQL("delete from NoRegistrados ");
        db.execSQL("delete from usuarios ");
    }

    /*
        actualizarParametros().

        Este método actualiza la tabla que contiene la información de los parámetros.
    */
    private void actualizarParametros(String linea) {
        DbConfigMgr.getInstance().actualizarParametros(mDb, linea);
    }
}
