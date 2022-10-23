package enruta.sistole_engie.services;

import enruta.sistole_engie.interfaces.IWebApi;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebApiService {
    private static Retrofit _retrofit;
    private static Retrofit.Builder _builder;
    private static HttpLoggingInterceptor _loggingInterceptor;
    private static OkHttpClient.Builder _httpClient;

    public static IWebApi Create(){
        String apiURL;

        //apiURL = "http://10.0.2.2:5000/";
        // https://10.0.2.2:5001/
        //apiURL = "http://192.168.2.103/sistolewebapi/";
        //apiURL = "http://192.168.2.103/sistoleweb_dev1/";
        //apiURL = "http://192.168.2.103:8080/";
        apiURL = "https://engie.sistoleweb.com/";

        return Create(apiURL);
    }

    public static IWebApi Create(String apiURL) {
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

            return _retrofit.create(IWebApi.class);
        } catch (Exception e) {
            return null;
        }
    }
}
