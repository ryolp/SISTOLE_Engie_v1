package enruta.sistole_gen.services;

import enruta.sistole_gen.TomaDeLecturasGenerica;
import enruta.sistole_gen.TransmisionesPadre;
import enruta.sistole_gen.TransmitionObject;
import enruta.sistole_gen.entities.LoginRequestEntity;
import enruta.sistole_gen.entities.LoginResponseEntity;
import enruta.sistole_gen.interfaces.ILoginApi;
import enruta.sistole_gen.trasmisionDatos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginApiManager {
    private ILoginApi service = null;
    private static LoginApiManager apiManager;
    private static String _Url="";

    private LoginApiManager(TomaDeLecturasGenerica tdlg, String servidor) throws Exception {
        createConection(tdlg, servidor);
    }

    protected void createConection(TomaDeLecturasGenerica tdlg, String servidor)  throws Exception {
        TransmitionObject to= new TransmitionObject();

        if(!tdlg.getEstructuras( to, trasmisionDatos.TRANSMISION, TransmisionesPadre.WIFI).equals("")){
            //throw new Exception("Error al leer configuración");
            to.ls_servidor = servidor;
        }

        if (service == null) {
            _Url = to.ls_servidor;
            service = WebApiService.Create(_Url);
        }
        else if (!to.ls_servidor.equals(_Url)){
            // Si la URL cambió entonces destruye el objeto de transmisión de Retrofil y crea uno nuevo..
            // ... con la nueva URL

            _Url = to.ls_servidor;
            service = null;
            service = WebApiService.Create(_Url);
        }
    }

    // Para crear una sola instancia de esta clase que será de gestión para solicitar la autenticación
    public static LoginApiManager getInstance(TomaDeLecturasGenerica tdlg, String servidor) throws Exception {
        if (apiManager == null)
            apiManager = new LoginApiManager(tdlg, servidor);
        else
            apiManager.createConection(tdlg, servidor);

        return apiManager;
    }

    // Se hace una llamada asíncrona al web api, y el resultado lo recibirá la función que se defina en callback
    public void echoPing(Callback<String> callback){
        Call<String> echoPingCall = service.echoping();

        echoPingCall.enqueue(callback);
    }

    public void autenticarEmpleado(LoginRequestEntity loginRequestEntity, Callback<LoginResponseEntity> callBack){
        Call<LoginResponseEntity> autenticarCall = service.autenticarEmpleado(loginRequestEntity);

        autenticarCall.enqueue(callBack);
    }

    public void validarEmpleadoSMS(LoginRequestEntity loginRequestEntity, Callback<LoginResponseEntity> callBack){
        Call<LoginResponseEntity> autenticarCall = service.validarEmpleadoSMS(loginRequestEntity);

        autenticarCall.enqueue(callBack);
    }

    public void autenticarEmpleado2(LoginRequestEntity loginRequestEntity, Callback<LoginResponseEntity> callBack){
        Call<LoginResponseEntity> autenticarCall = service.autenticarEmpleado2(loginRequestEntity.Usuario, loginRequestEntity.Password);

        autenticarCall.enqueue(callBack);
    }

    public void validarEmpleadoSMS2(LoginRequestEntity loginRequestEntity, Callback<LoginResponseEntity> callBack){
        Call<LoginResponseEntity> autenticarCall = service.validarEmpleadoSMS2(loginRequestEntity.Usuario, loginRequestEntity.CodigoSMS);

        autenticarCall.enqueue(callBack);
    }

    public String echoPing(){
        try {
            String resultado;

            Call<String> echoPingCall = service.echoping();

            Response<String> respuesta = echoPingCall.execute();

            return respuesta.body();
        }
        catch (Exception ex ){
            return "";
        }
    }
}
