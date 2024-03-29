package enruta.sistole_engie;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import enruta.sistole_engie.clases.BateriaMgr;
import enruta.sistole_engie.clases.Utils;
import enruta.sistole_engie.services.DbBaseMgr;
import enruta.sistole_engie.services.DbConfigMgr;

public class Lectura extends DbBaseMgr {

    public final static int SIN_FOTO_AL_FINAL = 0;
    public final static int FOTO_AL_FINAL = 1;

    public static int LEIDA_LECTURA = 0;
    public static int LEIDA_ANOMALIA = 1;

    String is_supervisionLectura, is_reclamacionLectura, is_reclamacion,
            is_sectorLargo, is_sectorCorto, is_tarifa, is_ilr, is_marcaMedidor,
            is_serieMedidor, is_tipoMedidor, is_aviso, is_comollegar1,
            is_comoLlegar2, numeroDePortal, numeroDeEdificio,
            estadoDelSuministro, dondeEsta, terminacion, ordenDeLectura, numerodeesferasReal, is_advertencias,
            serieMedidorReal, is_fechaAviso, is_tipoLectura, is_estadoDelSuministroReal, sinUso1 = "", sinUso2 = "", sinUso3 = "", sinUso4 = "", selloRetNumero = "", ls_codigoObservacion;

    byte[] registro;

    private String is_cliente, is_colonia, is_municipio, is_direccion,
            is_lectura, is_anomalia = "", is_texto, is_fecha, is_hora,
            is_comentarios, is_ism = "", is_saldoEnMetros = "";

    String intento1, intento2, intento3, intento4, intento5, intento6, intento7,
            is_subAnomalia = "", is_subAnomaliaInterna = "", ls_mensaje, is_escalera, is_piso, is_puerta, is_consumo, intentos, sospechosa, is_anomaliaDeInstalacion = "", is_advertenciasTipoAdicionales, confirmada = "", distinta = "", is_fix, is_satelites, is_toma, is_giro;

    int nis_rad, numerodeesferas, secuencia,
            verificacionForzada, baremo, secuenciaReal;

    long lecturaAnterior, consAnoAnt, consBimAnt, ilr;
    String poliza, is_ubicacion, is_estimaciones, contadorAlterno;


    private DBHelper dbHelper;

    private SQLiteDatabase db;

    private Context mContext;

    private int FotoAlFinal;

    //Anomalia anomalia = null, subAnomalia = null;

    Vector<Anomalia> anomalias = new Vector<Anomalia>(), subAnomalias = new Vector<Anomalia>();

    boolean sigoYo = true;

    long index;

    String is_longitud = "0.0", is_latitud = "0.0";

    String unidad;          // RL, 2022-10-03, Código de la Unidad en Sistole Web
    long idArchivo;         // RL, 2022-10-03, id del archivo en Sistole Web
    String codigoBarras;    // RL, 2022-10-03, código de barras del medidor en Sistole Web
    String nota1;           // RL, 2022-10-03,
    String nota2;           // RL, 2022-10-03,

    private String mMiLatitud;            // CE, 10/10/22, Geolocalizacion del Medidor
    private String mMiLongitud;           // CE, 10/10/22, Geolocalizacion del Medidor
    String estimacionesEngie;    // CE, 10/10/22, Numero de Estimaciones
    String tipoDeCliente;        // CE, 10/10/22, Tipo de Cliente
    private String tipoDeAcuse;          // CE, 10/10/22, Motivo por el que se pide Acuse

    public String mPorcion;        // RL, 2023-01-02, ID de la porcion
    public String mRegional;    // RL, 2023-01-02, Nombre de la regional
    public int mIdUnidadLect;   // RL, 2023-01-02, id de la Unidad
    private int mIdRegionalLect; // RL, 2023-01-02, id de la Regional
    private int mIntercambiarSerieMedidor;  // RL, 2023-01-17, Indicar de si se intercambia la serieMedidor por el código de barras del
    // ... medidor en la pantalla de Toma de Lecturas, Input y Búsqueda de Medidor.
    private String mCodigoRespuestaEncuesta = "";
    private String mMotivoLectura = "";
    private int mEsTuvoControlCalidad = 0;

    private Resources res;

    Globales globales;
    boolean requiereGPS = false;

    Lectura(Context context, int secuencial) throws Throwable {
        this.mContext = context;
        this.secuencia = secuencial;
        res = context.getResources();

        globales = ((Globales) context.getApplicationContext());

        llenacampos();
    }

    public String getFechaHora() {
        return is_fecha + is_hora;
    }

