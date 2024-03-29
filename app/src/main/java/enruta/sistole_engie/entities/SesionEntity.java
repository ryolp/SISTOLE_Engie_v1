package enruta.sistole_engie.entities;

import android.graphics.Bitmap;

import java.util.Calendar;
import java.util.Date;

public class SesionEntity {
    public String Usuario = "";
    public String Email = "";
    public boolean EsLecturista = false;
    public boolean EsAdministrador = false;
    public boolean EsSuperUsuario = false;
    public String NumCPL = "";
    public boolean AutenticarConSMS = false;
    public Date HoraFinSesion;
    public String VersionWeb;
    public Bitmap fotoEmpleado = null;
    public EmpleadoCplEntity empleado;
    public Boolean Autenticado = false;
    public String Unidad;
    public String Token;
    public long idArchivoUnidad;
    public String MensajeLecturista = "";
    public boolean hacerSincronizacion = false;
    public ParametrosCplEntity Parametros;

    public SesionEntity() {

    }

    public SesionEntity(LoginResponseEntity loginResponseEntity) {
        this.Usuario = loginResponseEntity.Usuario;
        this.Email = loginResponseEntity.Email;
        this.EsLecturista = loginResponseEntity.Empleado.EsLecturista;
        this.EsAdministrador = loginResponseEntity.Empleado.EsAdministrador;
        this.EsSuperUsuario = loginResponseEntity.Empleado.EsSuperUsuario;
        this.NumCPL = loginResponseEntity.NumCPL;
        this.AutenticarConSMS = loginResponseEntity.AutenticarConSMS;
        this.VersionWeb = loginResponseEntity.VersionWeb;
        this.empleado = loginResponseEntity.Empleado;
        this.Token = loginResponseEntity.Token;
        this.MensajeLecturista = loginResponseEntity.MensajeLecturista;
        this.Parametros = loginResponseEntity.Parametros;

        inicializarHoraVencimiento();
    }

    public void inicializarHoraVencimiento() {
        Calendar calendar = Calendar.getInstance();

        if (Parametros != null)
            calendar.add(Calendar.MINUTE, Parametros.MinutosSesionApp);
        else
            calendar.add(Calendar.HOUR, 8);

        HoraFinSesion = calendar.getTime();
    }

    public boolean esSesionVencida() {
        Date horaActual;

        if (!this.Autenticado)
            return true;

        horaActual = Calendar.getInstance().getTime();

        if (horaActual.after(this.HoraFinSesion))
            return true;
        else
            return false;
    }

}
