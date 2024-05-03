package eu.ldaldx.mobile.zscanner;

import android.annotation.SuppressLint;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RestClient {
    private static Retrofit retrofit = null;
    private static RestApi restApi = null;

    // Create a custom TrustManager that accepts all certificates - this will accept any self-signed certificate
    // however this app is designed to be used in trusted LAN environments
    // we accept self-signed cert from app server (zserver4mc) - if you need secure connection you need issue valid cert to zeserver4mc and pin it to this app
    //@SuppressLint("CustomX509TrustManager") - not suppressing that warning, so you have chance to see security warning
    TrustManager trustAllCerts = new X509TrustManager() {
        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[] {};
        }
    };


    public Retrofit getClient(String server, String port) {

        if(retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            /**/
            SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[] {trustAllCerts}, new java.security.SecureRandom());

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .hostnameVerifier(new HostnameVerifier() {
                            // we already accept self-signed cert from app server (zserver4mc) - if you need secure connection you need issue valid cert to zeserver4mc and pin it to this app
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        })
                        .build();



                retrofit = new Retrofit.Builder()
                        .baseUrl("https://" + server + ":" + port)
                        .addConverterFactory(MoshiConverterFactory.create())
                        .client(client)
                        .build();

                restApi = retrofit.create(RestApi.class);

            } catch(Exception ex) {
                throw new RuntimeException(ex.toString());
            }


        }

        return retrofit;
    }

    public static RestApi getApi() {
        return restApi;
    }

}
