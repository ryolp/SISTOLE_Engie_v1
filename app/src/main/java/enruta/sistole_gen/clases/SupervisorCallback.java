package enruta.sistole_gen.clases;

import enruta.sistole_gen.entities.LoginRequestEntity;
import enruta.sistole_gen.entities.LoginResponseEntity;
import enruta.sistole_gen.entities.SupervisorLogRequest;
import enruta.sistole_gen.entities.SupervisorLogResponse;

public interface SupervisorCallback {
    public void enExito(SupervisorLogRequest request, SupervisorLogResponse resp);
    public void enFallo(SupervisorLogRequest request, SupervisorLogResponse resp);
}
