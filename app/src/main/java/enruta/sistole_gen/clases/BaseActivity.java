package enruta.sistole_gen.clases;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import enruta.sistole_gen.DBHelper;
import enruta.sistole_gen.Globales;
import enruta.sistole_gen.TomaDeLecturasGenerica;
import enruta.sistole_gen.TransmisionesPadre;
import enruta.sistole_gen.TransmitionObject;
import enruta.sistole_gen.entities.SesionEntity;
import enruta.sistole_gen.services.DbConfigMgr;
import enruta.sistole_gen.services.WebApiManager;
import enruta.sistole_gen.trasmisionDatos;

public abstract class BaseActivity extends Activity {
    protected DBHelper dbHelper;
    protected SQLiteDatabase db;
    protected Globales globales  = null;

    protected Date getDateTime() {
        Calendar calendar = Calendar.getInstance();

        return calendar.getTime();
    }

    protected void showMessageLong(String mensaje) {
        Utils.showMessageShort(this, mensaje);
    }

    protected void showMessageShort(String mensaje) {
        Utils.showMessageLong(this, mensaje);
    }

    public void logMessageLong(String msg, Throwable t) {
        Utils.logMessageShort(this, msg, t);
    }

    public void logMessageShort(Context context, String msg, Throwable t) {
        Utils.logMessageLong(this, msg, t);
    }


    protected SesionEntity getSesion() {
        if (globales == null)
            return null;

        return globales.sesionEntity;
    }

    protected void openDatabase() {
        dbHelper = new DBHelper(this);

        db = dbHelper.getReadableDatabase();
    }

    protected void closeDatabase() {
        db.close();
        dbHelper.close();

    }

}
