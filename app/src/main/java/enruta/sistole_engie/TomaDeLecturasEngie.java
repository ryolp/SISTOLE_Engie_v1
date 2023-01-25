package enruta.sistole_engie;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Hashtable;
import java.util.Vector;

import enruta.sistole_engie.entities.InfoFotoEntity;
import enruta.sistole_engie.entities.ResumenEntity;
import enruta.sistole_engie.clases.Utils;
import enruta.sistole_engie.entities.EmpleadoCplEntity;
import enruta.sistole_engie.services.DbLecturasMgr;
import enruta.sistole_engie.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

public class TomaDeLecturasEngie extends TomaDeLecturasGenerica {

    public final static int MEDIDOR_ANTERIOR = 0;
    public final static int MEDIDOR_POSTERIOR = 1;
    public final static int NUM_ESFERAS = 2;
    public final static int NUM_MEDIDOR = 3;
    public final static int MARCA = 4;
    public final static int CALLE = 5;
    public final static int NUMERO = 6;
    public final static int PORTAL = 7;
    public final static int ESCALERA = 8;
    public final static int PISO = 9;
    public final static int PUERTA = 10;
    public final static int COMPLEMENTO = 11;
    public final static int LECTURA = 12;
    public final static int TIPO = 13;
    public final static int OBSERVACIONES = 14;
    public final static int NIC = 15;
    public final static int SELLO_RET_NUMERO = 16;
    public final static int SELLO_RET_ESTADO = 17;
    public final static int SELLO_RET_COLOR = 18;
    public final static int SELLO_RET_MODELO = 19;
    public final static int SELLO_INST_NUMERO = 20;
    public final static int SELLO_INST_COLOR = 21;
    public final static int SELLO_INST_MODELO = 22;
    public final static int CODIGO_OBSERVACION = 23;
    public final static int OBSERVACION = 24;
    public final static int DATOS_CAMPANA = 25;
    public final static int DATOS_OBSERVACIONES = 26;
    public final static int COLONIA = 27;

    //archivos a transmitir
    public final static int ARCHIVO_MENSAJES = 2;
    public final static int ARCHIVO_G = 3, ARCHIVO_I = 4, ARCHIVO_R = 5, ARCHIVO_M = 6, ARCHIVO_N = 7;

    Vector<TextView> textViews = new Vector<TextView>();
    MensajeEspecial mj_estaCortado;
    MensajeEspecial mj_sellos;
    MensajeEspecial mj_consumocero;
    MensajeEspecial mj_ubicacionVacia;
    MensajeEspecial mj_anomalia_seis;
    Hashtable<String, Integer> ht_calidades;

    // CE, REVISAR
    public TomaDeLecturasEngie(Context context) {
        super(context);
        long_registro = 344;
        globales.admonPass = "2002";

        procesosAlEntrar();

        String[] campos = {"poliza", "lectura", "fecha", "hora", "anomalia", "comentarios", "lecturista", "tipoLectura", "latitud", "longitud"};
        String separadorSalida = "";
        //Creamos los campos que serán de salida
        globales.tlc.getListaDeCamposFormateado(campos);

        globales.textoEsferas = "Digitos";
        globales.textoNoRegistrados = "No Registrados";

        mj_estaCortado = new MensajeEspecial("¿Sigue Cortado?", PREGUNTAS_SIGUE_CORTADO);
        mj_estaCortado.cancelable = false;
        Vector<Respuesta> respuesta = new Vector<Respuesta>();
        respuesta.add(new Respuesta("0", "Con Sellos"));
        respuesta.add(new Respuesta("1", "Sin Sellos"));
        respuesta.add(new Respuesta("2", "Reconectado"));

        mj_sellos = new MensajeEspecial("Retirar Sellos", getCamposGenerico("sellos"), RETIRAR_SELLOS);

        respuesta = new Vector<Respuesta>();
        respuesta.add(new Respuesta("4J", "J-Esta Habitado"));
        respuesta.add(new Respuesta("4O", "O-Esta Deshabitado"));
        respuesta.add(new Respuesta("4P", "P-Indeterminado"));
        mj_consumocero = new MensajeEspecial("Consumo Cero. Seleccione una de las opciones", respuesta, PREGUNTAS_CONSUMO_CERO);
        mj_consumocero.cancelable = false;

        respuesta = new Vector<Respuesta>();
        respuesta.add(new Respuesta("1A", "A - Accesible"));
        respuesta.add(new Respuesta("1B", "B - Batería"));
        respuesta.add(new Respuesta("1V", "V - Vivienda"));
        respuesta.add(new Respuesta("1R", "R - Accesible tras reja"));
        mj_ubicacionVacia = new MensajeEspecial("Capt. Ubicación", respuesta, PREGUNTAS_UBICACION_VACIA);
        mj_ubicacionVacia.cancelable = true;

        respuesta = new Vector<Respuesta>();
        respuesta.add(new Respuesta("G", "G - Vidrio empañado u opaco"));
        respuesta.add(new Respuesta("R", "R - Puerta trabada"));
        respuesta.add(new Respuesta("Z", "Z - Acceso Bloqueado"));
        respuesta.add(new Respuesta("5", "5 - No puede acceder por barrios carenciados"));
        mj_anomalia_seis = new MensajeEspecial("Seleccione", respuesta, ANOMALIA_SEIS);
        mj_anomalia_seis.cancelable = false;

        globales.reemplazarToastPorMensaje = false;


        globales.logo = R.drawable.logo_engie;

        globales.multiplesAnomalias = false;
// CE, 23/09/15, Vamos a hacer una prueba para ECOGAS
//		globales.convertirAnomalias=true;
        globales.convertirAnomalias = false;

        globales.longitudCodigoAnomalia = 3;
        globales.longitudCodigoSubAnomalia = 3;

        globales.rellenoAnomalia = " ";
        globales.rellenarAnomalia = false;

        globales.repiteAnomalias = false;

        globales.remplazarDireccionPorCalles = false;

        globales.mostrarCuadriculatdl = true;

        globales.mostrarRowIdSecuencia = true;

        globales.dejarComoAusentes = true;

        globales.mensajeDeConfirmar = R.string.msj_lecturas_verifique_1;

        globales.mostrarNoRegistrados = true;

        globales.tipoDeValidacion = Globales.CON_SMS;
        //globales.tipoDeValidacion = Globales.CONTRASEÑA;

        globales.mensajeContraseñaLecturista = R.string.str_login_msj_lecturista_contrasena_pana;
        globales.controlCalidadFotos = 0;

        globales.mostrarMacBt = false;
        globales.mostrarMacImpresora = false;
        globales.mostrarServidorGPRS = true;
        globales.mostrarServidorWIFI = false;
        globales.mostrarFactorBaremo = false;
        globales.mostrarTamañoFoto = true;
        globales.mostrarMetodoDeTransmision = false;
        globales.mostrarIngresoFacilMAC = false;
        globales.mostrarIngresoFacilMAC_BT = false;
        globales.mostrarUnicom = false;
        globales.mostrarRuta = false;
        globales.mostrarItinerario = false;
        globales.mostrarLote = false;
        globales.mostrarCPL = true;
        globales.mostrarSonido = false;

        globales.defaultTransmision = "1";
        globales.defaultRutaDescarga = "C:\\lectura";
//		globales.defaultRutaDescarga="C:\\sistolePruebas\\lectura";
        globales.defaultUnicom = "1120";                                // CE, Hay que ver si tenemos el campo CPL
        globales.defaultRuta = "03";
        globales.defaultItinerario = "4480";
        globales.defaultServidorGPRS = BuildConfig.BASE_URL;
//		globales.defaultServidorGPRS="http://www.espinosacarlos.com";
        globales.defaultServidorWIFI = "http://10.240.225.11/1120";
        globales.defaultServidorDeActualizacion = "";

        globales.letraPais = "T";

        globales.mostrarCodigoUsuario = true;

        globales.tomaMultiplesFotos = true;

        globales.porcentaje_main = 1.0;
        globales.porcentaje_main2 = 1.0;
        globales.porcentaje_hexateclado = .74998;
        globales.porcentaje_teclado = .6410;
        globales.porcentaje_lectura = 1.0;
        globales.porcentaje_info = 1.1;

        globales.calidadDeLaFoto = 100;

        globales.modoDeCierreDeLecturas = Globales.SOLO_TRANSMITIR_PENDIENTES;

        globales.mostrarGrabarEnSD = true;

        globales.mostrarCalidadFoto = true;

        globales.legacyCaptura = true;

        //Escondemos los elementos de la cuadricula que no nos interesan
        globales.ver_celda0 = true;
        globales.ver_celda1 = true;
        globales.ver_celda2 = true;
        globales.ver_celda3 = true;
        globales.ver_celda4 = false;

        globales.DiferirEntreAnomInstYMed = false;

        globales.maxIntentos = 0;

        globales.rellenarVaciaLectura = false;

        globales.puedoCancelarFotos = false;
        globales.mostrarOtraFoto = true;

        globales.tabs = new int[2];

        globales.tabs[0] = R.string.lbl_medidor;
        globales.tabs[1] = R.string.lbl_direccion;

        globales.longCampoUsuario = 20;
        globales.longCampoContrasena = 20;

        globales.fuerzaEntrarComoSuperUsuarioAdmon = true;

        globales.desencriptarEntrada = false;

        globales.tipoDeRecepcion = Globales.TRANSMION_NORMAL;

        globales.enviarLongitudDeCadenaAEnviar = false;

        globales.contraseñaUsuarioEncriptada = false;

        globales.preguntaSiTieneMedidor = false;

        globales.ultimoBloqueCapturado = "0000";

        globales.mostrarPausaActiva = false;
        globales.tiempoPausaActiva = 7200000;
        globales.fechaEnMilisegundos = 0;

        globales.quitarPrimerCaracterNombreFoto = false;

        globales.GPS = true;
        globales.bloquearBorrarSiIntento = false;

        globales.validacionCon123 = false;

        globales.ordenarAnomalias = true;

        globales.guardarSospechosa = false;

        globales.elegirQueDescargar = false;
        globales.modoDeBanderasAGrabar = Globales.BANDERAS_CONF_PANAMA;

        globales.preguntarSiSegundaVezDescargada = false;

        globales.sobreEscribirServidorConDefault = true;

        globales.bloquearCPLNoSuper = true;

        globales.tomarFotoCambioMedidor = true;

        globales.mostrarCambioMedidor = true;
//		globales.tipoDeEntradaUsuarioLogin=InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;

//		globales.dirigirAlUnicoResultado=true;
//		globales.switchBuscarPorMover=true;


        globales.envioAutomatico = true;
        globales.enviarSoloLoMarcado = true;
        globales.respetarLongitudDeEntradaAnomalias = false;
        InicializarMatrizDeCompatibilidades();
        inicializaTablaDeCalidades();
    }

