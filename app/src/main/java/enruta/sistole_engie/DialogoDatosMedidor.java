package enruta.sistole_engie;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import enruta.sistole_engie.entities.BuscarMedidorResponse;

public class DialogoDatosMedidor {
    protected TextView lblNumMedidor = null;
    protected TextView txtNumMedidor = null;
    protected TextView lblCodigoBarrasMedidor = null;
    protected TextView txtCodigoBarrasMedidor = null;
    protected TextView lblUnidad = null;
    protected TextView txtUnidad = null;
    protected TextView lblDireccion = null;
    protected TextView txtDireccion = null;
    protected TextView txtEsMedidorRobado = null;
    protected Button btnAceptar = null;
    protected Dialog mDialogoMostrarMedidor;
    protected Activity mActivity;
    protected View.OnClickListener mOnClickListener;

    public DialogoDatosMedidor(Activity activity)
    {
        mActivity = activity;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    protected void mostrarResultadoBusquedaWeb(BuscarMedidorResponse resp)
    {
        if (mDialogoMostrarMedidor == null) {
//            AlertDialog.Builder builder = new Dialog(mActivity);

//            builder.setCancelable(false);
//            builder.setTitle("Medidor encontrado");
//            builder.setPositiveButton("Aceptar", mOnClickListener);

            mDialogoMostrarMedidor = new Dialog(mActivity);

            LayoutInflater inflater = mActivity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.buscar_medidor_dialogo, null);

            mDialogoMostrarMedidor.setContentView(dialogView);
//            mDialogoMostrarMedidor = builder.create();

            mDialogoMostrarMedidor.setCancelable(false);
            mDialogoMostrarMedidor.setTitle("Medidor encontrado");

            lblNumMedidor = (TextView)mDialogoMostrarMedidor.findViewById(R.id.lblNumMedidor);
            txtNumMedidor = (TextView)mDialogoMostrarMedidor.findViewById(R.id.txtNumMedidor);
            lblCodigoBarrasMedidor = (TextView)mDialogoMostrarMedidor.findViewById(R.id.lblCodigoBarras);
            txtCodigoBarrasMedidor = (TextView)mDialogoMostrarMedidor.findViewById(R.id.txtCodigoBarras);

            lblUnidad = (TextView)mDialogoMostrarMedidor.findViewById(R.id.lblUnidad);
            txtUnidad = (TextView)mDialogoMostrarMedidor.findViewById(R.id.txtUnidad);
            lblDireccion = (TextView)mDialogoMostrarMedidor.findViewById(R.id.lblDireccion);
            txtDireccion = (TextView)mDialogoMostrarMedidor.findViewById(R.id.txtDireccion);
            txtEsMedidorRobado= (TextView)mDialogoMostrarMedidor.findViewById(R.id.lblEsMedidorRobado);
            btnAceptar= (Button) mDialogoMostrarMedidor.findViewById(R.id.btnAceptar);

            ;
            btnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogoMostrarMedidor.cancel();
                    mOnClickListener.onClick(v);
                }
            });
        }

        if (resp.EsMedidorRobado)
            txtEsMedidorRobado.setVisibility(View.VISIBLE);
        else
            txtEsMedidorRobado.setVisibility(View.GONE);

        if (resp.Cliente != null) {
            txtUnidad.setText(resp.Cliente.Unidad);
            txtDireccion.setText(resp.Cliente.DireccionCompleta);
            txtNumMedidor.setText(resp.Cliente.SerieMedidor);
            txtCodigoBarrasMedidor.setText(resp.Cliente.CodigoBarrasMedidor);
        }

        mDialogoMostrarMedidor.show();
    }
}
