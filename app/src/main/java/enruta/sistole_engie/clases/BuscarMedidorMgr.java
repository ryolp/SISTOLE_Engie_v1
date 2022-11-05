package enruta.sistole_engie.clases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Vector;

import enruta.sistole_engie.DBHelper;
import enruta.sistole_engie.Globales;
import enruta.sistole_engie.Lectura;
import enruta.sistole_engie.TodasLasLecturas;
import enruta.sistole_engie.entities.BuscarMedidorRequest;
import enruta.sistole_engie.entities.BuscarMedidorResponse;
import enruta.sistole_engie.entities.LoginResponseEntity;
import enruta.sistole_engie.services.WebApiManager;
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
        int nCol;

        try {
            codigoAux = "'%" + codigo + "%'";
            mQuery = "Select min(cast(secuenciaReal as integer)) secuencia from ruta ";
            mQuery += "where tipoLectura='' and (serieMedidor like '%" + codigo + "%' OR CodigoBarras like '%" + codigo + "%')";

            openDatabase();
            // c = mDb.rawQuery(mQuery, new String[]{codigoAux, codigoAux});
            c = mDb.rawQuery(mQuery, null);

            if (c.moveToFirst())
                secuencia = DBHelper.getInt(c, "secuencia", -1);
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
                                fallo(mRequest, null, ERROR_ENVIAR_1, "No hay conexión a internet. Intente nuevamente (1)", null);
                        }

                        @Override
                        public void onFailure(Call<BuscarMedidorResponse> call, Throwable t) {
                            fallo(mRequest, null, ERROR_ENVIAR_2, "No hay conexión a internet. Intente nuevamente (2). ", t);
                        }
                    }
            );
        } catch (Exception ex) {
            fallo(mRequest, mResp, ERROR_ENVIAR_3, "No hay conexión a internet. Intente nuevamente (3)", ex);
        }
    }

    private void exito(BuscarMedidorRequest req, BuscarMedidorResponse resp) {
        if (resp != null) {
            resp.NumError = 0;
        }

        if (mCallbackEnWeb != null)
            mCallbackEnWeb.enExito(resp);
    }

    private void fallo(BuscarMedidorRequest req, BuscarMedidorResponse resp, int codigo, String mensajeError, Throwable t) {
        String msg = "";

        if (!mensajeError.trim().equals("") && codigo >= ERROR_ENVIAR_1) {
            if (t != null)
                msg = t.getMessage();

            Log.d("CPL", mensajeError + " : " + msg);
        }

        if (resp != null) {
            resp.NumError = codigo;
            resp.MensajeError = mensajeError;
        }
        if (mCallbackEnWeb != null)
            mCallbackEnWeb.enFallo(req, resp, codigo, mensajeError);
    }

}
