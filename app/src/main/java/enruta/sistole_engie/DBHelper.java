package enruta.sistole_engie;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    // Ruta por defecto de las bases de datos en el sistema Android
    private static String DB_PATH = "/data/data/enruta.sistole_engie/databases/";

    private static String DB_NAME = "sistole_engie.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    private static DBHelper mInstance = null;

    private static int version = 41;

    /**
     * Constructor Toma referencia hacia el contexto de la aplicación que lo
     * invoca para poder acceder a los 'assets' y 'resources' de la aplicación.
     * Crea un objeto DBOpenHelper que nos permitirá controlar la apertura de la
     * base de datos.
     *
     * @param context
     */
    public DBHelper(Context context) {

        super(context, DB_NAME, null, version);
        this.myContext = context;

    }

    /**
     * Me permite no tener que cerrar el cursor a cada rato
     *
     * @param ctx
     * @return
     */
    public static DBHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * Crea una base de datos vacía en el sistema y la reescribe con nuestro
     * fichero de base de datos.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            // la base de datos existe y no hacemos nada.
        } else {
            // Llamando a este método se crea la base de datos vacía en la ruta
            // por defecto del sistema
            // de nuestra aplicación por lo que podremos sobreescribirla con
            // nuestra base de datos.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {
                throw new Error("Error copiando Base de Datos");
            }
        }

    }

    /**
     * Comprueba si la base de datos existe para evitar copiar siempre el
     * fichero cada vez que se abra la aplicación.
     *
     * @return true si existe, false si no existe
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {

            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            // si llegamos aqui es porque la base de datos no existe todavía.

        }
        if (checkDB != null) {

            checkDB.close();

        }
        return checkDB != null ? true : false;
    }

    /**
     * Copia nuestra base de datos desde la carpeta assets a la recién creada
     * base de datos en la carpeta de sistema, desde dónde podremos acceder a
     * ella. Esto se hace con bytestream.
     */
    private void copyDataBase() throws IOException {

        // Abrimos el fichero de base de datos como entrada
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Ruta a la base de datos vacía recién creada
        String outFileName = DB_PATH + DB_NAME;

        // Abrimos la base de datos vacía como salida
        OutputStream myOutput = new FileOutputStream(outFileName);

        // Transferimos los bytes desde el fichero de entrada al de salida
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Liberamos los streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void open() throws SQLException {

        // Abre la base de datos
        try {
            createDataBase();
        } catch (IOException e) {
            throw new Error("Ha sido imposible crear la Base de Datos");
        }

        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Vamos a crear las tablas necesarias
        //URL
        db.execSQL("CREATE TABLE URL (cpl TEXT, lote , centro TEXT, empresa , numBluetooth TEXT, macBluetooth TEXT, macImpresora TEXT, pais TEXT, factorBaremo TEXT, modo TEXT, numFotos TEXT, rutaDescarga TEXT, tamanoFotos TEXT, formatoFoto TEXT)");
        //Anomalias
        db.execSQL("CREATE TABLE Anomalia (desc , conv , capt , subanomalia , ausente , mens , lectura , foto , anomalia , activa , tipo , pais TEXT)");
        //Mensajes
        db.execSQL("CREATE TABLE Mensajes (mensaje TEXT)");

        //Lecturas (el campo descaradamente)
        db.execSQL("CREATE TABLE Lecturas (registro)");

        //No Registrados
        db.execSQL("CREATE TABLE NoRegistrados (poliza TEXT, envio default 0, "
                + "Calle default '', Colonia default '', NumMedidor default'', Lectura default '', Observaciones default '', "
                + "idLectura default 0, idUnidadLect default 0, idArchivo default 0, TipoRegistro default '', "
                + "idEmpleado default 0)");
        //Ruta
        db.execSQL("CREATE TABLE Ruta (supervisionLectura default '', reclamacionLectura default '', reclamacion default '', nisRad default '', poliza default '', sectorlargo default '', sectorCorto default '', tarifa default '', numEsferas default '', consAnoAnt default '', consBimAnt default '', ilr default '', marcaMedidor default '', tipoMedidor default '', serieMedidor default '', aviso default '', comoLlegar2 default '', comoLlegar1 default '', numPortal default '', numEdificio default '', secuencia default '', cliente default '', colonia default '', " +
                "direccion default '', lectura default '', anomalia default '', texto default '', intentos default '', fecha default '', hora default '', sospechosa default '', intento1 default '', intento2 default '', intento3 default '', intento4 default '', intento5 default '', intento6 default '', intento7 default '', dondeEsta default '', estadoDelSuministro default '', registro default '', subAnomalia default '', comentarios default '', terminacion default '-1', fotoAlFinal default 0, ordenDeLectura  default '', latitud default '0.0', longitud default '0.0', anomInst default '', tipoLectura default '', " +
                "sinUso1 default '', sinUso2 default '', sinUso3 default '', sinUso4 default '', sinUso5 default '', sinUso6 default '', sinUso7 default '', sinUso8 default '', consumo default '', mensaje default '', numEsferasReal default '', serieMedidorReal default '', estadoDelSuministroReal default '', " +
                "codigoLectura default '', lecturaAnterior default '', baremo default '', divisionContrato default '', lecturista default '', supervision default '', advertencias default '', ubicacion default '', situacionDelSuministro default '', fechaAviso default '', rutaReal default '', estimaciones default '', " +
                "escalera default '',piso default '', puerta default '', secuenciaReal, anomaliaDeInstalacion default '', ism default '', saldoEnMetros default '', advertenciasTipoAdicionales default '' , fechaReintento1 default '', fechaReintento2 default '', fechaReintento3 default '', fechaReintento4 default '', fechaReintento5 default '', fechaReintento6 default '', fechaReintento7 default '', fix default '', " +
                "indicadorGPS default '', satelites default '', unicom default '', ruta default '', itinerario default '', ciclo default '', tabulador default '', " +
                "selloRetNumero default '', selloRetEstado default '', selloRetColor default '', selloRetModelo default '', " +
                "selloInstNumero default '', selloInstColor default '', selloInstModelo default '', codigoObservacion default '', observacion default '', datosCampana default '', toma default '', giro default '', envio default 0, " +
                "idArchivo default 0, codigoBarras default '', nota1 default '', nota2 default '', miLatitud default '', miLongitud default '', EstimacionesEngie default '', TipoDeCliente default '', TipoDeAcuse default '', " +
                "Porcion default '', idUnidadLect default 0, idRegionalLect default 0, Regional default '', idEmpleado default 0, nivelBateria default 0, " +
                "IntercambiarSerieMedidor default 0, CodigoRespuestaEncuesta default '', MotivoLectura DEFAULT '') ");
        //Usuarios
        db.execSQL("CREATE TABLE usuarios (usuario , contrasena , nombre, rol default 1, fotosControlCalidad default 1, baremo default 75)");
        //fotos
        db.execSQL("CREATE TABLE fotos (secuencial, nombre , foto, envio default 0, temporal, idLectura default 0, idEmpleado default 0, "
            + " idArchivo default 0, NumId default 0)");
        //Encabezado
        db.execSQL("CREATE TABLE encabezado (cpl, centro, lote, descargada, lecturista, registro, ultimoSeleccionado default 0)");
        //Configuraciones globales y extras
        db.execSQL("CREATE TABLE config (key, value, selected)");
        //Configuraciones globales y extras
        db.execSQL("CREATE TABLE usoAnomalias (anomalia, veces default 0, fecha )");

        //RL, 2022-09, Fotos de lecturista: para su perfil y de las evidencias de inicio y finalización de labores.
        db.execSQL("CREATE TABLE fotosLecturista (secuencial, nombre, tipo, foto, envio default 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Aqui actualizamos las tablas , pero que no se nos olvide agregarlo tambien en onCreate
        if (oldVersion <= 1) {
            db.execSQL("ALTER  TABLE Ruta add column dondeEsta");
            db.execSQL("ALTER  TABLE Ruta add column estadoDelSuministro");
            db.execSQL("ALTER  TABLE Ruta add column registro");
        }

        if (oldVersion <= 2) {
            db.execSQL("DROP  TABLE fotos");
            db.execSQL("CREATE TABLE fotos (secuencial, nombre , foto, envio)");
        }

        if (oldVersion <= 3) {
            db.execSQL("ALTER  TABLE Ruta add column subAnomalia");
        }

        if (oldVersion <= 4) {
            db.execSQL("ALTER  TABLE Ruta add column comentarios");
        }

        if (oldVersion <= 5) {
            db.execSQL("ALTER  TABLE config add column selected");
        }

        if (oldVersion <= 6) {
            db.execSQL("ALTER  TABLE encabezado add column registro");
        }

        if (oldVersion <= 7) {
            db.execSQL("ALTER  TABLE Ruta add column terminacion default '-1'");

        }

        if (oldVersion <= 8) {
            db.execSQL("ALTER  TABLE fotos add column temporal default 0");
        }


        if (oldVersion <= 9) {
            db.execSQL("ALTER  TABLE ruta add column fotoAlFinal default 0");
        }

        if (oldVersion <= 10) {
            db.execSQL("ALTER  TABLE ruta add column ordenDeLectura default ''");
        }

        if (oldVersion <= 11) {
            db.execSQL("ALTER  TABLE ruta add column latitud default '0.0'");
            db.execSQL("ALTER  TABLE ruta add column longitud default '0.0'");
        }

        if (oldVersion <= 12) {
            db.execSQL("CREATE TABLE usoAnomalias (anomalia, veces default 0, fecha )");
        }

        if (oldVersion <= 13) {
            db.execSQL("ALTER  TABLE ruta add column anomInst");

        }

        if (oldVersion <= 14) {
            db.execSQL("ALTER  TABLE ruta add column tipoLectura");

        }

        if (oldVersion <= 15) {
            db.execSQL("ALTER  TABLE ruta add column sinUso1 default ''");
            db.execSQL("ALTER  TABLE ruta add column sinUso2 default ''");
            db.execSQL("ALTER  TABLE ruta add column sinUso3 default ''");
            db.execSQL("ALTER  TABLE ruta add column sinUso4 default ''");
            db.execSQL("ALTER  TABLE ruta add column sinUso5 default ''");
            db.execSQL("ALTER  TABLE ruta add column sinUso6 default ''");
            db.execSQL("ALTER  TABLE ruta add column sinUso7 default ''");
            db.execSQL("ALTER  TABLE ruta add column sinUso8 default ''");
            db.execSQL("ALTER  TABLE ruta add column consumo default ''");

        }

        if (oldVersion <= 16) {
            db.execSQL("ALTER  TABLE ruta add column mensaje default ''");
        }

        if (oldVersion <= 17) {

            db.execSQL("ALTER  TABLE ruta add column numEsferasReal default ''");
            db.execSQL("ALTER  TABLE ruta add column serieMedidorReal default ''");
            db.execSQL("ALTER  TABLE ruta add column estadoDelSuministroReal default ''");
        }

        if (oldVersion <= 18) {

            db.execSQL("ALTER  TABLE ruta add column codigoLectura default ''");
            db.execSQL("ALTER  TABLE ruta add column lecturaAnterior default ''");
            db.execSQL("ALTER  TABLE ruta add column baremo default ''");
            db.execSQL("ALTER  TABLE ruta add column divisionContrato default ''");
            db.execSQL("ALTER  TABLE ruta add column lecturista default ''");
            db.execSQL("ALTER  TABLE ruta add column supervision default ''");
            db.execSQL("ALTER  TABLE ruta add column advertencias default ''");
            db.execSQL("ALTER  TABLE ruta add column ubicacion default ''");
            db.execSQL("ALTER  TABLE ruta add column situacionDelSuministro default ''");
            db.execSQL("ALTER  TABLE ruta add column fechaAviso default ''");
            db.execSQL("ALTER  TABLE ruta add column rutaReal default ''");

        }


        if (oldVersion <= 19) {

            db.execSQL("ALTER  TABLE ruta add column escalera default ''");
            db.execSQL("ALTER  TABLE ruta add column piso default ''");
            db.execSQL("ALTER  TABLE ruta add column puerta default ''");


        }

        if (oldVersion <= 20) {

            db.execSQL("ALTER  TABLE ruta add column estimaciones default ''");

        }

        if (oldVersion <= 21) {

            db.execSQL("ALTER  TABLE ruta add column secuenciaReal");


        }

        if (oldVersion <= 22) {
            db.execSQL("ALTER  TABLE usuarios add column rol default 1");
            db.execSQL("ALTER  TABLE usuarios add column fotosControlCalidad default 1");
            db.execSQL("ALTER  TABLE usuarios add column baremo default 75");
        }

        if (oldVersion <= 23) {


            db.execSQL("ALTER  TABLE encabezado add column ultimoSeleccionado default 0");
        }

        if (oldVersion <= 26) {
            db.execSQL("ALTER  TABLE ruta add column advertenciasTipoAdicionales default ''");

        }

        if (oldVersion <= 27) {
            db.execSQL("ALTER  TABLE ruta add column anomaliaDeInstalacion default ''");
            db.execSQL("ALTER  TABLE ruta add column ism default ''");
            db.execSQL("ALTER  TABLE ruta add column saldoEnMetros default ''");

        }

        if (oldVersion <= 29) {


            db.execSQL("ALTER  TABLE ruta add column intento7 ");

            db.execSQL("ALTER  TABLE ruta add column fechaReintento1 default '' ");
            db.execSQL("ALTER  TABLE ruta add column fechaReintento2 default '' ");
            db.execSQL("ALTER  TABLE ruta add column fechaReintento3 default '' ");
            db.execSQL("ALTER  TABLE ruta add column fechaReintento4 default '' ");
            db.execSQL("ALTER  TABLE ruta add column fechaReintento5 default '' ");
            db.execSQL("ALTER  TABLE ruta add column fechaReintento6 default '' ");
            db.execSQL("ALTER  TABLE ruta add column fechaReintento7 default '' ");

            db.execSQL("ALTER  TABLE ruta add column fix default '' ");
        }

        if (oldVersion <= 30) {
            db.execSQL("ALTER  TABLE ruta add column indicadorGPS default '' ");
        }

        if (oldVersion <= 31) {
            db.execSQL("ALTER  TABLE ruta add column satelites default '0.000' ");
        }

        if (oldVersion <= 32) {
            db.execSQL("ALTER  TABLE ruta add column unicom default '' ");
            db.execSQL("ALTER  TABLE ruta add column ruta default '' ");
            db.execSQL("ALTER  TABLE ruta add column itinerario default '' ");
            db.execSQL("ALTER  TABLE ruta add column ciclo default '' ");
            db.execSQL("ALTER  TABLE ruta add column tabulador default '' ");
            db.execSQL("ALTER  TABLE ruta add column selloRetNumero default '' ");
            db.execSQL("ALTER  TABLE ruta add column selloRetEstado default '' ");
            db.execSQL("ALTER  TABLE ruta add column selloRetColor default '' ");
            db.execSQL("ALTER  TABLE ruta add column selloRetModelo default '' ");
            db.execSQL("ALTER  TABLE ruta add column selloInstNumero default '' ");
            db.execSQL("ALTER  TABLE ruta add column selloInstColor default '' ");
            db.execSQL("ALTER  TABLE ruta add column selloInstModelo default '' ");
            db.execSQL("ALTER  TABLE ruta add column codigoObservacion default '' ");
            db.execSQL("ALTER  TABLE ruta add column observacion default '' ");
            db.execSQL("ALTER  TABLE ruta add column datosCampana default '' ");
        }

        if (oldVersion <= 33) {
            db.execSQL("ALTER  TABLE ruta add column giro default '' ");
            db.execSQL("ALTER  TABLE ruta add column toma default '' ");
        }

        if (oldVersion <= 34) {
            db.execSQL("ALTER  TABLE ruta add column envio default 0 ");
            db.execSQL("ALTER  TABLE NoRegistrados add column envio default 0");
        }

        // CE, 10/10/22, Vamos a agregar las Nuevas Columnas que se necesitan
        // RL, 31/12/2022, Se cambian a funciones que verifican y añaden la columna si falta

        verifyColumnInTable(db, "ruta", "idArchivo", "0");
        verifyColumnInTable(db, "ruta", "codigoBarras", "''");
        verifyColumnInTable(db, "ruta", "nota1", "''");
        verifyColumnInTable(db, "ruta", "nota2", "''");
        verifyColumnInTable(db, "ruta", "miLatitud", "''");
        verifyColumnInTable(db, "ruta", "miLongitud", "''");
        verifyColumnInTable(db, "ruta", "EstimacionesEngie", "''");
        verifyColumnInTable(db, "ruta", "TipoDeCliente", "''");
        verifyColumnInTable(db, "ruta", "TipoDeAcuse", "''");

        // RL, 15/12/22, Vamos a agregar las Nuevas Columnas que se necesitan

        verifyColumnInTable(db, "ruta", "Porcion", "''");
        verifyColumnInTable(db, "ruta", "idUnidadLect", "0");
        verifyColumnInTable(db, "ruta", "idRegionalLect", "0");
        verifyColumnInTable(db, "ruta", "Regional", "''");
        verifyColumnInTable(db, "ruta", "idEmpleado", "0");
        verifyColumnInTable(db, "ruta", "nivelBateria", "0");
        verifyColumnInTable(db, "ruta", "IntercambiarSerieMedidor", "0");
        verifyColumnInTable(db, "ruta", "CodigoRespuestaEncuesta", "''");
        verifyColumnInTable(db, "ruta", "MotivoLectura", "''");

        verifyColumnInTable(db, "fotos", "idLectura", "0");
        verifyColumnInTable(db, "fotos", "idArchivo", "0");
        verifyColumnInTable(db, "fotos", "idEmpleado", "0");
        verifyColumnInTable(db, "fotos", "NumId", "0");

        verifyColumnInTable(db, "NoRegistrados", "idLectura", "0");
        verifyColumnInTable(db, "NoRegistrados", "idUnidadLect", "0");
        verifyColumnInTable(db, "NoRegistrados", "idArchivo", "0");
        verifyColumnInTable(db, "NoRegistrados", "idEmpleado", "0");
        verifyColumnInTable(db, "NoRegistrados", "Calle", "''");
        verifyColumnInTable(db, "NoRegistrados", "Colonia", "''");
        verifyColumnInTable(db, "NoRegistrados", "NumMedidor", "''");
        verifyColumnInTable(db, "NoRegistrados", "Lectura", "''");
        verifyColumnInTable(db, "NoRegistrados", "Observaciones", "''");
        verifyColumnInTable(db, "NoRegistrados", "TipoRegistro", "''");
        //verifyColumnInTableAutoincrement(db, "NoRegistrados", "idNoRegistrado");
    }

    /*
        Verifica que la columna exista en la tabla de la base de datos, si no existe la añade
     */


    private void verifyColumnInTable(SQLiteDatabase inDatabase, String inTable, String columnToCheck, String defaultValue) {
        if (!existsColumnInTable(inDatabase, inTable, columnToCheck))
            inDatabase.execSQL("ALTER TABLE "+inTable + " add column "+columnToCheck +" default " + defaultValue);
    }

    private void verifyColumnInTableAutoincrement(SQLiteDatabase inDatabase, String inTable, String columnToCheck) {
        if (!existsColumnInTable(inDatabase, inTable, columnToCheck))
            inDatabase.execSQL("ALTER TABLE "+inTable + " add column "+columnToCheck +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL");
    }

    /*
        Regresa true si la columna existe en la tabla de la base de datos, de lo contrario false
     */

    private boolean existsColumnInTable(SQLiteDatabase inDatabase, String inTable, String columnToCheck) {
        Cursor mCursor = null;
        try {
            // Query 1 row
            mCursor = inDatabase.rawQuery("SELECT " + columnToCheck + " FROM " + inTable + " LIMIT 0", null);

            // getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
            if (mCursor.getColumnIndex(columnToCheck) != -1)
                return true;
            else
                return false;

        } catch (Exception Exp) {
            // Something went wrong. Missing the database? The table?
            Log.d("... - existsColumnInTable", "When checking whether a column exists in the table, an error occurred: " + Exp.getMessage());
            return false;
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }

    public static int getInt(Cursor c, String columnName, int defaultValue) {
        int idx;

        if (c == null)
            return defaultValue;

        idx = c.getColumnIndex("secuencia");

        if (idx < 0)
            return defaultValue;

        return c.getInt(idx);
    }
}