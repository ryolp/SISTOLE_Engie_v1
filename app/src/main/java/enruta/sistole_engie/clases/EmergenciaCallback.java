package enruta.sistole_engie.clases;

import enruta.sistole_engie.entities.OperacionResponse;

public interface EmergenciaCallback {
    public void enExito(OperacionResponse resp, int solicitudEmergencia);
    public void enFallo(OperacionResponse resp);
}
