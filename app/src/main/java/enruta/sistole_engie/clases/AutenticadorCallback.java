package enruta.sistole_engie.clases;

import enruta.sistole_engie.entities.LoginRequestEntity;
import enruta.sistole_engie.entities.LoginResponseEntity;

public interface AutenticadorCallback {
    public void enAutenticarExito(LoginRequestEntity request, LoginResponseEntity resp);
    public void enAutenticarFallo(LoginRequestEntity request, LoginResponseEntity resp);
    public void enValidarSMSExito(LoginRequestEntity request, LoginResponseEntity resp);
    public void enValidarSMSFallo(LoginRequestEntity request, LoginResponseEntity resp);

}