    private void llenacampos() throws Throwable {
        openDatabase();

        String params[] = {String.valueOf(secuencia)};
        Cursor c;

        c = db.rawQuery(
                "Select *, rowid from ruta where cast(secuenciaReal as Integer)= cast(? as Integer)",
                params);

        if (c.getCount() > 0) {
            c.moveToFirst();

            sinUso1 = getString(c, "sinUso1", "");
            sinUso2 = getString(c, "sinUso2", "");
            sinUso3 = getString(c, "sinUso3", "");
            sinUso4 = getString(c, "sinUso4", "");
            is_toma = getString(c, "toma", "");
            is_giro = getString(c, "giro", "");
            selloRetNumero = getString(c, "selloRetNumero", "");
            ls_codigoObservacion = getString(c, "codigoObservacion", "");
            is_anomaliaDeInstalacion = getString(c, "anomaliaDeInstalacion", "");
            is_advertenciasTipoAdicionales = getString(c, "advertenciasTipoAdicionales", "");
            is_supervisionLectura = getString(c, "supervisionLectura", "");

            //lamacion = c.getString(c.getColumnIndex("reclamacion")).trim();

            is_reclamacionLectura = getString(c, "reclamacionLectura", "");
            is_sectorLargo = getString(c, "sectorlargo", "");
            is_sectorCorto = getString(c, "sectorCorto", "");
            is_tarifa = getString(c, "tarifa", "");
            is_ilr = getString(c, "ilr", "");
            is_ism = getString(c, "ism", "");

            is_saldoEnMetros = getString(c, "saldoEnMetros", "");
            is_marcaMedidor = getString(c, "marcaMedidor", "");
            is_serieMedidor = getString(c, "serieMedidor", "").trim();
            is_tipoMedidor = getString(c, "tipoMedidor", "");
            is_aviso = getString(c, "aviso", "");
            is_comollegar1 = getString(c, "comoLlegar1", "");
            is_comoLlegar2 = getString(c, "comoLlegar2", "");

            contadorAlterno = getString(c, "rowid", "");
            serieMedidorReal = getString(c, "serieMedidorReal", "");
            secuenciaReal = getInt(c, "secuenciaReal", 0);

            is_cliente = getString(c, "cliente", "");
            is_colonia = getString(c, "colonia", "");

            is_direccion = getString(c, "direccion", "");
            is_lectura = getString(c, "lectura", "").trim();

            String ls_anomalia = getString(c, "anomalia", "");

            //is_texto = c.getString(c.getColumnIndex("texto"));

            is_fecha = getString(c, "fecha", "");
            is_hora = getString(c, "hora", "");

            String ls_subAnomalia = getString(c, "subAnomalia", "");

            is_comentarios = getString(c, "comentarios", "").trim();
            is_advertencias = getString(c, "advertencias", "");
            is_consumo = getString(c, "consumo", "");
            is_tipoLectura = getString(c, "tipoLectura", "");
            is_estadoDelSuministroReal = getString(c, "estadoDelSuministroReal", "");

            if (is_comentarios == null) {
                is_comentarios = "";
            }

            is_escalera = getString(c, "escalera", "");
            is_piso = getString(c, "piso", "");
            is_puerta = getString(c, "puerta", "");
            is_ubicacion = getString(c, "ubicacion", "");
            is_estimaciones = getString(c, "estimaciones", "").trim();
            is_fechaAviso = getString(c, "fechaAviso", "").trim();

            nis_rad = getInt(c, "nisRad", 0);
            poliza = getString(c, "poliza", "");
            numerodeesferas = getInt(c, "numEsferas", 0);
            numerodeesferasReal = getString(c, "numEsferasReal", "").trim();

            numeroDePortal = getString(c, "numPortal", "");
            numeroDeEdificio = getString(c, "numEdificio", "");

            secuencia = getInt(c, "secuencia", 0);
            intentos = getString(c, "intentos", "").trim();
            sospechosa = getString(c, "sospechosa", "").trim();

            lecturaAnterior = getLong(c, "lecturaAnterior", 0);
            consBimAnt = getLong(c, "consBimAnt", 0);
            consAnoAnt = getLong(c, "consAnoAnt", 0);
            ilr = getLong(c, "ilr", 0);

            baremo = getInt(c, "baremo", 0);

//			if (secuencia % 2 == 0)
//				requiereGPS = true;
//			else
//				requiereGPS = false;

//			requiereGPS = true;

            requiereGPS = getInt(c, "indicadorGPS", 0) == 1;

            ordenDeLectura = getString(c, "ordenDeLectura", "");

            intento1 = getString(c, "intento1", "");
            intento2 = getString(c, "intento2", "");
            intento3 = getString(c, "intento3", "");
            intento4 = getString(c, "intento4", "");
            intento5 = getString(c, "intento5", "");
            intento6 = getString(c, "intento6", "");
            intento7 = getString(c, "intento7", "");

            FotoAlFinal = getInt(c, "fotoAlFinal", 0);

            terminacion = getString(c, "terminacion", "");

            is_latitud = getString(c, "latitud", "");
            is_longitud = getString(c, "longitud", "");

            ls_mensaje = getString(c, "mensaje", "");


            estadoDelSuministro = getString(c, "estadoDelSuministro", "").trim();
            dondeEsta = getString(c, "dondeEsta", "");

            //registro = c.getBlob(c.getColumnIndex("registro"));

            // Obtener el idArchivo de donde se obtuvieron los datos

            idArchivo = getLong(c, "idArchivo", 0);
            codigoBarras = getString(c, "codigoBarras", "");
            nota1 = getString(c, "nota1", "");
            nota2 = getString(c, "nota2", "");
            mMiLatitud = getString(c, "miLatitud", "");
            mMiLongitud = getString(c, "miLongitud", "");
            estimacionesEngie = getString(c, "EstimacionesEngie", "");
            tipoDeCliente = getString(c, "TipoDeCliente", "");
            tipoDeAcuse = getString(c, "TipoDeAcuse", "");

            unidad = is_sectorCorto;

            mRegional = getString(c, "Regional", "");
            mPorcion = getString(c, "Porcion", "");
            mIdUnidadLect = getInt(c, "idUnidadLect", 0);
            mIdRegionalLect = getInt(c, "idRegionalLect", 0);
            mIntercambiarSerieMedidor = getInt(c, "IntercambiarSerieMedidor", 0);
            mMotivoLectura = getString(c, "MotivoLectura", "");

//			if (ls_anomalia.equals("") && secuencia%3==0)
//				ls_anomalia="AC*";
            if (!ls_anomalia.trim().equals(""))
                setAnomalia(ls_anomalia.trim());

            if (!is_anomaliaDeInstalacion.trim().equals(""))
                setAnomalia(is_anomaliaDeInstalacion.trim());


            if (!ls_subAnomalia.trim().equals(""))
                setSubAnomalia(ls_subAnomalia.trim());

            // La lectura decidirá si es ausente o no
            if (!lecturaAusente() && is_lectura.equals("")) {
                sigoYo = true;
            } else
                sigoYo = false;
        } else {
            throw new Throwable(
                    "No se encontraron lecturas con dicho secuencial");
        }

        if (globales.habilitarPuntoDecimal && !is_lectura.equals("") && globales.tll.getLecturaActual().is_aviso.startsWith("Dem")) {
            is_lectura = String.valueOf(Double.parseDouble(is_lectura) / 1000f);
        }

        c.close();
        closeDatabase();

    }

    void establecerFotoAlFinal(int estado) {
        openDatabase();

        db.execSQL("Update ruta set fotoAlFinal=" + estado
                + " where cast(secuenciaReal as Integer)= cast(" + secuencia
                + " as Integer)");

        closeDatabase();

    }

    private void openDatabase() {
        dbHelper = new DBHelper(mContext);

        db = dbHelper.getReadableDatabase();
    }

    private void closeDatabase() {
        db.close();
        dbHelper.close();

    }

    public String getColonia() {
        return is_colonia.trim();
    }

    public void setColonia(String ls_colonia) {
        is_colonia = ls_colonia;
    }

    /*
     * public void setLectura(String ls_lectura, String ls_anomalia){
     *
     * }
     */
    public boolean lecturaAusente() {
        boolean retorno = false;

        for (Anomalia anomalia : anomalias) {
            if (anomalia.esAusente()) {
                retorno = true;
                break;
            }
        }

//		if (anomalia != null) {
//			retorno = anomalia.esAusente();
//		}

        return retorno;
    }

    public void guardarSospechosa() {
        guardarSospechosa(Integer.parseInt(sospechosa));
    }

    public void guardarSospechosa(int veces) {
        if (!globales.guardarSospechosa)
            return;
        openDatabase();
        ContentValues cv_params = new ContentValues();

        sospechosa = String.valueOf(veces);

        cv_params.put("sospechosa", sospechosa);

        String params[] = {String.valueOf(secuenciaReal)};

        db.update("ruta", cv_params,
                "cast(secuenciaReal as Integer)= cast(? as Integer)", params);

        closeDatabase();
    }

    public void guardar(int ordenDeLectura) {
        guardar(true, ordenDeLectura);
    }

