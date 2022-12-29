package enruta.sistole_engie.clases;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import enruta.sistole_engie.Globales;
import enruta.sistole_engie.entities.ArchivosLectRequest;
import enruta.sistole_engie.entities.ArchivosLectResponse;
import enruta.sistole_engie.services.DbConfigMgr;

public class DescargarLecturasProcesoMgr extends BaseMgr {
    protected DescargarLecturasProceso descargarLecturasProceso = null;
    protected DescargarLecturasMgr mDescargarMgr = null;
    protected DescargarLecturasProcesoMgr.EnCallback mCallBack = null;

    public DescargarLecturasProcesoMgr(Context context, Globales globales) {
        mContext = context;
        mGlobales = globales;
    }

    public void setEnCallback(DescargarLecturasProcesoMgr.EnCallback callback) {
        mCallBack = callback;
    }

    public interface EnCallback {
        public void enExito();

        public void enFallo(String mensajeError);

        public void enError(int numError, String mensajeError, String detalleError);

        public void enMensaje(String mensaje);

        public void enProgreso(String progreso, int porcentaje);
    }

    public void descargarLecturas() {
        String archivo;

        notificarMensaje("Solicitando lecturas...");

        archivo = DbConfigMgr.getInstance().getArchivo(mContext);

        if (mDescargarMgr == null) {
            mDescargarMgr = new DescargarLecturasMgr(mContext, mGlobales);

            mDescargarMgr.setCallback(new DescargarLecturasMgr.DescargarLecturasCallBack() {
                @Override
                public void enExitoComunicacion(ArchivosLectRequest request, ArchivosLectResponse resp) {
                    exito(resp);
                }

                @Override
                public void enFalloComunicacion(ArchivosLectRequest request, ArchivosLectResponse resp, int numError,
                                                String mensajeError, String detalleError) {
                    falloComunicacion(numError, mensajeError, detalleError);
                }
            });

            mDescargarMgr.descargarArchivo(archivo);
        }
    }

    private void exito(ArchivosLectResponse resp) {
        if (resp.Exito) {
            notificarMensaje("Procesando información recibida...");
            procesarLecturas(resp.Contenido);
        } else if (mCallBack != null) {
            mCallBack.enFallo(resp.Mensaje);
        }
    }

    private void falloComunicacion(int numError, String mensajeError, String detalleError) {
        if (mCallBack != null)
            mCallBack.enError(numError, mensajeError, detalleError);
    }

    private void notificarMensaje(String mensaje) {
        if (mCallBack != null)
            mCallBack.enMensaje(mensaje);
    }


    private void procesarLecturas(String contenido) {
        ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        descargarLecturasProceso = new DescargarLecturasProceso(mContext, mGlobales, handler, contenido);

        descargarLecturasProceso.setEnNotificaciones(new DescargarLecturasProceso.EnNotificaciones() {
            @Override
            public void enMensaje(String mensaje) {
                notificarMensaje(mensaje);
            }

            @Override
            public void enProgreso(String progreso, int porcentaje) {
                if (mCallBack != null)
                    mCallBack.enProgreso(progreso, porcentaje);
            }

            @Override
            public void enError(String mensajeError, String detalleError) {
                if (mCallBack != null)
                    mCallBack.enError(1, mensajeError, detalleError);
            }

            @Override
            public void enFinalizado() {
                if (mCallBack != null)
                    mCallBack.enExito();
            }
        });

        backgroundExecutor.execute(descargarLecturasProceso);
    }

    public boolean puedoCargar() {
        boolean puedo = false;
        openDatabase();

        Cursor c = mDb.rawQuery("Select descargada from encabezado", null);

        c.moveToFirst();

        if (c.getCount() > 0) {
            if (getInt(c, "descargada", 0) == 0) {
                //Si no ha sido descargada, habrá que verificar si ya hay una lectura ingresada
                c.close();
                c = mDb.rawQuery("Select count(*) canti from Ruta where trim(tipoLectura)<>''"
                        + "order by cast(secuencia as Integer) asc limit 1", null);
                c.moveToFirst();
                puedo = getInt(c, "canti", 0) == 0;
            } else
                puedo = true;
        } else
            puedo = true;

        c.close();
        closeDatabase();

        return puedo;
    }

    public void finalize() {
        closeDatabase();
    }
}
