package enruta.sistole_gen.clases;

import enruta.sistole_gen.entities.LoginRequestEntity;
import enruta.sistole_gen.entities.LoginResponseEntity;

public interface AutenticadorCallback {
    public void enAutenticarExito(LoginRequestEntity request, LoginResponseEntity resp);
    public void enAutenticarFallo(LoginRequestEntity request, LoginResponseEntity resp);
    public void enValidarSMSExito(LoginRequestEntity request, LoginResponseEntity resp);
    public void enValidarSMSFallo(LoginRequestEntity request, LoginResponseEntity resp);

}
