package enruta.sistole_gen.clases;

import enruta.sistole_gen.entities.OperacionGenericaRequest;
import enruta.sistole_gen.entities.OperacionGenericaResponse;

public interface OperacionGenericaCallback {
    public void enExito(OperacionGenericaRequest request, OperacionGenericaResponse resp);
    public void enFallo(OperacionGenericaRequest request, OperacionGenericaResponse resp, int numError, String mensajeError);
}
