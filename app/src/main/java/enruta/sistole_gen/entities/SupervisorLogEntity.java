package enruta.sistole_gen.entities;

import java.util.Date;

public class SupervisorLogEntity {
    public long idSupervisorLog;
    public Date FechaRevision;
    public long idEmpleado;
    public long idEmpleadoSupervisor;
    public long idArchivo;
    public String Unidad;
    public long TotalLecturas;
    public long CantRealizadas;
    public long CantPendientes;
    public long CantFotos;
    public long CantLecturasPorEnviar;
    public long CantFotosPorEnviar;
    public String Latitud;
    public String Longitud;
}
