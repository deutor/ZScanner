package eu.ldaldx.mobile.zscanner;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RestClient {
    private static Retrofit retrofit = null;
    private static RestApi restApi = null;

    public static Retrofit getClient(String server, String port) {

        if(retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + server + ":" + port)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .client(client)
                    .build();

            restApi = retrofit.create(RestApi.class);

        }

        return retrofit;
    }

    public static Retrofit getClient() {
        return retrofit;
    }

    public static RestApi getApi() {
        return restApi;
    }

}
