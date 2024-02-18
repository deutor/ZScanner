package eu.ldaldx.mobile.zscanner;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RestClient {
    private static Retrofit retrofit = null;
    private static RestApi restApi = null;

    // Create a custom TrustManager that accepts all certificates
    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
    };


    public Retrofit getClient(String server, String port) {

        if(retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            /*
            SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch(Exception ex) {

            }

             */


            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
            //        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();




            retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + server + ":" + port)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .client(client)
                    .build();

            restApi = retrofit.create(RestApi.class);
        }

        return retrofit;
    }

    public static RestApi getApi() {
        return restApi;
    }

}
