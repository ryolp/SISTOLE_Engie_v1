package enruta.sistole_gen.clases;

import enruta.sistole_gen.entities.OperacionRequest;
import enruta.sistole_gen.entities.OperacionResponse;

public interface EmergenciaCallback {
    public void enExito(OperacionResponse resp, boolean emergenciaConfirmada);
    public void enFallo(OperacionResponse resp);
}
