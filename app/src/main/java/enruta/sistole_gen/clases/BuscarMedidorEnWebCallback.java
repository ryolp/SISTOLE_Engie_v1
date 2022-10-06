package enruta.sistole_gen.clases;

import enruta.sistole_gen.entities.BuscarMedidorRequest;
import enruta.sistole_gen.entities.BuscarMedidorResponse;

public interface BuscarMedidorEnWebCallback {
    public void enExito(BuscarMedidorResponse resp);
    public void enFallo(BuscarMedidorRequest req, BuscarMedidorResponse resp, int codigo, String mensaje);
}
