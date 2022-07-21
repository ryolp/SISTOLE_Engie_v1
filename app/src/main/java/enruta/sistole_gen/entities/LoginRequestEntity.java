package enruta.sistole_gen.entities;

public class LoginRequestEntity {
    public String Usuario;
    public String Email;
    public String Password;
    public String CodigoSMS;

    public LoginRequestEntity(String Usuario, String Email, String Password, String CodigoSMS){
        this.Usuario = Usuario;
        this.Email = Email;
        this.Password = Password;
        this.CodigoSMS = CodigoSMS;
    }
}
