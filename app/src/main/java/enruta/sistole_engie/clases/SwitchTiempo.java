package enruta.sistole_engie.clases;

import java.util.Calendar;
import java.util.Date;

public class SwitchTiempo {
    private boolean mActivo = false;
    private Date mTiempo = null;

    public void activarPor(int segundos)
    {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, segundos);    // Agregar a la hora actual n segundos
        mActivo = true;                             // Activar el switch
        mTiempo = calendar.getTime();
    }

    public void desactivar()
    {
        mActivo = false;
        mTiempo = null;
    }

    public boolean EsActivo()
    {
        Date horaActual;

        if (!mActivo || mTiempo == null) {
            mActivo = false;
            return false;
        }

        horaActual = Calendar.getInstance().getTime();

        if (horaActual.after(mTiempo))
        {
            mActivo = false;
            return false;
        }
        else
            return true;
    }
}