    /**
     * Validacion de una lectura
     *
     * @param ls_lectAct
     * @return Regresa el mensaje de error
     */
// CE, REVISAR
    public String validaLectura(String ls_lectAct) {

        //Priemro sacamos los intentos

        int intento = 1;

        if (ls_lectAct.length() == 0) {
            return NO_SOSPECHOSA + "|Ingrese una lectura";
        }

//		globales.fotoForzada=true;
//		globales.ignorarContadorControlCalidad=true;

        if (ls_lectAct.contains(".")) {
            ls_lectAct = ls_lectAct.substring(0, ls_lectAct.indexOf(".")) + Main.rellenaString(ls_lectAct.substring(ls_lectAct.indexOf(".") + 1), "0", 3, false);
        }

        long ll_lectAct = Long.parseLong(ls_lectAct);

        //Es valida
        // RL, 2022-07-28, Queremos foto en consumo 0
        //if ((globales.il_lect_max >= ll_lectAct && globales.il_lect_min <= ll_lectAct && is_lectAnt.equals("")

        if ((globales.il_lect_max >= ll_lectAct && globales.il_lect_min < ll_lectAct && is_lectAnt.equals("")
                && !globales.tll.getLecturaActual().confirmarLectura())) {
            is_lectAnt = "";
            return "";
        }

        if (!is_lectAnt.equals(ls_lectAct)) {
            if (!is_lectAnt.equals("") || globales.bModificar) {
                globales.tll.getLecturaActual().intentos = String.valueOf(Lectura.toInteger(globales.tll.getLecturaActual().intentos) + 1);
                globales.tll.getLecturaActual().distinta = "1";
            }
            is_lectAnt = ls_lectAct;
            //Contador de lectura distinta

            globales.ignorarContadorControlCalidad = true;

            //if (globales.contadorIntentos<globales.maxIntentos){
            return /*NO_*/SOSPECHOSA + "|" + globales.getString(R.string.msj_lecturas_verifique);
            //}
        } else {
            globales.tll.getLecturaActual().confirmada = "1";
        }

        globales.ignorarContadorControlCalidad = true;
        is_lectAnt = "";
        return "";
    }

    // CE, REVISAR

    public String getNombreFoto(Globales globales, SQLiteDatabase db, long secuencial, String is_terminacion, String ls_anomalia) {
        String ls_nombre = "", ls_unicom;
        Cursor c;

        /**
         * Este es el fotmato del nombre de la foto
         *
         * NumMedidor a 10 posiciones,
         * fecha	  a YYYYMMDD
         * hora		  a HHMMSS
         */

        ls_nombre = Main.rellenaString(globales.tll.getLecturaActual().sinUso3.trim(), "0", globales.tlc.getLongCampo("sinUso3"), true) + "-";
        ls_nombre += Main.rellenaString(globales.tll.getLecturaActual().is_serieMedidor, "0", globales.tlc.getLongCampo("serieMedidor"), true) + "-";
//    	if (ls_anomalia.equals("")){
//    		if (!globales.is_lectura.equals("")){
//    			ls_nombre =Main.rellenaString(globales.is_lectura, "0", globales.tlc.getLongCampo("lectura"), true) + "-";
//    		}
//
//    	}
//    	else{
//    		ls_nombre =Main.rellenaString(ls_anomalia, "0", globales.tlc.getLongCampo("anomalia"), true) + "-";
//    	}
        ls_nombre += Main.obtieneFecha("ymd");
        ls_nombre += Main.obtieneFecha("his");
        ls_nombre += ".JPG";

        return ls_nombre;
    }

    /*
        Función para regresar datos de la foto, incluyendo datos de nombre, unidad, porción, regional.
     */

    // RL, 2023-01-02, Regresar una estructura de datos, con la información suficiente para transmitir la foto con sus datos relacionados.

    public InfoFotoEntity getInfoFoto(Globales globales, SQLiteDatabase db, long secuencial, String is_terminacion, String ls_anomalia) {
        String ls_nombre = "", ls_unicom;
        Cursor c;
        InfoFotoEntity infoFoto = new InfoFotoEntity();
        Lectura lect;

        /**
         * Este es el fotmato del nombre de la foto
         *
         * NumMedidor a 10 posiciones,
         * fecha	  a YYYYMMDD
         * hora		  a HHMMSS
         */

        lect = globales.tll.getLecturaActual();

        ls_nombre = Main.rellenaString(lect.sinUso3.trim(), "0", globales.tlc.getLongCampo("sinUso3"), true) + "-";
        ls_nombre += Main.rellenaString(lect.is_serieMedidor, "0", globales.tlc.getLongCampo("serieMedidor"), true) + "-";
//    	if (ls_anomalia.equals("")){
//    		if (!globales.is_lectura.equals("")){
//    			ls_nombre =Main.rellenaString(globales.is_lectura, "0", globales.tlc.getLongCampo("lectura"), true) + "-";
//    		}
//
//    	}
//    	else{
//    		ls_nombre =Main.rellenaString(ls_anomalia, "0", globales.tlc.getLongCampo("anomalia"), true) + "-";
//    	}
        ls_nombre += Main.obtieneFecha("ymd");
        ls_nombre += Main.obtieneFecha("his");
        ls_nombre += ".JPG";

        infoFoto.nombreFoto = ls_nombre;
        infoFoto.idLectura = Utils.convToLong(lect.poliza);
        infoFoto.Unidad = lect.unidad;
        infoFoto.Regional = lect.Regional;
        infoFoto.Porcion = lect.Porcion;

        return infoFoto;
    }

    // CE, REVISAR
    public Vector<String> getInformacionDelMedidor(Lectura lectura) {
        Vector<String> datos = new Vector<String>();

        //Esta variable la usaremos para poder determinar si algun dato es vacio y mostrar solo lo necesario en la pantalla
        String comodin = "";

//**********************************************************************************************
// CE, 23/09/15, Vamos a hacer una prueba para Ecogas Chihuahua'
//		datos.add(lectura.is_sectorCorto);
        //Distrito
//		datos.add("Distrito: " + lectura.is_sectorCorto.substring(1, 3));
        //Planta
//		datos.add("Planta: " + lectura.is_sectorCorto.substring(3, 5));
        //Sector F
//		datos.add("Sector F: " + lectura.is_sectorCorto.substring(5, 7));
        //Manzana
//		datos.add("Manzana: " + lectura.is_sectorCorto.substring(7, 11));
        //Manzana
//		datos.add("Lote: " + lectura.is_sectorCorto.substring(11, 14));

//		datos.add("Fraccion: " + lectura.is_sectorCorto.substring(14, 16));

//		datos.add("Sector: " + lectura.is_sectorCorto.substring(16, 18));
        datos.add(lectura.getDireccion());
//		datos.add(lectura.getColonia());
//**********************************************************************************************


//		datos.add((!lectura.numeroDeEdificio.trim().equals("")?lectura.numeroDeEdificio.trim():""));
        datos.add("Contrato: " + lectura.sinUso2);

// CE, 23/09/15, Vamos a hacer una prueba para Ecogas Chihuahua'
//		datos.add("Clave Usuario: " + lectura.sinUso3);
        datos.add(lectura.getNombreCliente().trim());

// CE, 10/10/22, Vamos a mostrar las Nuevas Columnas

        // If Unidad = Tampico mostrar Serie Medidor else mostrar código de barras.

        if (!lectura.getIntercambiarSerieMedidor())
            datos.add("Codigo de Barras: " + lectura.getCodigoBarras().trim());
        else
            datos.add("Codigo de Barras: " + lectura.getSerieMedidor().trim());

        datos.add(lectura.getNota1().trim());
        datos.add(lectura.getNota2().trim());

//RL, 10/10/24, Se deshabilitan termporalmente. Obligado por ya saben quién...
//        datos.add("miLatitud: " + lectura.getMiLatitud().trim());
//        datos.add("miLongitud: " + lectura.getMiLongitud().trim());

        //Vamos a agregar los campos que se van llenando mientras se agregan anomalias
        String ls_anom = lectura.getAnomaliasCapturadas();
        String ls_comentarios = "";
        if (!globales.tll.getLecturaActual().getAnomaliaAMostrar().equals("")) {
            // Tiene una anomalia
            ls_comentarios = context.getString(R.string.str_anomalia) + ": " + globales.is_presion;
            if (!globales.tll.getLecturaActual().getSubAnomaliaAMostrar().equals("")) {
                // Tiene una subanomalia
                ls_comentarios += /*", " */ "\n" + "Observaciones" + ": "
                        + globales.tll.getLecturaActual().getSubAnomaliaAMostrar();
            }
            ls_comentarios += "\n";
        }
//		if (globales.tll.getLecturaActual().getComentarios().length()>18)
        ls_comentarios +=/*"\n" + ls_comentarios + */globales.tll.getLecturaActual().getComentarios()/*.substring(18)*/;
        if (!ls_comentarios.trim().equals("")) ;
        datos.add(ls_comentarios);
        return datos;
    }

    @Override
    public MensajeEspecial getMensaje() {
//		if (globales.tll.getLecturaActual().selloRetNumero.equals("") && globales.tll.getLecturaActual().is_aviso.startsWith("Dem"))
//			return mj_sellos;

        return null;
    }

    // CE, REVISAR   **** LAS ANOMALIAS DE MEDIDOR SON COMPATIBLES CON LAS ANOMALIAS DE INSTALACION Y VICEVERSA ****
    private void InicializarMatrizDeCompatibilidades() {
        anomaliasCompatibles = new Hashtable<String, String>();
        anomaliasCompatibles.put("M", "I");        // CE, Habria que ver como escribir esto de manera generica
        anomaliasCompatibles.put("I", "M");        // CE, Habria que ver como escribir esto de manera generica
    }

