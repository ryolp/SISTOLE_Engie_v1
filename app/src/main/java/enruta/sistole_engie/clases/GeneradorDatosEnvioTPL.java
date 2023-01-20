package enruta.sistole_engie.clases;

import android.database.Cursor;

public class GeneradorDatosEnvioTPL {
    private Cursor mCursor;

    public String generarInfoLectura(Cursor c) throws Exception  {
        String dato;
        String tipoRegistro = "LEC";

        if (c == null)
            return "";

        mCursor = c;

        dato = Utils.concatenarColumnas("|",
                tipoRegistro,
                getString("poliza"),
                getString("lectura"),
                getString("fecha"),
                getString("hora"),
                getString("anomalia"),
                getString("comentarios"),
                getString("lecturista"),
                getString("tipoLectura"),
                getString("latitud"),
                getString("longitud"),
                getString("nivelBateria"),
                getString("idEmpleado"),
                getString("idArchivo"),
                getString("idUnidadLect"),
                getString("sectorCorto"),
                getString("idRegionalLect"),
                getString("Regional"),
                getString("Porcion")
        );

        return dato;
    }

    public String generarNoregistrado(Cursor c) throws Exception {
        String dato;

        if (c == null)
            return "";

        mCursor = c;

        dato = Utils.concatenarColumnas("|",
                getString("TipoRegistro"),
                getString("idLectura"),
                getString("idUnidadLect"),
                getString("idArchivo"),
                getString("idEmpleado"),
                getString("Calle"),
                getString("Colonia"),
                getString("NumMedidor"),
                getString("Lectura"),
                getString("Observaciones")
        );

        return dato;
    }

    private String getString(String columna) throws Exception {
        String dato;

        dato = Utils.getString(mCursor, columna, "");   // Obtener el dato de la base de datos SQL Lite
        dato = dato.replaceAll("\\|", " "); // Quitar el caracter pipe (|) por si se capturó

        return dato;
    }
}
