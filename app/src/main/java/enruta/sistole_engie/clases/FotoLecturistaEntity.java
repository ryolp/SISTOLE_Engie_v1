package enruta.sistole_engie.clases;

import android.media.Image;
import java.util.Date;

public class FotoLecturistaEntity {
    public long idEmpleado;
    public Date FechaCreacion;
    public Image Foto;

    public FotoLecturistaEntity()
    {
        FechaCreacion = Utils.getDateTime();
    }
}
