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

/*
    DescargarLecturasProcesoMgr().

    Clase que utiliza las clases DescargarLecturasMgr y DescargarLecturasProceso para descargar las lecturas
    ... del servidor, registrarlas en la base de datos y notificar el thread principal de los eventos durante
    ... el proceso para que el thread principal notifique el usuario mediante la interface gráfica del
    ... avance del proceso o de los eventos que ocurren.

    Primero se descargan las lecturas utilizando la clase DescargarLecturasMgr.
    Cuando se reciban las lecturas se procesan con la clase DescargarLecturasProceso.
*/

public class DescargarLecturasProcesoMgr extends BaseMgr {
    protected DescargarLecturasProceso descargarLecturasProceso = null;
    protected DescargarLecturasMgr mDescargarMgr = null;
    protected DescargarLecturasProcesoMgr.EnCallback mCallBack = null;

    /*
        DescargarLecturasProcesoMgr().

        Constructor de la clase.
    */

    public DescargarLecturasProcesoMgr(Context context, Globales globales) {
        mContext = context;
        mGlobales = globales;
    }

    /*
        setEnCallback().

        Metodo para guardar una referencia al método del proceso que llama a la instancia de esta clase,
        el cual recibirá las notificaciones de este proceso.
    */

    public void setEnCallback(DescargarLecturasProcesoMgr.EnCallback callback) {
        mCallBack = callback;
    }

    /*
        EnCallback.

        Interface para el envío de las notificaciones al proceso que llama a la instancia de esta clase.
    */

    public interface EnCallback {
        public void enExito();

        public void enFallo(String mensajeError);

        public void enError(int numError, String mensajeError, String detalleError);

        public void enMensaje(String mensaje);

        public void enProgreso(String progreso, int porcentaje);
    }

    /*
        descargarLecturas().

        Método principal para iniciar el proceso para descargar las lecturas del servidor, registrarlas en la
        base de datos del celular y notificar al thread principal de los eventos y avances.
    */

    public void descargarLecturas() {
        String archivo;

        notificarMensaje("Solicitando lecturas...");

        // Inicializar la clase que se encarga de descargar el archivo de lecturas...
        // ... de manera asíncrona.

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

    /*
        exito().
        Si la recepción de los datos fue exitosa, entonces el proceso continua con
        procesar la información recibida. Notificar al proceso que llamó esta intancia
        que se recibió la información.
     */
    private void exito(ArchivosLectResponse resp) {
        if (resp.Exito) {
            notificarMensaje("Procesando información recibida...");
            procesarLecturas(resp.Contenido);
        } else if (mCallBack != null) {
            mCallBack.enFallo(resp.Mensaje);
        }
    }

    /*
        falloComunicacion().
        Si la recepción de los datos tuvo un error, entonces notificar al proceso que llamó
        a esta instancia que hubo un fallo en la comunicación
    */
    private void falloComunicacion(int numError, String mensajeError, String detalleError) {
        if (mCallBack != null)
            mCallBack.enError(numError, mensajeError, detalleError);
    }

    /*
        Método que utiliza la interface definida para notificar al proceso que llamó a esta
        instancia.
    */
    private void notificarMensaje(String mensaje) {
        if (mCallBack != null)
            mCallBack.enMensaje(mensaje);
    }

    /*
        Método que se encarga de:
            Crear la instancia de la clase DescargarLecturasProceso
            Inicializar el thread que procesará las lecturas a través de DescargarLecturasProceso
            Inicializar los eventos que se reciben del thread para notificarlos al thread principal.
    */
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