    /*
        Guarda la lectura capturada con sus datos asociados.
     */
    public void guardar(boolean agregarOrdenDeLectura, int ordenDeLectura) {

        ContentValues cv_params = new ContentValues();
        int nivelBateria;

        //globales.tdlg.setConsumo();

        //Guardamos el dia que se ingreso el aviso en caso de tener uno
        //if ((globales.tdlg.esSegundaVisita(is_anomalia, is_subAnomalia) || requiereLectura()==Anomalia.LECTURA_AUSENTE)&& is_fechaAviso.equals("")){
        if (/*!is_anomalia.equals("") && */is_fechaAviso.equals("") && this.getAnomaliasABorrar().respuestas.size() > 0) {
            try {

                is_fechaAviso = Main.obtieneFecha(globales.tlc.getRellenoCampo("fechaAviso"));
            } catch (Throwable e) {

            }
        }
//		else if(/*is_anomalia.equals("")*/ this.getAnomaliasABorrar().respuestas.size()==0){
//			is_fechaAviso="";
//		}

        //Si no tienen lectura quitamos la fecha y hora, de lo contrario la calculamos
        if (is_lectura.equals("") && is_anomalia.equals("") && is_anomaliaDeInstalacion.equals("")) {
            is_fecha = "";
            is_hora = "";
        } else {
            is_fecha = Main.obtieneFecha(globales.tlc.getRellenoCampo("fecha"));
            is_hora = Main.obtieneFecha(globales.tlc.getRellenoCampo("hora"));
        }

        if (is_lectura.equals("") && globales.rellenarVaciaLectura) {
            is_lectura = Main.rellenaString("", " ", globales.tlc.getLongCampo("lectura"), false);
        }

        if (!sospechosa.trim().equals("") || !intentos.trim().equals("")) {
            sospechosa = Main.rellenaString(sospechosa, "0", globales.tlc.getLongCampo("sospechosa"), true);
            intentos = Main.rellenaString(intentos, "0", globales.tlc.getLongCampo("intentos"), true);
        }

        if (!globales.prefijoAnomalia.equals("") && !is_anomalia.trim().equals("")) {
            is_anomalia = globales.prefijoAnomalia + is_anomalia;
        }

        if (is_lectura.contains("."))
            is_lectura = is_lectura.substring(0, is_lectura.indexOf(".")) + Main.rellenaString(is_lectura.substring(is_lectura.indexOf(".") + 1), "0", 3, false);

//		if (!intentos.trim().equals("")){
//			intentos=Main.rellenaString(intentos, "0", globales.tlc.getLongCampo("intentos"), true);
//		}

        switch (globales.modoDeBanderasAGrabar) {
            case Globales.BANDERAS_CONF_COLOMBIA:
                sospechosa = confirmada;
                intentos = distinta;
                break;
            case Globales.BANDERAS_CONF_PANAMA:
                sospechosa = "00";
                intentos = Main.rellenaString(intentos.trim(), "0", globales.tlc.getLongCampo("intentos"), true);
//			intentos="00";
                break;

//		case Globales.BANDERAS_INTENTOS_ECA:
//			if (globales.tll.getLecturaActual().intento1.equals("")){
//				sospechosa="0";
//			}else if (globales.tll.getLecturaActual().intento2.equals("")){
//				sospechosa="1";
//			}else if (globales.tll.getLecturaActual().intento3.equals("")){
//				sospechosa="2";
//			}else if (globales.tll.getLecturaActual().intento4.equals("")){
//				sospechosa="3";
//			}else if (globales.tll.getLecturaActual().intento5.equals("")){
//				sospechosa="4";
//			}else if (globales.tll.getLecturaActual().intento6.equals("")){
//				sospechosa="5";
//			}
//			else{
//				sospechosa="6";
//			}
        }

        if (globales.location == null) {
            is_longitud = "";
            is_latitud = "";
            is_fix = "";
        } else {
            is_longitud = String.valueOf(globales.location.getLongitude());
            is_latitud = String.valueOf(globales.location.getLatitude());

            is_satelites = String.valueOf(globales.Satellites);
            is_fix = globales.fix;
        }

        globales.tdlg.accionesAntesDeGrabarLectura();

        globales.tdlg.setTipoLectura();

        openDatabase();

        nivelBateria = BateriaMgr.getBatteryPercentage(mContext);

        cv_params.put("lectura", is_lectura);
        cv_params.put("consumo", is_consumo);
        cv_params.put("anomalia", is_anomalia);
        cv_params.put("anomaliaDeInstalacion", is_anomaliaDeInstalacion);
        cv_params.put("ism", is_ism);
        cv_params.put("saldoEnMetros", is_saldoEnMetros);
        cv_params.put("marcaMedidor", is_marcaMedidor);
        cv_params.put("tipoMedidor", is_tipoMedidor);
        cv_params.put("subAnomalia", is_subAnomalia);
        cv_params.put("terminacion", terminacion);
        if (globales.guardarSospechosa)
            cv_params.put("sospechosa", sospechosa);//Confirmada
        cv_params.put("intentos", intentos);//Distinta
        cv_params.put("fecha", is_fecha);
        cv_params.put("hora", is_hora);
        //cv_params.put("registro", registro);
        cv_params.put("comentarios", is_comentarios);
//		cv_params.put("intento1", intento1);
//		cv_params.put("intento2", intento2);
//		cv_params.put("intento3", intento3);
//		cv_params.put("intento4", intento4);
//		cv_params.put("intento5", intento5);
//		cv_params.put("intento6", intento6);
        cv_params.put("mensaje", ls_mensaje);
        cv_params.put("ordenDeLectura", this.ordenDeLectura);
        cv_params.put("ubicacion", is_ubicacion);
        cv_params.put("advertencias", is_advertencias);
        cv_params.put("numEsferasReal", numerodeesferasReal);
        cv_params.put("serieMedidorReal", serieMedidorReal);
        cv_params.put("direccion", is_direccion);
        cv_params.put("colonia", is_colonia);
        cv_params.put("numEdificio", numeroDeEdificio);
        cv_params.put("numPortal", numeroDePortal);
        cv_params.put("escalera", is_escalera);
        cv_params.put("piso", is_piso);
        cv_params.put("puerta", is_puerta);
        cv_params.put("lecturista", globales.getUsuario());
        cv_params.put("idEmpleado", globales.getIdEmpleado());
        cv_params.put("fechaAviso", is_fechaAviso);
        cv_params.put("aviso", is_aviso);
        cv_params.put("tipoLectura", is_tipoLectura);
        cv_params.put("estadoDelSuministroReal", is_estadoDelSuministroReal);
        cv_params.put("latitud", is_latitud);
        cv_params.put("longitud", is_longitud);
        cv_params.put("nivelBateria", nivelBateria);
        cv_params.put("fix", is_fix);
        cv_params.put("codigoObservacion", ls_codigoObservacion);
        cv_params.put("codigoRespuestaEncuesta", mCodigoRespuestaEncuesta);
        cv_params.put("ControlCalidad", mEsTuvoControlCalidad);
        cv_params.put("envio", 1);

        String params[] = {String.valueOf(secuenciaReal)};

        db.update("ruta", cv_params,
                "cast(secuenciaReal as Integer)= cast(? as Integer)", params);


        closeDatabase();

        globales.tdlg.accionesDespuesDeGrabarLectura();
    }

