package enruta.sistole_gen.clases;

import enruta.sistole_gen.entities.ArchivosLectRequest;
import enruta.sistole_gen.entities.ArchivosLectResponse;
import enruta.sistole_gen.entities.SupervisorLogRequest;
import enruta.sistole_gen.entities.SupervisorLogResponse;

public interface ArchivosLectCallback {
    public void enExitoComunicacion(ArchivosLectRequest request, ArchivosLectResponse resp);
    public void enFalloComunicacion(ArchivosLectRequest request, ArchivosLectResponse resp, int numError, String mensajeError);
    public void enSinArchivos();
}
