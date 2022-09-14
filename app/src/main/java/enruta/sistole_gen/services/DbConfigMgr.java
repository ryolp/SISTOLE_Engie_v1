package enruta.sistole_gen.services;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import enruta.sistole_gen.DBHelper;

public class DbConfigMgr {
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

            servidor = c.getString(c.getColumnIndex("value"));

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
}