    void setTerminacion(String terminacion) {
        this.terminacion = terminacion;

        openDatabase();
        ContentValues cv_params = new ContentValues();

        cv_params.put("terminacion", terminacion);

        String params[] = {String.valueOf(secuenciaReal)};

        db.update("ruta", cv_params,
                "cast(secuenciaReal as Integer)= cast(? as Integer)", params);

        closeDatabase();

    }

    /*
     * public Anomalia getAnomalia(){ return anomalia; }
     */

    public void forzarLecturas() {

        // if (is_serieMedidor.contains("CF") /*&& is_anomalia.equals("") &&
        // is_lectura.equals("")*/){
        // is_lectura="0";
        // }
        // else{
        is_anomalia = "888";
        is_comentarios = "NO HABILITADO";
        is_tipoLectura = "4";
        // }

        guardar(false, 0);
    }

    public void setComentarios(String ls_comentarios) {

        if (globales.multiplesAnomalias) {
            //No se pueden agregar comentarios vacios
            if (ls_comentarios.equals("")) {
                return;
            }
            //No puede tener punto y coma, se confundirá el programa
            ls_comentarios = eliminaCaracter(ls_comentarios, ";");
//			if (!is_comentarios.equals("")){
//				is_comentarios +=";";
//			}
            is_comentarios += ls_comentarios + ";";
        } else
            is_comentarios = ls_comentarios;
    }

    public String getAnomalia() {
        return is_anomalia;
    }

    public void setAnomalia(String ls_anomalia) {
        int tope = 1, comienzo = 0;


        Anomalia anomalia = null;

//		if (ls_anomalia.equals("") && !globales.multiplesAnomalias){
//		
//			//Si no acepta anomalias multiples debe limpiar el vector
//
//				anomalias.clear();
//			return;
//		}

        if (ls_anomalia.equals("")) {
            //No se aceptan vacios
            return;
        }

        if (!globales.multiplesAnomalias) {

            if (!globales.prefijoAnomalia.equals("") && ls_anomalia.length() > 3) {
                ls_anomalia = ls_anomalia.substring(globales.prefijoAnomalia.length());
            }

            is_anomalia = ls_anomalia;
            is_subAnomalia = "";
        } else {
            //Cuando acepta multiples se concatenan
            is_anomalia += ls_anomalia;
            tope = ls_anomalia.length();
            comienzo = ls_anomalia.lastIndexOf("*") + 1;
        }

        if (ls_anomalia.equals("*")) {
            //Se agrego un asterisco lo que haya antes no nos interesa
            anomalias.clear();
            return;
        }

        for (int i = comienzo; i < tope; i++) {

            if (globales.multiplesAnomalias) {
                anomalia = new Anomalia(mContext, ls_anomalia.substring(i, i + 1), "", false);
            } else {
                anomalia = new Anomalia(mContext, ls_anomalia, "", false);

                if (globales.DiferirEntreAnomInstYMed && anomalia.is_tipo.equals("I")) {
                    is_anomalia = "";
                    is_anomaliaDeInstalacion = ls_anomalia;
                } else {
                    is_anomaliaDeInstalacion = "";
                }

            }


            if (globales.multiplesAnomalias || anomalias.size() == 0) {
                anomalias.add(anomalia);
            } else {
                //Si no soporta multiples anomalias debe siempre guardar en el primero

                anomalias.set(0, anomalia);

            }
        }


//		if (!ls_anomalia.equals(""))
//			anomalia = new Anomalia(context, ls_anomalia, false);
//		else
//			anomalia = null;
    }

    public void setSubAnomalia(String ls_anomalia) {
        int tope = 1, cominezo = 0;
        String[] subanomaliasArray;

        Anomalia subAnomalia = null;

        ls_anomalia = ls_anomalia.trim();
        if (ls_anomalia.equals("")) {
            //Aqui no se aceptan
            return;
        }


        if (!globales.multiplesAnomalias) {
            is_subAnomalia = ls_anomalia;
        } else {
            //Cuando acepta multiples se concatenan, separandolos por un ;
//			if (is_subAnomalia.length()>0 && !ls_anomalia.endsWith("*") )
//				is_subAnomalia+=";";

            if (ls_anomalia.equals("*")) {
                is_subAnomaliaInterna += "*";
            } else {
                is_subAnomalia += eliminaCaracter(ls_anomalia, "*");
                ;
                if (!is_subAnomalia.endsWith(";")) {
                    is_subAnomalia += ";";
                }

                is_subAnomaliaInterna += ls_anomalia;
                if (!is_subAnomaliaInterna.endsWith(";")) {
                    is_subAnomaliaInterna += ";";
                }
            }


            //is_subAnomaliaInterna=is_subAnomalia;

            if (anomalias.size() == 0) {
                is_subAnomaliaInterna += "*";
            }

            if (ls_anomalia.equals("*")) {
                //Se agrego un asterisco lo que haya antes no nos interesa
                subAnomalias.clear();
                return;
            }

            ls_anomalia = is_subAnomaliaInterna.substring(is_subAnomaliaInterna.lastIndexOf("*") + 1, is_subAnomaliaInterna.length());
        }

        if (ls_anomalia.equals("")) {
            return;
        }


        subanomaliasArray = ls_anomalia.split(";");

        tope = subanomaliasArray.length;

        for (int i = 0; i < tope; i++) {
//			if (!ls_anomalia.equals(""))

            if (globales.multiplesAnomalias)
                subAnomalia = new Anomalia(mContext, subanomaliasArray[i], "", true);
            else {
                subAnomalia = new Anomalia(mContext, subanomaliasArray[i], is_anomalia, true);
            }
//			else
//			{
//				//Si no acepta anomalias multiples debe limpiar el vector
//				if (!globales.multiplesAnomalias ){
//					subAnomalias.clear();
//				}
//				else
//				{
//					//No agregaremos una subanomalia nula
//					return;
//				}
//			}

            if (globales.multiplesAnomalias || subAnomalias.size() == 0) {
                subAnomalias.add(subAnomalia);
            } else {
                //Si no soporta multiples anomalias debe siempre guardar en el primero

                subAnomalias.set(0, subAnomalia);

            }


        }


//		if (!ls_anomalia.equals(""))
//			subAnomalia = new Anomalia(context, ls_anomalia, true);
//		else
//			subAnomalia = null;

    }

    // public void setIntento(int intento, String ls_lectura, boolean
    // esCorrecta){
    // if(intentos>1){
    // sospechosa=1;
    // }
    // this.intentos=intento;
    //
    // switch(intento){
    // case 1:
    // intento1=ls_lectura;
    // break;
    //
    // case 2:
    // intento2=ls_lectura;
    // break;
    //
    // case 3:
    // intento3=ls_lectura;
    // break;
    //
    // case 4:
    // intento4=ls_lectura;
    // break;
    //
    // case 5:
    // intento5=ls_lectura;
    // break;
    //
    // case 6:
    // intento6=ls_lectura;
    // break;
    // }
    // if (esCorrecta)
    // is_lectura=ls_lectura;
    //
    //
    //
    //
    // guardar(false, 0);
    // }

