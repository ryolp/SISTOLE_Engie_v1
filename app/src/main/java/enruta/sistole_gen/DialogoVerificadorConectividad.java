package enruta.sistole_gen;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

import enruta.sistole_gen.clases.Utils;
import enruta.sistole_gen.clases.VerificadorConectividadCallback;
import enruta.sistole_gen.clases.VerificadorConectividadMgr;

public class DialogoVerificadorConectividad {
    private TextView mTxtConectividad;
    private TextView mTxtSistoleDisponible;
    private TextView mTxtSesion;
    private Activity mActivity;
    private Button mBtnAceptar;
    private Dialog mDialogo;
    protected View.OnClickListener mOnClickListener = null;
    private Globales mGlobales;
    private boolean mSesionIniciada;
    private VerificadorConectividadMgr mVerificadorConectividadMgr = null;

    public DialogoVerificadorConectividad(Activity activity, Globales globales) {
        mActivity = activity;
        mGlobales = globales;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void verificarConectividad()
    {
        Utils.showMessageLong(mActivity, "Verificando conectividad");

        if (mVerificadorConectividadMgr == null) {
            mVerificadorConectividadMgr = new VerificadorConectividadMgr(mActivity, mGlobales);

            mVerificadorConectividadMgr.setOnCallback(new VerificadorConectividadCallback() {
                @Override
                public void enExito(boolean exitoConexion, boolean exitoSesion) {
                    mostrarEstatusConectividad(exitoConexion, exitoSesion);
                }

                @Override
                public void enFallo(int numError, String mensaje) {
                    Utils.showMessageLong(mActivity, "Hubo un error al verificar la conectividad : " + mensaje);
                    mostrarEstatusConectividad(false, false);
                }
            });
        }

        mVerificadorConectividadMgr.verificarConexion();
    }

    private void mostrarEstatusConectividad(boolean exitoConexion, boolean exitoSesion) {
        if (mDialogo == null) {
//            AlertDialog.Builder builder = new Dialog(mActivity);

//            builder.setCancelable(false);
//            builder.setTitle("Medidor encontrado");
//            builder.setPositiveButton("Aceptar", mOnClickListener);

            mDialogo = new Dialog(mActivity);

            LayoutInflater inflater = mActivity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.verificador_conectividad_dialogo, null);

            mDialogo.setContentView(dialogView);
//            mDialogo = builder.create();

            mDialogo.setCancelable(false);
            mDialogo.setTitle("Estatus Conectividad");

            mTxtConectividad = (TextView) mDialogo.findViewById(R.id.txtConectividad);
            mTxtSistoleDisponible = (TextView) mDialogo.findViewById(R.id.txtSistoleDisponible);
            mTxtSesion = (TextView) mDialogo.findViewById(R.id.txtSesion);
            mBtnAceptar = (Button) mDialogo.findViewById(R.id.btnAceptar);

            mBtnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogo.cancel();
                    mOnClickListener.onClick(v);
                }
            });
        }


        mSesionIniciada = false;
        if (mGlobales != null) {
            if (mGlobales.sesionEntity != null)
                mSesionIniciada = true;
        }

        cambiarEstatusControl(mTxtConectividad,  (Utils.isNetworkAvailable(mActivity)) ? 1 : 2);
        cambiarEstatusControl(mTxtSistoleDisponible, exitoConexion ? 1:2);
        cambiarEstatusControl(mTxtSesion, exitoSesion ? 1:2);

        mDialogo.show();
    }

    private void cambiarEstatusControl(TextView txt, int estatus) {
        switch (estatus) {
            case 1:     // Estatus OK
                txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkmark_green, 0, 0, 0);
                break;
            case 2:     // Estatus No OK
                txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_cancel_24, 0, 0, 0);
                break;
            default:     // Estatus Indefinido
                txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_update_disabled_24, 0, 0, 0);
                break;
        }
    }
}
