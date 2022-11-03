package enruta.sistole_engie.clases;

public interface BuscarMedidorCallback {
    public void enExito(String codigo, int secuencia);
    public void enFallo(String codigo, String mensajeError);
}
