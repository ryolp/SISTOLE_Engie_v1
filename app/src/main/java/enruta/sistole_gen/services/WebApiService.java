package enruta.sistole_gen.services;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import enruta.sistole_gen.TransmitionObject;
import enruta.sistole_gen.interfaces.ILoginApi;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebApiService {
    private static Retrofit _retrofit;
    private static Retrofit.Builder _builder;
    private static HttpLoggingInterceptor _loggingInterceptor;
    private static OkHttpClient.Builder _httpClient;

    public static ILoginApi Create(){
        String apiURL;

        //apiURL = "http://10.0.2.2:5000/";
        // https://10.0.2.2:5001/
        //apiURL = "http://192.168.2.103/sistolewebapi/";
        //apiURL = "http://192.168.2.103/sistoleweb_dev1/";
        //apiURL = "http://192.168.2.103:8080/";
        apiURL = "http://engie.sistoleweb.com/";

        return Create(apiURL);
    }

    public static ILoginApi Create(String apiURL) {
        try {
            if (!apiURL.endsWith("/"))
                apiURL = apiURL + "/";

            _builder = new Retrofit.Builder()
                    .baseUrl(apiURL)
                    .addConverterFactory(GsonConverterFactory.create());

            _retrofit = _builder.build();

            _loggingInterceptor =
                    new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BASIC);

            _httpClient = new OkHttpClient.Builder();

            if (!_httpClient.interceptors().contains(_loggingInterceptor)) {
                _httpClient.addInterceptor(_loggingInterceptor);
                _builder.client(_httpClient.build());
            }

            return _retrofit.create(ILoginApi.class);
        } catch (Exception e) {
            return null;
        }
    }
}