    // CE, REVISAR
    @Override
    public boolean esAnomaliaCompatible(String anomaliaAInsertar, String anomaliasCapturadas) {
        int lastIndexOfStar = 0;
        boolean esCompatible = true;
        return esCompatible;
    }

    @Override
    public ComentariosInputBehavior getAvisoMensajeInput(String anomalia) {
        ComentariosInputBehavior cib_config = null;
        return cib_config;
    }

    @Override
    public void RealizarModificacionesDeAnomalia(String anomalia, String comentarios) {
    }

    @Override
    public void RealizarModificacionesDeAnomalia(String anomalia) {
    }

    @Override
    public void DeshacerModificacionesDeAnomalia(String anomalia) {
    }

    @Override
    public MensajeEspecial mensajeDeConsumo(String ls_lectAct) {
        //Vamos a investigar si es un Consumo Cero

        int li_lectAct = Integer.parseInt(ls_lectAct);
        if ((globales.tll.getLecturaActual().consAnoAnt == li_lectAct) && (globales.tll.getLecturaActual().consBimAnt == li_lectAct)) {
            //Si es la misma o menor... quiere decir que no hubo un consumo
            return mj_consumocero;
        }
        return null;
    }

    @Override
    public void RespuestaMensajeSeleccionada(MensajeEspecial me, int respuesta) {
        // TODO Auto-generated method stub

        switch (me.respondeA) {
            case PREGUNTAS_SIGUE_CORTADO:
                if (respuesta == MensajeEspecial.NO) {
                    //Borramos si hay una j
                    globales.tll.getLecturaActual().deleteAnomalia("J");
                    //Agregamos la anomalia J al vector de anomalias
                    cambiosAnomaliaAntesDeGuardar(globales.is_lectura);
                    globales.tll.getLecturaActual().setAnomalia("J");
                    globales.is_presion = globales.tll.getLecturaActual().getAnomalia();
                    globales.tll.getLecturaActual().is_estadoDelSuministroReal = "0";
                } else {
                    globales.tll.getLecturaActual().is_estadoDelSuministroReal = "1";
                }
                break;
            case PREGUNTAS_CONSUMO_CERO:
                //Borramos la anomalia y la sub
//                globales.tll.getLecturaActual().deleteAnomalia(me.regresaValor(respuesta).substring(0, 1));
                //Agregamos
//                cambiosAnomaliaAntesDeGuardar(globales.is_lectura);
//                globales.tll.getLecturaActual().setAnomalia(me.regresaValor(respuesta).substring(0, 1));
//                globales.tll.getLecturaActual().setSubAnomalia(me.regresaValor(respuesta));

//                globales.is_presion=globales.tll.getLecturaActual().getAnomalia();
                break;
            case PREGUNTAS_EN_EJECUCION:
                break;

            case PREGUNTAS_UBICACION_VACIA:
                globales.tll.getLecturaActual().is_ubicacion = me.regresaValor(respuesta).substring(1, 2);
                globales.tll.getLecturaActual().deleteAnomalia(me.regresaValor(respuesta).substring(0, 1));
                globales.tll.getLecturaActual().setAnomalia(me.regresaValor(respuesta).substring(0, 1));
                globales.tll.getLecturaActual().setSubAnomalia(me.regresaValor(respuesta));
                break;

            case ANOMALIA_SEIS:
                globales.tll.getLecturaActual().deleteAnomalia("6");
//			globales.tll.getLecturaActual().deleteAnomalia("R");
//			globales.tll.getLecturaActual().deleteAnomalia("Z");
//			globales.tll.getLecturaActual().deleteAnomalia("5");
//			globales.tll.getLecturaActual().deleteAnomalia("G");
//			globales.tll.getLecturaActual().deleteAnomalia(me.regresaValor(respuesta));
                globales.tll.getLecturaActual().setAnomalia(me.regresaValor(respuesta));
                globales.tll.getLecturaActual().setAnomalia("6");
                break;
        }

    }

    @Override
    public ComentariosInputBehavior getCampoGenerico(int campo) {
        ComentariosInputBehavior cib_config = null;
        switch (campo) {
            case CALLE:

                cib_config = new ComentariosInputBehavior("Direccion", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 30, "");
                break;
            case COLONIA:
                cib_config = new ComentariosInputBehavior("Colonia", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 50, "");
                break;
            case PUERTA:
                cib_config = new ComentariosInputBehavior("Número Exterior", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 6, "");
                break;
            case NUM_MEDIDOR:
                cib_config = new ComentariosInputBehavior("Número de Medidor:", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 20, "");
                break;
            case NUM_ESFERAS:
                cib_config = new ComentariosInputBehavior("Digitos:", InputType.TYPE_CLASS_NUMBER, 2, "");
                break;
            case LECTURA:
                cib_config = new ComentariosInputBehavior("Lectura:", InputType.TYPE_CLASS_NUMBER, 8, "");
                break;
            case MARCA:
                cib_config = new ComentariosInputBehavior("Marca Medidor", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 3, "");
                break;
            case TIPO:
                cib_config = new ComentariosInputBehavior("Tipo de Medidor", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 10, "");
                break;
            case SELLO_RET_NUMERO:
                cib_config = new ComentariosInputBehavior("Número de Sello Retirado", InputType.TYPE_CLASS_NUMBER, 10, "");
                cib_config.obligatorio = false;    // Puede estar en blanco
                break;
            case SELLO_RET_ESTADO:
                cib_config = new ComentariosInputBehavior("Estado (NUevo,ENcontrata,INstalado,REtirado,DAñado,VIolado,CLandestino,EXtraviado,ILegible)", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 2, "RE");
                cib_config.obligatorio = false;    // Puede estar en blanco
                break;
            case SELLO_RET_COLOR:
                cib_config = new ComentariosInputBehavior("Color (AZul,BLanco,AMarillo,ROjo,GRis)", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 2, "AZ");
                cib_config.obligatorio = false;    // Puede estar en blanco
                break;
            case SELLO_RET_MODELO:
                cib_config = new ComentariosInputBehavior("Modelo (ROtocil,CAndado,PLomo)", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 2, "CA");
                cib_config.obligatorio = false;    // Puede estar en blanco
                break;
            case SELLO_INST_NUMERO:
                cib_config = new ComentariosInputBehavior("Número de Sello Instalado", InputType.TYPE_CLASS_NUMBER, 10, "");
                cib_config.obligatorio = false;    // Puede estar en blanco
                break;
            case SELLO_INST_COLOR:
                cib_config = new ComentariosInputBehavior("Color Instalado (AZul,BLanco,AMarillo,ROjo,GRis)", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 2, "AZ");
                cib_config.obligatorio = false;    // Puede estar en blanco
                break;
            case SELLO_INST_MODELO:
                cib_config = new ComentariosInputBehavior("Modelo Instalado (ROtocil,CAndado,PLomo)", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 2, "CA");
                cib_config.obligatorio = false;    // Puede estar en blanco
                break;
            case CODIGO_OBSERVACION:
                cib_config = new ComentariosInputBehavior("Código Observación", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 5, "");
                cib_config.obligatorio = false;    // Puede estar en blanco
                break;
            case OBSERVACION:
                cib_config = new ComentariosInputBehavior("Observación", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 25, "");
                cib_config.obligatorio = false;    // Puede estar en blanco
                break;
            case DATOS_CAMPANA:
                cib_config = new ComentariosInputBehavior("Datos de Campaña", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 40, "");
                cib_config.obligatorio = false;    // Puede estar en blanco
                break;
            case OBSERVACIONES:
                cib_config = new ComentariosInputBehavior("Observaciones:", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 30, "");
                cib_config.obligatorio = false;
                break;
            case NIC:
                cib_config = new ComentariosInputBehavior("NIC", InputType.TYPE_CLASS_NUMBER, 7, "");
                cib_config.obligatorio = false;
                break;
        }
        return cib_config;
    }

    @Override
    public int[] getCamposGenerico(String anomalia) {
        int[] campos = null;
        if (anomalia.equals("noregistrados")) {
            campos = new int[5];

            campos[0] = CALLE;
            campos[1] = COLONIA;
            campos[2] = NUM_MEDIDOR;
            campos[3] = LECTURA;
            campos[4] = OBSERVACIONES;

        } else if (anomalia.equals("cambiomedidor")) {
            campos = new int[1];

            campos[0] = NUM_MEDIDOR;
        } else if (anomalia.equals("observacion")) {
            campos = new int[2];
            campos[0] = CODIGO_OBSERVACION;
            campos[1] = OBSERVACION;
        } else if (anomalia.equals("campana")) {
            campos = new int[1];
            campos[0] = DATOS_CAMPANA;
        } else if (anomalia.equals("sellos")) {
            campos = new int[7];
            campos[0] = SELLO_RET_NUMERO;
            campos[1] = SELLO_INST_NUMERO;
            campos[2] = SELLO_RET_ESTADO;
            campos[3] = SELLO_RET_COLOR;
            campos[4] = SELLO_RET_MODELO;
            campos[5] = SELLO_INST_COLOR;
            campos[6] = SELLO_INST_MODELO;
        }
//		else if (anomalia.equals("002") || anomalia.equals("003") || anomalia.equals("004") ||
//				anomalia.equals("101") || anomalia.equals("200") || anomalia.equals("205") || anomalia.equals("208") ) {
//			campos= new int[1];
//			campos[0]=OBSERVACIONES;
//		}
        return campos;
    }

