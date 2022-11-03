package enruta.sistole_engie.clases;

import enruta.sistole_engie.entities.OperacionGenericaRequest;
import enruta.sistole_engie.entities.OperacionGenericaResponse;

public interface OperacionGenericaCallback {
    public void enExito(OperacionGenericaRequest request, OperacionGenericaResponse resp);
    public void enFallo(OperacionGenericaRequest request, OperacionGenericaResponse resp, int numError, String mensajeError);
}
