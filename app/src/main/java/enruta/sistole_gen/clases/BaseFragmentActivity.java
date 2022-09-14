package enruta.sistole_gen.clases;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import enruta.sistole_gen.Globales;
import enruta.sistole_gen.TomaDeLecturasGenerica;
import enruta.sistole_gen.TransmisionesPadre;
import enruta.sistole_gen.TransmitionObject;
import enruta.sistole_gen.services.DbConfigMgr;
import enruta.sistole_gen.services.WebApiManager;
import enruta.sistole_gen.trasmisionDatos;

public class BaseFragmentActivity extends FragmentActivity {
    public Globales globales;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        globales = ((Globales) getApplicationContext());
    }

    protected WebApiManager getWebApiManager() throws Exception {
        try {
            TransmitionObject to= new TransmitionObject();
            TomaDeLecturasGenerica tdlg;
            String servidor = "";

            tdlg =globales.tdlg;

            if (tdlg != null){
                if(!tdlg.getEstructuras( to, trasmisionDatos.TRANSMISION, TransmisionesPadre.WIFI).equals("")){
                    //throw new Exception("Error al leer configuración");
                    servidor = to.ls_servidor.trim();
                }
            }

            if (servidor.trim().equals(""))
                servidor = DbConfigMgr.getInstance().getServidor(this);

            if (servidor.trim().equals(""))
                servidor = globales.defaultServidorGPRS;


            return WebApiManager.getInstance(servidor);
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
