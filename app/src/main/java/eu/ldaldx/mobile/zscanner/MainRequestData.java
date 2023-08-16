package eu.ldaldx.mobile.zscanner;

import com.squareup.moshi.Json;

import java.io.Serializable;


public class MainRequestData implements Serializable {

  @Json(name = "action")
  private String action;

  @Json(name = "version")
  private Integer version;

  @Json(name = "sessionID")
  private String sessionID;


  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Integer getVersion() {
    return this.version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }


  public String getSessionID() {
    return sessionID;
  }

  public void setSessionID(String sessionID) {
    this.sessionID = sessionID;
  }

}
