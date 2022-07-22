package enruta.sistole_gen.entities;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class UsuarioEntity {
    public String Usuario;
    public String Email;
    public boolean EsLecturista;
    public boolean EsAdministrador;
    public boolean EsSuperUsuario;
    public String NumCPL;
    public boolean AutenticarConSMS;
    public Date HoraFinSesion;

    public UsuarioEntity(){

    }

    public UsuarioEntity(LoginResponseEntity loginResponseEntity){
        this.Usuario = loginResponseEntity.Usuario;
        this.Email = loginResponseEntity.Email;
        this.EsLecturista = loginResponseEntity.EsLecturista;
        this.EsAdministrador = loginResponseEntity.EsAdministrador;
        this.EsSuperUsuario = loginResponseEntity.EsSuperUsuario;
        this.NumCPL = loginResponseEntity.NumCPL;
        this.AutenticarConSMS = loginResponseEntity.AutenticarConSMS;
        inicializarHoraVencimiento();
    }

    public void inicializarHoraVencimiento(){
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.HOUR, 4);
        HoraFinSesion = calendar.getTime();
    }

    public boolean esSesionVencida(){
        Date horaActual;

        horaActual = Calendar.getInstance().getTime();

        if (horaActual.after(this.HoraFinSesion))
            return true;
        else
            return false;
    }

}
