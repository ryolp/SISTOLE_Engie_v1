package enruta.sistole_engie;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import android.graphics.drawable.ColorDrawable;

import java.util.Calendar;
import java.util.Date;

import enruta.sistole_engie.entities.OperacionRequest;
import enruta.sistole_engie.entities.OperacionResponse;
import enruta.sistole_engie.entities.LoginRequestEntity;
import enruta.sistole_engie.entities.LoginResponseEntity;
import enruta.sistole_engie.entities.SesionEntity;
import enruta.sistole_engie.services.WebApiManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CPL extends Activity {

    public final static int NINGUNO = 0;
    public final static int ADMINISTRADOR = 1;
    public final static int LECTURISTA = 2;
    public final static int SUPERUSUARIO = 3;

    public final static int ENTRADA = 1;
    public final static int LOGIN = 2;
    public final static int MAIN = 3;

    public final static int CAMBIAR_USUARIO = 1;

    public int ii_perfil = NINGUNO;
    public int ii_pantallaActual = NINGUNO;


    boolean esSuperUsuario = false;

    String is_nombre_Lect = "";

    private TextView tv_msj_login, tv_usuario, tv_contrasena, tv_version;
    private EditText et_usuario, et_contrasena;
    private Button btnEntrar;

    DBHelper dbHelper;
    SQLiteDatabase db;

    String superUsuarioPass = "9776";

    String usuario = "";

    Globales globales;
    ImageView iv_logo;

    // RL, 2022-07-14, Campos para validación SMS

    private TextView lblMensaje;
    private TextView lblCodigoSMS;
    private EditText txtCodigoSMS;
    private Button btnAutenticar;
    private Button btnValidarSMS;
    private int intentosAutenticacion = 0;
    private int intentosCodigoSMS = 0;

    // RL, 2022-09-13, Se agrega referencia al logo de Enruta
    private ImageView iv_nosotros;

    // RL, 2022-09-13, Variables para activar el modo de ayuda

    private int clicksModoAyuda0 = 0;
    private int clicksModoAyuda1 = 0;
    private Date fechaModoAyuda0 = null;
    private Date fechaModoAyuda1 = null;
    private ColorDrawable lastBackgroundColor;
    private boolean dialogoConfirmarAyuda = false;

    // RL. 2022-11-08, quitar la referencia de eventos de los botones del layout...
    // ... y ponerlos en código como sugiere Android.

    private Button btnAdministrador;
    private Button btnLecturista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cpl);
        ii_pantallaActual = ENTRADA;

        globales = ((Globales) getApplicationContext());

        inicializarControles();
        inicializarEventosControles();

        try {
            tv_version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + ", " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        estableceVariablesDePaises();
        validarPermisos();
    }

