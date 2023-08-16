package eu.ldaldx.mobile.zscanner;

import com.squareup.moshi.Json;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;


public class LoginRequestData implements Serializable {
  @Json(name = "password")
  private String password;

  @Json(name = "userToken")
  private String userToken;
  private String serverType;

  @Json(name = "scanner")
  private String scanner;

  @Json(name = "action")
  private String action;

  @Json(name = "version")
  private Integer version;

  @Json(name = "user")
  private String user;

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getServerType() {
    return this.serverType;
  }

  public void setServerType(String serverType) {
    this.serverType = serverType;
  }

  public String getScanner() {
    return this.scanner;
  }

  public void setScanner(String scanner) {
    this.scanner = scanner;
  }

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

  public String getUser() {
    return this.user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getUserToken() {
    return userToken;
  }

  public void setUserToken(String userToken) {
    this.userToken = userToken;
  }

}
