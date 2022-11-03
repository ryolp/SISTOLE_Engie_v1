package enruta.sistole_engie.clases;

import enruta.sistole_engie.entities.ArchivosLectRequest;
import enruta.sistole_engie.entities.ArchivosLectResponse;

public interface ArchivosLectCallback {
    public void enExitoComunicacion(ArchivosLectRequest request, ArchivosLectResponse resp);
    public void enFalloComunicacion(ArchivosLectRequest request, ArchivosLectResponse resp, int numError, String mensajeError);
    public void enSinArchivos();
}