    @Override
    public void regresaDeCamposGenericos(Bundle bu_params, String anomalia) throws Exception {
        String cadena =/*globales.lote*/"";
        Lectura lectura;
        ContentValues cv_datos = new ContentValues();

        if (anomalia.equals("noregistrados")) {
            lectura = globales.tll.getLecturaActual();

            if (lectura == null)
                throw new Exception("No se encontró una lectura para realizar la operación.");

            openDatabase();

//            cadena += Main.rellenaString(bu_params.getString(String.valueOf(CALLE)), " ", 30, false);
//            cadena += Main.rellenaString(bu_params.getString(String.valueOf(COLONIA)), " ", 50, false);
//            cadena += Main.rellenaString(bu_params.getString(String.valueOf(NUM_MEDIDOR)), " ", 20, false);
//            cadena += Main.rellenaString(bu_params.getString(String.valueOf(NUM_ESFERAS)), "0", 2, true);
//            cadena += Main.rellenaString(bu_params.getString(String.valueOf(LECTURA)), "0", 8, true);
//
//            cadena += Main.rellenaString(bu_params.getString(String.valueOf(OBSERVACIONES)), " ", 25, false);

            cv_datos.put("envio", 1);
            cv_datos.put("idArchivo", lectura.idArchivo);
            cv_datos.put("idLectura", lectura.poliza);
            cv_datos.put("idUnidadLect", lectura.idUnidadLect);
            cv_datos.put("idEmpleado", globales.getIdEmpleado());
            cv_datos.put("Calle", bu_params.getString(String.valueOf(CALLE)));
            cv_datos.put("Colonia", bu_params.getString(String.valueOf(COLONIA)));
            cv_datos.put("NumMedidor", bu_params.getString(String.valueOf(NUM_MEDIDOR)));
            cv_datos.put("Lectura", bu_params.getString(String.valueOf(LECTURA)));
            cv_datos.put("Observaciones", bu_params.getString(String.valueOf(OBSERVACIONES)));
            cv_datos.put("TipoRegistro", "NR");

            //Guardamos en la bd
            //db.execSQL("insert into noRegistrados (envio, poliza) values(1, '" + cadena + "')");

            db.insert("noRegistrados", null, cv_datos);

            closeDatabase();
        } else if (anomalia.equals("cambiomedidor")) {
            lectura = globales.tll.getLecturaActual();

            if (lectura == null)
                throw new Exception("No se encontró una lectura para realizar la operación.");

            cv_datos.put("envio", 1);
            cv_datos.put("idArchivo", lectura.idArchivo);
            cv_datos.put("idLectura", lectura.poliza);
            cv_datos.put("idUnidadLect", lectura.idUnidadLect);
            cv_datos.put("Calle", lectura.getDireccion());
            cv_datos.put("Colonia", lectura.getColonia());
            cv_datos.put("NumMedidor", bu_params.getString(String.valueOf(NUM_MEDIDOR)));
            cv_datos.put("TipoRegistro", "CM");

            openDatabase();

            // db.execSQL("insert into noRegistrados(envio, poliza) values(1, '" + cadena + "')");

            db.insert("noRegistrados", null, cv_datos);

            closeDatabase();
        } else if (anomalia.equals("sellos")) {
            //Hay que poner el sello de instalacion
            String retirado = bu_params.getString(String.valueOf(SELLO_RET_NUMERO)).trim().equals("") ? "0" : bu_params.getString(String.valueOf(SELLO_RET_NUMERO)).trim();
            String instalado = bu_params.getString(String.valueOf(SELLO_INST_NUMERO)).trim().equals("") ? "0" : bu_params.getString(String.valueOf(SELLO_INST_NUMERO)).trim();
            globales.tll.getLecturaActual().selloRetNumero = retirado;
            openDatabase();
            db.execSQL("Update ruta set selloRetNumero='" + retirado + "', " +
                    "selloRetEstado='" + ObtenerCodigo(bu_params.getString(String.valueOf(SELLO_RET_ESTADO)), 3) + "', " +
                    "selloRetColor='" + ObtenerCodigo(bu_params.getString(String.valueOf(SELLO_RET_COLOR)), 1) + "', " +
                    "selloRetModelo='" + ObtenerCodigo(bu_params.getString(String.valueOf(SELLO_RET_MODELO)), 2) + "', " +
                    "selloInstNumero='" + instalado + "', " +
                    "selloInstColor='" + ObtenerCodigo(bu_params.getString(String.valueOf(SELLO_INST_COLOR)), 1) + "', " +
                    "selloInstModelo='" + ObtenerCodigo(bu_params.getString(String.valueOf(SELLO_INST_MODELO)), 2) + "'" +
                    " where secuenciaReal=" + globales.il_lect_act);
            closeDatabase();
        } else if (anomalia.equals("observacion")) {
        } else if (anomalia.equals("campana")) {
        } else {
            globales.tll.getLecturaActual().ls_codigoObservacion = "OB032";
            globales.tll.getLecturaActual().setComentarios(bu_params.getString("input"));
        }
    }

    // CE, REVISAR
    @Override
    public ContentValues getCamposBDAdicionales() {
        // TODO Auto-generated method stub
        ContentValues cv_params = new ContentValues();

//		cv_params.put("intentos", 0);
//		cv_params.put("sospechosa", "0");
////		cv_params.put("nisRad", 0);
//		cv_params.put("dondeEsta", "");
//		cv_params.put("anomInst", "");
//		cv_params.put("tipoLectura", "");
//		cv_params.put("comentarios", "");
//		cv_params.put("hora", "");
//		cv_params.put("fecha", "");
//		cv_params.put("lectura", "");
//		cv_params.put("anomalia", "");
////		cv_params.put("sectorCorto", "");
////		cv_params.put("sectorLargo", "");
////		cv_params.put("comoLlegar2", "");
////		cv_params.put("comoLlegar1", "");
//		cv_params.put("intento1", "");
//		cv_params.put("intento2", "");
//		cv_params.put("intento3", "");
//		cv_params.put("intento4", "");
//		cv_params.put("intento5", "");
//		cv_params.put("intento6", "");
//		cv_params.put("intento7", "");
        return cv_params;
    }

    // CE, REVISAR
    @Override
    public void creaTodosLosCampos() {
//		globales.tlc.add(new Campo(21, "anomaliaDeInstalacion", 11, 1, Campo.D, "0"));//Distinta		// CE, REVISAR, *** HAY UN CAMPO QUE NO TENIA ARGENTINA ***
//		globales.tlc.add(new Campo(27, "nisRad", 16, 10, Campo.D, " "));
//		globales.tlc.add(new Campo(27, "poliza", 16, 10, Campo.D, " "));
//		globales.tlc.add(new Campo(5, "comoLlegar1", 93, 23, Campo.I, " "));
//		globales.tlc.add(new Campo(5, "comoLlegar2", 116, 50, Campo.I, " "));
//		globales.tlc.add(new Campo(8,"sectorCorto", 307, 3, Campo.D, "0"));
//		globales.tlc.add(new Campo(8,"sectorLargo", 310, 20, Campo.D, "0"));
//		globales.tlc.add(new Campo(31, "hora", 400, 6, Campo.F, "his", false));
//		globales.tlc.add(new Campo(8, "baremo", 487, 3, Campo.D, "0", false));
//		globales.tlc.add(new Campo(9, "advertencias", 119, 0, Campo.I, " "));
//		globales.tlc.add(new Campo(10, "ilr", 121, 0, Campo.D, "0"));
//		globales.tlc.add(new Campo(11, "ism", 128, 0, Campo.D, "0"));
//		globales.tlc.add(new Campo(12, "saldoEnMetros", 129, 0, Campo.D, "0"));
//		globales.tlc.add(new Campo(15, "consumo", 144, 0, Campo.D, "0"));
//		globales.tlc.add(new Campo(18, "divisionContrato", 159, 0, Campo.D, "0"));
//		globales.tlc.add(new Campo(23, "tipoMedidor", 390, 1, Campo.I, " "));
//		globales.tlc.add(new Campo(27, "indicadorGPS", 349, 1, Campo.D, " "));
//		globales.tlc.add(new Campo(0, "sinUso1", 0, 16, Campo.I, " "));
        globales.tlc.add(new Campo(0, "poliza", 0, 10, Campo.D, " "));
        globales.tlc.add(new Campo(1, "tarifa", 10, 30, Campo.I, " "));
        globales.tlc.add(new Campo(2, "cliente", 40, 60, Campo.I, " "));
        globales.tlc.add(new Campo(3, "colonia", 100, 50, Campo.I, " "));
        globales.tlc.add(new Campo(4, "toma", 150, 4, Campo.I, " "));
        globales.tlc.add(new Campo(5, "giro", 154, 6, Campo.I, " "));
        globales.tlc.add(new Campo(6, "direccion", 160, 50, Campo.I, " "));
        globales.tlc.add(new Campo(7, "marcaMedidor", 210, 5, Campo.I, " "));
        globales.tlc.add(new Campo(8, "serieMedidor", 215, 20, Campo.D, " "));
        globales.tlc.add(new Campo(9, "sectorcorto", 235, 50, Campo.I, " ")); //Clave de localizacion
        globales.tlc.add(new Campo(10, "numEsferas", 285, 2, Campo.D, " "));


        globales.tlc.add(new Campo(11, "consBimAnt", 287, 8, Campo.D, "0"));
        globales.tlc.add(new Campo(12, "consAnoAnt", 295, 8, Campo.D, "0"));
        globales.tlc.add(new Campo(13, "sinUso1", 303, 10, Campo.I, " "));

        globales.tlc.add(new Campo(14, "lecturaanterior", 313, 8, Campo.I, " "));
        globales.tlc.add(new Campo(15, "sinUso2", 321, 10, Campo.I, " "));
        globales.tlc.add(new Campo(16, "sinUso3", 331, 10, Campo.I, " "));
        globales.tlc.add(new Campo(17, "sinUso4", 341, 3, Campo.I, " "));

        // 2022-09-26 / RL / Se agregan nuevas columnas

        globales.tlc.add(new Campo(18, "idArchivo", 344, 10, Campo.I, " "));
        globales.tlc.add(new Campo(19, "codigoBarras", 354, 50, Campo.I, " "));
        globales.tlc.add(new Campo(20, "TipoDeAcuse", 454, 10, Campo.I, " "));
        globales.tlc.add(new Campo(21, "nota2", 414, 10, Campo.I, " "));

        // CE, 10/10/22, Vamos a agregar unas Columnas Nuevas
        globales.tlc.add(new Campo(22, "miLatitud", 424, 5, Campo.I, " "));
        globales.tlc.add(new Campo(23, "miLongitud", 429, 5, Campo.I, " "));
        globales.tlc.add(new Campo(24, "Estimaciones", 434, 10, Campo.I, " "));
        globales.tlc.add(new Campo(25, "TipoDeCliente", 444, 10, Campo.I, " "));
        globales.tlc.add(new Campo(26, "nota1", 404, 10, Campo.I, " "));

        // RL, 13/12/22, Columnas nuevas
        globales.tlc.add(new Campo(27, "Porcion", 464, 10, Campo.I, " "));
        globales.tlc.add(new Campo(28, "idUnidadLect", 474, 2, Campo.I, " "));
        globales.tlc.add(new Campo(29, "idRegionalLect", 476, 2, Campo.I, " "));
        globales.tlc.add(new Campo(30, "Regional", 478, 2, Campo.I, " "));
        globales.tlc.add(new Campo(31, "IntercambiarSerieMedidor", 480, 1, Campo.I, " "));

        // Columnas que ya existían estaban en este código de la versión 1.0.11 y anteriores

        globales.tlc.add(new Campo(11, "tipoLectura", 405, 1, Campo.I, " ", false));
        globales.tlc.add(new Campo(12, "lectura", 406, 8, Campo.I, " ", false));
        globales.tlc.add(new Campo(13, "anomalia", 420, 3, Campo.I, " ", false));
        globales.tlc.add(new Campo(14, "fecha", 425, 10, Campo.F, "d/m/y", false));
        globales.tlc.add(new Campo(15, "hora", 425, 6, Campo.F, "his", false));
        globales.tlc.add(new Campo(16, "sospechosa", 439, 2, Campo.D, "0", false));
        globales.tlc.add(new Campo(16, "intentos", 439, 2, Campo.D, "0", false));


        globales.tlc.add(new Campo(51, "comentarios", 501, 60, Campo.I, " ", false));
        globales.tlc.add(new Campo(51, "lecturista", 501, 10, Campo.I, " ", false));

        globales.tlc.add(new Campo(51, "latitud", 501, 20, Campo.I, " ", false));
        globales.tlc.add(new Campo(51, "longitud", 501, 20, Campo.I, " ", false));


    }

