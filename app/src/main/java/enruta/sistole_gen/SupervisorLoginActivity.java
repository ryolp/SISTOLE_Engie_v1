package enruta.sistole_gen;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Vector;

import enruta.sistole_gen.clases.Autenticador;
import enruta.sistole_gen.clases.AutenticadorCallback;
import enruta.sistole_gen.clases.BaseActivity;
import enruta.sistole_gen.clases.ResumenEntity;
import enruta.sistole_gen.clases.Utils;
import enruta.sistole_gen.entities.LoginRequestEntity;
import enruta.sistole_gen.entities.LoginResponseEntity;

public class SupervisorLoginActivity extends BaseActivity {

    private final int ESTADO_INICIAL = 0;
    private final int ESTADO_AUTENTICADO = 1;
    private final int ESTADO_SMS_VALIDADO = 2;

    private Button btnAutenticar = null;
    private Button btnValidarSMS = null;
    private Button btnEnviarInforme = null;
    private Button btnCancelar = null;
    private TextView lblMensajeSupervisor= null;
    private TextView lblUsuario = null;
    private EditText txtUsuario = null;
    private TextView lblPassword = null;
    private EditText txtPassword = null;
    private TextView lblCodigoSMS = null;
    private EditText txtCodigoSMS = null;
    private GridView gv_resumen = null;
    private ResumenEntity mResumen = null;
    private Autenticador mAutenticador = null;

    private int estado = ESTADO_INICIAL;

    /* -------------------------------------------------------------------------------------------
        Constructor de la clase
    ------------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_login);

        inicializarControles();
        inicializarEventosControles();
    }

    /* -------------------------------------------------------------------------------------------
        Se llama cuando el Activity se vuelve visible al usuario
    ------------------------------------------------------------------------------------------- */

    @Override
    protected void onResume() {
        super.onResume();


    }

    /* -------------------------------------------------------------------------------------------
        Inicializa las referencias a los controles
    ------------------------------------------------------------------------------------------- */

    protected void inicializarControles() {
        if (globales == null)
            globales = ((Globales) getApplicationContext());

        if (btnAutenticar == null)
            btnAutenticar = (Button) findViewById(R.id.btnAutenticar);

        if (btnValidarSMS == null)
            btnValidarSMS = (Button) findViewById(R.id.btnValidarSMS);

        if (btnEnviarInforme == null)
            btnEnviarInforme = (Button) findViewById(R.id.btnEnviarInforme);

        if (btnCancelar == null)
            btnCancelar = (Button) findViewById(R.id.btnCancelar);

        if (lblMensajeSupervisor == null)
            lblMensajeSupervisor = (TextView) findViewById(R.id.lblMensajeSupervisor);

        if (lblUsuario == null)
            lblUsuario = (TextView) findViewById(R.id.lblUsuario);

        if (txtUsuario == null)
            txtUsuario = (EditText) findViewById(R.id.txtUsuario);

        if (lblPassword == null)
            lblPassword = (TextView) findViewById(R.id.lblPassword);

        if (txtPassword == null)
            txtPassword = (EditText) findViewById(R.id.txtPassword);

        if (lblCodigoSMS == null)
            lblCodigoSMS = (TextView) findViewById(R.id.lblCodigoSMS);

        if (txtCodigoSMS == null)
            txtCodigoSMS = (EditText) findViewById(R.id.txtCodigoSMS);

        if (gv_resumen == null)
            gv_resumen = (GridView) findViewById(R.id.gv_resumen);

        cambiarEstadoControles(ESTADO_INICIAL);
    }

    /* -------------------------------------------------------------------------------------------
        Inicializa los eventos de los controles
    ------------------------------------------------------------------------------------------- */

