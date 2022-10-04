package enruta.sistole_gen.clases;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import enruta.sistole_gen.TomaDeLecturas;
import enruta.sistole_gen.entities.OperacionRequest;
import enruta.sistole_gen.entities.OperacionResponse;
import enruta.sistole_gen.entities.SesionEntity;
import enruta.sistole_gen.entities.SupervisorLogRequest;
import enruta.sistole_gen.entities.SupervisorLogResponse;
import enruta.sistole_gen.services.WebApiManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergenciaMgr {
    protected Context mContext;
    protected EmergenciaCallback mCallback = null;

    public final int NO_ERROR = 0;
    public final int ERROR_WEB_API = 1;
    public final int ERROR_USUARIO_NO_AUTENTICADO = 2;
    public final int ERROR_ENVIAR_1 = 3;
    public final int ERROR_ENVIAR_2 = 4;
    public final int ERROR_ENVIAR_3 = 5;
    public final int ERROR_ENVIAR_4 = 6;

    private OperacionResponse mResp = null;
    private OperacionRequest mRequest = null;
    private boolean mEmergenciaConfirmada = false;

    public EmergenciaMgr(Context context) {
        mContext = context;
    }

    public void setEmergenciaCallback(EmergenciaCallback callback) {
        this.mCallback = callback;
    }

    public void enviarSolicitudEmergencia(SesionEntity sesionEntity, Location location, boolean emergenciaConfirmada) {
        OperacionRequest req;
        OperacionResponse resp;

        try {
            mResp = new OperacionResponse();
            mRequest= new OperacionRequest();

            mEmergenciaConfirmada = emergenciaConfirmada;

            req = new OperacionRequest();
            req.idEmpleado = sesionEntity.empleado.idEmpleado;
            req.FechaOperacion = Utils.getDateTime();
            req.ValorBoolean = emergenciaConfirmada;

            if (location != null) {
                req.LongitudGPS = String.valueOf(location.getLongitude());
                req.LatitudGPS =  String.valueOf(location.getLatitude());
            }

            WebApiManager.getInstance(mContext).solicitarAyuda(req, new Callback<OperacionResponse>() {
                        @Override
                        public void onResponse(Call<OperacionResponse> call, Response<OperacionResponse> response) {
                            String valor;
                            OperacionResponse resp;

                            if (response.isSuccessful()) {
                                resp = response.body();
                                if (resp.Exito) {
                                    exito(mResp);
                                } else {
                                    fallo(mResp, ERROR_ENVIAR_1, "Error al solicitar ayuda (1). Intente nuevamente");
                                }
                            } else
                                fallo(mResp, ERROR_ENVIAR_2, "Error al solicitar ayuda (2). Intente nuevamente");
                        }

                        @Override
                        public void onFailure(Call<OperacionResponse> call, Throwable t) {
                            fallo(mResp, ERROR_ENVIAR_3, "Error al solicitar ayuda (3). Intente nuevamente :" + t.getMessage());
                        }
                    }
            );
        } catch (Exception ex) {
            fallo(mResp, ERROR_ENVIAR_4, "Error al solicitar ayuda (4). Intente nuevamente :" + ex.getMessage());
        }
    }

    private void exito(OperacionResponse resp) {
        if (resp != null) {
            resp.NumError = 0;
        }

        if (mCallback != null)
            mCallback.enExito(resp, mEmergenciaConfirmada);
    }

    private void fallo(OperacionResponse resp, int codigo, String mensaje) {
        if (!mensaje.trim().equals("") && codigo >= ERROR_ENVIAR_1) {
            Log.d("CPL", mensaje);
        }

        if (resp != null) {
            resp.NumError = codigo;
            resp.MensajeError = mensaje;
        }
        if (mCallback != null)
            mCallback.enFallo(resp);
    }
}
