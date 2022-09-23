package enruta.sistole_gen;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
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

import enruta.sistole_gen.entities.LoginRequestEntity;
import enruta.sistole_gen.entities.LoginResponseEntity;
import enruta.sistole_gen.entities.UsuarioEntity;
import enruta.sistole_gen.services.WebApiManager;
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

    TextView tv_msj_login, tv_usuario, tv_contrasena, tv_version;
    EditText et_usuario, et_contrasena;
    Button btnEntrar;

    DBHelper dbHelper;
    SQLiteDatabase db;

    String superUsuarioPass = "9776";

    String usuario = "";

    Globales globales;
    ImageView iv_logo;

    // RL, 2022-07-14, Campos para validación SMS

    TextView lblMensaje;
    TextView lblCodigoSMS;
    EditText txtCodigoSMS;
    Button btnAutenticar;
    Button btnValidarSMS;
    int intentosAutenticacion = 0;
    int intentosCodigoSMS = 0;
    private boolean mTienePermisos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cpl);
        ii_pantallaActual = ENTRADA;

        globales = ((Globales) getApplicationContext());

        inicializarControles();

        try {
            tv_version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + ", " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        estableceVariablesDePaises();
    }

    private void inicializarControles() {
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        tv_version = (TextView) findViewById(R.id.tv_version_lbl);
        lblMensaje = (TextView) findViewById(R.id.txtMensaje);
    }

    /*
        Validación de permisos
     */

    private void validarPermisos() {
        String msg = "";

        mTienePermisos = true;

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mTienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            mTienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            mTienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            mTienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            mTienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mTienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            mTienePermisos = false;
        }

        if (ActivityCompat.checkSelfPermission(CPL.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            mTienePermisos = false;
        }

        if (!mTienePermisos) {
            lblMensaje.setText("Faltan permisos");
            lblMensaje.setVisibility(View.VISIBLE);
        }
        else
            lblMensaje.setVisibility(View.GONE);
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
        if (globales.usuarioEntity == null)
            return false;

        if (!globales.conservarSesion)
            return false;

        if (globales.usuarioEntity.esSesionVencida())
            return false;
        else
            globales.usuarioEntity.inicializarHoraVencimiento();

        return true;
    }


    public void entrarAdministrador(View v) {
        entrarAdministrador2(v, false);
    }

    public void entrarAdministrador2(View v, boolean bForzarAdministrador) {
        ii_perfil = ADMINISTRADOR;

        if (!mTienePermisos)
        {
            showMessageLong("No ha proporcionado los permisos necesarios para que funcione la aplicación");
            return;
        }

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
        if (!mTienePermisos)
        {
            showMessageLong("No ha proporcionado los permisos necesarios para que funcione la aplicación");
            return;
        }

        ii_perfil = LECTURISTA;
        ii_pantallaActual = LOGIN;

        //Hay que adaptar según el tipo de validacion

        if (!esSesionActiva()) {
            setContentView(R.layout.p_login);
            getObjetosLogin();
            globales.secuenciaSuperUsuario += "C";

            deshabilitarTodosLosControles();

            et_usuario.setFilters(new InputFilter[]{new InputFilter.LengthFilter(globales.longCampoUsuario)});
            et_contrasena.setFilters(new InputFilter[]{new InputFilter.LengthFilter(globales.longCampoContrasena)});
            et_usuario.setInputType(globales.tipoDeEntradaUsuarioLogin);

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

    private void deshabilitarTodosLosControles() {
        tv_usuario.setVisibility(View.GONE);
        et_usuario.setVisibility(View.GONE);
        btnAutenticar.setVisibility(View.GONE);

        tv_contrasena.setVisibility(View.GONE);
        et_contrasena.setVisibility(View.GONE);

        lblCodigoSMS.setVisibility(View.GONE);
        txtCodigoSMS.setVisibility(View.GONE);
        btnValidarSMS.setVisibility(View.GONE);
        btnEntrar.setVisibility(View.GONE);
        btnEntrar.setEnabled(false);
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
        } else {
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
    }

    private void mostrarAutenticacionSuperusuario() {

    }

    private WebApiManager getLoginApiManager() throws Exception {
        try {
            return WebApiManager.getInstance(globales.tdlg, globales.defaultServidorGPRS);
        } catch (Exception ex) {
            throw ex;
        }
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

    private void autenticar(View view) {
        String usuario = "";
        String password = "";
        boolean esSuperUsuario = false;

        usuario = "";
        password = "";
        try {
            usuario = et_usuario.getText().toString().trim();
            password = et_contrasena.getText().toString().trim();

            if (usuario.contains("*9776")) {
                esSuperUsuario = true;

                entrarAdministrador2(null, true);
                return;
            } else if (usuario.equals("") || password.equals("")) {
                showMessageLong("Falta capturar el usuario y/o contraseña");
                return;
            }


            boolean finalEsSuperUsuario = esSuperUsuario;

            LoginRequestEntity loginRequestEntity = new LoginRequestEntity(usuario, "", password, "", getVersionName(), getVersionCode());

            showMessageLong("Autenticando");

            getLoginApiManager().autenticarEmpleado(loginRequestEntity, new Callback<LoginResponseEntity>() {
                        @Override
                        public void onResponse(Call<LoginResponseEntity> call, Response<LoginResponseEntity> response) {
                            String valor;
                            LoginResponseEntity loginResponseEntity;

                            if (response.isSuccessful())
                                procesarAutenticacion(response.body(), finalEsSuperUsuario);
                            else
                                showMessageLong("Error al autenticar (1)");
                        }

                        @Override
                        public void onFailure(Call<LoginResponseEntity> call, Throwable t) {
                            showMessageLong("Error al autenticar (2):" + t.getMessage());
                            Log.d("CPL", "Error al autenticar (2):" + t.getMessage());

                            if (finalEsSuperUsuario)
                                entrarAdministrador2(null, true);
                        }
                    }
            );
        } catch (Exception ex) {
            boolean finalEsSuperUsuario = esSuperUsuario;

            showMessageLong("Error al autenticar (3):" + ex.getMessage());
            Log.d("CPL", "Error al autenticar (3):" + ex.getMessage());

            if (finalEsSuperUsuario)
                entrarAdministrador2(null, true);

        }
    }

    private void procesarAutenticacion(LoginResponseEntity loginResponseEntity, boolean esSuperUsuario) {
        intentosCodigoSMS = 0;

        if (loginResponseEntity.Error) {
            showMessageLong("Error al autenticar (3):" + loginResponseEntity.Mensaje);
            if (esSuperUsuario)
                entrarAdministrador2(null, true);
            return;
        }

        if (loginResponseEntity.Exito) {
            globales.usuarioEntity = new UsuarioEntity(loginResponseEntity);

            if (loginResponseEntity.AutenticarConSMS) {
                globales.usuarioEntity.Autenticado = false;
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
                globales.usuarioEntity.Autenticado = true;
                irActivityMain();
            }
        } else {
            if (esSuperUsuario)
                entrarAdministrador2(null, true);

            globales.usuarioEntity = null;
            intentosAutenticacion++;

            if (intentosAutenticacion >= 5) {
                showMessageLong("Máximo de intentos");
                deshabilitarAutenticacion();
            } else
                showMessageLong("Usuario o contraseña incorrecta. Intento " + intentosAutenticacion + " de 5");
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

            LoginRequestEntity loginRequestEntity = new LoginRequestEntity(usuario, "", "", codigoSMS, getVersionName(), getVersionCode());

            showMessageLong("Validando código SMS");
            getLoginApiManager().validarEmpleadoSMS(loginRequestEntity, new Callback<LoginResponseEntity>() {
                        @Override
                        public void onResponse(Call<LoginResponseEntity> call, Response<LoginResponseEntity> response) {
                            String valor;
                            LoginResponseEntity loginResponseEntity;

                            if (response.isSuccessful())
                                procesarValidacionSMS(response.body());
                            else {
                                globales.usuarioEntity = null;
                                showMessageLong("Error al validar SMS (1)");
                                Log.d("CPL", "Error al validar SMS (1)");
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponseEntity> call, Throwable t) {
                            globales.usuarioEntity = null;
                            showMessageLong("Error al validar SMS (2):" + t.getMessage());
                            Log.d("CPL", "Error al validar SMS (2):" + t.getMessage());
                        }
                    }
            );
        } catch (Exception ex) {
            globales.usuarioEntity = null;
            showMessageLong("Error al validar SMS (3):" + ex.getMessage());
            Log.d("CPL", "Error al validar SMS (3):" + ex.getMessage());
        }
    }


    private void procesarValidacionSMS(LoginResponseEntity loginResponseEntity) {
        if (loginResponseEntity.Error) {
            globales.usuarioEntity = null;
            showMessageLong("Error al validar SMS (3):" + loginResponseEntity.Mensaje);
            return;
        }

        if (loginResponseEntity.Exito) {
            globales.usuarioEntity = new UsuarioEntity(loginResponseEntity);
            globales.usuarioEntity.Autenticado = true;
            irActivityMain();
        } else {
            intentosCodigoSMS++;

            if (intentosCodigoSMS >= 5) {
                showMessageLong("Se alcanzó el máximo de intentos");
                deshabilitarAutenticacionSMS();
                globales.usuarioEntity = null;
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

        esSuperUsuario = globales.usuarioEntity.EsSuperUsuario;
        is_nombre_Lect = globales.usuarioEntity.Usuario;

        switch (ii_perfil) {
            case ADMINISTRADOR:
                if (!globales.usuarioEntity.EsAdministrador && !globales.usuarioEntity.EsSuperUsuario) {
                    showMessageLong("No tiene permisos de administrador");
                    return;
                }
                break;
            case LECTURISTA:
                if (!globales.usuarioEntity.EsAdministrador && !globales.usuarioEntity.EsSuperUsuario && !globales.usuarioEntity.EsLecturista) {
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

    private void showMessageLong(String sMessage) {
        Toast.makeText(this, sMessage, Toast.LENGTH_LONG).show();
    }

    private void showMessageShort(String sMessage) {
        Toast.makeText(this, sMessage, Toast.LENGTH_SHORT).show();
    }

    public void salir() {
        if (globales != null) {
            globales.usuarioEntity = null;
        }
        finish();
    }

    protected void limpiarVariables() {
        if (globales != null) {
            globales.usuarioEntity = null;
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

        validarPermisos();

        if (globales == null)
            return;

        if (globales.usuarioEntity == null)
            return;

        if (globales.usuarioEntity.esSesionVencida())
            globales.usuarioEntity = null;
        else
            globales.usuarioEntity.inicializarHoraVencimiento();
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

}
