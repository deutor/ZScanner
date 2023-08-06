package eu.ldaldx.mobile.zscanner;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class APIClient2 {
    private static Retrofit retrofit = null;
    static APIInterface api = null;

    static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.1:44044")
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
