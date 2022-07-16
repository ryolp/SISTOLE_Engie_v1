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

import enruta.sistole_gen.interfaces.ILoginApi;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebApiService {
    private static Retrofit mRetrofit;

    public static ILoginApi Create() {
        String apiURL;

        try {
            //apiURL = "http://10.0.2.2:5000/";
            //apiURL = "http://192.168.2.103/sistolewebapi/";
            //apiURL = "http://192.168.2.103/sistoleweb_dev1/";
            apiURL = "http://192.168.2.103:8080/";

            if (!apiURL.endsWith("/"))
                apiURL = apiURL + "/";

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(apiURL)        // https://10.0.2.2:5001/
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return mRetrofit.create(ILoginApi.class);
        } catch (Exception e) {
            return null;
        }
    }
}
