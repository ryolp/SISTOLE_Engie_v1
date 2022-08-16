package enruta.sistole_gen.entities;

public class LoginRequestEntity {
    public String Usuario;
    public String Email;
    public String Password;
    public String CodigoSMS;
    public String VersionName;
    public String VersionCode;

    public LoginRequestEntity(String Usuario, String Email, String Password, String CodigoSMS, String VersionName, String VersionCode){
        this.Usuario = Usuario;
        this.Email = Email;
        this.Password = Password;
        this.CodigoSMS = CodigoSMS;
        this.VersionName = VersionName;
        this.VersionCode = VersionCode;
    }
}
