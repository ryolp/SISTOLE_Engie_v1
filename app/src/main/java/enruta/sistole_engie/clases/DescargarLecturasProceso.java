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
import enruta.sistole_engie.services.DbLecturasMgr;

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

    public interface EnNotificaciones {
        public void enMensaje(String mensaje);

        public void enProgreso(String progreso, int porcentaje);

        public void enError(String mensajeError, String detalleError);

        public void enFinalizado();
    }

    public DescargarLecturasProceso(Context context, Globales globales, Handler handler, String contenido) {
        mContext = context;
        mGlobales = globales;
        mHandler = handler;
        mContenido = contenido;
    }

    public void setEnNotificaciones(DescargarLecturasProceso.EnNotificaciones progreso) {
        mNotificaciones = progreso;
    }

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

                    mensajeProgreso = (valorActual + 1) + " " + mContext.getString(R.string.de) + " " + cantidadTotal
                            + " " + mContext.getString(R.string.registros) + "\n"
                            + String.valueOf(porcentaje) + "%";

                    mNotificaciones.enProgreso(mensajeProgreso, porcentaje);
                }
            });
        }
    }

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


    private void openDatabase() {
        if (mDbHelper == null)
            mDbHelper = new DBHelper(mContext);

        if (mDb == null)
            mDb = mDbHelper.getReadableDatabase();
    }

    private void closeDatabase() {
        if (mDb != null)
            if (mDb.isOpen()) {
                mDb.close();
                mDbHelper.close();
            }
    }

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

        lineas = contenido.split("\\r?\\n");

        try {
            borrarRuta(mDb);

            mDb.beginTransaction();

            cantRegistros = lineas.length;

            for (String ls_linea : lineas) {
                if (ls_linea.length() == 0)
                    throw new AppException("El archivo recibido tiene un formato incorrecto");

                if (i != 0 && !ls_linea.startsWith("#")
                        && !ls_linea.startsWith("!")
                        && !mGlobales.tdlg.esUnRegistroRaro(ls_linea)
                        && ls_linea.startsWith("L")) {
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
                } else if (ls_linea.startsWith("#")) {// Esto indica que
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

                notificarProgreso(i + 1, cantRegistros);

                i++;
            }

            notificarProgreso(i + 1, cantRegistros);

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

    private static void borrarRuta(SQLiteDatabase db) {
        db.execSQL("delete from ruta ");
        db.execSQL("delete from fotos ");
        db.execSQL("delete from Anomalia ");
        db.execSQL("delete from encabezado ");
        db.execSQL("delete from NoRegistrados ");
        db.execSQL("delete from usuarios ");
    }
}
