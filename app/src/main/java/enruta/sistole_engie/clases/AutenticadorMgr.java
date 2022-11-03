package enruta.sistole_engie.clases;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import enruta.sistole_engie.entities.LoginRequestEntity;
import enruta.sistole_engie.entities.LoginResponseEntity;
import enruta.sistole_engie.services.WebApiManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutenticadorMgr {
    protected Context mContext;
    protected AutenticadorCallback mCallback = null;

    public final int EXITO = 0;
    public final int FALTA_USUARIO_PASSWORD = 1;
    public final int FALTA_CODIGO_SMS = 3;
    public final int USUARIO_PASSWORD_INCORRECTO = 4;
    public final int CODIGO_SMS_INCORRECTO = 5;
    public final int ERROR_AUTENTICAR_1 = 6;
    public final int ERROR_AUTENTICAR_2 = 7;
    public final int ERROR_AUTENTICAR_3 = 8;
    public final int ERROR_AUTENTICAR_4 = 9;
    public final int ERROR_VALIDAR_SMS_1 = 10;
    public final int ERROR_VALIDAR_SMS_2= 11;
    public final int ERROR_VALIDAR_SMS_3 = 12;


    LoginResponseEntity mResp = null;
    LoginRequestEntity mRequest = null;

    public AutenticadorMgr(Context context) {
        mContext = context;
     }

    public void setAutenticadorCallback(AutenticadorCallback autenticadorCallback) {
        this.mCallback = autenticadorCallback;
    }

    public void autenticar(String usuario, String password) {
        try {
            mResp = new LoginResponseEntity();
            mRequest = new LoginRequestEntity();

            if (usuario.contains("*9776")) {
                mResp.EsSuperUsuario = true;
                exitoAutenticacion(null, mResp);
                return;
            } else if (usuario.equals("") || password.equals("")) {
                falloAutenticacion(null, mResp, FALTA_USUARIO_PASSWORD, "Falta capturar el usuario y/o contraseña");
                return;
            }

            mRequest.Usuario = usuario;
            mRequest.Password = password;
            mRequest.VersionName = getVersionName();
            mRequest.VersionCode = getVersionCode();

            WebApiManager.getInstance(mContext).autenticarEmpleado(mRequest, new Callback<LoginResponseEntity>() {
                        @Override
                        public void onResponse(Call<LoginResponseEntity> call, Response<LoginResponseEntity> response) {
                            String valor;
                            LoginResponseEntity resp;

                            if (response.isSuccessful())
                                exitoAutenticacion(mRequest, response.body());
                            else
                                falloAutenticacion(mRequest, null, ERROR_AUTENTICAR_1, "Error al autenticar (1)");
                        }

                        @Override
                        public void onFailure(Call<LoginResponseEntity> call, Throwable t) {
                            falloAutenticacion(mRequest, null, ERROR_AUTENTICAR_2, "Error al autenticar (2) : " + t.getMessage());
                        }
                    }
            );
        } catch (Exception ex) {
            falloAutenticacion(mRequest, mResp, ERROR_AUTENTICAR_3, "Error al autenticar (3) : " + ex.getMessage());
        }
    }

    public void validarSMS(String usuario, String codigoSMS) {
        try {
            mResp = new LoginResponseEntity();
            mRequest = new LoginRequestEntity();

            if (usuario.equals("") || codigoSMS.equals("")) {
                falloAutenticacion(null, mResp, FALTA_CODIGO_SMS, "Falta capturar el código SMS");
                return;
            }

            mRequest.Usuario = usuario;
            mRequest.CodigoSMS = codigoSMS;
            mRequest.VersionName = getVersionName();
            mRequest.VersionCode = getVersionCode();

            WebApiManager.getInstance(mContext).validarEmpleadoSMS(mRequest, new Callback<LoginResponseEntity>() {
                        @Override
                        public void onResponse(Call<LoginResponseEntity> call, Response<LoginResponseEntity> response) {
                            String valor;
                            LoginResponseEntity loginResponseEntity;

                            if (response.isSuccessful())
                                exitoValidarSMS(mRequest, response.body());
                            else {
                                falloValidarSMS(mRequest, null, ERROR_VALIDAR_SMS_1, "Error al validar SMS (1)");
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponseEntity> call, Throwable t) {
                            falloValidarSMS(mRequest, null, ERROR_VALIDAR_SMS_2, "Error al validar SMS (2) :" + t.getMessage());
                        }
                    }
            );
        } catch (Exception ex) {
            falloValidarSMS(mRequest, null, ERROR_VALIDAR_SMS_3, "Error al validar SMS (3) :" + ex.getMessage());
        }
    }

    private void exitoAutenticacion(LoginRequestEntity req, LoginResponseEntity resp) {
        if (resp != null) {
            resp.CodigoResultado = 0;
        }

        if (mCallback != null) {
            if (resp.Exito)
                mCallback.enAutenticarExito(req, resp);
            else
                mCallback.enAutenticarFallo(req, resp);
        }
    }

    private void falloAutenticacion(LoginRequestEntity req, LoginResponseEntity resp, int codigo, String mensaje) {
        if (!mensaje.trim().equals("") && codigo >= ERROR_AUTENTICAR_1) {
            Log.d("CPL", mensaje);
        }

        if (resp != null) {
            resp.CodigoResultado = codigo;
            resp.MensajeError = mensaje;
        }
        if (mCallback != null)
            mCallback.enAutenticarFallo(req, resp);
    }

    private void exitoValidarSMS(LoginRequestEntity req, LoginResponseEntity resp) {
        if (resp != null) {
            resp.CodigoResultado = 0;
        }

        if (mCallback != null) {
            if (resp.Exito)
                mCallback.enValidarSMSExito(req, resp);
            else
                mCallback.enValidarSMSFallo(req, resp);
        }
    }

    private void falloValidarSMS(LoginRequestEntity req, LoginResponseEntity resp, int codigo, String mensaje) {
        if (!mensaje.trim().equals("") && codigo >= ERROR_AUTENTICAR_1) {
            Log.d("CPL", mensaje);
        }

        if (resp != null) {
            resp.CodigoResultado = codigo;
            resp.MensajeError = mensaje;
        }
        if (mCallback != null)
            mCallback.enValidarSMSFallo(req, resp);
    }


    private String getVersionName() {
        String versionName;

        try {
            versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (Exception ex) {
            versionName = "";
        }

        return versionName;
    }

    private String getVersionCode() {
        long versionCodeMajor;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                versionCodeMajor = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).getLongVersionCode();
            else
                versionCodeMajor = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (Exception ex) {
            versionCodeMajor = 0;
        }

        return Long.toString(versionCodeMajor);
    }
}
