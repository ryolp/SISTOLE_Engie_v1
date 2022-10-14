package enruta.sistole_engie.clases;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import enruta.sistole_engie.Globales;
import enruta.sistole_engie.entities.ArchivosLectRequest;
import enruta.sistole_engie.entities.ArchivosLectResponse;
import enruta.sistole_engie.entities.ResumenEntity;
import enruta.sistole_engie.services.DbLecturasMgr;
import enruta.sistole_engie.services.WebApiManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArchivosLectMgr {
    private final int EXITO = 0;
    private final int FALTA = 1;
    private final int ERROR_ENVIAR_1 = 2;
    private final int ERROR_ENVIAR_2 = 3;
    private final int ERROR_ENVIAR_3 = 4;
    private final int ERROR_ENVIAR_4 = 5;

    private Context mContext;
    private ArchivosLectCallback mCallback = null;
    private ArchivosLectRequest mRequest;
    private Globales mGlobales;
    private ArrayList<Long> mListadoArchivosLect = new ArrayList<Long>();
    private long mIdArchivo = 0;
    private ResumenEntity mResumen;


    public ArchivosLectMgr(Context context, Globales globales) {
        mContext = context;
        mGlobales = globales;
    }

    public void setCallback(ArchivosLectCallback callback) {
        this.mCallback = callback;
    }

    public void marcarArchivosDescargados(){
        if (mListadoArchivosLect.size() == 0)
        {
            if (mCallback != null)
                mCallback.enSinArchivos();
        }
        else {
            mIdArchivo = mListadoArchivosLect.get(0);
            mListadoArchivosLect.remove(0);
            marcarArchivoDescargado(mIdArchivo);
        }
    }
    public void marcarArchivosTerminados()
    {
        mResumen = DbLecturasMgr.getInstance().getResumen(mContext);

        if (mResumen.totalRegistros >= 0 && mResumen.cantLecturasPendientes == 0) {
            mListadoArchivosLect = DbLecturasMgr.getInstance().getIdsArchivo(mContext);

            if (mListadoArchivosLect != null) {
                if (mListadoArchivosLect.size() != 0) {
                    mIdArchivo = mListadoArchivosLect.get(0);
                    mListadoArchivosLect.remove(0);
                    marcarArchivoTerminado(mIdArchivo);
                }
            }
        }
    }

    public void marcarArchivoDescargado(long idArchivo) {
        mRequest = new ArchivosLectRequest();
        mRequest.idEmpleado = mGlobales.getIdEmpleado();
        mRequest.idArchivo = idArchivo;

        try {
            WebApiManager.getInstance(mContext).marcarArchivoDescargado(mRequest, new Callback<ArchivosLectResponse>() {
                        @Override
                        public void onResponse(Call<ArchivosLectResponse> call, Response<ArchivosLectResponse> response) {
                            String valor;
                            ArchivosLectResponse resp;

                            if (response.isSuccessful()) {
                                resp = response.body();
                                exito(mRequest, resp);
                            } else
                                fallo(mRequest, null, ERROR_ENVIAR_2, "Error al marcar archivo descargado (2). Intente nuevamente.");
                        }

                        @Override
                        public void onFailure(Call<ArchivosLectResponse> call, Throwable t) {
                            fallo(mRequest, null, ERROR_ENVIAR_3, "Error al marcar archivo descargado (3). Intente nuevamente.");
                        }
                    }
            );
        } catch (Exception ex) {
            fallo(mRequest, null, ERROR_ENVIAR_4, "Error al marcar archivo descargado (4). Intente nuevamente.");
        }
    }

    public void marcarArchivoTerminado(long idArchivo) {
        mRequest = new ArchivosLectRequest();
        mRequest.idEmpleado = mGlobales.getIdEmpleado();
        mRequest.idArchivo = idArchivo;

        try {
            WebApiManager.getInstance(mContext).marcarArchivoTerminado(mRequest, new Callback<ArchivosLectResponse>() {
                        @Override
                        public void onResponse(Call<ArchivosLectResponse> call, Response<ArchivosLectResponse> response) {
                            String valor;
                            ArchivosLectResponse resp;

                            if (response.isSuccessful()) {
                                resp = response.body();
                                if (resp.Exito) {
                                    exitoTerminado(mRequest, resp);
                                } else
                                    falloTerminado(mRequest, null, ERROR_ENVIAR_1, "Error al marcar archivo descargado (1). Intente nuevamente.");
                            } else
                                falloTerminado(mRequest, null, ERROR_ENVIAR_2, "Error al marcar archivo descargado (2). Intente nuevamente.");
                        }

                        @Override
                        public void onFailure(Call<ArchivosLectResponse> call, Throwable t) {
                            falloTerminado(mRequest, null, ERROR_ENVIAR_3, "Error al marcar archivo descargado (3). Intente nuevamente.");
                        }
                    }
            );
        } catch (Exception ex) {
            falloTerminado(mRequest, null, ERROR_ENVIAR_4, "Error al marcar archivo descargado (4). Intente nuevamente.");
        }
    }

    private void exito(ArchivosLectRequest req, ArchivosLectResponse resp) {
        if (resp != null) {
            resp.NumError = 0;
        }

        if (mListadoArchivosLect.size() == 0){
            if (mCallback != null)
                mCallback.enExitoComunicacion(req, resp);
        }
        else
        {
            mIdArchivo =mListadoArchivosLect.get(0);
            mListadoArchivosLect.remove(0);
            marcarArchivoDescargado(mIdArchivo);
        }
    }

    private void fallo(ArchivosLectRequest req, ArchivosLectResponse resp, int numError, String mensajeError) {
        if (!mensajeError.trim().equals("") && numError >= ERROR_ENVIAR_1) {
            Log.d("CPL", mensajeError);
        }

        if (resp != null) {
            resp.NumError = numError;
            resp.MensajeError = mensajeError;
        }
        if (mCallback != null)
            mCallback.enFalloComunicacion(req, resp, numError, mensajeError);
    }

    private void exitoTerminado(ArchivosLectRequest req, ArchivosLectResponse resp) {
        if (resp != null) {
            resp.NumError = 0;
        }

        if (resp.Exito) {
            if (mListadoArchivosLect.size() == 0) {
                if (mCallback != null)
                    mCallback.enExitoComunicacion(req, resp);
            } else {
                mIdArchivo = mListadoArchivosLect.get(0);
                mListadoArchivosLect.remove(0);
                marcarArchivoTerminado(mIdArchivo);
            }
        }
        else
        {
            if (mCallback != null)
                mCallback.enExitoComunicacion(req, resp);
        }
    }

    private void falloTerminado(ArchivosLectRequest req, ArchivosLectResponse resp, int numError, String mensajeError) {
        if (!mensajeError.trim().equals("") && numError >= ERROR_ENVIAR_1) {
            Log.d("CPL", mensajeError);
        }

        if (resp != null) {
            resp.NumError = numError;
            resp.MensajeError = mensajeError;
        }
        if (mCallback != null)
            mCallback.enFalloComunicacion(req, resp, numError, mensajeError);
    }

    public void inicializarListaArchivosLect(){
        mListadoArchivosLect.clear();
    }

    public void agregarArchivoLect(long idArchivo)
    {
        if (!mListadoArchivosLect.contains(idArchivo))
            mListadoArchivosLect.add(idArchivo);
    }


}