    protected void inicializarEventosControles() {
        if (btnAutenticar != null) {
            btnAutenticar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autenticar();
                }
            });
        }

        if (btnValidarSMS != null) {
            btnValidarSMS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validarSMS();
                }
            });
        }

        if (btnEnviarInforme != null) {
            btnEnviarInforme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enviarInforme();
                }
            });
        }

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelar();
                }
            });
        }
    }

    protected void finalizado() {
        // setResult(Activity.RESULT_OK, resultIntent);
        setResult(Activity.RESULT_OK);
        finish();
    }

    protected void cancelar() {
        // setResult(Activity.RESULT_OK, resultIntent);
        setResult(Activity.RESULT_OK);
        finish();
    }

    protected void cambiarEstadoControles(int p_estado) {
        estado = p_estado;

        switch (estado) {
            case ESTADO_INICIAL:
                txtUsuario.setText("");
                txtPassword.setText("");
                txtCodigoSMS.setText("");

                lblMensajeSupervisor.setVisibility(View.VISIBLE);
                lblUsuario.setVisibility(View.VISIBLE);
                txtUsuario.setVisibility(View.VISIBLE);
                lblPassword.setVisibility(View.VISIBLE);
                txtPassword.setVisibility(View.VISIBLE);
                lblCodigoSMS.setVisibility(View.GONE);
                txtCodigoSMS.setVisibility(View.GONE);
                btnValidarSMS.setVisibility(View.GONE);
                btnEnviarInforme.setVisibility(View.GONE);
                gv_resumen.setVisibility(View.GONE);
                break;
            case ESTADO_AUTENTICADO:
                txtCodigoSMS.setText("");

                lblMensajeSupervisor.setVisibility(View.VISIBLE);
                lblUsuario.setVisibility(View.GONE);
                txtUsuario.setVisibility(View.GONE);
                lblPassword.setVisibility(View.GONE);
                txtPassword.setVisibility(View.GONE);

                lblCodigoSMS.setVisibility(View.VISIBLE);
                txtCodigoSMS.setVisibility(View.VISIBLE);

                btnValidarSMS.setVisibility(View.VISIBLE);
                btnAutenticar.setVisibility(View.GONE);
                btnEnviarInforme.setVisibility(View.GONE);
                gv_resumen.setVisibility(View.GONE);
                break;
            case ESTADO_SMS_VALIDADO:
                lblMensajeSupervisor.setVisibility(View.GONE);
                lblUsuario.setVisibility(View.GONE);
                txtUsuario.setVisibility(View.GONE);
                lblPassword.setVisibility(View.GONE);
                txtPassword.setVisibility(View.GONE);

                lblCodigoSMS.setVisibility(View.GONE);
                txtCodigoSMS.setVisibility(View.GONE);

                btnValidarSMS.setVisibility(View.GONE);
                btnAutenticar.setVisibility(View.GONE);
                btnEnviarInforme.setVisibility(View.VISIBLE);
                break;
        }
    }

    /* -------------------------------------------------------------------------------------------
        Inicializa el autenticador de usuarios
    ------------------------------------------------------------------------------------------- */

    protected void inicializarAutenticador()
    {
        if (mAutenticador == null)
            mAutenticador = new Autenticador(this);

        mAutenticador.setAutenticadorCallback(new AutenticadorCallback() {
            @Override
            public void enAutenticarExito(LoginRequestEntity request, LoginResponseEntity resp) {
                cambiarEstadoControles(ESTADO_AUTENTICADO);

                if (resp.Empleado == null)
                {
                    showMessageLong( "Error en la respuesta de autenticación");
                    return;
                }

                if (!resp.Empleado.EsSupervisor)
                {
                    showMessageLong( "No está registrado como Supervidor. Contacte a Soporte");
                    return;
                }

                if (resp.AutenticarConSMS)
                    cambiarEstadoControles(ESTADO_AUTENTICADO);
                else
                    procesarAutenticado();
            }

            @Override
            public void enAutenticarFallo(LoginRequestEntity request, LoginResponseEntity resp) {
                Utils.showMessageLong(SupervisorLoginActivity.this, resp.MensajeError);
            }

            @Override
            public void enValidarSMSExito(LoginRequestEntity request, LoginResponseEntity resp) {
                cambiarEstadoControles(ESTADO_AUTENTICADO);
                    procesarAutenticado();
            }

            @Override
            public void enValidarSMSFallo(LoginRequestEntity request, LoginResponseEntity resp) {
                showMessageLong( resp.MensajeError);
            }
        });
    }

    /* -------------------------------------------------------------------------------------------
        Realiza las acciones de autenticación
    ------------------------------------------------------------------------------------------- */

    protected void autenticar() {
        String usuario;
        String password;

        inicializarAutenticador();

        usuario = txtUsuario.getText().toString().trim();
        password = txtPassword.getText().toString().trim();

        mAutenticador.autenticar(usuario, password);
    }

    /* -------------------------------------------------------------------------------------------
        Realiza las acciones de validar el SSMS
    ------------------------------------------------------------------------------------------- */

    protected void validarSMS() {
        String usuario;
        String codigoSMS;

        try {
            usuario = txtUsuario.getText().toString().trim();
            codigoSMS = txtCodigoSMS.getText().toString().trim();

            if (mAutenticador==null) throw new AssertionError("El objetivo no puede ser nulo");

            mAutenticador.validarSMS(usuario, codigoSMS);
        }
        catch (Exception e){
            logMessageLong("Error al validar SMS", e);
        }
    }

    protected void procesarAutenticado() {
        cambiarEstadoControles(ESTADO_SMS_VALIDADO);
        mostrarResumen();
    }


    /* -------------------------------------------------------------------------------------------
        Realiza las acciones de validar el SSMS
    ------------------------------------------------------------------------------------------- */

    protected void enviarInforme() {
        finalizado();
    }


    /* -------------------------------------------------------------------------------------------
        Muestra el grid de resumen
    ------------------------------------------------------------------------------------------- */

    public void mostrarResumen() {
        Cursor c;
        TomaDeLecturasEngie tdlg;

        try {
            openDatabase();

            tdlg = (TomaDeLecturasEngie) globales.tdlg;

            mResumen = tdlg.getResumenEntity(db);
            Vector<EstructuraResumen> resumen = tdlg.getResumen(mResumen);

            gv_resumen.setAdapter(new ResumenGridAdapter(this, resumen, 36));

            gv_resumen.setVisibility(View.VISIBLE);
        }
        catch (Exception e )
        {

        }
        finally {
            closeDatabase();
        }
    }

}