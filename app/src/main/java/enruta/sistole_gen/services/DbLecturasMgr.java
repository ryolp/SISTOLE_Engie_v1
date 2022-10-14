package enruta.sistole_gen.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

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
            resumen.totalRegistros = c.getLong(c.getColumnIndex("canti"));

            c = db.rawQuery("Select count(*) canti from ruta where tipoLectura='0'", null);
            c.moveToFirst();
            resumen.cantLecturasRealizadas = c.getLong(c.getColumnIndex("canti"));

            c = db.rawQuery("Select count(*) canti from ruta where trim(tipoLectura)=''", null);
            c.moveToFirst();
            resumen.cantLecturasPendientes = c.getLong(c.getColumnIndex("canti"));

            return resumen;
        } catch (Exception e) {
            throw e;
        } finally {
            closeDatabase();
        }
    }

    public long getCantidadPendientes(Context context)
    {
        try {
            ResumenEntity resumen = new ResumenEntity();
            Cursor c;
            long n;

            openDatabase(context);

            c = db.rawQuery("Select count(*) canti from ruta where trim(tipoLectura)=''", null);
            c.moveToFirst();
            n = c.getLong(c.getColumnIndex("canti"));

            return n;
        } catch (Exception e) {
            throw e;
        } finally {
            closeDatabase();
        }
    }

    public String getUnidad(Context context)
    {
        try {
            ResumenEntity resumen = new ResumenEntity();
            Cursor c;
            String s="";

            openDatabase(context);

            c = db.rawQuery("SELECT  sectorCorto FROM ruta LIMIT 1", null);
            if (c.moveToFirst())
                s = c.getString(c.getColumnIndex("sectorCorto"));

            return s;
        } catch (Exception e) {
            return "";
        } finally {
            closeDatabase();
        }
    }

    public ArrayList<Long> getIdsArchivo(Context context)
    {
        try {
            ResumenEntity resumen = new ResumenEntity();
            ArrayList<Long> lista = new ArrayList<Long>();
            Cursor c;
            long n;

            openDatabase(context);

            c = db.rawQuery("Select idArchivo from ruta GROUP BY idArchivo", null);

            while(c.moveToNext()) {
                n = c.getLong(c.getColumnIndex("idArchivo"));
                lista.add(n);
            }

            // Pendiente obtener todos los Id Archivo para luego usarlos para cerrar cuando termine la ruta.

            return lista;
        } catch (Exception e) {
            throw e;
        } finally {
            closeDatabase();
        }
    }
}
