package eu.ldaldx.mobile.zscanner;

import com.squareup.moshi.Json;
import java.io.Serializable;

public class LoginResponseData implements Serializable{
    @Json(name = "valid")
    private Boolean valid;

    private String serverType;

    @Json(name = "sessionID")
    private String sessionID;

    @Json(name = "userID")
    private String userID;

    @Json(name = "message")
    private String message;


    public Boolean getValid() {
        return this.valid;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public String getUserID() {
        return this.userID;
    }

    public String getMessage() {
        return this.message;
    }
}
