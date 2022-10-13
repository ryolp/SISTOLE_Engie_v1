package enruta.sistole_gen.clases;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import enruta.sistole_gen.TodasLasLecturas;
import enruta.sistole_gen.entities.LoginResponseEntity;
import enruta.sistole_gen.entities.SesionEntity;
import enruta.sistole_gen.entities.SupervisorLogEntity;
import enruta.sistole_gen.entities.SupervisorLogRequest;
import enruta.sistole_gen.entities.SupervisorLogResponse;
import enruta.sistole_gen.entities.ResumenEntity;
import enruta.sistole_gen.services.WebApiManager;
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
                                fallo(mRequest, null, ERROR_ENVIAR_1, "Error al enviar informe (1)");
                        }

                        @Override
                        public void onFailure(Call<SupervisorLogResponse> call, Throwable t) {
                            fallo(mRequest, null, ERROR_ENVIAR_2, "Error al enviar informe (2) : " + t.getMessage());
                        }
                    }
            );
        } catch (Exception ex) {
            fallo(mRequest, mResp, ERROR_ENVIAR_3, "Error al enviar informe (3) : " + ex.getMessage());
        }
    }

    private void exito(SupervisorLogRequest req, SupervisorLogResponse resp) {
        if (resp != null) {
            resp.NumError = 0;
        }

        if (mCallback != null)
            mCallback.enExito(req, resp);
    }

    private void fallo(SupervisorLogRequest req, SupervisorLogResponse resp, int codigo, String mensaje) {
        if (!mensaje.trim().equals("") && codigo >= ERROR_ENVIAR_1) {
            Log.d("CPL", mensaje);
        }

        if (resp != null) {
            resp.NumError = codigo;
            resp.MensajeError = mensaje;
        }
        if (mCallback != null)
            mCallback.enFallo(req, resp);
    }

}
