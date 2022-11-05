package enruta.sistole_engie.clases;

import android.content.Context;
import android.util.Log;

import enruta.sistole_engie.Globales;
import enruta.sistole_engie.entities.LoginResponseEntity;
import enruta.sistole_engie.entities.OperacionGenericaRequest;
import enruta.sistole_engie.entities.OperacionGenericaResponse;
import enruta.sistole_engie.services.WebApiManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OperacionGenericaMgr {
    public final int EXITO = 0;
    public final int FALTA = 1;
    public final int ERROR_ENVIAR_1 = 2;
    public final int ERROR_ENVIAR_2 = 3;
    public final int ERROR_ENVIAR_3 = 4;
    public final int ERROR_ENVIAR_4 = 5;

    protected Context mContext;
    protected OperacionGenericaCallback mCallback = null;
    protected OperacionGenericaRequest mRequest = null;
    protected Globales mGlobales;

    public OperacionGenericaMgr(Context context, Globales globales) {
        mContext = context;
    }

    public void setSupervisorCallback(OperacionGenericaCallback callback) {
        this.mCallback = callback;
    }

    public void enviarOperacion(String param1, String param2, String param3) {
        mRequest = new OperacionGenericaRequest();
        mRequest.Param1 = param1;
        mRequest.Param2 = param2;
        mRequest.Param3 = param3;


        try {
            WebApiManager.getInstance(mContext).operacionGenerica(mRequest,
                    new Callback<OperacionGenericaResponse>() {
                        @Override
                        public void onResponse(Call<OperacionGenericaResponse> call, Response<OperacionGenericaResponse> response) {
                            String valor;
                            LoginResponseEntity resp;

                            if (response.isSuccessful())
                                exito(mRequest, response.body());
                            else
                                fallo(mRequest, null, ERROR_ENVIAR_1, "No hay conexión a internet. Intente nuevamente. (1)", null);
                        }

                        @Override
                        public void onFailure(Call<OperacionGenericaResponse> call, Throwable t) {
                            fallo(mRequest, null, ERROR_ENVIAR_2, "No hay conexión a internet. Intente nuevamente. (2)", t);
                        }
                    }
            );
        } catch (Exception ex) {
            fallo(mRequest, null, ERROR_ENVIAR_3, "No hay conexión a internet. Intente nuevamente. (3)", ex);
        }
    }

    private void exito(OperacionGenericaRequest req, OperacionGenericaResponse resp) {
        if (resp != null) {
            resp.NumError = 0;
        }

        if (mCallback != null)
            mCallback.enExito(req, resp);
    }

    private void fallo(OperacionGenericaRequest req, OperacionGenericaResponse resp, int numError, String mensajeError, Throwable t) {
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
        if (mCallback != null)
            mCallback.enFallo(req, resp, numError, mensajeError);
    }
}
