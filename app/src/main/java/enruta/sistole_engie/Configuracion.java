package enruta.sistole_engie;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import enruta.sistole_engie.clases.Utils;

public class Configuracion extends Activity {

    Vector<Entry> objetosAMostar;


    DBHelper dbHelper;
    SQLiteDatabase db;
    boolean guardar = false;
    int ii_rol = CPL.SUPERUSUARIO;

    final static int MAC_BT = 0;
    final static int MAC_IMPR = 1;
    Globales globales;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracion);
        Vector<XmlSpinnerItem> items;
        globales = ((Globales) getApplicationContext());

        Bundle bu_params = getIntent().getExtras();

        try {
            if (bu_params.getInt("guardar") == 1)
                guardar = true;
        } catch (Throwable e) {

        }
        try {
            ii_rol = bu_params.getInt("rol");

        } catch (Throwable e) {

        }


        //Vamos a crear todos y cada uno de los objetos que tendrá esta ventana, serán de cierta manera dinamicos...

        objetosAMostar = new Vector<Entry>();

        if (globales.mostrarMetodoDeTransmision) {
            objetosAMostar.add(new XmlTextView(getString(R.string.lbl_configuracion_modo_trans), "16", "TextView", null, true, "", 0, Typeface.ITALIC));
        }
        items = new Vector<XmlSpinnerItem>();
        items.add(new XmlSpinnerItem(getString(R.string.lbl_configuracion_modo_preguntar), "0"));
        items.add(new XmlSpinnerItem("GPRS", "1"));
        items.add(new XmlSpinnerItem(getString(R.string.lbl_configuracion_modo_BT), "2"));
        items.add(new XmlSpinnerItem(getString(R.string.lbl_configuracion_modo_WIFI), "3"));
        objetosAMostar.add(new XmlSpinner("", "15", "Spinner", true, "modo_trans", "modo_trans", globales.defaultTransmision, items));

        if (globales.mostrarLote) {
            objetosAMostar.add(new XmlTextView(getString(R.string.info_lote), "16", "TextView", null, true, "", 0, Typeface.ITALIC));
            objetosAMostar.add(new XmlEditText(globales.defaultLote, "16", "EditText", null, true, "lote", "7", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "lote"));
        }

        if (globales.mostrarCPL) {
            objetosAMostar.add(new XmlTextView(getString(R.string.info_CPL), "16", "TextView", null, true, "", 0, Typeface.ITALIC));
            objetosAMostar.add(new XmlEditText(globales.defaultCPL, "16", "EditText", null, true, "cpl", "10", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "cpl"));
        }

        if (globales.mostrarUnicom) {
            objetosAMostar.add(new XmlTextView("Unicom", "16", "TextView", null, true, "", 0, Typeface.ITALIC));
            objetosAMostar.add(new XmlEditText(globales.defaultUnicom, "16", "EditText", null, true, "unicom", "4", InputType.TYPE_CLASS_NUMBER, "unicom"));
        }

        if (globales.mostrarRuta) {
            objetosAMostar.add(new XmlTextView("Ruta", "16", "TextView", null, true, "", 0, Typeface.ITALIC));
            objetosAMostar.add(new XmlEditText(globales.defaultRuta, "16", "EditText", null, true, "ruta", "2", InputType.TYPE_CLASS_NUMBER, "ruta"));
        }

        if (globales.mostrarItinerario) {
            objetosAMostar.add(new XmlTextView("Itinerario", "16", "TextView", null, true, "", 0, Typeface.ITALIC));
            objetosAMostar.add(new XmlEditText(globales.defaultItinerario, "16", "EditText", null, true, "itinerario", "4", InputType.TYPE_CLASS_NUMBER, "itinerario"));
        }

        if (globales.mostrarFactorBaremo) {
            objetosAMostar.add(new XmlTextView("Factor Baremo:", "16", "TextView", null, true, "", 0, Typeface.ITALIC));
            objetosAMostar.add(new XmlEditText("100", "16", "EditText", null, true, "baremo", "3", InputType.TYPE_CLASS_NUMBER, "baremo"));
        }

        if (globales.mostrarMacBt) {
            objetosAMostar.add(new XmlTextView(getString(R.string.info_macBluetooth), "16", "TextView", null, true, "", 0, Typeface.ITALIC));
            objetosAMostar.add(new XmlEditText(".", "16", "EditText", null, true, "mac_bt", "17", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "mac_bluetooth"));
        }
        //objetosAMostar.add( new XmlTextView("Num. Bluetooth:", "16", "TextView", "0", true, "", 0,Typeface.ITALIC) );


        if (globales.mostrarMacImpresora) {
            objetosAMostar.add(new XmlTextView(getString(R.string.info_macImpresora), "16", "TextView", null, true, "", 0, Typeface.ITALIC));
            objetosAMostar.add(new XmlEditText(".", "16", "EditText", null, true, "mac_impr", "17", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "mac_impresora"));
        }

        if (globales.mostrarServidorWIFI) {
            objetosAMostar.add(new XmlTextView("Servidor Wifi", "16", "TextView", null, true, "", 0, Typeface.ITALIC));
            objetosAMostar.add(new XmlEditText(globales.defaultServidorGPRS, "16", "EditText", null, true, "servidor_wifi", "100", InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "server_wifi"));

        }


        if (globales.mostrarServidorGPRS) {
            objetosAMostar.add(new XmlTextView(getString(R.string.info_servidorGPRS), "16", "TextView", null, true, "", 0, Typeface.ITALIC));
            objetosAMostar.add(new XmlEditText(globales.defaultServidorGPRS, "16", "EditText", null, true, "servidor_gprs", "100", InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "server_gprs"));

        }
        objetosAMostar.add(new XmlTextView(getString(R.string.info_rutaDescarga), "16", "TextView", null, true, "", 0, Typeface.ITALIC));
        objetosAMostar.add(new XmlEditText(/*"C:\\Apps\\sgl\\lectura"*/ globales.defaultRutaDescarga, "16", "EditText", null, true, "ruta_descarga", "100", InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "ruta_descarga"));

        if (globales.mostrarTamañoFoto) {
            objetosAMostar.add(new XmlTextView(getString(R.string.info_tamanoFoto), "16", "TextView", null, true, "", 0, Typeface.ITALIC));
        }

        items = new Vector<XmlSpinnerItem>();
        Camera mCamera = CamaraActivity.getCameraInstance(globales.camaraFrontal);
        Camera.Parameters cp = mCamera.getParameters();
        List<Camera.Size> cs = cp.getSupportedPictureSizes();

        for (int i = 0; i < cs.size(); i++) {
            items.add(new XmlSpinnerItem(cs.get(i).width + "x" + cs.get(i).height, String.valueOf(i)));
        }

        int sizeDef = seleccionarResolucionMinima(cs);

        objetosAMostar.add(new XmlSpinner("", "16", "Spinner", true, "tam_fotos", "tam_fotos", String.valueOf(sizeDef), items));
        mCamera.release();

        if (globales.mostrarCalidadFoto) {
            objetosAMostar.add(new XmlTextView(getString(R.string.info_calidad_foto), "16", "TextView", null, true, "", 0, Typeface.ITALIC));
            objetosAMostar.add(new XmlEditText(String.valueOf(globales.calidadDeLaFoto), "16", "EditText", null, true, "calidad_foto", "3", InputType.TYPE_CLASS_NUMBER, "calidad_foto"));
        }

        objetosAMostar.add(new XmlTextView(getString(R.string.info_modo), "16", "TextView", null, true, "", 0, Typeface.ITALIC));
        items = new Vector<XmlSpinnerItem>();
        items.add(new XmlSpinnerItem(getString(R.string.info_modo_normal), "0"));
        items.add(new XmlSpinnerItem(getString(R.string.info_modo_sin_fotos), "1"));
        items.add(new XmlSpinnerItem(getString(R.string.info_modo_fotos), "2"));
        items.add(new XmlSpinnerItem(getString(R.string.info_modo_fotos_cc), "3"));
        objetosAMostar.add(new XmlSpinner("", "16", "Spinner", true, "modo", "modo_config", "2", items));

        objetosAMostar.add(new XmlTextView(getString(R.string.info_sonidos), "16", "TextView", null, true, "", 0, Typeface.ITALIC));
        items = new Vector<XmlSpinnerItem>();
        items.add(new XmlSpinnerItem(getString(R.string.info_encendido), "0"));
        items.add(new XmlSpinnerItem(getString(R.string.info_apagado), "1"));
        objetosAMostar.add(new XmlSpinner("", "16", "Spinner", true, "sonidos", "sonidos", "0", items));


        //Vamos a agregarlos uno por uno

        agregaCampos();

        if (guardar) {
            guardar();
            finish();
        }


    }

    public void agregaCampos() {

        Cursor c;
        String tmp_text = null;

        int id;
        View view = null;

        //c=db.rawQuery("Select * from config" , null);
        //c.moveToFirst();
        LinearLayout layout = (LinearLayout) findViewById(R.id.ll_config);

        for (Entry tmp : objetosAMostar) {
            if (tmp.type.equals("TextView")) {
                XmlTextView entry = (XmlTextView) tmp;
                TextView tv_view;
                if (!tmp.crear) {
                    id = getResources().getIdentifier(tmp.view_name, "id", this.getPackageName());
                    view = findViewById(id);
                    tv_view = (TextView) view;
                } else {
                    tv_view = new TextView(this);
                    tv_view.setTag(tmp.view_name);
                }


                if (entry.label != null) {
                    tv_view.setText(entry.label);
                }

                if (entry.color != null) {
                    tv_view.setTextColor(Color.parseColor(entry.color));
                }

                if (entry.size != null) {
                    tv_view.setTextSize(Integer.parseInt(entry.size));
                }

                //Agregamos la gravedad de android
                tv_view.setGravity(entry.gravity);
                //Agregamos el Style (negritas, normal o Italicas)
                tv_view.setTypeface(null, entry.style);

                layout.addView(tv_view);


            }
            if (tmp.type.equals("EditText")) {
                XmlEditText entry = (XmlEditText) tmp;
                EditText et_view;
                if (!tmp.crear) {
                    id = getResources().getIdentifier(tmp.view_name, "id", this.getPackageName());
                    view = findViewById(id);
                    et_view = (EditText) view;
                } else {
                    et_view = new EditText(this);
                    et_view.setTag(tmp.view_name);
                }

                //Vamos a poner el valor que se encuentre en la base de datos, pero si no esta, deberemos
                if (entry.dbField != null && !entry.dbField.equals("")) {

                    //Aqui buscamos la key
                    openDatabase();

                    c = db.rawQuery("Select * from config where key='" + entry.dbField + "'", null);
                    if (c.getCount() > 0) {
                        c.moveToFirst();
                        try {
                            tmp_text = Utils.getString(c, "value", "");
                        } catch (Exception e) {
                            tmp_text = "";
                        }

                    }

                    et_view.setText(tmp_text == null ? entry.label : tmp_text);

                    c.close();
                    closeDatabase();

                    tmp_text = null;


                } else if (entry.label != null) {
                    et_view.setText(entry.label);
                }

                if (entry.color != null) {
                    et_view.setTextColor(Color.parseColor(entry.color));
                }

                if (entry.size != null) {
                    et_view.setTextSize(Integer.parseInt(entry.size));
                }
                et_view.setInputType(entry.inputType);

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(entry.maxLenght);
                et_view.setFilters(FilterArray);


                switch (ii_rol) {
                    case CPL.LECTURISTA:
                        et_view.setEnabled(false);
                        break;
                }
                
                /* RL - 2022-09-12
                Deshabilitar el control Edit Text del modo */

                if (tmp.view_name.equals("calidad_foto"))
                    et_view.setEnabled(false);

                // RL, 2022-10-04, Habilitar controles críticos solo al super usuario

                if (!globales.esSuperUsuario) {
                    if (tmp.view_name.equals("servidor_gprs"))
                        et_view.setEnabled(false);

                    if (tmp.view_name.equals("servidor_wifi"))
                        et_view.setEnabled(false);

                    if (tmp.view_name.equals("ruta_descarga"))
                        et_view.setEnabled(false);
                }


                //Agregamos al layout
                layout.addView(et_view);
            }

            if (tmp.type.equals("Spinner")) {
                XmlSpinner entry = (XmlSpinner) tmp;
                Spinner spinner;

                if (!tmp.crear) {
                    id = getResources().getIdentifier(tmp.view_name, "id", this.getPackageName());
                    view = findViewById(id);
                    spinner = (Spinner) view;
                } else {
                    spinner = new Spinner(this);
                    spinner.setTag(tmp.view_name);
                }

                /* RL / 2022-09-12 / Deshabilitar el control Spinner del modo
                    RL / 2023-06-16 / Habilitar el control cuando se tienen los permisos de superusuario
                * */

                if (tmp.view_name.equals("modo"))
                    spinner.setEnabled(globales.esSuperUsuario);

                /* RL - 2022-10-04
                Deshabilitar el control Spínner del tamaño de fotos si no es Superusuario  */

                if (tmp.view_name.equals("tam_fotos")) {
                    spinner.setEnabled(globales.esSuperUsuario);
                }

                ArrayList<String> spinnerArray = new ArrayList<String>();

                for (XmlSpinnerItem sp : entry.items) {
                    spinnerArray.add(sp.label);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                //Estilo usado
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Aplicamos los datos
                spinner.setAdapter(adapter);

                //Agregamos al layout
//		    	 	layout.addView(spinner);

                if (!((!globales.mostrarMetodoDeTransmision && tmp.view_name.equals("modo_trans")) ||
                        (!globales.mostrarTamañoFoto && tmp.view_name.equals("tam_fotos")))) {
                    //Agregamos al layout
                    layout.addView(spinner);
                }

                openDatabase();

                tmp_text = null;

                //Hay que seleccionar el actual
                c = db.rawQuery("Select * from config where key='" + entry.dbField + "'", null);
                if (c.getCount() > 0) {
                    c.moveToFirst();

                    try {
                        tmp_text = Utils.getString(c, "value", "");
                    } catch(Exception e) {
                        tmp_text = "";
                    }

                }

                if (!globales.mostrarTamañoFoto && tmp.view_name.equals("tam_fotos")) {
                    globales.defaultTamañoFoto = String.valueOf(tmp_text == null ? ((XmlSpinner) tmp).selected : Integer.parseInt(tmp_text));
                }

                spinner.setSelection(tmp_text == null ? ((XmlSpinner) tmp).selected : Integer.parseInt(tmp_text));
                switch (ii_rol) {
                    case CPL.LECTURISTA:
                        spinner.setEnabled(false);
                        break;
                }

                c.close();
                closeDatabase();
            }


        }
        // c.close();
        //closeDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.configuracion, menu);

        MenuItem mi_guardar = menu.findItem(R.id.m_guardar);
        MenuItem mi_ingresarMac = menu.findItem(R.id.m_ingresarMAC);
        MenuItem mi_ingresarMACIMPR = menu.findItem(R.id.m_ingresarMACIMPR);

        switch (ii_rol) {
            case CPL.LECTURISTA:
                mi_guardar.setVisible(false);
                mi_ingresarMac.setVisible(false);
                mi_ingresarMACIMPR.setVisible(false);
                break;


        }

        if (!globales.mostrarIngresoFacilMAC) {
            mi_ingresarMACIMPR.setVisible(false);
        }

        if (!globales.mostrarIngresoFacilMAC_BT) {
            mi_ingresarMac.setVisible(false);
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        EditText et_view;
        Intent intent;
        View view;
        switch (item.getItemId()) {
            case R.id.m_guardar:
                mayusculasBluetooth();
                guardar();
                finish();
                break;

            case R.id.m_ingresarMAC:
                intent = new Intent(this, Input.class);
                view = findViewById(R.id.ll_config);
                et_view = (EditText) view.findViewWithTag("mac_bt");
                intent.putExtra("comentarios", et_view.getText().toString());
                intent.putExtra("tipo", Input.MAC);

                startActivityForResult(intent, MAC_BT);
                break;

            case R.id.m_ingresarMACIMPR:
                intent = new Intent(this, Input.class);
                view = findViewById(R.id.ll_config);
                et_view = (EditText) view.findViewWithTag("mac_impr");
                intent.putExtra("comentarios", et_view.getText().toString());
                intent.putExtra("tipo", Input.MAC);

                startActivityForResult(intent, MAC_IMPR);
                break;
        }
        return true;
    }

    public void mayusculasBluetooth() {
        View view = findViewById(R.id.ll_config);
        EditText et_view;
        if (globales.mostrarMacBt) {
            et_view = (EditText) view.findViewWithTag("mac_bt");

            et_view.setText(et_view.getText().toString().toUpperCase());
        }


        if (globales.mostrarMacImpresora) {
            et_view = (EditText) view.findViewWithTag("mac_impr");

            et_view.setText(et_view.getText().toString().toUpperCase());
        }


    }

    public static class Entry {
        public String label;
        public String size;
        public String type;
        public int visible = 0;
        public String view_name;
        boolean crear = false;


        private Entry(String label, String size, String type, String view_name, boolean crear) {
            this.label = label;
            this.size = size;
            this.type = type;
            this.view_name = view_name;
            this.crear = crear;
        }

        private Entry(String label, String size) {
            this.label = label;
            this.size = size;
        }


    }


    public static class XmlTextView extends Entry {
        public String color;
        public int gravity;
        public int style;

        public boolean crear;

        XmlTextView(String label, String size, String type, String color, boolean crear, String view_name, int gravity, int style) {
            super(label, size, type, view_name, crear);
            this.color = color;
            this.gravity = gravity;
            this.style = style;
        }

        private XmlTextView(String label, String size, String color, int gravity, int style) {
            super(label, size);
            this.color = color;
            this.gravity = gravity;
            this.style = style;
        }

    }

    public static class XmlEditText extends Entry {
        public String color;
        public String dbField;
        int maxLenght;
        int inputType;

        public boolean crear;

        /**
         * Crea un objeto segun los parametros especificados
         *
         * @param label     Valor a mostrar
         * @param size      Tamaño de la fuente
         * @param type      tipo de control
         * @param color     Color del texto
         * @param crear     si el objeto debe ser creado
         * @param view_name nombre dentro del layout
         * @param maxLenght cantidad de caracteres que el objeto puede admitir
         * @param inputType Tipo de campo de entrada (InputType)
         * @param dbField   Campo en la BD
         */
        XmlEditText(String label, String size, String type, String color, boolean crear, String view_name,
                    String maxLenght, int inputType, String dbField) {
            super(label, size, type, view_name, crear);
            this.color = color;
            this.maxLenght = (maxLenght != null ? Integer.parseInt(maxLenght) : 10);
            this.inputType = inputType;
            this.dbField = dbField;
        }

        private XmlEditText(String label, String size, String color) {
            super(label, size);
            this.label = label;
            this.size = size;
            this.color = color;
        }

    }

    public static class XmlSpinner extends Entry {
        public String dbField;
        public int selected;
        public Vector<XmlSpinnerItem> items;

        private XmlSpinner(String label, String size, String type, boolean crear, String view_name,
                           String dbField, String selected, Vector<XmlSpinnerItem> items) {
            super(label, size, type, view_name, crear);
            this.dbField = dbField;
            this.selected = selected != null ? Integer.parseInt(selected) : 0;
            this.items = items;
        }

    }

    public static class XmlSpinnerItem extends XmlTextView {
        int value;

        public XmlSpinnerItem(String label, String value) {
            super(label, null, null, 0, 0);
            this.value = (value != null ? Integer.parseInt(value) : 0);
        }
    }

    private void openDatabase() {
        dbHelper = new DBHelper(this);

        db = dbHelper.getReadableDatabase();
    }

    private void closeDatabase() {
        db.close();
        dbHelper.close();
    }

    public void guardar() {
        //buscaremos el tag de cada uno
        View view = findViewById(R.id.ll_config);
        String texto = "";
        String dbField = "";
        String tableToUpdate = "config";
        if (objetosAMostar != null) {
            for (Entry tmp : objetosAMostar) {

                if (tmp.type.equals("TextView"))
                    continue;

                texto = "";
                if (tmp.type.equals("EditText")) {
                    EditText et_view = (EditText) view.findViewWithTag(tmp.view_name);

                    texto = et_view.getText().toString();
                    dbField = ((XmlEditText) tmp).dbField;


                    if (dbField.equals("calidad_foto")) {
                        if (Integer.parseInt(texto) > 100) {
                            texto = "100";
                        }
                        globales.calidadDeLaFoto = Integer.parseInt(texto);
                    }

                    if (dbField.equals("cpl")) {
                        texto = texto.trim();
                    }
//						else if (dbField.equals("cpl")){
//							if (globales.bloquearCPLNoSuper && !globales.esSuperUsuario)
//								continue;
//						}


                }
                if (tmp.type.equals("Spinner")) {
                    Spinner sp_view = (Spinner) view.findViewWithTag(tmp.view_name);
                    if (sp_view != null) {
                        // continue;
                        texto = String.valueOf(sp_view.getSelectedItemPosition());
                    } else {
                        if (!globales.mostrarMetodoDeTransmision && tmp.view_name.equals("modo_trans")) {

                            texto = globales.defaultTransmision;
                        } else if (!globales.mostrarTamañoFoto && tmp.view_name.equals("tam_fotos")) {
                            texto = globales.defaultTamañoFoto;
                        } else {
                            continue;
                        }
                    }

                    dbField = ((XmlSpinner) tmp).dbField;

                    if (dbField.equals("sonidos")) {
                        globales.sonidos = texto.equals("0");
                    } else if (dbField.equals("lote")) {
                        globales.lote = texto;
                    }

                }

                openDatabase();

                //Solo vamos a actualizar pero primero deberemos ver si debemos actualizar
                Cursor c = db.rawQuery("Select * from " + tableToUpdate + " where key='" + dbField + "'", null);

                if (c.getCount() > 0) {
                    db.execSQL("update " + tableToUpdate + " set key='" + dbField + "', value='" + texto + "' where key='" + dbField + "'");
                } else {
                    db.execSQL("insert into " + tableToUpdate + " ( key, value) values('" + dbField + "', '" + texto + "')");
                }
                c.close();
                closeDatabase();
            }
        }
    }


    private int seleccionarResolucionMinima(List<Camera.Size> resolutions) {
        //Quiero saber si el celular es capaz de tener una resolucionn de 2MP...
        //Si no tiene de 2MP, que sea la resolucion mas baja, y si solo tiene una, pues que regrese la unica que tiene

        //Solo lo busca si no se ha definido alguna resolucion

        //Aqui buscamos la key
        openDatabase();

        boolean bNoHay = false;

        Cursor c = db.rawQuery("Select * from config where key='tam_fotos'", null);
        if (c.getCount() == 0) {

            bNoHay = true;

        }


        c.close();
        closeDatabase();

        if (!bNoHay)
            return 0;

        Camera.Size masBaja = null;
        int posMasBaja = -1, selected = 0;

        int resolution;


        for (int i = 0; resolutions.size() > i; i++) {

            Camera.Size size = resolutions.get(i);

            resolution = (int) Math.round((size.height * size.width) / 1000000.0);
			
			/*if (resolution == 2){
				return i;
			}
			else */

            // RL - 2022-09-12 - Se cambia la resolución que ha resultado buena con Engie
            if (size.width == 640 && size.height == 480) {
                return i;
            }

            if (masBaja == null) {
                //Es la primera mas baja
                posMasBaja = i;
                masBaja = size;


            } else if (masBaja != null) {
                //Vamos a checar si es la mas baja
                double actual = size.width * size.height;
                double anterior = masBaja.height * masBaja.width;

                if (actual < anterior) {
                    posMasBaja = i;
                    masBaja = size;
                }
            }


        }

        return posMasBaja;

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        View view;
        EditText et_view;
        Bundle bu_params;
        switch (requestCode) {
            case MAC_BT:

                if (resultCode == Activity.RESULT_OK) {
                    bu_params = data.getExtras();
                    view = findViewById(R.id.ll_config);
                    et_view = (EditText) view.findViewWithTag("mac_bt");

                    et_view.setText(bu_params.getString("input"));


                }
                break;

            case MAC_IMPR:

                if (resultCode == Activity.RESULT_OK) {
                    bu_params = data.getExtras();
                    view = findViewById(R.id.ll_config);
                    et_view = (EditText) view.findViewWithTag("mac_impr");

                    et_view.setText(bu_params.getString("input"));


                }
                break;
        }
    }

    @Override
    protected void onResume() {
        //Ahora si abrimos
        if (globales.tdlg == null) {
            super.onResume();
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            System.exit(0);
            return;
        }
        super.onResume();
    }

}