    // CE, REVISAR
    @Override
    public long getLecturaMinima() {
        return globales.tll.getLecturaActual().consBimAnt;
    }

    // CE, REVISAR
    @Override
    public long getLecturaMaxima() {
        return globales.tll.getLecturaActual().consAnoAnt;
    }

    // CE, Es para llenar la cuadricula
    @Override
    public String obtenerContenidoDeEtiqueta(String ls_etiqueta) {
        // TODO Auto-generated method stub
        if (ls_etiqueta.equals("campo0")) {
            return globales.tll.getLecturaActual().getTipoDeCliente().trim();
        } else if (ls_etiqueta.equals("campo1")) {
            return globales.tll.getLecturaActual().is_giro.trim();
        } else if (ls_etiqueta.equals("campo2")) {
            return globales.tll.getLecturaActual().getEstimacionesEngie().trim();
        } else if (ls_etiqueta.equals("campo3")) {
            return globales.tll.getLecturaActual().getTipoDeAcuse().trim();
        } else {
            return "";
        }
    }


    public String obtenerTituloDeEtiqueta(String ls_etiqueta) {
        if (ls_etiqueta.equals("campo0")) {
            return "Tipo";
        } else if (ls_etiqueta.equals("campo1")) {
            return "Aviso";
        } else if (ls_etiqueta.equals("campo2")) {
            return "Estim.";
        } else if (ls_etiqueta.equals("campo3")) {
            return "Acuse";
        } else {
            return super.obtenerTituloDeEtiqueta(ls_etiqueta);
        }
    }

    // CE, REVISAR
    @Override
    public FormatoDeEtiquetas getMensajedeRespuesta() {
        // TODO Auto-generated method stub
//		if (globales.tll.getLecturaActual().is_aviso.startsWith("Act")) {
//			return new FormatoDeEtiquetas(globales.tll.getLecturaActual().is_aviso.trim(), R.color.DarkGreen);
//		}else if (globales.tll.getLecturaActual().is_aviso.startsWith("Dem")) {
//			return new FormatoDeEtiquetas(globales.tll.getLecturaActual().is_aviso.trim(), R.color.Red);
//		}else if(globales.tll.getLecturaActual().is_aviso.startsWith("Rea")) {
//			return new FormatoDeEtiquetas(globales.tll.getLecturaActual().is_aviso.trim(), R.color.blue);
//		}else{
//			return new FormatoDeEtiquetas(globales.tll.getLecturaActual().is_aviso.trim(), R.color.Orange);
//		}

        if (globales.tll.getLecturaActual().getTipoDeAcuse().trim().equals("1"))
            return new FormatoDeEtiquetas("Acuse por Reclamacion", R.color.BlueViolet);
        else if (globales.tll.getLecturaActual().getTipoDeAcuse().trim().equals("2"))
            return new FormatoDeEtiquetas("Acuse por Cliente Nuevo", R.color.Brown);
        else if (globales.tll.getLecturaActual().getTipoDeAcuse().trim().equals("3"))
            return new FormatoDeEtiquetas("Acuse por Comercio", R.color.Coral);
        else if (globales.tll.getLecturaActual().getTipoDeAcuse().trim().equals("4"))
            return new FormatoDeEtiquetas("Acuse por Cliente VIP", R.color.DarkSalmon);
        else if (globales.tll.getLecturaActual().is_giro.trim().equals("20"))
            return new FormatoDeEtiquetas("Cliente suscrito a Paperless", R.color.Blue);
        else
            return new FormatoDeEtiquetas(globales.tll.getLecturaActual().is_tarifa.trim(), R.color.Orange);
//		return null;
    }

    @Override
    public String getMensajedeAdvertencia() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public void regresaDeBorrarLectura() {
    }

    // CE, REVISAR
    @Override
    public void cambiosAnomaliaAntesDeGuardar(String ls_lect_act) {
        // TODO Auto-generated method stub
    }

    @Override
    public void anomaliasARepetir() {
    }

    @Override
    public void subAnomaliasARepetir() {
    }

    @Override
    public boolean avanzarDespuesDeAnomalia(String ls_anomalia, String ls_subAnom, boolean guardar) {
//		if (esSegundaVisita(ls_anomalia, ls_subAnom)){
//			//Grabamos
//			if (guardar)
//				globales.tll.getLecturaActual().guardar(true, globales.tll.getSiguienteOrdenDeLectura());
//			return true;
//		}
        return false;
    }

    @Override
    public boolean esSegundaVisita(String ls_anomalia, String ls_subAnom) {
        return false;
    }

    // CE, REVISAR
    @Override
    public String getDescripcionDeBuscarMedidor(Lectura lectura,
                                                int tipoDeBusqueda, String textoBuscado) {
        String ls_preview = "";
        String serieMedidor = "";
        String codigoBarrasMedidor = "";

        switch (tipoDeBusqueda) {
            case BuscarMedidorTabsPagerAdapter.MEDIDOR:
                serieMedidor = lectura.is_serieMedidor;
                codigoBarrasMedidor = lectura.codigoBarras;

                if (serieMedidor.equals(codigoBarrasMedidor))
                    ls_preview = Lectura.marcarTexto(serieMedidor, textoBuscado, false);
                else {
                    ls_preview = "#SM: "+ Lectura.marcarTexto(serieMedidor, textoBuscado, false);
                    ls_preview += "<br>#CB: " + Lectura.marcarTexto(codigoBarrasMedidor, textoBuscado, false);
                }

                ls_preview += "<br>" + lectura.is_sectorCorto;
                if (!lectura.getColonia().equals(""))
                    ls_preview += "<br>" + lectura.getColonia();
                ls_preview += "<br>" + lectura.getDireccion();
                break;

            case BuscarMedidorTabsPagerAdapter.DIRECCION:
                serieMedidor = lectura.is_serieMedidor;
                codigoBarrasMedidor = lectura.codigoBarras;

                if (serieMedidor.equals(codigoBarrasMedidor))
                    ls_preview = Lectura.marcarTexto(serieMedidor, textoBuscado, false);
                else {
                    ls_preview = "#SM: "+ Lectura.marcarTexto(serieMedidor, textoBuscado, false);
                    ls_preview += "<br>#CB: " + Lectura.marcarTexto(codigoBarrasMedidor, textoBuscado, false);
                }

                ls_preview += "<br>" + Lectura.marcarTexto(lectura.is_sectorCorto, textoBuscado, false);
                if (!lectura.getColonia().equals(""))
                    ls_preview += "<br>" + Lectura.marcarTexto(lectura.getColonia(), textoBuscado, false);
                ls_preview += "<br>" + Lectura.marcarTexto(lectura.getDireccion(), textoBuscado, false);
                break;
        }

        return ls_preview;
    }

    public SpannableStringBuilder getDescripcionDeBuscarMedidorColor(Lectura lectura,
                                                                     int tipoDeBusqueda, String textoBuscado) {
        SpannableStringBuilder ls_preview = new SpannableStringBuilder();
        String serieMedidor = "";
        String codigoBarrasMedidor = "";

        switch (tipoDeBusqueda) {
            case BuscarMedidorTabsPagerAdapter.MEDIDOR:
                serieMedidor = lectura.is_serieMedidor;
                codigoBarrasMedidor = lectura.codigoBarras;

                if (serieMedidor.equals(codigoBarrasMedidor))
                    ls_preview.append(Lectura.marcarTexto(serieMedidor, textoBuscado, false));
                else {
                    ls_preview.append("#SM: ");
                    ls_preview.append(Lectura.marcarTexto(serieMedidor, textoBuscado, false));
                    ls_preview.append("<br>#CB: ");
                    ls_preview.append(Lectura.marcarTexto(codigoBarrasMedidor, textoBuscado, false));
                }

                ls_preview.append("<br>" + lectura.is_sectorCorto);
                if (!lectura.getColonia().equals(""))
                    ls_preview.append("<br>" + lectura.getColonia());
                ls_preview.append("<br>" + lectura.getDireccion());
                break;

            case BuscarMedidorTabsPagerAdapter.DIRECCION:
                ls_preview.append(lectura.is_serieMedidor);

                ls_preview.append("<br>" + Lectura.marcarTexto(lectura.is_sectorCorto, textoBuscado, false));
                if (!lectura.getColonia().equals(""))
                    ls_preview.append("<br>" + Lectura.marcarTexto(lectura.getColonia(), textoBuscado, false));
                ls_preview.append("<br>" + Lectura.marcarTexto(lectura.getDireccion(), textoBuscado, false));
                break;
        }

        return ls_preview;
    }

