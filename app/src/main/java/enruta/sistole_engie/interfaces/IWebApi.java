package enruta.sistole_engie.interfaces;

import enruta.sistole_engie.entities.ArchivosLectRequest;
import enruta.sistole_engie.entities.ArchivosLectResponse;
import enruta.sistole_engie.entities.BuscarMedidorRequest;
import enruta.sistole_engie.entities.BuscarMedidorResponse;
import enruta.sistole_engie.entities.OperacionGenericaRequest;
import enruta.sistole_engie.entities.OperacionGenericaResponse;
import enruta.sistole_engie.entities.OperacionRequest;
import enruta.sistole_engie.entities.OperacionResponse;
import enruta.sistole_engie.entities.LoginRequestEntity;
import enruta.sistole_engie.entities.LoginResponseEntity;
import enruta.sistole_engie.entities.SupervisorLogRequest;
import enruta.sistole_engie.entities.SupervisorLogResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IWebApi {
    @GET("api/login/echoping")
    Call<String>echoping();

    @POST("api/loginv2/autenticarEmpleado")
    Call<LoginResponseEntity>autenticarEmpleado(@Body LoginRequestEntity loginRequestEntity);

    @POST("api/loginv2/validarEmpleadoSMS")
    Call<LoginResponseEntity>validarEmpleadoSMS(@Body LoginRequestEntity loginRequestEntity);

    @GET("autenticarEmpleado.aspx")
    Call<LoginResponseEntity>autenticarEmpleado2(@Query("usuario") String usuario, @Query("password") String password);

    @GET("validarEmpleadoSMS.aspx")
    Call<LoginResponseEntity>validarEmpleadoSMS2(@Query("usuario") String usuario, @Query("codigosms") String codigoSMS);

    @POST("api/operaciones/CheckIn")
    Call<OperacionResponse>checkIn(@Body OperacionRequest request);

    @POST("api/operaciones/CheckSeguridad")
    Call<OperacionResponse>checkSeguridad(@Body OperacionRequest request);

    @POST("api/operaciones/CheckOut")
    Call<OperacionResponse>checkOut(@Body OperacionRequest request);

    @POST("api/operaciones/CerrarArchivo")
    Call<OperacionResponse>cerrarArchivo(@Body OperacionRequest request);

    @POST("api/operaciones/SolicitarAyuda2")
    Call<OperacionResponse>solicitarAyuda(@Body OperacionRequest request);

//    @POST("api/operaciones/marcarArchivoDescargado")
//    Call<OperacionResponse>marcarArchivoDescargado(@Body OperacionRequest request);

    @POST("api/operaciones/marcarArchivoDescargado")
    Call<ArchivosLectResponse>marcarArchivoDescargado(@Body ArchivosLectRequest request);

    @POST("api/operaciones/descargarArchivo")
    Call<ArchivosLectResponse>descargarArchivo(@Body ArchivosLectRequest request);

    @POST("api/operaciones/marcarArchivoTerminado")
    Call<ArchivosLectResponse>marcarArchivoTerminado(@Body ArchivosLectRequest request);

    @POST("api/loginv2/verificarConexion")
    Call<LoginResponseEntity>verificarConexion(@Body LoginRequestEntity request);

    @POST("api/supervisor/RegistrarLog")
    Call<SupervisorLogResponse>registrarLogSupervisor(@Body SupervisorLogRequest request);

    @POST("api/operaciones/BuscarMedidor")
    Call<BuscarMedidorResponse> buscarMedidor(@Body BuscarMedidorRequest request);

    @POST("api/operaciones/OperacionGenerica")
    Call<OperacionGenericaResponse> operacionGenerica(@Body OperacionGenericaRequest request);
}
