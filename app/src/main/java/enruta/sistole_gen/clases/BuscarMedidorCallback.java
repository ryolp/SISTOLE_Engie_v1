package enruta.sistole_gen.clases;

import enruta.sistole_gen.entities.OperacionResponse;

public interface BuscarMedidorCallback {
    public void enExito(String codigo, int secuencia);
    public void enFallo(String codigo, String mensajeError);
}