//    protected void onStart() {
//        super.onStart();
//
//        inicializarControles();
//    }

    /*
        Inicializar las referencias a los controles del activity
     */

    private void inicializarControles() {
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_nosotros = (ImageView) findViewById(R.id.iv_nosotros);
        tv_version = (TextView) findViewById(R.id.tv_version_lbl);
        lblMensaje = (TextView) findViewById(R.id.txtMensaje);
        btnAdministrador = (Button) findViewById(R.id.b_admon);
        btnLecturista = (Button) findViewById(R.id.b_lecturista);

        if (btnAdministrador != null)
            btnAdministrador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    entrarAdministrador(view);
                }
            });

        if (btnLecturista != null)
            btnLecturista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    entrarLecturista(view);
                }
            });
    }

        /*
        Inicializar los eventos de los botones principales del activity
     */

    private void inicializarEventosControles() {
        if (btnAdministrador != null)
            btnAdministrador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    entrarAdministrador(view);
                }
            });

        if (btnLecturista != null)
            btnLecturista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    entrarLecturista(view);
                }
            });
    }

    /*
        Validación de permisos
     */

    private void validarPermisos() {
        boolean tienePermisos = true;
        String msg = "";

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            tienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            tienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            tienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            tienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            tienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            tienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            tienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            tienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            tienePermisos = false;
        }

        if (!tienePermisos) {
            showMessageLong("Faltan permisos");
            lblMensaje.setText("Faltan permisos");
        }
    }

    /**
     * Aqui se van a cargar las variables que correspondan a cada pais
     */
    private void estableceVariablesDePaises() {
        // TODO Auto-generated method stub

        switch (globales.ii_pais) {
            case Globales.ARGENTINA:
                globales.tdlg = new TomaDeLecturasArgentina(this);
                break;
            case Globales.COLOMBIA:
                globales.tdlg = new TomaDeLecturasColombia(this);
                break;
            case Globales.ELECTRICARIBE:
                globales.tdlg = new TomaDeLecturasElectricaribe(this);
                break;
            case Globales.PANAMA:
                globales.tdlg = new TomaDeLecturasPanama(this);
                break;
            case Globales.COMAPA_TAMPICO:
                globales.tdlg = new TomaDeLecturasComapaTampico(this);
                break;
            case Globales.ENGIE:
                globales.tdlg = new TomaDeLecturasEngie(this);
                break;
        }

        iv_logo.setImageResource(globales.logo);

    }

    protected boolean esSesionActiva() {
        if (globales.sesionEntity == null)
            return false;

        if (!globales.conservarSesion)
            return false;

        if (globales.sesionEntity.esSesionVencida())
            return false;
        else
            globales.sesionEntity.inicializarHoraVencimiento();

        return true;
    }


    public void entrarAdministrador(View v) {
        entrarAdministrador2(v, false);
    }

    public void entrarAdministrador2(View v, boolean bForzarAdministrador) {
        ii_perfil = ADMINISTRADOR;

        setContentView(R.layout.p_login);
        ii_pantallaActual = LOGIN;
        getObjetosLogin();
        et_contrasena.setFilters(new InputFilter[]{new InputFilter.LengthFilter(globales.longCampoContrasena)});
        tv_msj_login.setText(R.string.str_login_msj_admon);

        if (!esSesionActiva()) {
            if (globales.tipoDeValidacion == globales.CON_SMS && !bForzarAdministrador)
                habilitarControlesAutenticacionSMS();
            else {
                tv_usuario.setVisibility(View.VISIBLE);
                et_usuario.setVisibility(View.GONE);

                et_contrasena.setVisibility(View.VISIBLE);
                tv_contrasena.setVisibility(View.GONE);

                deshabilitarControlesAutenticacionSMS();

                et_contrasena.requestFocus();
            }


            globales.secuenciaSuperUsuario = "A";
            mostrarTeclado();

            intentosAutenticacion = 0;

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else
            irActivityMain();
    }

    public void entrarLecturista(View v) {
        ii_perfil = LECTURISTA;
        ii_pantallaActual = LOGIN;
        setContentView(R.layout.p_login);
        getObjetosLogin();
        globales.secuenciaSuperUsuario += "C";

        et_usuario.setFilters(new InputFilter[]{new InputFilter.LengthFilter(globales.longCampoUsuario)});
        et_contrasena.setFilters(new InputFilter[]{new InputFilter.LengthFilter(globales.longCampoContrasena)});
        et_usuario.setInputType(globales.tipoDeEntradaUsuarioLogin);

        //Hay que adaptar según el tipo de validacion

        if (!esSesionActiva()) {
            deshabilitarControlesAutenticacionSMS();
            switch (globales.tipoDeValidacion) {

                case Globales.AMBAS:

                    String ls_usuarioGuardado = globales.tdlg.getUsuarioGuardado();

                    if (ls_usuarioGuardado.trim().length() == 0) {
                        et_usuario.requestFocus();

                    } else {
                        et_usuario.setText(ls_usuarioGuardado);
                        et_contrasena.requestFocus();
                    }


                    break;

                case Globales.USUARIO:
                    et_usuario.requestFocus();


                    et_contrasena.setVisibility(View.GONE);
                    tv_contrasena.setVisibility(View.GONE);

                    tv_usuario.setVisibility(View.VISIBLE);
                    et_usuario.setVisibility(View.VISIBLE);

                    break;

                case Globales.CONTRASEÑA:
                case Globales.SIN_VALIDACION:
                    tv_usuario.setVisibility(View.VISIBLE);
                    et_usuario.setVisibility(View.GONE);

                    et_contrasena.setVisibility(View.VISIBLE);
                    tv_contrasena.setVisibility(View.GONE);

                    et_contrasena.requestFocus();
                    break;
                case Globales.CON_SMS:
                    habilitarControlesAutenticacionSMS();
                    break;
            }

//		if(globales.tipoDeValidacion==Globales.CONTRASEÑA)
//			tv_msj_login.setText(R.string.str_login_msj_lecturista_contrasena);
//		else
            tv_msj_login.setText(globales.mensajeContraseñaLecturista);

            mostrarTeclado();

            intentosAutenticacion = 0;

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else
            irActivityMain();
    }

    private void deshabilitarControlesAutenticacionSMS() {
        lblCodigoSMS.setVisibility(View.GONE);
        txtCodigoSMS.setVisibility(View.GONE);
        btnValidarSMS.setVisibility(View.GONE);
        btnAutenticar.setVisibility(View.GONE);
    }

    private void habilitarControlesAutenticacionSMS() {
        tv_usuario.setVisibility(View.VISIBLE);
        et_usuario.setVisibility(View.VISIBLE);
        et_usuario.setInputType(InputType.TYPE_CLASS_TEXT);
        et_usuario.setFilters(new InputFilter[]{});
        btnAutenticar.setVisibility(View.VISIBLE);

        tv_contrasena.setVisibility(View.VISIBLE);
        et_contrasena.setVisibility(View.VISIBLE);
        et_contrasena.setInputType(InputType.TYPE_CLASS_TEXT);
        et_contrasena.setFilters(new InputFilter[]{});

        lblCodigoSMS.setVisibility(View.GONE);
        txtCodigoSMS.setVisibility(View.GONE);
        btnValidarSMS.setVisibility(View.GONE);
        btnEntrar.setVisibility(View.GONE);
        btnEntrar.setEnabled(false);

        et_usuario.setFocusableInTouchMode(true);
        et_usuario.setFocusable(true);
        et_usuario.requestFocus();
    }

    public void entrar(View v) {
        boolean validar = false;
        switch (ii_perfil) {
            case ADMINISTRADOR:
                esconderTeclado();
                validar = validarAdministrador();
                break;
            case LECTURISTA:
                esconderTeclado();
                validar = validarLecturista();

                break;
        }

        if (validar) {
            //Aqui abrimos la actividad

            //Hay que empezar a restingir las cosas que cada uno puede hacer

            Intent intent = new Intent(this, Main.class);
            intent.putExtra("rol", ii_perfil);
            intent.putExtra("esSuperUsuario", esSuperUsuario);
            intent.putExtra("nombre", is_nombre_Lect);


            startActivityForResult(intent, MAIN);
        } else {

            switch (ii_perfil) {
                case ADMINISTRADOR:
                    Toast.makeText(this, getString(R.string.msj_cpl_verifique_contrasena), Toast.LENGTH_LONG).show();
                    globales.secuenciaSuperUsuario += "B";
                    break;
                case LECTURISTA:
                    if (globales.tipoDeValidacion == Globales.CONTRASEÑA)
                        Toast.makeText(this, getString(R.string.msj_cpl_verifique_contrasena), Toast.LENGTH_LONG).show();
                    else if (globales.tipoDeValidacion == Globales.USUARIO)
                        Toast.makeText(this, getString(R.string.msj_cpl_verifique_usuario), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(this, getString(R.string.msj_cpl_verifique_usuario_contrasena), Toast.LENGTH_LONG).show();
                    break;
            }
            et_usuario.setText("");
            et_contrasena.setText("");

        }
    }

//    protected void entrar2() {
//        Intent intent = new Intent(this, Main.class);
//        intent.putExtra("rol", ii_perfil);
//        intent.putExtra("esSuperUsuario", esSuperUsuario);
//        intent.putExtra("nombre", is_nombre_Lect);
//
//        startActivityForResult(intent, MAIN);
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bu_params = null;
        if (data != null) {
            bu_params = data.getExtras();
        }
        switch (requestCode) {
            case MAIN:
                if (resultCode == Activity.RESULT_CANCELED) {
                    finish(); //Cancelo con el back
                } else if (resultCode == Activity.RESULT_OK) {
                    //Cuando cambia de usuario...
                    if (bu_params.getInt("opcion") == CAMBIAR_USUARIO) {
                        cambiarUsuario();
                    }
                }


        }
    }


    public boolean validarLecturista() {
        boolean esValido = false;

        esSuperUsuario = (et_contrasena.getText().toString().equals(superUsuarioPass) || et_usuario.getText().toString().equals(superUsuarioPass)) && globales.secuenciaSuperUsuario.equals(Globales.SECUENCIA_CORRECTA_SUPER);

        //Hay que buscar que la combinacion usuario y contraseña sean correctos
        if (esSuperUsuario) {
            esValido = true;
            is_nombre_Lect = "Super Usuario";
            globales.setUsuario("9776");
        } else {
            openDatabase();
            Cursor c;

            String ls_contraseña = et_contrasena.getText().toString().trim();

            if (globales.contraseñaUsuarioEncriptada) {
                byte[] medidor = Main.rellenaString(ls_contraseña, " ", globales.longCampoContrasena, false).getBytes();

                globales.tdlg.EncriptarDesencriptarConParametros(medidor, 0, globales.longCampoContrasena);

                ls_contraseña = new String(medidor);
            }

            switch (globales.tipoDeValidacion) {
                case Globales.CONTRASEÑA:
                    c = db.rawQuery("Select * from usuarios where trim (contrasena)='" + et_contrasena.getText().toString().trim() + "'", null);
                    break;
                case Globales.USUARIO:
                    c = db.rawQuery("Select * from usuarios where lower(trim(usuario))='" + et_usuario.getText().toString().trim().toLowerCase() + "' ", null);
                    break;
                case Globales.SIN_VALIDACION:
                    globales.setUsuario(et_contrasena.getText().toString().trim());
                    closeDatabase();
                    return true;
                default:
                    if (globales.validacionCon123) {
                        c = db.rawQuery("Select * from usuarios where trim(usuario)='" + et_usuario.getText().toString().trim() + "' "
                                , null);
                    } else {
                        c = db.rawQuery("Select * from usuarios where trim(usuario)='" + et_usuario.getText().toString().trim() + "' " +
                                " and trim (contrasena)='" + ls_contraseña + "'", null);
                    }

                    break;
            }


            if (c.getCount() > 0) {
                esValido = true;
                c.moveToFirst();
                if (globales.validacionCon123) {
                    esValido = et_contrasena.getText().toString().trim().equals("123");
                }

                if (globales.tipoDeValidacion == Globales.CONTRASEÑA) {
                    globales.setUsuario(et_contrasena.getText().toString().trim());
                } else {
                    globales.setUsuario(et_usuario.getText().toString().trim());
                }
                globales.controlCalidadFotos = c.getInt(c.getColumnIndex("fotosControlCalidad"));
                globales.baremo = Lectura.toInteger(c.getString(c.getColumnIndex("baremo")));
                is_nombre_Lect = c.getString(c.getColumnIndex("nombre"));

            }


            c.close();
//			c= db.rawQuery("Select * from usuarios ", null) ;
//			c.moveToFirst();
//			String usuario= c.getString(0);
//			String contraseña=c.getString(1);
//			
//			c.moveToNext();
//			usuario= c.getString(0);
//			contraseña=c.getString(1);

            closeDatabase();
        }


        return esValido;
    }

    private void openDatabase() {
        dbHelper = new DBHelper(this);

        db = dbHelper.getReadableDatabase();
    }

    private void closeDatabase() {
        db.close();
        dbHelper.close();

    }

    public boolean validarAdministrador() {
        openDatabase();
        //Buscamos si existe la palabra administrador en los ususatios
        Cursor c;
        c = db.rawQuery("Select * from usuarios where rol in ('2', '3') ", null);


        if (c.getCount() > 0) {
            //Existe un administrador, usaremos su contraseña para entrar al sistema
            c.close();
            c = db.rawQuery("Select * from usuarios where rol in ('2', '3') and trim (contrasena)='" + et_contrasena.getText().toString().trim() + "'", null);
            if (c.getCount() > 0) {
                c.close();
                esSuperUsuario = (et_contrasena.getText().toString().equals(superUsuarioPass));
                return true;
            } else if (!globales.fuerzaEntrarComoSuperUsuarioAdmon) {

                c.close();
                return false;
            }
        }
        c.close();
        closeDatabase();
        esSuperUsuario = (et_contrasena.getText().toString().equals(superUsuarioPass));
        globales.esSuperUsuario = esSuperUsuario;
//		return true; //Entra con todos
        return this.et_contrasena.getText().toString().equals(globales.admonPass) || this.et_contrasena.getText().toString().equals(superUsuarioPass);
    }


    public void cambiarUsuario() {
        setContentView(R.layout.cpl);
        esSuperUsuario = false;
        ii_pantallaActual = ENTRADA;
        ii_perfil = NINGUNO;
        globales.setUsuario("");
        TextView tv_version = (TextView) findViewById(R.id.tv_version_lbl);

        try {
            tv_version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + ", " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_logo.setImageResource(globales.logo);
        globales.anomaliaARepetir = "";
        globales.subAnomaliaARepetir = "";

        globales.tdlg.procesosAlEntrar();
        inicializarControles();
        inicializarEventosControles();
    }

    public void getObjetosLogin() {
        tv_msj_login = (TextView) findViewById(R.id.tv_msj_login);
        et_usuario = (EditText) findViewById(R.id.et_usuario);
        et_contrasena = (EditText) findViewById(R.id.et_contrasena);
        tv_usuario = (TextView) findViewById(R.id.tv_usuario);
        tv_contrasena = (TextView) findViewById(R.id.tv_contrasena);

        lblCodigoSMS = (TextView) findViewById(R.id.lblCodigoSMS);
        txtCodigoSMS = (EditText) findViewById(R.id.txtCodigoSMS);

        btnAutenticar = (Button) findViewById(R.id.btnAutenticar);
        btnValidarSMS = (Button) findViewById(R.id.btnValidarSMS);
        btnEntrar = (Button) findViewById(R.id.b_entrar);

        et_usuario.setFocusableInTouchMode(true);
        et_contrasena.setFocusableInTouchMode(true);
        txtCodigoSMS.setFocusableInTouchMode(true);

        inicializarEventosControlesLogin();

        OnEditorActionListener oeal = new OnEditorActionListener() {


            @Override
            public boolean onEditorAction(TextView arg0, int arg1,
                                          KeyEvent arg2) {
                // TODO Auto-generated method stub
                entrar(arg0);
                return false;
            }
        };

        if (globales.tipoDeValidacion == Globales.USUARIO) {
            et_usuario.setOnEditorActionListener(oeal);
            et_contrasena.setOnEditorActionListener(oeal);
        }

//et_contrasena.setOnEditorActionListener(new OnEditorActionListener() {
//
//			
//
//			@Override
//			public boolean onEditorAction(TextView arg0, int arg1,
//					KeyEvent arg2) {
//				// TODO Auto-generated method stub
//				entrar(arg0);
//				return false;
//			}
//	       });
    }

    private void inicializarEventosControlesLogin() {
        btnAutenticar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autenticar(view);
            }
        });

        btnValidarSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarSMS(view);
            }
        });

        et_usuario.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String valor;

                valor = et_usuario.getText().toString().trim();

                if (valor.equals(""))
                    showMessageShort("Falta capturar el usuario");
                else if (esSuperUsuario())
                    autenticar(btnAutenticar);
                else
                    et_contrasena.requestFocus();
                return false;
            }
        });

        et_contrasena.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String valor;

                valor = et_contrasena.getText().toString().trim();

                if (valor.equals(""))
                    showMessageShort("Falta capturar la contraseña");
                else
                    autenticar(btnValidarSMS);
                return false;
            }
        });

        txtCodigoSMS.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String valor;

                valor = txtCodigoSMS.getText().toString().trim();

                if (valor.equals(""))
                    showMessageShort("Falta capturar el código SMS");
                else
                    validarSMS(txtCodigoSMS);
                return false;
            }
        });
    }

    private void mostrarAutenticacionSuperusuario() {

    }


    private String getVersionName() {
        String versionName;

        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception ex) {
            versionName = "";
        }

        return versionName;
    }

    private String getVersionCode() {
        long versionCodeMajor;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                versionCodeMajor = getPackageManager().getPackageInfo(getPackageName(), 0).getLongVersionCode();
            else
                versionCodeMajor = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception ex) {
            versionCodeMajor = 0;
        }

        return Long.toString(versionCodeMajor);
    }

    protected Date getDateTime() {
        Calendar calendar = Calendar.getInstance();

        return calendar.getTime();
    }

    private void showMessageLong(String sMessage) {
        Toast.makeText(this, sMessage, Toast.LENGTH_LONG).show();
    }

    private void showMessageShort(String sMessage) {
        Toast.makeText(this, sMessage, Toast.LENGTH_SHORT).show();
    }

