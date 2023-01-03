package enruta.sistole_engie;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Vector;

import enruta.sistole_engie.clases.AutenticadorMgr;
import enruta.sistole_engie.clases.BaseActivity;
import enruta.sistole_engie.entities.ResumenEntity;
import enruta.sistole_engie.clases.SupervisorCallback;
import enruta.sistole_engie.clases.SupervisorMgr;
import enruta.sistole_engie.clases.Utils;
import enruta.sistole_engie.entities.LoginRequestEntity;
import enruta.sistole_engie.entities.LoginResponseEntity;
import enruta.sistole_engie.entities.SupervisorLogRequest;
import enruta.sistole_engie.entities.SupervisorLogResponse;

public class SupervisorLoginActivity extends BaseActivity {

    private final int ESTADO_INICIAL = 0;
    private final int ESTADO_AUTENTICADO = 1;
    private final int ESTADO_SMS_VALIDADO = 2;

    private Button btnAutenticar = null;
    private Button btnValidarSMS = null;
    private Button btnEnviarInforme = null;
    private Button btnCancelar = null;
    private TextView lblMensajeSupervisor = null;
    private TextView lblUsuario = null;
    private EditText txtUsuario = null;
    private TextView lblPassword = null;
    private EditText txtPassword = null;
    private TextView lblCodigoSMS = null;
    private EditText txtCodigoSMS = null;
    private GridView gv_resumen = null;
    private ResumenEntity mResumen = null;
    private AutenticadorMgr mAutenticadorMgr = null;
    private SupervisorMgr mSupervisorMgr = null;
    private long mIdEmpleadoSupervisor = 0;

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

        if (globales.tll == null) {
            mostrarMensaje("Información", "No se han descargado las lecturas para hacer la supervisión.",
                    "", new DialogoMensaje.Resultado() {
                        @Override
                        public void Aceptar(boolean EsOk) {
                            finalizado(false);
                        }
                    });
        }
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

    protected void finalizado(boolean exito) {
        if (exito)
            setResult(Activity.RESULT_OK);
        else
            setResult(Activity.RESULT_CANCELED);
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
                btnAutenticar.setVisibility(View.VISIBLE);
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

    protected void inicializarAutenticador() {
        if (mAutenticadorMgr == null)
            mAutenticadorMgr = new AutenticadorMgr(this);

        mAutenticadorMgr.setAutenticadorCallback(new AutenticadorMgr.AutenticadorCallback() {
            @Override
            public void enExitoAutenticacion(LoginRequestEntity request, LoginResponseEntity resp) {
                if (resp.Empleado == null) {
                    mostrarMensaje("Error", "No se encontró al usuario. Intente nuevamente.", resp.MensajeError, null);
                    return;
                }

                if (!resp.Empleado.EsSupervisor) {
                    mostrarMensaje("Alerta", "No está registrado como Supervidor. Contacte a Soporte");
                    return;
                }

                if (resp.AutenticarConSMS)
                    cambiarEstadoControles(ESTADO_AUTENTICADO);
                else
                    procesarAutenticado(resp);
            }

            @Override
            public void enFalloAutenticacion(LoginRequestEntity request, LoginResponseEntity resp) {
                mostrarMensaje("Alerta", resp.Mensaje, resp.MensajeError, null);
            }

            @Override
            public void enErrorAutenticacion(LoginRequestEntity request, LoginResponseEntity resp) {
                mostrarMensaje("Alerta", getString(R.string.str_no_hay_conexion_internet_1), resp.MensajeError, null);
            }

            @Override
            public void enExitoValidarSMS(LoginRequestEntity request, LoginResponseEntity resp) {
                cambiarEstadoControles(ESTADO_AUTENTICADO);
                procesarAutenticado(resp);
            }

            @Override
            public void enFalloValidarSMS(LoginRequestEntity request, LoginResponseEntity resp) {
                mostrarMensaje("Alerta", resp.Mensaje, resp.MensajeError, null);
            }

            @Override
            public void enErrorValidarSMS(LoginRequestEntity request, LoginResponseEntity resp) {
                mostrarMensaje("Alerta", resp.Mensaje, resp.MensajeError, null);
            }
        });
    }

    /* -------------------------------------------------------------------------------------------
        Realiza las acciones de autenticación
    ------------------------------------------------------------------------------------------- */

    protected void autenticar() {
        String usuario;
        String password;

        try {
            inicializarAutenticador();

            usuario = txtUsuario.getText().toString().trim();
            password = txtPassword.getText().toString().trim();

            if (usuario.equals("") || password.equals("")) {
                mostrarMensaje("Alerta", "Falta capturar el usuario o la contraseña", "", null);
                return;
            }

            showMessageShort("Autenticando");
            mAutenticadorMgr.autenticar(usuario, password);
        } catch (Exception e) {
            mostrarMensaje("Error", "Error al querer autenticar. Intente nuevamente.", e.getMessage(), null);
        }
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

            if (usuario.equals("") || codigoSMS.equals("")) {
                mostrarMensaje("Alerta", "Falta capturar el código SMS", "", null);
                return;
            }

            if (mAutenticadorMgr == null) throw new AssertionError("El objeto no puede ser nulo");

            showMessageShort("Validando SMS");
            mAutenticadorMgr.validarSMS(usuario, codigoSMS);
        } catch (Exception e) {
            mostrarMensaje("Error", "Error al validar SMS. Intente nuevamente.", e.getMessage(), null);
        }
    }

    protected void procesarAutenticado(LoginResponseEntity resp) {
        try {
            if (resp.Empleado.idEmpleado == globales.sesionEntity.empleado.idEmpleado) {
                mostrarMensaje("Alerta", "El lecturista no puede supervisarse a si mismo");
                cambiarEstadoControles(ESTADO_INICIAL);
            } else {
                cambiarEstadoControles(ESTADO_SMS_VALIDADO);
                mIdEmpleadoSupervisor = resp.Empleado.idEmpleado;
                mostrarResumen();
            }
        } catch (Exception e) {
            mostrarMensaje("Error", "Error al mostrar el informe. Intente nuevamente.", e.getMessage(), null);
        }
    }


    /* -------------------------------------------------------------------------------------------
        Realiza las acciones de validar el SSMS
    ------------------------------------------------------------------------------------------- */

    protected void enviarInforme() {

        try {
            if (mSupervisorMgr == null) {
                mSupervisorMgr = new SupervisorMgr(this);

                mSupervisorMgr.setSupervisorCallback(new SupervisorCallback() {
                    @Override
                    public void enExito(SupervisorLogRequest request, SupervisorLogResponse resp) {
                        informeEnviado();
                    }

                    @Override
                    public void enFallo(SupervisorLogRequest request, SupervisorLogResponse resp) {
                        mostrarMensaje("Alerta", resp.Mensaje, resp.MensajeError, null);
                    }
                });
            }

            showMessageShort("Enviando informe");
            mSupervisorMgr.enviarInforme(globales.sesionEntity, globales.tll, globales.location, mIdEmpleadoSupervisor, mResumen);
        } catch (Throwable t) {
            mostrarMensaje("Alerta", "No se pudo enviar el informe. Intente nuevamente.", t.getMessage(), null);
        } finally {
            mSupervisorMgr = null;
        }
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
        } catch (Exception e) {
            throw e;
        } finally {
            closeDatabase();
        }
    }

    private void informeEnviado() {
        mostrarMensaje("Información", "Informe enviado", "", new DialogoMensaje.Resultado() {
            @Override
            public void Aceptar(boolean EsOk) {
                finalizado(true);
            }
        });
    }



}