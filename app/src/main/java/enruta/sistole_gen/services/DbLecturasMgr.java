package enruta.sistole_gen.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import enruta.sistole_gen.DBHelper;
import enruta.sistole_gen.entities.ResumenEntity;

public class DbLecturasMgr {
    private static DbLecturasMgr lecturasMgr;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private DbLecturasMgr() {

    }

    public static DbLecturasMgr getInstance(){
        lecturasMgr = new DbLecturasMgr();
        return lecturasMgr;
    }

    private void openDatabase(Context context) {
        dbHelper = new DBHelper(context);

        db = dbHelper.getReadableDatabase();
    }

    private void closeDatabase() {
        db.close();
        dbHelper.close();
    }

    public ResumenEntity getResumen(Context context){
        try {
            ResumenEntity resumen = new ResumenEntity();
            Cursor c;

            openDatabase(context);

            c = db.rawQuery("Select count(*) canti from Ruta", null);
            c.moveToFirst();
            resumen.TotalRegistros = c.getLong(c.getColumnIndex("canti"));

            c = db.rawQuery("Select count(*) canti from ruta where tipoLectura='0'", null);
            c.moveToFirst();
            resumen.Realizados = c.getLong(c.getColumnIndex("canti"));

            return resumen;
        } catch (Exception e) {
            throw e;
        } finally {
            closeDatabase();
        }
    }
}
