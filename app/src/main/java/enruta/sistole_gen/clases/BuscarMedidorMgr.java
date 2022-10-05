package enruta.sistole_gen.clases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Vector;

import enruta.sistole_gen.BuscarMedidor;
import enruta.sistole_gen.BuscarMedidorGridAdapter;
import enruta.sistole_gen.BuscarMedidorTabsPagerAdapter;
import enruta.sistole_gen.DBHelper;
import enruta.sistole_gen.Lectura;
import enruta.sistole_gen.TodasLasLecturas;

public class BuscarMedidorMgr {
    protected Context mContext;
    protected TodasLasLecturas mTll;
    protected Vector<Lectura> mLecturas;
    protected BuscarMedidorCallback mCallback;
    protected int mSecuencia;
    protected DBHelper mDbHelper;
    protected SQLiteDatabase mDb;
    protected String mQuery;

    public BuscarMedidorMgr(Context context) {
        mContext = context;
    }

    public void setOnBuscarMedidorListener(BuscarMedidorCallback callback) {
        mCallback = callback;
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

}
