package enruta.sistole_gen.entities;

public class UsuarioEntity {
    public String Usuario;
    public String Email;
    public boolean EsLecturista;
    public boolean EsAdministrador;
    public boolean EsSuperUsuario;
    public String NumCPL;
    public boolean AutenticarConSMS;

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
    }

}
