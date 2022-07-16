package enruta.sistole_gen.services;

import enruta.sistole_gen.entities.LoginRequest;
import enruta.sistole_gen.entities.LoginResponse;
import enruta.sistole_gen.interfaces.ILoginApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginApiManager {
    private static ILoginApi service;
    private static LoginApiManager apiManager;

    private LoginApiManager() {
        service = WebApiService.Create();
    }

    // Para crear una sola instancia de esta clase que será de gestión para solicitar la autenticación
    public static LoginApiManager getInstance(){
        if (apiManager == null)
            apiManager = new LoginApiManager();

        return apiManager;
    }

    // Se hace una llamada asíncrona al web api, y el resultado lo recibirá la función que se defina en callback
    public void echoPing(Callback<String> callback){
        Call<String> echoPingCall = service.echoping();

        echoPingCall.enqueue(callback);
    }

    public void autenticarEmpleado(LoginRequest loginRequest, Callback<LoginResponse> callBack){
        Call<LoginResponse> autenticarCall = service.autenticarEmpleado(loginRequest);

        autenticarCall.enqueue(callBack);
    }

    public void validarEmpleadoSMS(LoginRequest loginRequest, Callback<LoginResponse> callBack){
        Call<LoginResponse> autenticarCall = service.validarEmpleadoSMS(loginRequest);

        autenticarCall.enqueue(callBack);
    }

    public void autenticarEmpleado2(LoginRequest loginRequest, Callback<LoginResponse> callBack){
        Call<LoginResponse> autenticarCall = service.autenticarEmpleado2(loginRequest.Usuario, loginRequest.Password);

        autenticarCall.enqueue(callBack);
    }

    public void validarEmpleadoSMS2(LoginRequest loginRequest, Callback<LoginResponse> callBack){
        Call<LoginResponse> autenticarCall = service.validarEmpleadoSMS2(loginRequest.Usuario, loginRequest.CodigoSMS);

        autenticarCall.enqueue(callBack);
    }

    public static String echoPing(){
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