    public void setLectura(String lectura) {
        is_lectura = lectura;
    }

    public String getDireccion() {
//		String ls_cadena = "";
//
//		// ls_cadena=is_direccion.trim();
//
//		/*
//		 * if(numeroDeEdificio.trim().length()>0){ ls_cadena+= " " +
//		 * numeroDeEdificio.trim(); }
//		 * 
//		 * if(numeroDePortal.trim().length()>0){ ls_cadena+= " " +
//		 * numeroDePortal.trim(); }
//		 * 
//		 * if(is_colonia.trim().length()>0){ ls_cadena+= " " +
//		 * is_colonia.trim(); }
//		 */
//
//		ls_cadena += is_colonia.trim() + "\n" + is_comollegar1.trim() + "\n"
//				+ dondeEsta.trim();
//
//		return ls_cadena;

        return is_direccion.trim();
    }

    public void setDireccion(String ls_direccion) {


        is_direccion = ls_direccion;
    }

    public String getAcceso() {
        String ls_cadena = "";
        if (numeroDeEdificio.trim().length() != 0
                && numeroDePortal.trim().length() != 0) {
            ls_cadena = "Pda. ";
            ls_cadena += numeroDeEdificio.trim() + "-" + numeroDePortal.trim();
        }
        return ls_cadena;
    }

    public String getLectura() {
        return is_lectura;
    }

    public String getNombreAnomalia() {

        if (!globales.multiplesAnomalias && anomalias.size() > 0) {
            return anomalias.get(0).is_desc;
        } else if (globales.multiplesAnomalias) {
            String cadena = "";

            for (Anomalia anomalia : anomalias) {
                cadena += anomalia.is_desc + " ";
            }
            return cadena;
        }

//		if (anomalia != null)
//			return anomalia.is_desc;
//		else
        return "";
    }

    public String getNombreCliente() {
        return is_cliente;
    }

    private String generaCadenaAEnviar() {
        String ls_cadena = "";
        String ls_lectura;
        String ls_tmpSubAnom = "";

        ls_cadena = is_lectura.length() == 0 ? "4" : "0"; // Indicador de tipo
        // de lectura
        ls_cadena += Main.rellenaString(is_lectura, "0",
                globales.tlc.getLongCampo("Lectura"), true);
        ls_cadena += Main.rellenaString(is_lectura, is_lectura.equals("") ? " "
                : "0", globales.tlc.getLongCampo("Lectura"), true);
        ls_cadena += is_fecha;
        ls_cadena += is_hora;
        ls_cadena += Main.rellenaString(is_anomalia,
                is_anomalia.equals("") ? " " : "0",
                globales.tlc.getLongCampo("anomalia"), true);
        // Esto no se bien de que se trata, asi que de momento dejaremos
        // ceros...
        ls_cadena += Main.rellenaString(String.valueOf(sospechosa), "0",
                globales.tlc.getLongCampo("sospechosa"), true);
        ls_cadena += Main.rellenaString(String.valueOf(intentos), "0",
                globales.tlc.getLongCampo("intentos"), true);

        if (is_anomalia.equals("888"))
            ls_tmpSubAnom = "9";
        else
            ls_tmpSubAnom = "";

        ls_cadena += Main
                .rellenaString(ls_tmpSubAnom, "0", globales.tlc.getLongCampo("anomInst"),
                        true);

        return ls_cadena;
    }

    public String getComentarios() {
        String ls_cadena = "";

//		if (subAnomalia != null) {
//			ls_cadena += subAnomalia.is_desc;
//		}
//
//		if (!ls_cadena.equals("")) {
//			ls_cadena += "\n";
//		}

        ls_cadena += is_comentarios;
        return ls_cadena;
    }

    public boolean confirmarLectura() {
        Boolean esConfirmar = false;
        Boolean hacerControlCalidad = false;
        Boolean esSospechosa;

        esConfirmar = is_supervisionLectura.equals("1") || is_reclamacionLectura.equals("1")
                || this.getEsAcuseRecibo();

        esSospechosa = (Utils.convToInt(sospechosa) > 0);

        if (!esConfirmar && !esSospechosa) {
            hacerControlCalidad = globales.getHacerControlCalidad();
            mEsTuvoControlCalidad = hacerControlCalidad ? 1:0;
        }

        return esConfirmar  || hacerControlCalidad;
    }

    public Spanned getInfoPreview(int tipoDeBusqueda, String textoBuscado, int totalMedidores) {
        String ls_preview = "";
        // int antes=0;
        //
        // antes=is_serieMedidor.indexOf(textoBuscado);
        //
        // //ls_preview= "<![CDATA[";
        //
        //
        // if (antes>=0){
        // if (antes>0)
        // ls_preview += is_serieMedidor.substring(0, antes-1);
        //
        // ls_preview += "<b>" +is_serieMedidor.substring(antes, antes +
        // textoBuscado.length() ) + "</b>";
        //
        // //if ((antes+ textoBuscado.length() + 1)<=is_serieMedidor.length())
        // ls_preview += is_serieMedidor.substring(antes+ textoBuscado.length()
        // /*+ 1*/);
        //
        //
        //
        // }
        // else{
        // ls_preview= this.is_serieMedidor;
        // }

        ls_preview = globales.tdlg.getDescripcionDeBuscarMedidor(this,
                tipoDeBusqueda, textoBuscado);

        ls_preview += "<br>" + (globales.mostrarRowIdSecuencia ? contadorAlterno : secuencia) + " " + mContext.getString(R.string.de) + " " + totalMedidores;

        // ls_preview +="\n"+ getDireccion();
        // ls_preview += "<br>" +is_colonia.trim();
        // ls_preview +="<br>" + getAcceso();

        // ls_preview +="]]>" ;

        return Html.fromHtml(ls_preview);
    }

    public static String marcarTexto(String texto, String textoBuscado,
                                     boolean restarUnEspacio) {
        int antes = 0;
        String ls_preview = "";

        antes = texto.indexOf(textoBuscado);

        if (antes >= 0) {
            if (antes > 0)
                ls_preview += texto.substring(0, antes);

            ls_preview += "<b>"
                    + texto.substring(antes, antes + textoBuscado.length())
                    + "</b>";

            // if ((antes+ textoBuscado.length() + 1)<=is_serieMedidor.length())
            ls_preview += texto
                    .substring(antes + textoBuscado.length() /* + 1 */);

        } else {
            ls_preview = texto;
        }

        return ls_preview;
    }

    public static SpannableStringBuilder marcarTextoColor(String texto, String textoBuscado,
                                                          boolean restarUnEspacio) {
        int antes = 0;
        SpannableStringBuilder sb = new SpannableStringBuilder(texto);
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.parseColor("red"));
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        int inicio = -1;
        int fin = -1;

