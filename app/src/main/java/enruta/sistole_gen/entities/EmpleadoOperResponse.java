package enruta.sistole_gen.entities;

import java.util.Date;

public class EmpleadoOperResponse {
    public long idEmpleado = 0;
    public int idEmpleadoEstatusOper;
    public String EmpleadoEstatusOper  = "";
    public Date FechaEstatus = null;
    public Boolean Exito =false;
    public String Mensaje = "";
    public String MensajeError = "";
}
