package enruta.sistole_engie.clases;

import android.content.Context;
import android.util.Log;

import enruta.sistole_engie.Globales;
import enruta.sistole_engie.entities.LoginRequestEntity;
import enruta.sistole_engie.entities.LoginResponseEntity;
import enruta.sistole_engie.services.WebApiManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificadorConectividadMgr {
    protected Context mContext;
    protected VerificadorConectividadCallback mCallback = null;

    protected final int EXITO = 0;
    protected final int FALTA = 1;
    protected final int ERROR_ENVIAR_1 = 2;
    protected final int ERROR_ENVIAR_2 = 3;
    protected final int ERROR_ENVIAR_3 = 4;
    protected final int ERROR_ENVIAR_4 = 5;
    protected Globales mGlobales = null;

    public VerificadorConectividadMgr(Context context, Globales globales)
    {
        mContext = context;
        mGlobales = globales;
    }

    public void setOnCallback(VerificadorConectividadCallback callback) {
        mCallback = callback;
    }

    public void verificarConexion() {
        LoginRequestEntity req;
        LoginResponseEntity resp;

        try {
            req = new LoginRequestEntity();
            req.idEmpleado = mGlobales.getIdEmpleado();
            req.Usuario = mGlobales.sesionEntity.Usuario;
            req.Token = mGlobales.getSesionToken();

            WebApiManager.getInstance(mContext).verificarConexion(req, new Callback<LoginResponseEntity>() {
                        @Override
                        public void onResponse(Call<LoginResponseEntity> call, Response<LoginResponseEntity> response) {
                            String valor;
                            LoginResponseEntity resp;

                            if (response.isSuccessful()) {
                                resp = response.body();
                                if (resp.Exito) {
                                    exito(true, resp.SesionOk);
                                } else {
                                    fallo(ERROR_ENVIAR_1, "Error al verificar conexión (1)");
                                }
                            } else
                                fallo(ERROR_ENVIAR_2, "Error al verificar conexión (2)");

                        }

                        @Override
                        public void onFailure(Call<LoginResponseEntity> call, Throwable t) {
                            fallo(ERROR_ENVIAR_3, "Error al verificar conexión (3)");
                        }
                    }
            );
        } catch (Exception ex) {
            fallo(ERROR_ENVIAR_4, "Error al verificar conexión (4)");
        }
    }
    private void exito(boolean exitoConectividad, boolean exitoSesion){
        if (mCallback != null)
            mCallback.enExito(exitoConectividad, exitoSesion);
    }

    private void fallo(int numError, String mensaje) {
        if (!mensaje.trim().equals("") && numError >= ERROR_ENVIAR_1) {
            Log.d("CPL", mensaje);
        }

        if (mCallback != null)
            mCallback.enFallo(numError, mensaje);
    }
}