    @Override
    public String validaAnomalia(String ls_anomalia) {
        return "";
    }

    @Override
    public String getPrefijoComentario(String ls_anomalia) {
        return "";
    }

    @Override
    public void repetirAnomalias() {
    }

    // CE, REVISAR
    @Override
    public void setConsumo() {

//		globales.tll.getLecturaActual().deleteAnomalia("LI");
//		globales.tll.getLecturaActual().deleteAnomalia("CB");
//		globales.tll.getLecturaActual().deleteAnomalia("CE");
//		try{
//			long lectura =Long.parseLong(globales.is_lectura);
//			if (lectura<globales.tll.getLecturaActual().lecturaAnterior){
//				globales.tll.getLecturaActual().setAnomalia("LI");
//			}
//			else if (lectura<getLecturaMinima()){
//				globales.tll.getLecturaActual().setAnomalia("CB");
//			}
//			else if  (lectura>getLecturaMaxima()){
//				globales.tll.getLecturaActual().setAnomalia("CE");
//			}
//		}
//		catch(Throwable e){
//
//		}

    }

    @Override
    public long getConsumo(String lectura) {
        return 0;
    }

    public void setTipoLectura() {
        super.setTipoLectura();
    }

    @Override
    public String validaCamposGenericos(String anomalia, Bundle bu_params) {
        if (anomalia.equals("sellos")) {
            if (bu_params.getString(String.valueOf(SELLO_RET_NUMERO)).trim().equals("") &&
                    bu_params.getString(String.valueOf(SELLO_INST_NUMERO)).equals("")) {
                return "Los campos de número de sello retirado y número de sello instalado no puden estar vacios, llene alguno de ellos.";
            }
        }

        return "";
    }

    @Override
    public MensajeEspecial regresaDeAnomalias(String ls_anomalia, boolean esAnomalia) {
        Cursor c = null;

        try {
            if (esAnomalia) {
                return null;
            }
            openDatabase();

            c = db.rawQuery("Select * from anomalia where substr(desc, 1, 3)='" + ls_anomalia + "'", null);

            if (c.getCount() == 0) {
                c.close();
                closeDatabase();
                return null;
            }
            c.moveToFirst();

            if (Utils.getString(c, "subanomalia", "").equals("S")) {
                String desc = Utils.getString(c, "desc", "");
//			desc=Main.rellenaString(desc, " ", 18, false);
//			globales.tll.getLecturaActual().setComentarios("OB"+desc.substring(0,3));
                globales.tll.getLecturaActual().ls_codigoObservacion = "OB" + desc.substring(0, 3);
            }
        } catch (Throwable t) {
            Utils.showMessageLong(context, t.getMessage());
        } finally {
            if (c != null)
                c.close();
            closeDatabase();
            return null;
        }
    }

    @Override
    public boolean puedoRepetirAnomalia() {
        return false;
    }

    @Override
    public String remplazaValorDeArchivo(int tipo, String ls_anomalia, String valor) {
        return valor;
    }

    @Override
    public void cambiosAnomalia(String anomalia) {
    }

    public void cambiosAlBorrarAnomalia(String anomaliaBorrada) {
    }

    public void inicializaTablaDeCalidades() {
    }

    @Override
    public int cambiaCalidadSegunTabla(String Anomalia, String subAnomalia) {
//		Integer calidad = 20;
        Integer calidad = globales.calidadDeLaFoto;
        return calidad.intValue();
    }

    public boolean continuarConLaFoto() {
        return true;
    }

    public void AgregarAnomaliasManualmente(SQLiteDatabase db) {
    }

    public void accionesDespuesDeCargarArchivo(SQLiteDatabase db) {

        db.execSQL("update ruta set indicadorGPS=1");

// CE, 23/09/15, Vamos a hacer una prueba para Ecogas Chihuahua'
//		db.execSQL("update anomalia set conv='NL' where conv='N'");
//		db.execSQL("update anomalia set conv='RG' where conv='R'");

//		db.execSQL("update anomalia set conv='CE' where conv='1'");
//		db.execSQL("update anomalia set conv='CB' where conv='2'");
//		db.execSQL("update anomalia set conv='LI' where conv='L'");
    }

    public void agregarRegistroRaro(SQLiteDatabase db, String registro) {
    }

    public boolean esUnRegistroRaro(String registro) {
        return false;
    }

    public int[] getArchivosATransmitir(int metodoDeTransmision) {
        return new int[0];
    }

    public String getNombreArchvio(int tipo) throws Exception {

        String ls_archivo = "";
        TransmitionObject to = new TransmitionObject();

        switch (tipo) {
//		case SALIDA:
////			TransmitionObject to= new TransmitionObject();
//			getEstructuras(  to,  TransmisionesPadre.TRANSMISION,TransmisionesPadre.WIFI);
//
//			to.ls_categoria=to.ls_categoria.substring(0, to.ls_categoria.length()-1)+"D";
//
//			return to.ls_categoria;
            case NO_REGISTRADOS:
//			TransmitionObject to= new TransmitionObject();
                getEstructuras(to, TransmisionesPadre.TRANSMISION, TransmisionesPadre.WIFI);

                to.ls_categoria = to.ls_categoria.substring(0, to.ls_categoria.length() - 3) + "NEW";

                return to.ls_categoria;
        }

        return super.getNombreArchvio(tipo);
    }

    public Cursor getContenidoDelArchivo(SQLiteDatabase db, int tipo) {

        String ls_select = "";

        Cursor c = null;

        switch (tipo) {
            case SALIDA:
                //Primero el encabezado


                //Ya tenemos el encabezado, ahora el query normal
                ls_select = "Select 'E:'||";
                ls_select += globales.tlc.getCampoObjeto("poliza").campoSQLFormateado();
                ls_select += " || " + globales.tlc.getCampoObjeto("lectura").campoSQLFormateado();
                ls_select += " || " + globales.tlc.getCampoObjeto("lecturista").campoSQLFormateado();
                ls_select += " || '" + Main.obtieneFecha("mdY") + "15" + Main.obtieneFecha("his") + "'";
                ls_select += " || case tipoLectura='4' then '0' else '1' end";
                ls_select += " || " + globales.tlc.getCampoObjeto("anomalia").campoSQLFormateado();
                ls_select += " || " + globales.tlc.getCampoObjeto("hora").campoSQLFormateado();
                ls_select += " || " + globales.tlc.getCampoObjeto("fecha").campoSQLFormateado();
                ls_select += " || " + globales.tlc.getCampoObjeto("sospechosa").campoSQLFormateado();
                ls_select += " || " + globales.tlc.getCampoObjeto("poliza").campoSQLFormateado();
                ls_select += " || " + globales.tlc.getCampoObjeto("nisrad").campoSQLFormateado();

                ls_select += " TextoSalida from ruta union ";
                ls_select += "select 'RFIN' TextoSalida";
                c = db.rawQuery(ls_select, null);
                break;
        }
        return c;
    }

    public int opcionAElegir(int tipo) {
        switch (tipo) {
            case ARCHIVO_MENSAJES:
                return trasmisionDatosBt.OPCION_CPL_A_PC_MENSAJES;
        }
        return 0;
    }

    /*
        Regresa una clase con la información del resumen, de manera que pueda usarse en donde se ocupan sin los datos formateados.
     */

    public ResumenEntity getResumenEntity(SQLiteDatabase db) throws Exception {
        Cursor c;
        ResumenEntity resumen = new ResumenEntity();

        c = db.rawQuery("Select count(*) canti from Ruta", null);
        c.moveToFirst();
        resumen.totalRegistros = Utils.getLong(c, "canti", 0);

        try {
            c = db.rawQuery("Select value from config where key='cpl'", null);
            c.moveToFirst();
            resumen.archivo = Utils.getString(c, "value", "");
        } catch (Throwable e) {
            resumen.archivo = "";
        }

        c = db.rawQuery("Select count(*) canti from ruta where tipoLectura='0'", null);
        c.moveToFirst();
        resumen.cantLecturasRealizadas = Utils.getLong(c, "canti", 0);

        c = db.rawQuery("Select count(*) canti from fotos", null);
        c.moveToFirst();
        resumen.cantFotos = Utils.getLong(c, "canti", 0);
        c.close();

        c = db.rawQuery("Select count(*) canti from ruta where tipoLectura='4'", null);
        c.moveToFirst();
        resumen.cantSinLectura = Utils.getLong(c, "canti", 0);
        c.close();

        c = db.rawQuery("Select count(*) canti from ruta where trim(anomalia)<>''", null);
        c.moveToFirst();
        resumen.cantConAnomalia = Utils.getLong(c, "canti", 0);
        c.close();

        c = db.rawQuery("Select count(*) canti from ruta where trim(tipoLectura)=''", null);
        c.moveToFirst();
        resumen.cantLecturasPendientes = Utils.getLong(c, "canti", 0);
        c.close();

        c = db.rawQuery("Select count(*) canti from NoRegistrados", null);
        c.moveToFirst();
        resumen.cantNoRegistrados = Utils.getLong(c, "canti", 0);
        c.close();

        c = db.rawQuery("Select count(*) canti from ruta where envio=1", null);
        c.moveToFirst();
        resumen.ordenesSinEnviar = Utils.getLong(c, "canti", 0);
        c.close();

        c = db.rawQuery("Select count(*) canti from fotos where envio=1", null);
        c.moveToFirst();
        resumen.fotosSinEnviar = Utils.getLong(c, "canti", 0);
        c.close();

        return resumen;
    }

