package eu.ldaldx.mobile.zscanner;

import com.squareup.moshi.Json;
import java.io.Serializable;

public class LoginResponseData implements Serializable{
    @Json(name = "valid")
    private Boolean valid;

    private String serverType;

    @Json(name = "sessionID")
    private String sessionID;

    @Json(name = "message")
    private String message;


    public Boolean getValid() {
        return this.valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }



    public String getSessionID() {
        return this.sessionID;
    }

    public void setSessionID(String scanner) {
        this.sessionID = sessionID;
    }


    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }








}
