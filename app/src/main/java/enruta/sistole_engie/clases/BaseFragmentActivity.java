package enruta.sistole_engie.clases;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import enruta.sistole_engie.Globales;
import enruta.sistole_engie.TomaDeLecturasGenerica;
import enruta.sistole_engie.TransmisionesPadre;
import enruta.sistole_engie.TransmitionObject;
import enruta.sistole_engie.services.DbConfigMgr;
import enruta.sistole_engie.services.WebApiManager;
import enruta.sistole_engie.trasmisionDatos;

public class BaseFragmentActivity extends FragmentActivity {
    public Globales globales;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        globales = ((Globales) getApplicationContext());
    }

    protected WebApiManager getWebApiManager() throws Exception {
        try {
            String servidor = "";

            if (servidor.trim().equals(""))
                servidor = DbConfigMgr.getInstance().getServidor(this);

            if (servidor.trim().equals(""))
                servidor = globales.defaultServidorGPRS;

            return WebApiManager.getInstance(this);
        } catch (Exception ex) {
            throw ex;
        }
    }

    protected Date getDateTime() {
        Calendar calendar = Calendar.getInstance();

        return calendar.getTime();
    }

    protected void showMessageLong(String sMessage) {
        Toast.makeText(getApplicationContext(), sMessage, Toast.LENGTH_LONG).show();
    }

    protected void showMessageShort(String sMessage) {
        Toast.makeText(getApplicationContext(), sMessage, Toast.LENGTH_SHORT).show();
    }
}
