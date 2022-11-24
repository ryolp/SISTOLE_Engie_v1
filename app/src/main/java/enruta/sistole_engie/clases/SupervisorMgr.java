package enruta.sistole_engie.clases;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import enruta.sistole_engie.TodasLasLecturas;
import enruta.sistole_engie.entities.LoginResponseEntity;
import enruta.sistole_engie.entities.SesionEntity;
import enruta.sistole_engie.entities.SupervisorLogEntity;
import enruta.sistole_engie.entities.SupervisorLogRequest;
import enruta.sistole_engie.entities.SupervisorLogResponse;
import enruta.sistole_engie.entities.ResumenEntity;
import enruta.sistole_engie.services.WebApiManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupervisorMgr {
    protected Context mContext;
    protected SupervisorCallback mCallback = null;

    public final int EXITO = 0;
    public final int FALTA = 1;
    public final int ERROR_ENVIAR_1 = 2;
    public final int ERROR_ENVIAR_2 = 3;
    public final int ERROR_ENVIAR_3 = 4;
    public final int ERROR_ENVIAR_4 = 5;

    SupervisorLogResponse mResp = null;
    SupervisorLogRequest mRequest = null;

    public SupervisorMgr(Context context) {
        mContext = context;
    }

    public void setSupervisorCallback(SupervisorCallback callback) {
        this.mCallback = callback;
    }

    public void enviarInforme(SesionEntity sesion, TodasLasLecturas tll, Location location,
                              long idEmpleadoSupervisor, ResumenEntity resumen) throws Exception {
        mRequest = new SupervisorLogRequest();
        mResp = new SupervisorLogResponse();
        mRequest.supervisorLog = new SupervisorLogEntity();

        if (sesion == null)
            throw new Exception("Finalizó la sesión del usuario");

        if (tll == null)
            throw new Exception("Finalizó la sesión del usuario");

        mRequest.supervisorLog.idEmpleado = sesion.empleado.idEmpleado;
        mRequest.supervisorLog.idEmpleadoSupervisor = idEmpleadoSupervisor;
        mRequest.supervisorLog.Unidad = tll.getLecturaActual().getUnidad();
        mRequest.supervisorLog.idArchivo = tll.getLecturaActual().getIdArchivo();
        mRequest.supervisorLog.FechaRevision = Utils.getDateTime();
        mRequest.supervisorLog.TotalLecturas = resumen.totalRegistros;
        mRequest.supervisorLog.CantRealizadas = resumen.cantLecturasRealizadas;
        mRequest.supervisorLog.CantPendientes = resumen.cantLecturasPendientes;
        mRequest.supervisorLog.CantFotos = resumen.cantFotos;
        mRequest.supervisorLog.CantLecturasPorEnviar = 0;
        mRequest.supervisorLog.CantFotosPorEnviar = 0;

        if (location != null) {
            mRequest.supervisorLog.Longitud = String.valueOf(location.getLongitude());
            mRequest.supervisorLog.Latitud = String.valueOf(location.getLatitude());
        }


        try {
            WebApiManager.getInstance(mContext).registrarLogSupervisor(mRequest,
                    new Callback<SupervisorLogResponse>() {
                        @Override
                        public void onResponse(Call<SupervisorLogResponse> call, Response<SupervisorLogResponse> response) {
                            String valor;
                            LoginResponseEntity resp;

                            if (response.isSuccessful())
                                exito(mRequest, response.body());
                            else
                                fallo(mRequest, null, ERROR_ENVIAR_1, "No hay conexión a internet. Intente nuevamente. (1)", null);
                        }

                        @Override
                        public void onFailure(Call<SupervisorLogResponse> call, Throwable t) {
                            fallo(mRequest, null, ERROR_ENVIAR_2, "No hay conexión a internet. Intente nuevamente. (2)", t);
                        }
                    }
            );
        } catch (Exception ex) {
            fallo(mRequest, mResp, ERROR_ENVIAR_3, "No hay conexión a internet. Intente nuevamente. (3)", ex);
        }
    }

    private void exito(SupervisorLogRequest req, SupervisorLogResponse resp) {
        if (resp != null) {
            resp.NumError = 0;
        }

        if (mCallback != null)
            mCallback.enExito(req, resp);
    }

    private void fallo(SupervisorLogRequest req, SupervisorLogResponse resp, int codigo, String mensajeError, Throwable t) {
        String msg = "";

        if (!mensajeError.trim().equals("") && codigo >= ERROR_ENVIAR_1) {
            if (t != null)
                msg = t.getMessage();

            Log.d("CPL", mensajeError + " : " + msg);
        }

        if (resp == null)
            resp = new SupervisorLogResponse();

        resp.NumError = codigo;
        resp.MensajeError = mensajeError;

        if (mCallback != null)
            mCallback.enFallo(req, resp);
    }

}
