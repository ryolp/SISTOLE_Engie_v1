package enruta.sistole_gen.entities;

public class LoginResponseEntity {
    public String Usuario;
    public String Email;
    public String Token;
    public String Mensaje;
    public String MensajeError;
    public String MensajeLecturista;
    public int CodigoResultado;
    public boolean Exito;
    public boolean Error;
    public boolean SesionOk;
    public boolean EsLecturista;
    public boolean EsAdministrador;
    public boolean EsSuperUsuario;
    public boolean AutenticarConSMS;
    public String NumCPL;
    public String VersionWeb;
    public EmpleadoCplEntity Empleado;

//    public LoginResponseEntity(String Usuario, String Email, String Token, String Mensaje, boolean Exito, boolean Error,
//                               boolean EsLecturista, boolean EsAdministrador, boolean EsSuperUsuario, boolean AutenticarConSMS,
//                               String NumCPL, EmpleadoCplEntity empleado) {
//        this.Usuario = Usuario;
//        this.Email = Email;
//        this.Token = Token;
//        this.Mensaje = Mensaje;
//        this.Exito = Exito;
//        this.Error = Error;
//        this.EsLecturista = EsLecturista;
//        this.EsAdministrador = EsAdministrador;
//        this.EsSuperUsuario = EsSuperUsuario;
//        this.AutenticarConSMS = AutenticarConSMS;
//        this.NumCPL = NumCPL;
//        this.Empleado = empleado;
//    }
}
