package enruta.sistole_engie.clases;

import enruta.sistole_engie.entities.BuscarMedidorRequest;
import enruta.sistole_engie.entities.BuscarMedidorResponse;

public interface BuscarMedidorEnWebCallback {
    public void enExito(BuscarMedidorResponse resp);
    public void enFallo(BuscarMedidorRequest req, BuscarMedidorResponse resp, int codigo, String mensaje);
}
