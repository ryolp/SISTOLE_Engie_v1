package enruta.sistole_gen.entities;

public class LoginResponse {
    public String Usuario;
    public String Email;
    public String Token;
    public String Mensaje;
    public boolean Exito;
    public boolean Error;
    public boolean EsLecturista;
    public boolean EsAdministrador;
    public boolean EsSuperUsuario;
    public boolean AutenticarConSMS;

    public LoginResponse(String Usuario, String Email, String Token, String Mensaje, boolean Exito, boolean Error,
                         boolean EsLecturista, boolean EsAdministrador, boolean EsSuperUsuario, boolean AutenticarConSMS) {
        this.Usuario = Usuario;
        this.Email = Email;
        this.Token = Token;
        this.Mensaje = Mensaje;
        this.Exito = Exito;
        this.Error = Error;
        this.EsLecturista = EsLecturista;
        this.EsAdministrador = EsAdministrador;
        this.EsSuperUsuario = EsSuperUsuario;
        this.AutenticarConSMS = AutenticarConSMS;
    }
}
