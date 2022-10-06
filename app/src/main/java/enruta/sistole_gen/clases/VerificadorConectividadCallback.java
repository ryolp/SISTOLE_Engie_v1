package enruta.sistole_gen.clases;

import enruta.sistole_gen.entities.BuscarMedidorRequest;
import enruta.sistole_gen.entities.BuscarMedidorResponse;

public interface VerificadorConectividadCallback {
    public void enExito(boolean exitoConexion, boolean exitoSesion);
    public void enFallo(int numError, String mensaje);
}