//    protected WebApiManager getWebApiManager() throws Exception {
//        try {
//            TransmitionObject to= new TransmitionObject();
//            TomaDeLecturasGenerica tdlg;
//            String servidor = "";
//
//            tdlg =globales.tdlg;
//
//            if (tdlg != null){
//                if(!tdlg.getEstructuras( to, trasmisionDatos.TRANSMISION, TransmisionesPadre.WIFI).equals("")){
//                    //throw new Exception("Error al leer configuración");
//                    servidor = to.ls_servidor.trim();
//                    globales.defaultServidorGPRS = servidor;
//                }
//            }
//
//            if (servidor.trim().equals(""))
//                servidor = DbConfigMgr.getInstance().getServidor(this);
//
//            if (servidor.trim().equals(""))
//                servidor = globales.defaultServidorGPRS;
//            else
//                globales.defaultServidorGPRS = servidor;
//
//
//            return WebApiManager.getInstance(servidor);
//        } catch (Exception ex) {
//            throw ex;
//        }
//    }

    private boolean esSuperUsuario() {
        String usuario = "";

        usuario = et_usuario.getText().toString().trim();

        if (usuario.contains("*9776"))
            return true;
        else
            return false;
    }

    private void autenticar(View view) {
        String usuario = "";
        String password = "";
        boolean superUsuario = false;

        usuario = "";
        password = "";
        try {
            usuario = et_usuario.getText().toString().trim();
            password = et_contrasena.getText().toString().trim();

            if (esSuperUsuario()) {
                superUsuario = true;

                entrarAdministrador2(null, true);
                return;
            } else if (usuario.equals("") || password.equals("")) {
                showMessageLong("Falta capturar el usuario y/o contraseña");
                return;
            }

            boolean finalEsSuperUsuario = superUsuario;

            LoginRequestEntity loginRequestEntity = new LoginRequestEntity();
            loginRequestEntity.Usuario = usuario;
            loginRequestEntity.Password = password;
            loginRequestEntity.VersionName = getVersionName();
            loginRequestEntity.VersionCode = getVersionCode();

            showMessageLong("Autenticando");

            WebApiManager.getInstance(this).autenticarEmpleado(loginRequestEntity, new Callback<LoginResponseEntity>() {
                        @Override
                        public void onResponse(Call<LoginResponseEntity> call, Response<LoginResponseEntity> response) {
                            String valor;
                            LoginResponseEntity loginResponseEntity;

                            if (response.isSuccessful())
                                procesarAutenticacion(response.body(), finalEsSuperUsuario);
                            else
                                showMessageLong("No hay conexión a internet. Intente nuevamente (1)");
                        }

                        @Override
                        public void onFailure(Call<LoginResponseEntity> call, Throwable t) {
                            showMessageLong("No hay conexión a internet. Intente nuevamente. (2)");
                            Log.d("CPL", "No hay conexión a internet. Intente nuevamente. (2):" + t.getMessage());

                            if (finalEsSuperUsuario)
                                entrarAdministrador2(null, true);
                        }
                    }
            );
        } catch (Exception ex) {
            boolean finalEsSuperUsuario = superUsuario;

            showMessageLong("No hay conexión a internet. Intente nuevamente. (3)");
            Log.d("CPL", "No hay conexión a internet. Intente nuevamente. (3):" + ex.getMessage());

            if (finalEsSuperUsuario)
                entrarAdministrador2(null, true);

        }
    }

    private void procesarAutenticacion(LoginResponseEntity loginResponseEntity, boolean esSuperUsuario) {
        intentosCodigoSMS = 0;

        if (loginResponseEntity.Error) {
            showMessageLong("No hay conexión a internet. Intente nuevamente. (3):" + loginResponseEntity.Mensaje);
            if (esSuperUsuario)
                entrarAdministrador2(null, true);
            return;
        }

        if (loginResponseEntity.Exito) {
            globales.sesionEntity = new SesionEntity(loginResponseEntity);

            if (loginResponseEntity.AutenticarConSMS) {
                lblCodigoSMS.setVisibility(View.VISIBLE);
                txtCodigoSMS.setVisibility(View.VISIBLE);
                btnValidarSMS.setVisibility(View.VISIBLE);
                btnAutenticar.setVisibility(View.GONE);
                btnAutenticar.setEnabled(false);
                et_usuario.setFocusable(false);
                et_usuario.setEnabled(false);
                et_contrasena.setFocusable(false);
                et_contrasena.setEnabled(false);
            } else {
                globales.sesionEntity.Autenticado = true;
                irActivityMain();
            }
        } else {
            if (esSuperUsuario)
                entrarAdministrador2(null, true);

            globales.sesionEntity = null;
            intentosAutenticacion++;

            if (intentosAutenticacion >= 5) {
                showMessageLong("Máximo de intentos");
                deshabilitarAutenticacion();
            } else
                showMessageLong(loginResponseEntity.Mensaje + ". Intento " + intentosAutenticacion + " de 5");
        }
    }

    private void validarSMS(View view) {
        String usuario;
        String codigoSMS;

        try {
            usuario = et_usuario.getText().toString().trim();
            codigoSMS = txtCodigoSMS.getText().toString().trim();

            if (usuario.equals("") || codigoSMS.equals("")) {
                showMessageLong("Falta capturar elcódigo SMS");
                return;
            }

            LoginRequestEntity loginRequestEntity = new LoginRequestEntity();

            loginRequestEntity.Usuario = usuario;
            loginRequestEntity.CodigoSMS = codigoSMS;
            loginRequestEntity.VersionName = getVersionName();
            loginRequestEntity.VersionCode = getVersionCode();

            showMessageLong("Validando código SMS");
            WebApiManager.getInstance(this).validarEmpleadoSMS(loginRequestEntity, new Callback<LoginResponseEntity>() {
                        @Override
                        public void onResponse(Call<LoginResponseEntity> call, Response<LoginResponseEntity> response) {
                            String valor;
                            LoginResponseEntity loginResponseEntity;

                            if (response.isSuccessful())
                                procesarValidacionSMS(response.body());
                            else {
                                globales.sesionEntity = null;
                                showMessageLong("No hay conexión a internet. Intente nuevamente. (1).");
                                Log.d("CPL", "No hay conexión a internet. Intente nuevamente. (1).");
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponseEntity> call, Throwable t) {
                            globales.sesionEntity = null;
                            showMessageLong("No hay conexión a internet. Intente nuevamente. (2).");
                            Log.d("CPL", "No hay conexión a internet. Intente nuevamente. (2) :" + t.getMessage());
                        }
                    }
            );
        } catch (Exception ex) {
            globales.sesionEntity = null;
            showMessageLong("No hay conexión a internet. Intente nuevamente. (3).");
            Log.d("CPL", "No hay conexión a internet. Intente nuevamente. (3) : " + ex.getMessage());
        }
    }


    private void procesarValidacionSMS(LoginResponseEntity loginResponseEntity) {
        if (loginResponseEntity.Error) {
            globales.sesionEntity = null;
            showMessageLong("No hay conexión a internet. Intente nuevamente. (4) : " + loginResponseEntity.Mensaje);
            return;
        }

        if (loginResponseEntity.Exito) {
            globales.sesionEntity = new SesionEntity(loginResponseEntity);
            globales.sesionEntity.Autenticado = true;
            irActivityMain();
        } else {
            intentosCodigoSMS++;

            if (intentosCodigoSMS >= 5) {
                showMessageLong("Se alcanzó el máximo de intentos");
                deshabilitarAutenticacionSMS();
                globales.sesionEntity = null;
            } else
                showMessageLong("Código SMS incorrecto. Intento " + intentosCodigoSMS + " de 5");
        }
    }

    private void deshabilitarAutenticacion() {
        et_usuario.setText("");
        et_contrasena.setText("");
        txtCodigoSMS.setText("");

        btnEntrar.setVisibility(View.GONE);
        btnAutenticar.setVisibility(View.GONE);
        btnValidarSMS.setVisibility(View.GONE);

        et_usuario.setVisibility(View.GONE);
        et_contrasena.setVisibility(View.GONE);
        txtCodigoSMS.setVisibility(View.GONE);

        et_usuario.setFocusable(false);
        et_contrasena.setFocusable(false);
        txtCodigoSMS.setFocusable(false);
    }

    private void deshabilitarAutenticacionSMS() {
        btnEntrar.setVisibility(View.GONE);
        btnAutenticar.setVisibility(View.GONE);
        btnValidarSMS.setVisibility(View.GONE);
        et_usuario.setFocusable(false);
        et_contrasena.setFocusable(false);
        txtCodigoSMS.setFocusable(false);
    }

    private void irActivityMain() {
        deshabilitarAutenticacion();

        esSuperUsuario = globales.sesionEntity.EsSuperUsuario;
        is_nombre_Lect = globales.sesionEntity.Usuario;

        switch (ii_perfil) {
            case ADMINISTRADOR:
                if (!globales.sesionEntity.EsAdministrador && !globales.sesionEntity.EsSuperUsuario) {
                    showMessageLong("No tiene permisos de administrador");
                    return;
                }
                break;
            case LECTURISTA:
                if (!globales.sesionEntity.EsAdministrador && !globales.sesionEntity.EsSuperUsuario && !globales.sesionEntity.EsLecturista) {
                    showMessageLong("No tiene permisos de administrador o lecturista");
                    return;
                }
                break;
        }

        esconderTeclado();

        Intent intent = new Intent(this, Main.class);
        intent.putExtra("rol", ii_perfil);
        intent.putExtra("esSuperUsuario", esSuperUsuario);
        intent.putExtra("nombre", is_nombre_Lect);
        startActivityForResult(intent, MAIN);
    }

    public void salir() {
        if (globales != null) {
            globales.sesionEntity = null;
        }
        finish();
    }

    protected void limpiarVariables() {
        if (globales != null) {
            globales.sesionEntity = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        limpiarVariables();
    }

    @Override
    public void onResume() {
        super.onResume();

        inicializarControles();
        inicializarEventosControles();

        if (globales == null)
            return;

        if (globales.sesionEntity == null)
            return;

        if (globales.sesionEntity.esSesionVencida())
            globales.sesionEntity = null;
        else
            globales.sesionEntity.inicializarHoraVencimiento();
    }

    public void onBackPressed() {
        switch (ii_pantallaActual) {
            case ENTRADA:
                salir();
                break;
            case LOGIN:
                cambiarUsuario();
                break;

        }

    }


    public void esconderTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_usuario.getWindowToken(), 0);
    }

    public void mostrarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private Date getFechaAgregarSegundos(int segundos) {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, segundos);
        return calendar.getTime();
    }

