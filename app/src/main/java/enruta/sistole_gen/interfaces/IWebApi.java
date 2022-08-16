package enruta.sistole_gen.interfaces;

import enruta.sistole_gen.entities.LoginRequestEntity;
import enruta.sistole_gen.entities.LoginResponseEntity;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IWebApi {
    @GET("api/login/echoping")
    Call<String>echoping();

    @POST("api/login/autenticarEmpleado")
    Call<LoginResponseEntity>autenticarEmpleado(@Body LoginRequestEntity loginRequestEntity);

    @POST("api/login/validarEmpleadoSMS")
    Call<LoginResponseEntity>validarEmpleadoSMS(@Body LoginRequestEntity loginRequestEntity);

    @GET("autenticarEmpleado.aspx")
    Call<LoginResponseEntity>autenticarEmpleado2(@Query("usuario") String usuario, @Query("password") String password);

    @GET("validarEmpleadoSMS.aspx")
    Call<LoginResponseEntity>validarEmpleadoSMS2(@Query("usuario") String usuario, @Query("codigosms") String codigoSMS);
}
