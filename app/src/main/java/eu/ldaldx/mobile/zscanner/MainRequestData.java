package eu.ldaldx.mobile.zscanner;

import com.squareup.moshi.Json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainRequestData implements Serializable {


  @Json(name = "request")
  private String request;

  @Json(name="actionarg")
  private String actionArgs;

  @Json(name="userID")
  private String userID;

  @Json(name = "version")
  private Integer version;

  @Json(name = "sessionID")
  private String sessionID;

  private List<MainRequestData.Data> data = new ArrayList<>();

  public void setDataFromLov(HashMap<String, String> lov) {
      if(lov == null) return;
      for(String key : lov.keySet()) {
          data.add(new Data(key, lov.get(key)));
      }
  }

  public void setActionArgs(String actionArgs) {
        this.actionArgs = actionArgs;
    }


  public void setRequest(String reqAction) {
    this.request = reqAction;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }


  public void setSessionID(String sessionID) {
    this.sessionID = sessionID;
  }
  public void setUserID(String userID) {
        this.userID = userID;
    }

  public static class Data implements Serializable {
      @Json(name = "name")
      private String name;
      @Json(name = "value")
      private String value;

      public Data(String name, String value) {
          this.name = name;
          this.value = value;
      }
  }


}
