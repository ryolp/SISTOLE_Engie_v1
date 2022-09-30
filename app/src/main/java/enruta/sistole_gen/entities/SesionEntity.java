package enruta.sistole_gen.entities;

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
    public Bitmap fotoEmpleado=null;
    public EmpleadoCplEntity empleado;
    public int ConectividadOk = 0;
    public int SistoleDisponibleOk = 0;
    public int SesionOk = 0;
    public Date fechaHoraVerificacion;
    public Boolean Autenticado = false;    

    public SesionEntity(){

    }

    public SesionEntity(LoginResponseEntity loginResponseEntity) {
        this.Usuario = loginResponseEntity.Usuario;
        this.Email = loginResponseEntity.Email;
        this.EsLecturista = loginResponseEntity.EsLecturista;
        this.EsAdministrador = loginResponseEntity.EsAdministrador;
        this.EsSuperUsuario = loginResponseEntity.EsSuperUsuario;
        this.NumCPL = loginResponseEntity.NumCPL;
        this.AutenticarConSMS = loginResponseEntity.AutenticarConSMS;
        this.VersionWeb = loginResponseEntity.VersionWeb;
        this.empleado = loginResponseEntity.Empleado;

        inicializarHoraVencimiento();
    }

    public void inicializarHoraVencimiento(){
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.HOUR, 4);
        HoraFinSesion = calendar.getTime();
    }

    public boolean esSesionVencida(){
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
