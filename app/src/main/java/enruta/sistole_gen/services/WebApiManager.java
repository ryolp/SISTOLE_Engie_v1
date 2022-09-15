package enruta.sistole_gen.services;

import enruta.sistole_gen.entities.OperacionRequest;
import enruta.sistole_gen.entities.OperacionResponse;
import enruta.sistole_gen.entities.LoginRequestEntity;
import enruta.sistole_gen.entities.LoginResponseEntity;
import enruta.sistole_gen.interfaces.IWebApi;
import retrofit2.Call;
import retrofit2.Callback;

public class WebApiManager {
    private IWebApi service = null;
    private static WebApiManager apiManager;
    private static String _servidorUrl="";

    private WebApiManager(String servidor) throws Exception {
        createConection(servidor);
    }

    protected void createConection(String servidor)  throws Exception {
        if (service == null) {
            _servidorUrl = servidor;
            service = WebApiService.Create(servidor);
        }
        else if (!_servidorUrl.equals(servidor)){
            // Si la URL cambió entonces destruye el objeto de transmisión de Retrofil y crea uno nuevo..
            // ... con la nueva URL

            _servidorUrl = servidor;
            service = null;
            service = WebApiService.Create(_servidorUrl);
        }
    }

    // Para crear una sola instancia de esta clase que será de gestión para solicitar la autenticación
    public static WebApiManager getInstance(String servidor) throws Exception {
        if (apiManager == null)
            apiManager = new WebApiManager(servidor);
        else
            apiManager.createConection(servidor);

        return apiManager;
    }

    // Se hace una llamada asíncrona al web api, y el resultado lo recibirá la función que se defina en callback
    public void echoPing(Callback<String> callback){
        Call<String> call = service.echoping();

        call.enqueue(callback);
    }

    public void autenticarEmpleado(LoginRequestEntity loginRequestEntity, Callback<LoginResponseEntity> callBack){
        Call<LoginResponseEntity> call = service.autenticarEmpleado(loginRequestEntity);

        call.enqueue(callBack);
    }

    public void validarEmpleadoSMS(LoginRequestEntity loginRequestEntity, Callback<LoginResponseEntity> callBack){
        Call<LoginResponseEntity> call = service.validarEmpleadoSMS(loginRequestEntity);

        call.enqueue(callBack);
    }

    public void autenticarEmpleado2(LoginRequestEntity loginRequestEntity, Callback<LoginResponseEntity> callBack){
        Call<LoginResponseEntity> call = service.autenticarEmpleado2(loginRequestEntity.Usuario, loginRequestEntity.Password);

        call.enqueue(callBack);
    }

    public void validarEmpleadoSMS2(LoginRequestEntity loginRequestEntity, Callback<LoginResponseEntity> callBack){
        Call<LoginResponseEntity> call = service.validarEmpleadoSMS2(loginRequestEntity.Usuario, loginRequestEntity.CodigoSMS);

        call.enqueue(callBack);
    }

    public void checkIn(OperacionRequest request, Callback<OperacionResponse> callBack){
        Call<OperacionResponse> call = service.checkIn(request);

        call.enqueue(callBack);
    }

    public void checkOut(OperacionRequest request, Callback<OperacionResponse> callBack){
        Call<OperacionResponse> call = service.checkOut(request);

        call.enqueue(callBack);
    }

    public void checkSeguridad(OperacionRequest request, Callback<OperacionResponse> callBack){
        Call<OperacionResponse> call = service.checkSeguridad(request);

        call.enqueue(callBack);
    }

    public void cerrarArchivo(OperacionRequest request, Callback<OperacionResponse> callBack){
        Call<OperacionResponse> call = service.cerrarArchivo(request);

        call.enqueue(callBack);
    }

    public void marcarArchivoDescargado(OperacionRequest request, Callback<OperacionResponse> callBack){
        Call<OperacionResponse> call = service.marcarArchivoDescargado(request);

        call.enqueue(callBack);
    }

    public void solicitarAyuda(OperacionRequest request, Callback<OperacionResponse> callBack){
        Call<OperacionResponse> call = service.solicitarAyuda(request);

        call.enqueue(callBack);
    }

    public void verificarConexion(LoginRequestEntity request, Callback<LoginResponseEntity> callBack){
        Call<LoginResponseEntity> call = service.verificarConexion(request);

        call.enqueue(callBack);
    }

//    public String echoPing(){
//        try {
//            String resultado;
//
//            Call<String> echoPingCall = service.echoping();
//
//            Response<String> respuesta = echoPingCall.execute();
//
//            return respuesta.body();
//        }
//        catch (Exception ex ){
//            return "";
//        }
//    }
}
