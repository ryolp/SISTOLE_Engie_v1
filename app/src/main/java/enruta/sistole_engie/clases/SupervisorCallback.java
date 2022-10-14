package enruta.sistole_engie.clases;

import enruta.sistole_engie.entities.SupervisorLogRequest;
import enruta.sistole_engie.entities.SupervisorLogResponse;

public interface SupervisorCallback {
    public void enExito(SupervisorLogRequest request, SupervisorLogResponse resp);
    public void enFallo(SupervisorLogRequest request, SupervisorLogResponse resp);
}
