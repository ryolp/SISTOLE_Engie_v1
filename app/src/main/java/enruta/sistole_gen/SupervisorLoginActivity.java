package enruta.sistole_gen;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SupervisorLoginActivity extends Activity {

    private final int ESTADO_INICIAL = 0;
    private final int ESTADO_AUTENTICADO = 1;
    private final int ESTADO_SMS_VALIDADO = 2;

    private Button btnAutenticar = null;
    private Button btnValidarSMS = null;
    private Button btnEnviarInforme = null;
    private TextView lblUsuario = null;
    private EditText txtUsuario = null;
    private TextView lblPassword = null;
    private EditText txtPassword = null;
    private TextView lblCodigoSMS = null;
    private EditText txtCodigoSMS = null;
    private int estado = ESTADO_INICIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_login);


    }

    @Override
    protected void onResume() {
        super.onResume();

        inicializarControles();
        inicializarEventosControles();
    }

    /* -------------------------------------------------------------------------------------------
        Inicializa las referencias a los controles
    ------------------------------------------------------------------------------------------- */

    protected void inicializarControles() {
        if (btnAutenticar == null)
            btnAutenticar = (Button) findViewById(R.id.btnAutenticar);

        if (btnValidarSMS == null)
            btnValidarSMS = (Button) findViewById(R.id.btnValidarSMS);

        if (btnEnviarInforme == null)
            btnEnviarInforme = (Button) findViewById(R.id.btnEnviarInforme);

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

        CambiarEstadoControles(ESTADO_INICIAL);
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
    }

    protected void finalizado()
    {
        // setResult(Activity.RESULT_OK, resultIntent);
        setResult(Activity.RESULT_OK);
        finish();
    }

    protected void CambiarEstadoControles(int p_estado){
        estado = p_estado;

        switch (estado ) {
            case ESTADO_INICIAL:
                txtUsuario.setText("");
                txtPassword.setText("");
                txtCodigoSMS.setText("");

                lblUsuario.setVisibility(View.VISIBLE);
                txtUsuario.setVisibility(View.VISIBLE);
                lblPassword.setVisibility(View.VISIBLE);
                txtPassword.setVisibility(View.VISIBLE);
                lblCodigoSMS.setVisibility(View.GONE);
                txtCodigoSMS.setVisibility(View.GONE);
                btnValidarSMS.setVisibility(View.GONE);
                btnEnviarInforme.setVisibility(View.GONE);
                break;
            case ESTADO_AUTENTICADO:
                txtCodigoSMS.setText("");

                lblUsuario.setVisibility(View.GONE);
                txtUsuario.setVisibility(View.GONE);
                lblPassword.setVisibility(View.GONE);
                txtPassword.setVisibility(View.GONE);

                lblCodigoSMS.setVisibility(View.VISIBLE);
                txtCodigoSMS.setVisibility(View.VISIBLE);

                btnValidarSMS.setVisibility(View.VISIBLE);
                btnAutenticar.setVisibility(View.GONE);
                btnEnviarInforme.setVisibility(View.GONE);
                break;
            case ESTADO_SMS_VALIDADO:
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
        Realiza las acciones de autenticación
    ------------------------------------------------------------------------------------------- */

    protected void autenticar() {
        CambiarEstadoControles(ESTADO_AUTENTICADO);
    }

    /* -------------------------------------------------------------------------------------------
        Realiza las acciones de validar el SSMS
    ------------------------------------------------------------------------------------------- */

    protected void validarSMS() {
        CambiarEstadoControles(ESTADO_SMS_VALIDADO);
    }

        /* -------------------------------------------------------------------------------------------
        Realiza las acciones de validar el SSMS
    ------------------------------------------------------------------------------------------- */

    protected void enviarInforme() {
        finalizado();
    }
}