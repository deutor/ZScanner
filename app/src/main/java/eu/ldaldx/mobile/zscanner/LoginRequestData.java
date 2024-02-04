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

  @Json(name = "userName")
  private String userName;


  public void setPassword(String password) {
    this.password = password;
  }

  public void setServerType(String serverType) {
    this.serverType = serverType;
  }

  public void setScanner(String scanner) {
    this.scanner = scanner;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setUserToken(String userToken) {
    this.userToken = userToken;
  }

}
