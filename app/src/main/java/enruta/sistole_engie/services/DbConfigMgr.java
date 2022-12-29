package enruta.sistole_engie.services;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import enruta.sistole_engie.DBHelper;

public class DbConfigMgr extends  DbBaseMgr {
    private static DbConfigMgr config;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private DbConfigMgr() {

    }

    public static DbConfigMgr getInstance(){
        config = new DbConfigMgr();
        return config;
    }

    private void openDatabase(Context context) {
        dbHelper = new DBHelper(context);

        db = dbHelper.getReadableDatabase();
    }

    private void closeDatabase() {
        db.close();
        dbHelper.close();
    }

    public String getServidor(Context context){
        String servidor="";

        try {
            openDatabase(context);

            Cursor c = db.rawQuery("Select value from config where key='server_gprs'", null);
            c.moveToFirst();

            if (c.getCount() == 0)
                return "";

            servidor = getString(c,"value", "");

        } catch (Exception e) {
            String error;
            servidor = "";
            error = e.getMessage();
        }
        finally {
            closeDatabase();
            return servidor;
        }
    }

    public String getArchivo(Context context){
        String archivo="";

        try {
            openDatabase(context);

            Cursor c = db.rawQuery("Select value from config where key='cpl'", null);
            c.moveToFirst();

            if (c.getCount() == 0)
                return "";

            archivo = getString(c,"value", "");

        } catch (Exception e) {
            String error;
            archivo = "";
            error = e.getMessage();
        }
        finally {
            closeDatabase();
            return archivo;
        }
    }
}
