package enruta.sistole_gen.clases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Vector;

import enruta.sistole_gen.DBHelper;
import enruta.sistole_gen.Globales;
import enruta.sistole_gen.Lectura;
import enruta.sistole_gen.TodasLasLecturas;
import enruta.sistole_gen.entities.BuscarMedidorRequest;
import enruta.sistole_gen.entities.BuscarMedidorResponse;
import enruta.sistole_gen.entities.LoginResponseEntity;
import enruta.sistole_gen.entities.SupervisorLogRequest;
import enruta.sistole_gen.entities.SupervisorLogResponse;
import enruta.sistole_gen.services.WebApiManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscarMedidorMgr {
    protected Context mContext;
    protected TodasLasLecturas mTll;
    protected Vector<Lectura> mLecturas;
    protected BuscarMedidorCallback mCallback;
    protected BuscarMedidorEnWebCallback mCallbackEnWeb;
    protected int mSecuencia;
    protected DBHelper mDbHelper;
    protected SQLiteDatabase mDb;
    protected String mQuery;
    protected BuscarMedidorRequest mRequest;
    protected BuscarMedidorResponse mResp;

    public final int EXITO = 0;
    public final int FALTA = 1;
    public final int ERROR_ENVIAR_1 = 2;
    public final int ERROR_ENVIAR_2 = 3;
    public final int ERROR_ENVIAR_3 = 4;
    public final int ERROR_ENVIAR_4 = 5;

    public BuscarMedidorMgr(Context context) {
        mContext = context;
    }

    public void setOnBuscarMedidorListener(BuscarMedidorCallback callback) {
        mCallback = callback;
    }

    public void setOnBuscarMedidorListener(BuscarMedidorEnWebCallback callback) {
        mCallbackEnWeb = callback;
    }

    public void buscarMedidorLocal(String codigo) {
        Cursor c = null;
        int secuencia;
        String codigoAux;

        try {
            codigoAux = "'%" + codigo + "%'";
            mQuery = "Select min(cast(secuenciaReal as integer)) secuencia from ruta ";
            mQuery += "where tipoLectura='' and (serieMedidor like '%" + codigo + "%' OR CodigoBarras like '%" + codigo + "%')";

            openDatabase();
            // c = mDb.rawQuery(mQuery, new String[]{codigoAux, codigoAux});
            c = mDb.rawQuery(mQuery, null);

            if (c.moveToFirst())
                secuencia = c.getInt(c.getColumnIndex("secuencia"));
            else
                secuencia = -1;
            if (mCallback != null)
                mCallback.enExito(codigo, secuencia);
        } catch (Exception e) {
            mCallback.enFallo(codigo, e.getMessage());
        }
    }

//    public void buscarMedidor(String codigo) {
//        Thread busqueda = new Thread() {
//            public void run() {
//
//                Cursor c = null;
//                int secuencia;
//
//                mQuery = "Select min(cast(secuenciaReal as integer)) secuencia from ruta ";
//                mQuery += "where tipoLectura='4' and serieMedidor like '%?%' OR CodigoBarras like '%?%'";
//
//                openDatabase();
//                c = mDb.rawQuery(mQuery, new String[]{mCodigo, mCodigo});
//
//                if (c.moveToFirst())
//                    secuencia = c.getInt(c.getColumnIndex("secuencia"));
//                else
//                    secuencia = -1;
//            }
//        };
//        busqueda.start();
//    }

    private void openDatabase() {
        mDbHelper = new DBHelper(mContext);
        mDb = mDbHelper.getReadableDatabase();
    }

    private void closeDatabase() {
        mDb.close();
        mDbHelper.close();
    }

    public void buscarMedidorEnWeb(Globales globales, String serieMedidor, String codigoBarrasMedidor)
    {
        mRequest = new BuscarMedidorRequest();
        mRequest.SerieMedidor = serieMedidor;
        mRequest.CodigoBarras = codigoBarrasMedidor;
        mRequest.FechaOperacion = Utils.getDateTime();
        mRequest.idEmpleado = globales.getIdEmpleado();
        mRequest.Token = globales.getSesionToken();

        if (globales.tll != null) {
            mRequest.Unidad = globales.tll.getLecturaActual().getUnidad();
            mRequest.idArchivo = globales.tll.getLecturaActual().getIdArchivo();
        }

        if (globales.location!=null)
        {
            mRequest.LatitudGPS = String.valueOf(globales.location.getLatitude());
            mRequest.LongitudGPS = String.valueOf(globales.location.getLongitude());
        }

        try {
            WebApiManager.getInstance(mContext).buscarMedidor(mRequest,
                    new Callback<BuscarMedidorResponse>() {
                        @Override
                        public void onResponse(Call<BuscarMedidorResponse> call, Response<BuscarMedidorResponse> response) {
                            String valor;
                            LoginResponseEntity resp;

                            if (response.isSuccessful())
                                exito(mRequest, response.body());
                            else
                                fallo(mRequest, null, ERROR_ENVIAR_1, "Error al enviar informe (1)");
                        }

                        @Override
                        public void onFailure(Call<BuscarMedidorResponse> call, Throwable t) {
                            fallo(mRequest, null, ERROR_ENVIAR_2, "Error al enviar informe (2) : " + t.getMessage());
                        }
                    }
            );
        } catch (Exception ex) {
            fallo(mRequest, mResp, ERROR_ENVIAR_3, "Error al enviar informe (3) : " + ex.getMessage());
        }
    }

    private void exito(BuscarMedidorRequest req, BuscarMedidorResponse resp) {
        if (resp != null) {
            resp.NumError = 0;
        }

        if (mCallbackEnWeb != null)
            mCallbackEnWeb.enExito(resp);
    }

    private void fallo(BuscarMedidorRequest req, BuscarMedidorResponse resp, int codigo, String mensaje) {
        if (!mensaje.trim().equals("") && codigo >= ERROR_ENVIAR_1) {
            Log.d("CPL", mensaje);
        }

        if (resp != null) {
            resp.NumError = codigo;
            resp.MensajeError = mensaje;
        }
        if (mCallbackEnWeb != null)
            mCallbackEnWeb.enFallo(req, resp, codigo, mensaje);
    }

}
