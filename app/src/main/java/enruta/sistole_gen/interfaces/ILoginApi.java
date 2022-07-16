package enruta.sistole_gen.interfaces;

import java.util.List;

import enruta.sistole_gen.entities.LoginRequest;
import enruta.sistole_gen.entities.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ILoginApi  {
    @GET("api/login/echoping")
    Call<String>echoping();

    @POST("api/login/autenticarEmpleado")
    Call<LoginResponse>autenticarEmpleado(@Body LoginRequest loginRequest);

    @POST("api/login/validarEmpleadoSMS")
    Call<LoginResponse>validarEmpleadoSMS(@Body LoginRequest loginRequest);

    @GET("autenticarEmpleado.aspx")
    Call<LoginResponse>autenticarEmpleado2(@Query("usuario") String usuario, @Query("password") String password);

    @GET("validarEmpleadoSMS.aspx")
    Call<LoginResponse>validarEmpleadoSMS2(@Query("usuario") String usuario, @Query("codigosms") String codigoSMS);
}
