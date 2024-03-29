package enruta.sistole_engie.clases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Vector;

import enruta.sistole_engie.DBHelper;
import enruta.sistole_engie.Globales;
import enruta.sistole_engie.Lectura;
import enruta.sistole_engie.TodasLasLecturas;
import enruta.sistole_engie.entities.ArchivosLectRequest;
import enruta.sistole_engie.entities.ArchivosLectResponse;
import enruta.sistole_engie.services.WebApiManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
    DescargarLecturasMgr().

    Clase que agrupa el proceso que descarga las lecturas del servidor.
*/
public class DescargarLecturasMgr extends BaseMgr {
    protected TodasLasLecturas mTll;
    protected Vector<Lectura> mLecturas;
    protected String mQuery;
    private DescargarLecturasCallBack mCallback = null;
    private ArchivosLectRequest mRequest;

    protected final int EXITO = 0;
    protected final int FALTA = 1;
    protected final int ERROR_ENVIAR_1 = 2;
    protected final int ERROR_ENVIAR_2 = 3;
    protected final int ERROR_ENVIAR_3 = 4;
    protected final int ERROR_ENVIAR_4 = 5;

    public interface DescargarLecturasCallBack {
        public void enExitoComunicacion(ArchivosLectRequest request, ArchivosLectResponse resp);
        public void enFalloComunicacion(ArchivosLectRequest request, ArchivosLectResponse resp, int numError,
                                        String mensajeError, String detalleError);
    }

    public void setCallback(DescargarLecturasCallBack callback)
    {
        mCallback = callback;
    }

    public DescargarLecturasMgr(Context context, Globales globales) {
        mContext = context;
        mGlobales = globales;
    }

    public void descargarArchivo(String nombreArchivo) {
        mRequest = new ArchivosLectRequest();
        mRequest.idEmpleado = mGlobales.getIdEmpleado();
        mRequest.NombreArchivo = nombreArchivo;
        mRequest.FechaOperacion = Utils.getDateTime();

            try {
            WebApiManager.getInstance(mContext).descargarArchivo(mRequest, new Callback<ArchivosLectResponse>() {
                        @Override
                        public void onResponse(Call<ArchivosLectResponse> call, Response<ArchivosLectResponse> response) {
                            String valor;
                            ArchivosLectResponse resp;

                            if (response.isSuccessful()) {
                                resp = response.body();
                                exito(mRequest, resp);
                            } else
                                fallo(mRequest, null, ERROR_ENVIAR_2, "No hay conexión a internet (1). Intente nuevamente.", null);
                        }

                        @Override
                        public void onFailure(Call<ArchivosLectResponse> call, Throwable t) {
                            fallo(mRequest, null, ERROR_ENVIAR_3, "No hay conexión a internet (2). Intente nuevamente.", t);
                        }
                    }
            );
        } catch (Exception ex) {
            fallo(mRequest, null, ERROR_ENVIAR_4, "No hay conexión a internet (3). Intente nuevamente.", ex);
        }
    }

    private void exito(ArchivosLectRequest req, ArchivosLectResponse resp) {
        if (resp != null) {
            resp.NumError = 0;
        }

        // Forzar el check de seguridad cada vez que se descargue un archivo.
        // Esto se debe a que si el usuario hace el check de seguridad, toma su foto ...
        // ... y esta no se ha transmitido y luego se valida como Administrador...
        ///... y baja una unidad, la foto se pierde y no se transmite.

        if (resp.Exito) {
            if (mGlobales != null)
                if (mGlobales.sesionEntity != null)
                    if (mGlobales.sesionEntity.empleado != null)
                        mGlobales.sesionEntity.empleado.RequiereCheckSeguridad = true;

        }

        if (mCallback != null)
            mCallback.enExitoComunicacion(req, resp);
    }

    private void fallo(ArchivosLectRequest req, ArchivosLectResponse resp, int numError, String mensajeError, Throwable t) {
        String msg = "";

        if (!mensajeError.trim().equals("") && numError >= ERROR_ENVIAR_1) {
            if (t != null)
                msg = t.getMessage();

            Log.d("CPL", mensajeError + " : " + msg);
        }

        if (resp != null) {
            resp.NumError = numError;
            resp.MensajeError = mensajeError;
        }
        if (mCallback != null) {
            if (t != null)
                msg = t.getMessage();

            mCallback.enFalloComunicacion(req, resp, numError, mensajeError, msg);
        }
    }
}