        inicio = texto.toString().indexOf(textoBuscado);

        if (inicio >= 0) {
            fin = textoBuscado.length();
            sb.setSpan(fcs, inicio, fin, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(bss, inicio, fin, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        return sb;
    }

    // public String formatedInfoReadMetter(){
    // String ls_preview="";
    //
    // if ((is_lectura.length()==0 && is_anomalia.length()==0)){
    // ls_preview="L";
    // //color= R.color.Red;
    // }
    // else if ((is_lectura.length()!=0 && is_anomalia.length()==0)){
    // ls_preview="L";
    // //color= R.color.Green;
    // }
    // else if ((is_lectura.length()==0 && is_anomalia.length()!=0)){
    // ls_preview="A";
    // //color= R.color.Red;
    // }
    // else if ((is_lectura.length()!=0 && is_anomalia.length()!=0)){
    // ls_preview="A";
    // //color= R.color.Orange;
    // }
    //
    //
    //
    //
    // return ls_preview;
    // }

    public int colorInfoReadMetter(int tipo) {
        // String ls_preview="";
        int color = R.color.Gray;

        if (((is_lectura.length() != 0 && (is_anomalia.length() == 0 || is_anomaliaDeInstalacion.length() == 0 || is_anomalia.endsWith("*"))) || (is_lectura
                .length() != 0 && (is_anomalia.length() != 0 || is_anomaliaDeInstalacion.length() != 0 || !is_anomalia.endsWith("*"))))
                && tipo == LEIDA_LECTURA) {
            // ls_preview="L";
            color = R.color.green;
        } else if ((is_lectura.length() == 0 && ((is_anomalia.length() != 0 || is_anomaliaDeInstalacion.length() != 0) && !is_anomalia.endsWith("*")))
                && tipo == LEIDA_ANOMALIA) {
            // ls_preview="A";
            color = R.color.red;
        } else if ((is_lectura.length() != 0 && ((is_anomalia.length() != 0 || is_anomaliaDeInstalacion.length() != 0) && !is_anomalia.endsWith("*")))
                && (tipo == LEIDA_ANOMALIA)) {
            // ls_preview="A";
            color = R.color.Orange;
        }

        return color;
    }

    public void setPuntoGPS(Location location) {
        if (location == null) {
            is_longitud = "0.0";
            is_latitud = "0.0";
        } else {
            is_longitud = String.valueOf(location.getLongitude());
            is_latitud = String.valueOf(location.getLatitude());
        }

        openDatabase();

        ContentValues cv_params = new ContentValues();

        String params[] = {String.valueOf(secuenciaReal)};

        cv_params.put("latitud", is_latitud);
        cv_params.put("longitud", is_longitud);

        db.update("Ruta", cv_params,
                "cast(secuenciaReal as Integer)= cast(? as Integer)", params);

        closeDatabase();

    }

    public String getMarcaDeMedidorAMostrar() {
        return is_marcaMedidor + "-" + is_tipoMedidor;
    }

    public String getAnomaliaDeInstalacionAMostrar() {
        return is_anomaliaDeInstalacion;
    }

    public String getAnomaliaAMostrar() {
//		String cadena="";
//		for (Anomalia anomalia:anomalias){
//			
//			if (globales.convertirAnomalias)
//				cadena += anomalia.is_conv ;
//		}
        if (globales.multiplesAnomalias) {
            return is_anomalia;
        } else {
            String cadena = "";
            for (Anomalia anomalia : anomalias) {

                if (globales.convertirAnomalias)
                    cadena += anomalia.is_conv;
                else
                    cadena += anomalia.is_anomalia;
            }

            return cadena;
        }


    }

    public String getSubAnomaliaAMostrar() {
//		String cadena="";
//		for (Anomalia subAnomalia:subAnomalias){
//			if (!cadena.equals(""))
//				cadena +=";";
//			
//			if (globales.convertirAnomalias && subAnomalia!=null)
//				cadena += subAnomalia.is_desc ;
//		}

        return is_subAnomalia;
    }

    public int requiereLectura() {
        if (anomalias.size() == 0)
            return Anomalia.SIN_ANOMALIA;
        //for (int i=0; i<anomalias.size();i++){
        //int

        if (subAnomalias.size() > 0) {
            if (globales.multiplesAnomalias) {
                String index = "";

                if (globales.convertirAnomalias)
                    index = anomalias.get(anomalias.size() - 1).is_conv;
                else
                    index = anomalias.get(anomalias.size() - 1).is_anomalia;

                for (Anomalia anom : subAnomalias) {
                    if ((anom.is_anomalia.startsWith(index) && !globales.convertirAnomalias) || (anom.is_conv.startsWith(index) && globales.convertirAnomalias)) {

                        return anom.requiereLectura();
                    }
                }

                return anomalias.get(anomalias.size() - 1).requiereLectura();

            } else {
                return subAnomalias.get(0).requiereLectura();
            }

        } else {
            return anomalias.get(anomalias.size() - 1).requiereLectura();
        }


        //}

    }

    public boolean hayAnomaliasConLecturaAusente() {
        if (anomalias.size() == 0)
            return false;
        for (int i = 0; i < anomalias.size(); i++) {
            //int


            if (globales.multiplesAnomalias) {
                if (subAnomalias.size() > 0) {
                    String index = "";

                    if (globales.convertirAnomalias)
                        index = anomalias.get(i).is_conv;
                    else
                        index = anomalias.get(i).is_anomalia;

                    for (Anomalia anom : subAnomalias) {
                        if ((anom.is_anomalia.startsWith(index)) || (anom.is_conv.startsWith(index))) {

                            if (Anomalia.LECTURA_AUSENTE == anom.requiereLectura()) {
                                return true;
                            }
                        }
                    }

                    if (anomalias.get(i).requiereLectura() == Anomalia.LECTURA_AUSENTE) {
                        return true;
                    }

                } else {
//					if ( subAnomalias.get(0).requiereLectura()==Anomalia.LECTURA_AUSENTE){
//						return true;
//					}
                    if (anomalias.get(i).requiereLectura() == Anomalia.LECTURA_AUSENTE) {
                        return true;
                    }

                }

            } else {
                if (anomalias.get(i).requiereLectura() == Anomalia.LECTURA_AUSENTE) {
                    return true;
                }
            }


        }

        return false;

    }

    public void borrarLecturasAusentes() {
        if (anomalias.size() == 0)
            return;
        for (int i = anomalias.size() - 1; i >= 0; i--) {
            //int
            String index = "";

            if (globales.convertirAnomalias)
                index = anomalias.get(i).is_conv;
            else
                index = anomalias.get(i).is_anomalia;

            if (globales.multiplesAnomalias) {
                if (subAnomalias.size() > 0) {


                    for (int j = subAnomalias.size() - 1; j >= 0; j--) {
                        Anomalia anom = subAnomalias.get(j);
                        if ((anom.is_anomalia.startsWith(index)) || (anom.is_conv.startsWith(index))) {
                            if (anom.ii_ausente == 4)
                                deleteAnomalia(index);
                        }
                    }


                } else {
                    if (anomalias.get(i).ii_ausente == 4)
                        deleteAnomalia(index);
                }


            } else {
                if (anomalias.get(i).ii_ausente == 4)
                    deleteAnomalia(index);
            }


        }


    }

    public int requiereFotoAnomalia() {

        if (anomalias.size() > 0) {
            if (subAnomalias.size() > 0) {
                if (globales.multiplesAnomalias) {
                    String index = anomalias.get(anomalias.size() - 1).is_anomalia;

                    if (globales.convertirAnomalias)
                        index = anomalias.get(anomalias.size() - 1).is_conv;
                    else
                        index = anomalias.get(anomalias.size() - 1).is_anomalia;

                    for (Anomalia anom : subAnomalias) {

                        String codigoAnomalia = "";
                        if (globales.convertirAnomalias)
                            codigoAnomalia = anom.is_conv;
                        else
                            codigoAnomalia = anom.is_anomalia;

                        if (codigoAnomalia.startsWith(index)) {
                            //return anom.ii_foto;
                            String ls_indicadorFoto = globales.tdlg.remplazaValorDeArchivo(TomaDeLecturasGenerica.FOTOS, globales.convertirAnomalias ? anom.is_conv : anom.is_anomalia, String.valueOf(anom.ii_foto));
                            return Integer.parseInt(ls_indicadorFoto);
                        }
                    }

                    //return anomalias.get(anomalias.size()-1).ii_foto;
                    String ls_indicadorFoto = globales.tdlg.remplazaValorDeArchivo(TomaDeLecturasGenerica.FOTOS, globales.convertirAnomalias ? anomalias.get(anomalias.size() - 1).is_conv : anomalias.get(anomalias.size() - 1).is_anomalia, String.valueOf(anomalias.get(anomalias.size() - 1).ii_foto));
                    return Integer.parseInt(ls_indicadorFoto);

                } else {
                    //return subAnomalias.get(subAnomalias.size()-1).ii_foto;
                    String ls_indicadorFoto = globales.tdlg.remplazaValorDeArchivo(TomaDeLecturasGenerica.FOTOS, globales.convertirAnomalias ? subAnomalias.get(subAnomalias.size() - 1).is_conv : subAnomalias.get(subAnomalias.size() - 1).is_anomalia, String.valueOf(subAnomalias.get(subAnomalias.size() - 1).ii_foto));
                    return Integer.parseInt(ls_indicadorFoto);
                }

            } else {
                String ls_indicadorFoto = globales.tdlg.remplazaValorDeArchivo(TomaDeLecturasGenerica.FOTOS, globales.convertirAnomalias ? anomalias.get(anomalias.size() - 1).is_conv : anomalias.get(anomalias.size() - 1).is_anomalia, String.valueOf(anomalias.get(anomalias.size() - 1).ii_foto));
                return Integer.parseInt(ls_indicadorFoto);
            }
        }


        return 0;
    }

    public Anomalia getUltimaAnomalia() {
        if (anomalias.size() > 0)
            return anomalias.get(anomalias.size() - 1);

        return null;
    }

    public MensajeEspecial getAnomaliasABorrar() {

        Vector<Respuesta> respuesta = new Vector<Respuesta>();

        for (Anomalia anom : anomalias) {
            if (anom.is_activa.equals("I")) {
                continue;
            }
            String codigoAnomalia = "";
            if (globales.convertirAnomalias)
                codigoAnomalia = anom.is_conv;
            else
                codigoAnomalia = anom.is_anomalia;

            respuesta.add(new Respuesta(codigoAnomalia, anom.is_desc));
        }

        MensajeEspecial mj_activo = new MensajeEspecial("Seleccione anomalia a borrar", respuesta, 0);
        return mj_activo;
    }

    public String getAnomaliasAIngresadas() {

        //Vector <Respuesta> respuesta= new Vector <Respuesta> () ;
        String respuesta = "";
        for (Anomalia anom : anomalias) {
            if (anom.is_activa.equals("I")) {
                continue;
            }
            String codigoAnomalia = "";
            if (globales.convertirAnomalias)
                codigoAnomalia = anom.is_conv;
            else
                codigoAnomalia = anom.is_anomalia;

            //respuesta.add(new Respuesta(codigoAnomalia, anom.is_desc));
            respuesta += codigoAnomalia;
        }

        //MensajeEspecial mj_activo= new MensajeEspecial("Seleccione anomalia a borrar", respuesta, 0);
        return respuesta;
    }

    /**
     * Elimina una anomalia y su subanomalia del arreglo con dado numero de anomalia
     *
     * @param index Espacio en el arreglo en donde se encuentra la anomalia
     * @return Un booleano que indica si la anomalia ha sido borrada
     */
    public boolean deleteAnomalia(String index) {
        boolean borrada = false;
        for (int i = 0; i < anomalias.size(); i++) {
            if (globales.convertirAnomalias) {
                if (anomalias.get(i).is_conv.equals(index)) {
                    borrada = deleteAnomalia(i);
                    break;
                }


            } else {
                if (anomalias.get(i).is_anomalia.equals(index)) {
                    borrada = deleteAnomalia(i);
                    break;
                }

            }
        }
        return borrada;
    }

    /**
     * Elimina una anomalia y su subanomalia del arreglo con dado numero de anomalia
     *
     * @param index Espacio en el arreglo en donde se encuentra la anomalia
     * @return Un booleano que indica si la anomalia ha sido borrada
     */
    public boolean deleteAnomalia(int index) {

        if (index >= anomalias.size()) {
            return false;
        }

        Anomalia anom = anomalias.get(index);
        String codigoAnomalia = "";
        if (globales.convertirAnomalias)
            codigoAnomalia = anom.is_conv;
        else
            codigoAnomalia = anom.is_anomalia;

        if (globales.multiplesAnomalias) {


            //Borramos la subanomalia relacionada
            for (int i = 0; i < subAnomalias.size(); i++) {

                if (subAnomalias.get(i).is_desc.startsWith(codigoAnomalia)) {
                    //Esa subAnomalia se borra

                    borrarComentarioAnomalia(subAnomalias.get(i).is_desc.substring(0, globales.longitudCodigoSubAnomalia));
                    globales.tdlg.DeshacerModificacionesDeAnomalia(subAnomalias.get(i).is_desc.substring(0, globales.longitudCodigoSubAnomalia));
                    subAnomalias.removeElementAt(i);
                    break;
                }
            }

            //ahora la anomalia
            anomalias.remove(index);

            //Hay que rehacer el is_anomalia y el is_subAnomalia
            String cadena = getAnomaliasCapturadas();

            is_anomalia = is_anomalia.substring(0, is_anomalia.lastIndexOf("*") + 1) + cadena;

            cadena = "";

            for (Anomalia anomalia : subAnomalias) {


                if (globales.convertirAnomalias)
                    cadena += anomalia.is_desc.substring(0, globales.longitudCodigoSubAnomalia);
                if (!cadena.equals("")) {
                    cadena += ";";
                }
            }


//			int asterico=is_subAnomaliaInterna.lastIndexOf("*");
//			if (asterico<0){
//				asterico=is_subAnomaliaInterna.length();
//			}

            is_subAnomaliaInterna = is_subAnomaliaInterna.substring(0, is_subAnomaliaInterna.lastIndexOf("*") + 1) + cadena;
            //is_subAnomaliaInterna= is_subAnomaliaInterna.substring(0,asterico) + cadena;


            is_subAnomalia = eliminaCaracter(is_subAnomaliaInterna, "*");
            //is_subAnomalia= is_subAnomalia.substring(0, is_subAnomalia.lastIndexOf("*")+1) + cadena;


        } else {
            //Borramos la subAnomalia, si es que hay
            if (subAnomalias.size() > 0)
                subAnomalias.removeElementAt(0);

            anomalias.remove(index);

            is_anomalia = "";
            is_anomaliaDeInstalacion = "";
            is_subAnomalia = "";
        }

        borrarComentarioAnomalia(codigoAnomalia);
        globales.tdlg.DeshacerModificacionesDeAnomalia(codigoAnomalia);
        globales.tdlg.cambiosAlBorrarAnomalia(codigoAnomalia);
        return true;


    }

    public void setCliente(String nombre) {
        is_cliente = nombre;
    }

    public String getAnomaliasCapturadas() {
        String cadena = "";

        for (Anomalia anomalia : anomalias) {

            if (globales.convertirAnomalias)
                cadena += anomalia.is_conv;
            else
                cadena += anomalia.is_anomalia;
        }
        return cadena;
    }

    public boolean containsSubAnomalia(String ls_subAnom) {

        for (Anomalia anomalia : subAnomalias) {

            if (anomalia.is_desc.startsWith(ls_subAnom))
                return true;
        }
        return false;
    }

    public String getCliente() {
        return is_cliente.trim();
    }

    public static int toInteger(String valor) {
        int li_valor = 0;

        try {
            li_valor = Integer.parseInt(valor.trim());
        } catch (Throwable e) {

        }

        return li_valor;

    }

    public static long toLong(String valor) {
        long li_valor = 0;

        try {
            li_valor = Long.parseLong(valor.trim());
        } catch (Throwable e) {

        }

        return li_valor;

    }

    public void borrarComentarioAnomalia(String ls_anomalia) {
        if (globales.multiplesAnomalias) {
            String[] ls_comentarios = is_comentarios.split(";");

            //Obtenemos el label que tendra al principio
            String ls_prefijo = globales.tdlg.getPrefijoComentario(ls_anomalia);

            if (ls_prefijo.equals("")) {
                return;
            }

            is_comentarios = "";
            for (String ls_comentario : ls_comentarios) {
                if (!ls_comentario.startsWith(ls_prefijo))
                    setComentarios(ls_comentario);
            }

        } else {
            is_comentarios = "";
        }
    }

    static String eliminaCaracter(String ls_cadena, String ls_caracter) {

        while (ls_cadena.indexOf(ls_caracter) != -1) {
            ls_cadena = ls_cadena.substring(0, ls_cadena.indexOf(ls_caracter)) + ls_cadena.substring(ls_cadena.indexOf(ls_caracter) + 1);
        }

        return ls_cadena;

    }

    public String getComentarioAnomalia(String ls_anomalia) {
        if (globales.multiplesAnomalias) {
            String[] ls_comentarios = is_comentarios.split(";");

            //Obtenemos el label que tendra al principio
            String ls_prefijo = globales.tdlg.getPrefijoComentario(ls_anomalia);

            for (String ls_comentario : ls_comentarios) {
                if (ls_comentario.startsWith(ls_prefijo))
                    return ls_comentario;
            }

        } else {
            return is_comentarios;
        }

        return "";
    }

    /**
     * Borra lo que haya en anomalias y lo remplaza con el nuevo arreglo
     *
     * @param ls_anomalia
     */
    public void reiniciaAnomalias(String ls_anomalia) {
        is_anomalia = "";
        is_anomaliaDeInstalacion = "";
        setAnomalia(ls_anomalia);
    }

    public void reiniciaSubAnomalias(String ls_anomalia) {
        is_subAnomalia = "";
        setSubAnomalia(ls_anomalia);
    }

    public String getUnidad() {
        return unidad;
    }

    public long getIdArchivo() {
        return idArchivo;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public String getCodigoBarrasCorregido() {
        return codigoBarras.replace("&", "/");
    }

    public String getSerieMedidor() {
        return is_serieMedidor;
    }

    public String getSerieMedidorCorregido() {
        return is_serieMedidor.replace("&", "/");
    }

    public String getNumMedidor() throws Exception {
        if (!getIntercambiarSerieMedidor())
            return getSerieMedidorCorregido();
        else
            return getCodigoBarrasCorregido();
    }

    public String getNota1() {
        return nota1;
    }

    public String getNota2() {
        return nota2;
    }

    public String getMiLatitud() {
        return mMiLatitud;
    }

    public String getMiLongitud() {
        return mMiLongitud;
    }

    public String getEstimacionesEngie() {
        return estimacionesEngie;
    }

    public String getTipoDeCliente() {
        return tipoDeCliente;
    }

    public String getTipoDeAcuse() {
        return tipoDeAcuse;
    }

    public boolean getEsAcuseRecibo() {
        String s;

        s = tipoDeAcuse.trim();

        if (s.equals("1"))
            return true;

        if (s.equals("2"))
            return true;

        if (s.equals("3"))
            return true;

        if (s.equals("4"))
            return true;

        if (s.equals("5"))
            return true;

        return false;
    }

    public boolean getEsFaunaNociva() {
        String s;

        s = tipoDeAcuse.trim();

        if (s.equals("6"))
            return true;
        else
            return false;
    }

    public boolean getIntercambiarSerieMedidor() throws Exception {
        int valor;

        valor = DbConfigMgr.getInstance().getIntercambiarSerieMedidor(mContext);

        if (valor >= 1)
            return true;
        else if (valor == 0)
            return false;
        else if (mIntercambiarSerieMedidor != 0)
            return true;
        else
            return false;
    }

    public boolean getAlinearDerechaNumMedidor() throws Exception {
        int valor;

        valor = DbConfigMgr.getInstance().getAlinearDerechaNumMedidor(mContext);

        if (valor != 0)
            return true;
        else
            return false;
    }

    public String getCodigoRespuestaEncuesta() {
        return mCodigoRespuestaEncuesta;
    }

    public void setCodigoRespuestaEncuesta(String valor) {
        mCodigoRespuestaEncuesta = valor;
    }

    public String getmMotivoLectura() {
        return mMotivoLectura;
    }

}