    public Vector<EstructuraResumen> getResumen(ResumenEntity resumenIn) {
        float porcentaje = 0;
        Vector<EstructuraResumen> resumenOut = new Vector<EstructuraResumen>();

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("##0.00", otherSymbols);

        // RLR, 2022-10-23, Se agregan información de la unidad.
        try {
            resumenOut.add(new EstructuraResumen(DbLecturasMgr.getInstance().getUnidades(this.context), "Unidad:"));
        } catch (Exception e) {
            resumenOut.add(new EstructuraResumen("---", "Unidad:"));
        }

        resumenOut.add(new EstructuraResumen("", ""));

        resumenOut.add(new EstructuraResumen("Lecturas en la Ruta", String.valueOf(resumenIn.totalRegistros)));
        resumenOut.add(new EstructuraResumen(context.getString(R.string.msj_main_fotos_tomadas), String.valueOf(resumenIn.cantFotos)));
        resumenOut.add(new EstructuraResumen("", ""));

        porcentaje = (((float) resumenIn.cantLecturasPendientes * 100) / (float) resumenIn.totalRegistros);
        resumenOut.add(new EstructuraResumen("Lecturas Pendientes", String.valueOf(resumenIn.cantLecturasPendientes), formatter.format(porcentaje) + "%"));
        porcentaje = (((float) resumenIn.cantLecturasRealizadas * 100) / (float) resumenIn.totalRegistros);
        resumenOut.add(new EstructuraResumen("Registros con Lectura", String.valueOf(resumenIn.cantLecturasRealizadas), formatter.format(porcentaje) + "%"));
        porcentaje = (((float) resumenIn.cantSinLectura * 100) / (float) resumenIn.totalRegistros);
        resumenOut.add(new EstructuraResumen("Registros sin Lectura", String.valueOf(resumenIn.cantSinLectura), formatter.format(porcentaje) + "%"));


        resumenOut.add(new EstructuraResumen("", "")); //Agregamos una linea mas

        resumenOut.add(new EstructuraResumen("Lecturas por Enviar", String.valueOf(resumenIn.ordenesSinEnviar)));
        resumenOut.add(new EstructuraResumen("Fotos por Enviar", String.valueOf(resumenIn.fotosSinEnviar)));

        resumenOut.add(new EstructuraResumen("", "")); //Agregamos una linea mas

//    	 resumenOut.add(new EstructuraResumen("Mensajes", String.valueOf(resumenIn.mensajes)));
        if (globales.mostrarNoRegistrados)
            resumenOut.add(new EstructuraResumen("No Registrados", String.valueOf(resumenIn.cantNoRegistrados)));

        resumenOut.add(new EstructuraResumen("", "")); //Agregamos una linea mas

        return resumenOut;
    }

    public Vector<EstructuraResumen> getResumen(SQLiteDatabase db) throws Exception {
        ResumenEntity resumenIn;
        Vector<EstructuraResumen> resumenOut = null;

        resumenIn = getResumenEntity(db);
        if (resumenIn != null)
            resumenOut = getResumen(resumenIn);

        return resumenOut;
    }

    public void activacionDesactivacionOpciones(boolean esSuperUsuario) {
        if (esSuperUsuario && globales != null) {
            globales.mostrarMetodoDeTransmision = false;
            globales.mostrarServidorGPRS = true;
            globales.mostrarBorrarRuta = true;
        }
    }

    public void procesosAlEntrar() {
        openDatabase();

        Cursor c = db.rawQuery("Select * from usuarios", null);

        if (c.getCount() == 0)
            new Usuario(context, "!                    administrador       2002                ADMIN                                                                                                                                                                                                                                                                                                                                                                                                                                  304075", db);

        c.close();
        closeDatabase();
    }

    public void EncriptarDesencriptar(byte[] medidor) {
        byte comodin0 = 2;
        byte comodin1 = 3;
        byte comodin2 = 4;

        int X = 0;
        int rep = 0;
        byte byteLetra;

        for (int i = 0; i < medidor.length - 2; i++) {
            byteLetra = medidor[i];
            if (byteLetra == 10) medidor[i] = 10;
            else if (byteLetra == 9) medidor[i] = 9;
            else if (byteLetra == 94) medidor[i] = 94;
            else if (X == 0) medidor[i] = (byte) (byteLetra ^ comodin0);
            else if (X == 1) medidor[i] = (byte) (byteLetra ^ comodin1);
            else if (X == 2) medidor[i] = (byte) (byteLetra ^ comodin2);

            X = X + (byte) 1;
            if ((X == 2) && (rep == 0)) {
                X = 1;
                rep = 1;
            } else {
                if ((X == 2) && (rep > 0)) rep = 0;
            }
            if (X == 3) X = 0;
        }
    }

    public String getEstructuras(TransmitionObject to, int tipo, int tipoTransmision) throws Exception {
        openDatabase();
        String resultado = getEstructuras(db, to, tipo, tipoTransmision);
        closeDatabase();
        return resultado;
    }

    public String getEstructuras(SQLiteDatabase db, TransmitionObject to, int tipo, int tipoTransmision) throws Exception {
        String ls_subcarpeta, ls_ruta, ls_itinerario, ls_unicom;
        //openDatabase();

        //Tomamos el servidor desde la pantalla de configuracion
        Cursor c = null;

//			 if (tipoTransmision==TransmisionesPadre.BLUETOOTH){
        c = db.rawQuery("Select value from config where key='server_gprs'", null);
        c.moveToFirst();
        if (!validaCampoDeConfig(c))
            return String.format(context.getString(R.string.msj_config_no_disponible), context.getString(R.string.info_servidorGPRS), context.getString(R.string.str_configuracion), context.getString(R.string.info_servidorGPRS));
//			 }

        to.ls_servidor = Utils.getString(c, "value", "");
        c.close();
        //Ahora vamos a ver que archivo es el que vamos a recibir... para nicaragua es el clp + la extension
        //Lo vamos a guardar en categoria, Asi le llamamos a los archivos desde "SuperLibretaDeDirecciones" 2013 (c) ;)


        c = db.rawQuery("Select value from config where key='cpl'", null);

        c.moveToFirst();
        if (!validaCampoDeConfig(c))
            return String.format(context.getString(R.string.msj_config_no_disponible), "CPL", context.getString(R.string.str_configuracion), "CPL");

        //ls_categoria="";
        ls_itinerario = Utils.getString(c, "value", "");
        c.close();

        //Por ultimo la ruta de descarga... Como es un servidor web, hay que quitarle el C:\... mejor empezamos desde lo que sigue. De cualquier manera, deberá tener el siguiente formato
        //Ruta de descarga.subtr(3) + Entrada  + \ + lote
        c = db.rawQuery("Select value from config where key='ruta_descarga'", null);
        c.moveToFirst();
        if (!validaCampoDeConfig(c))
            return String.format(context.getString(R.string.msj_config_no_disponible), context.getString(R.string.info_rutaDescarga), context.getString(R.string.str_configuracion), context.getString(R.string.info_rutaDescarga));

        to.ls_carpeta = Utils.getString(c, "value", "");


        c.close();

        if (tipoTransmision == TransmisionesPadre.WIFI || tipoTransmision == TransmisionesPadre.GPRS) {
            if (to.ls_carpeta.indexOf(":") >= 0) {
                to.ls_carpeta = to.ls_carpeta.substring(to.ls_carpeta.indexOf(":") + 2);
            }
//				   to.ls_carpeta="";

            if (to.ls_servidor.endsWith("/") && !to.ls_servidor.equals(""))
                to.ls_servidor += to.ls_servidor.substring(0, to.ls_servidor.length() - 1);

        }


//			   if (!to.ls_carpeta.endsWith("\\") && !to.ls_carpeta.equals("") )
//				   to.ls_carpeta+="\\";
//
        if (to.ls_carpeta.endsWith("\\") && !to.ls_carpeta.equals(""))
            to.ls_carpeta = to.ls_carpeta.substring(0, to.ls_carpeta.length() - 1);

        to.ls_carpeta += tipo == TransmisionesPadre.TRANSMISION ? "Entrada" : "Salida";

        to.ls_carpeta += "\\activos";

//				   if (tipo==TransmisionesPadre.TRANSMISION){
//					   to.ls_carpeta+="\\" + Main.obtieneFecha("ymd");
//				   }


//			   c=db.rawQuery("Select value from config where key='lote'", null);
//			   c.moveToFirst();
//			   if (!validaCampoDeConfig(c ))
//					  return String.format(context.getString(R.string.msj_config_no_disponible),context.getString(R.string.info_lote) , context.getString(R.string.str_configuracion),context.getString(R.string.info_lote));
//
//			   ls_subcarpeta=  c.getString(c.getColumnIndex("value"));
//
//			   ls_carpeta+="\\" + ls_subcarpeta ;
//
//			   c.close();

        to.ls_categoria = ls_itinerario + ".tpl2";

        // closeDatabase();
        return "";
    }


    public String encabezadoAEnviar(String ls_carpeta, String ls_categoria) {
        return globales.letraPais + ls_carpeta + "\\";
    }


    public byte[] encabezadoAMandar(SQLiteDatabase db) throws Exception {
        TransmitionObject to = new TransmitionObject();
        getEstructuras(db, to, TransmisionesPadre.TRANSMISION, TransmisionesPadre.WIFI);

        Cursor c = db.rawQuery("Select value from config where key='ruta_descarga'", null);

        c.moveToFirst();

        String ruta = Utils.getString(c, "value", "") + "\\";

        c.close();

        c = db.rawQuery("Select value from config where key='cpl'", null);

        c.moveToFirst();

        ruta += Utils.getString(c, "value", "") + "\\UP\\";

        c.close();

        c = db.rawQuery("Select registro from encabezado", null);

        c.moveToFirst();
        byte[] bytesAEnviar = Utils.getBlob(c, "registro");
        c.close();

        bytesAEnviar[0] = '.';
        bytesAEnviar[2] = 'D';

        for (int i = 0; i < ruta.length(); i++) bytesAEnviar[i + 20] = ruta.getBytes()[i];

        //Con lectura
        String valor;
        c = db.rawQuery("Select count(*) canti from ruta", null);
        c.moveToFirst();

        valor = String.valueOf(Utils.getLong(c, "canti", 0));
        valor = Main.rellenaString(valor, " ", 4, false);
        c.close();

        for (int i = 0; i < 4; i++) bytesAEnviar[i + 227] = valor.getBytes()[i]; //Con lectura

        bytesAEnviar[230] = '\t';
        bytesAEnviar[239] = '\t';
        bytesAEnviar[240] = '1';

        valor = Main.obtieneFecha("ymd");
        for (int i = 0; i < 8; i++) bytesAEnviar[i + 231] = valor.getBytes()[i]; //Fecha
        return bytesAEnviar;
    }