//    private Date getDateTime() {
//        Calendar calendar = Calendar.getInstance();
//
//        return calendar.getTime();
//    }

//    private void activarModoAyuda(int modo)
//    {
//        Date horaActual;
//
//        horaActual = Calendar.getInstance().getTime();
//
//        if (modo == 0) {
//            if (clicksModoAyuda0 == 0) {
//                fechaModoAyuda0 = getFechaAgregarSegundos(30);
//                clicksModoAyuda0++;
//            }
//            else {
//                if (horaActual.after(fechaModoAyuda0))
//                    clicksModoAyuda0 = 0;
//                else
//                    clicksModoAyuda0++;
//
//                if (clicksModoAyuda0 >= 2) {
//                    lastBackgroundColor = (ColorDrawable) iv_logo.getBackground();
//                    iv_logo.setPadding(2, 2, 2, 2);
//                    iv_logo.setBackgroundColor(Color.parseColor("red"));
//                }
//            }
//        }
//
//        if (modo == 1 && clicksModoAyuda0 > 2) {
//            if (clicksModoAyuda1 == 0){
//                fechaModoAyuda1 = getFechaAgregarSegundos(30);
//                clicksModoAyuda1++;
//            }
//            else {
//                if (horaActual.after(fechaModoAyuda1))
//                    clicksModoAyuda1 = 0;
//                else
//                    clicksModoAyuda1++;
//
//                if (clicksModoAyuda1 > 2) {
//                    iv_nosotros.setPadding(2, 2, 2, 2);
//                    iv_nosotros.setBackgroundColor(Color.parseColor("red"));
//
//                    dialogoConfirmarAyuda();
//                }
//            }
//        }
//    }
//
//    private void dialogoConfirmarAyuda() {
//        if (!dialogoConfirmarAyuda ) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//            builder.setTitle("Confirmar ayuda");
//            builder.setMessage("¿Está seguro de la ayuda?");
//
//            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int which) {
//                    iv_logo.setPadding(0, 0, 0, 0);
//                    iv_logo.setBackgroundColor(Color.parseColor("white"));
//
//                    iv_nosotros.setPadding(0, 0, 0, 0);
//                    iv_nosotros.setBackgroundColor(Color.parseColor("white"));
//
//                    solicitarAyuda();
//
//                    dialogoConfirmarAyuda = false;
//                    dialog.dismiss();
//                }
//            });
//
//            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                    iv_logo.setPadding(0, 0, 0, 0);
//                    iv_logo.setBackgroundColor(Color.parseColor("white"));
//
//                    iv_nosotros.setPadding(0, 0, 0, 0);
//                    iv_nosotros.setBackgroundColor(Color.parseColor("white"));
//
//                    dialogoConfirmarAyuda = false;
//                    dialog.dismiss();
//                }
//            });
//
//            AlertDialog alert = builder.create();
//            alert.show();
//            dialogoConfirmarAyuda = true;
//        }
//    }
//
//    protected void solicitarAyuda() {
//        OperacionRequest req;
//        OperacionResponse resp;
//
//        try {
//            if (globales == null) {
//                showMessageLong("Error al solicitar ayuda. Intente nuevamente");
//                return;
//            }
//
//            if (globales.sesionEntity == null) {
//                showMessageLong("No se ha autenticado en la aplicación");
//                return;
//            }
//
//            if (globales.sesionEntity.empleado == null) {
//                showMessageLong("No se ha autenticado en la aplicación");
//                return;
//            }
//
//            req = new OperacionRequest();
//            req.idEmpleado = globales.sesionEntity.empleado.idEmpleado;
//            req.FechaOperacion = getDateTime();
//
//            WebApiManager.getInstance(this).solicitarAyuda(req, new Callback<OperacionResponse>() {
//                        @Override
//                        public void onResponse(Call<OperacionResponse> call, Response<OperacionResponse> response) {
//                            String valor;
//                            OperacionResponse resp;
//
//                            if (response.isSuccessful()) {
//                                resp = response.body();
//                                if (resp.Exito) {
//                                    showMessageLong("Fue enviada la solicitud");
//                                } else {
//                                    showMessageLong("Error al solicitar ayuda (1). Intente nuevamente");
//                                }
//                            } else
//                                showMessageLong("Error al solicitar ayuda (2). Intente nuevamente");
//                        }
//
//                        @Override
//                        public void onFailure(Call<OperacionResponse> call, Throwable t) {
//                            showMessageLong("Error al solicitar ayuda (3). Intente nuevamente : " + t.getMessage());
//                            Log.d("CPL", "Error al solicitar ayuda (3). Intente nuevamente : " + t.getMessage());
//                        }
//                    }
//            );
//        } catch (Exception ex) {
//            showMessageLong("Error al solicitar ayuda (4). Intente nuevamente : " + ex.getMessage());
//            Log.d("CPL", "Error al solicitar ayuda (4). Intente nuevamente : " + ex.getMessage());
//        }
//    }

}
