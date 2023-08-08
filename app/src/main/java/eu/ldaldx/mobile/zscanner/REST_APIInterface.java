package eu.ldaldx.mobile.zscanner;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface REST_APIInterface {
    @POST("/api/v1/login")
    Call<LoginResponseData> doLogin(@Body LoginRequestData loginRequestData);

//    @GET("/api/v1/login")
//    Call<LoginData> doGetLogin(@Body LoginData loginData);

}