    public void noRegistradosinMedidor() throws Exception {
        Bundle bu_params = new Bundle();

        bu_params.putString(String.valueOf(NUM_MEDIDOR), "CONDIR");
        bu_params.putString(String.valueOf(LECTURA), "0");

        regresaDeCamposGenericos(bu_params, "noregistrados");
    }

    public void accionesAntesDeGrabarLectura() {
        globales.ultimoBloqueCapturado = globales.tll.getLecturaActual().sinUso1;
        globales.ultimoMedidorCapturado = globales.tll.getLecturaActual().is_serieMedidor;
    }

    public void accionesDespuesDeGrabarLectura() {


    }

    public Vector<EstructuraResumen> getPrincipal(SQLiteDatabase db) {
        String unicom = "";
        String ruta = "";
        String itinerario = "";
        String ciclo = "";
        String cpl = "";
        String mac_bt = "";
        String mac_impr = "";

        Vector<EstructuraResumen> resumen = new Vector<EstructuraResumen>();
//    	resumen.add(new EstructuraResumen("", "")); //Agregamos una linea mas

        Cursor c;
        try {
            c = db.rawQuery("Select unicom, ruta, itinerario, ciclo from ruta limit 1 ", null);
            c.moveToFirst();
            unicom = Utils.getString(c, "unicom", "");
            ruta = Utils.getString(c, "ruta", "");
            itinerario = Utils.getString(c, "itinerario", "");
            ciclo = Utils.getString(c, "ciclo", "");


        } catch (Throwable e) {

        }

        try {
            c = db.rawQuery("Select value from config where key='cpl'", null);
            c.moveToFirst();
            cpl = Utils.getString(c, "value", "");
        } catch (Throwable e) {

        }

        try {
            c = db.rawQuery("Select value from config where key='server_gprs'", null);
            c.moveToFirst();
            mac_bt = Utils.getString(c, "value", "");
        } catch (Throwable e) {

        }


//		resumen.add(new EstructuraResumen(unicom, "Unicom"));
//		resumen.add(new EstructuraResumen(ruta, "Ruta"));
//		resumen.add(new EstructuraResumen(itinerario, "Itinerario"));
//		resumen.add(new EstructuraResumen(ciclo, "Ciclo"));
        resumen.add(new EstructuraResumen(cpl, "Unidad"));
//		resumen.add(new EstructuraResumen(mac_bt, "Servidor"));

        resumen.add(new EstructuraResumen("", "")); //Agregamos una linea mas

        // RLR, 2022-08, Se agregan los datos del lecturista
        if (globales.sesionEntity != null) {
            EmpleadoCplEntity emp;

            emp = globales.sesionEntity.empleado;
            if (emp != null) {
                try {
                    resumen.add(new EstructuraResumen(DbLecturasMgr.getInstance().getUnidades(this.context), "Unidad:"));
                } catch (Exception e) {
                    resumen.add(new EstructuraResumen("---", "Unidad:"));
                }
                resumen.add(new EstructuraResumen(emp.NombreCompleto, "Lect:"));
                resumen.add(new EstructuraResumen(Utils.convToDateTimeStr(emp.FechaIngreso), "F. Activo:"));
                resumen.add(new EstructuraResumen(emp.Telefono, "Cel:"));
                resumen.add(new EstructuraResumen(emp.Regional, "Reg.:"));
                resumen.add(new EstructuraResumen(emp.Agencia, "Agencia:"));
                resumen.add(new EstructuraResumen(globales.sesionEntity.VersionWeb, "Ver.Web:"));
                resumen.add(new EstructuraResumen("", "")); //Agregamos una linea mas
            }
        }

        return resumen;
//		String ls_resumen;
//		Cursor c;
//			try{
//				c=db.rawQuery("Select value from config where key='unicom'", null);
//	    		c.moveToFirst();
//	    		unicom=c.getString(c.getColumnIndex("value"));
//			}
//			catch(Throwable e){
//
//			}
//
//			try{
//				c=db.rawQuery("Select value from config where key='ruta'", null);
//	    		c.moveToFirst();
//	    		ruta=c.getString(c.getColumnIndex("value"));
//			}
//			catch(Throwable e){
//
//			}
//
//			try{
//				c=db.rawQuery("Select value from config where key='itinerario'", null);
//	    		c.moveToFirst();
//	    		itinerario=c.getString(c.getColumnIndex("value"));
//			}
//			catch(Throwable e){
//
//			}
//
//	    		try{
//	    			c=db.rawQuery("Select value from config where key='mac_bluetooth'", null);
//	        		c.moveToFirst();
//	        		mac_bt=c.getString(c.getColumnIndex("value"));
//	    		}catch(Throwable e){
//
//	    		}
//
//
//	    		try{
//	    			c=db.rawQuery("Select value from config where key='mac_impresora'", null);
//	        		c.moveToFirst();
//	        		mac_impr=c.getString(c.getColumnIndex("value"));
//	    		}catch(Throwable e){
//
//	    		}
//
//
//
//
//
//	    	//ll_restantes = ll_total-ll_tomadas ;
//			Vector <EstructuraResumen>resumen= new Vector<EstructuraResumen>();
//
//			resumen.add(new EstructuraResumen(unicom, "Unicom"));
//			resumen.add(new EstructuraResumen(ruta,"Ruta"));
//			resumen.add(new EstructuraResumen(itinerario,"Itinerario"));
//
////	    	ls_resumen="CPL: " + cpl +"\n" +
////	    			"Lote: " +  lote +"\n";
//
//	    	if (!mac_bt.equals("") && !mac_bt.equals(".")){
//
////	    			ls_resumen+="MAC BT: \n"+ mac_bt +"\n";
//	    		resumen.add(new EstructuraResumen(mac_bt, /*getString(R.string.info_macBluetooth)*/"MAC"));
//	    	}
//
//	    	if (!mac_impr.equals("") && !mac_impr.equals(".")){
//
////				ls_resumen+="\nMAC Impr: \n"+ mac_impr;
//	    		resumen.add(new EstructuraResumen( mac_impr, context.getString(R.string.info_macImpresora)));
//		}
//
//	    	if (!globales.getUsuario().equals("")){
//
//	    			resumen.add(new EstructuraResumen( globales.getUsuario()+"-" + globales.is_nombre_Lect, "Lect."));
//	    	}
//
//
//	    	resumen.add(new EstructuraResumen("", "")); //Agregamos una linea mas
//
//	    	 return resumen;

    }


    private String ObtenerCodigo(String strTexto, int nTipo) {
        String lect;
        lect = strTexto;
        if (nTipo == 1) {
            while (lect.length() < 5) lect = lect + " ";//color
            if (lect.trim().length() > 1) {
                if (lect.substring(0, 2).equalsIgnoreCase("AZ")) lect = "CL001";
                else if (lect.substring(0, 2).equalsIgnoreCase("BL")) lect = "CL002";
                else if (lect.substring(0, 2).equalsIgnoreCase("AM")) lect = "CL003";
                else if (lect.substring(0, 2).equalsIgnoreCase("RO")) lect = "CL004";
                else if (lect.substring(0, 2).equalsIgnoreCase("GR")) lect = "CL005";
                else lect = "CL999";
            }
        } else if (nTipo == 2) {
            while (lect.length() < 5) lect = lect + " ";//modelo
            if (lect.trim().length() > 1) {
                if (lect.substring(0, 2).equalsIgnoreCase("RO")) lect = "ML001";
                else if (lect.substring(0, 2).equalsIgnoreCase("CA")) lect = "ML002";
                else if (lect.substring(0, 2).equalsIgnoreCase("PL")) lect = "ML003";
                else lect = "ML999";
            }
        } else {
            while (lect.length() < 5) lect = lect + " ";//estado
            if (lect.trim().length() > 1) {
                if (lect.substring(0, 2).equalsIgnoreCase("NU")) lect = "ES101";
                else if (lect.substring(0, 2).equalsIgnoreCase("EN")) lect = "ES102";
                else if (lect.substring(0, 2).equalsIgnoreCase("IN")) lect = "ES103";
                else if (lect.substring(0, 2).equalsIgnoreCase("RE")) lect = "ES104";
                else if (lect.substring(0, 2).equalsIgnoreCase("DA")) lect = "ES105";
                else if (lect.substring(0, 2).equalsIgnoreCase("VI")) lect = "ES106";
                else if (lect.substring(0, 2).equalsIgnoreCase("CL")) lect = "ES107";
                else if (lect.substring(0, 2).equalsIgnoreCase("EX")) lect = "ES108";
                else if (lect.substring(0, 2).equalsIgnoreCase("IL")) lect = "ES109";
                else lect = "ES999";
            }
        }
        return lect;
    }

    // RL, 2022-10-06, se modifica porque truena con la última lectura cuando no se sigue el camino de corrección.
    public boolean mostrarVentanaDeSellos() {
        String selloRetNumero;
        String aviso;
        Lectura lect;

        lect = globales.tll.getLecturaActual();

        if (lect != null) {
            selloRetNumero = Utils.ifNullStr(globales.tll.getLecturaActual().selloRetNumero);
            aviso = Utils.ifNullStr(globales.tll.getLecturaActual().is_aviso);
        } else {
            selloRetNumero = "";
            aviso = "";
        }

        return selloRetNumero.equals("") && aviso.startsWith("Dem");
    }

    public boolean tomarFotoModificar() {
        String aviso;
        Lectura lect;

        lect = globales.tll.getLecturaActual();

        if (lect != null)
            aviso = Utils.ifNullStr(lect.is_aviso);
        else
            aviso = "";

        // TODO Auto-generated method stub
        if (aviso.startsWith("Rea") || aviso.startsWith("Dem")) {
            return false;
        }
        return true;
    }

    public String carpetaDeFotos(String ls_carpeta) {
        ls_carpeta = subirDirectorio(ls_carpeta, 3);
        ls_carpeta += "/fotos/";
        return ls_carpeta;
    }

    Cursor lineasAEscribir(SQLiteDatabase db) {
        String argumentos = "";
        if (!globales.transmitirTodo) {
            argumentos = "where envio=1";

        } else {
            argumentos = "where trim(tipoLectura)<>''";
        }
        Cursor c = db.rawQuery("select * from Ruta " + argumentos, null);
        return c;

    }
}
