package enruta.sistole_engie.clases;

import android.database.Cursor;

public class GeneradorArchivoTPL {
    private Cursor mCursor;

    public String generarInfoLectura(Cursor c) {
        String dato;

        if (c == null)
            return "";

        mCursor = c;

        dato = Utils.concatenarColumnas("|",
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

    private String getString(String columna) {
        String dato;

        dato = Utils.getString(mCursor, columna, "");   // Obtener el dato de la base de datos SQL Lite
        dato = dato.replaceAll("\\|", " "); // Quitar el caracter pipe (|) por si se capturó

        return dato;
    }
}
