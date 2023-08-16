package eu.ldaldx.mobile.zscanner;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestApi {
    @POST("/api/v1/login")
    Call<LoginResponseData> doLogin(@Body LoginRequestData loginRequestData);

    @POST("/api/v1/action")
    Call<MainResponseData> doAction(@Body MainRequestData mainRequestData);
}



