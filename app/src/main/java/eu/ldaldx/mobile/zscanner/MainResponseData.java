package eu.ldaldx.mobile.zscanner;

import com.squareup.moshi.Json;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;
import java.util.Comparator;
import java.util.List;

public class MainResponseData implements Serializable {

  static private float screenPixelDensity = 1.5f;

  static private int convP2DP(int pixels) {
    float dpValue = pixels * screenPixelDensity;
    return Math.round(dpValue);
  }


  private List<Data> data;

  private List<Browser> browser;

  private List<Action> action;

  private List<Menu> menu;



  public List<Data> getData() {
    return this.data;
  }

  public void setData(List<Data> data) {
    this.data = data;
  }

  public List<Browser> getBrowser() {
    return this.browser;
  }

  public void setBrowser(List<Browser> browser) {
    this.browser = browser;
  }

  public List<Action> getAction() {
    return this.action;
  }

  public void setAction(List<Action> action) {
    this.action = action;
  }

  public List<Menu> getMenu() {
    return this.menu;
  }

  public void setMenu(List<Menu> menu) {
    this.menu = menu;
  }

  public void setPixelDensity(float screenPixelDensity) {
      MainResponseData.screenPixelDensity = screenPixelDensity;
  }

  public static class Data implements Serializable {
    @Json(name = "sequence")
    private int sequence;
    @Json(name = "column1")
    private String column1;
    @Json(name = "column2")
    private String column2;
    @Json(name = "column3")
    private String column3;
    @Json(name = "column4")
    private String column4;

    @Json(name = "onGo")
    private String ongo;

    @Json(name = "onValue")
    private String onvalueChanged;

    public String getColumn1() {
      return this.column1;
    }

    public String getOnGo() {
      return this.ongo;
    }

    public String getColumn4() {
      return this.column4;
    }

    public String getColumn3() {
      return this.column3;
    }

    public String getColumn2() {
      return this.column2;
    }


    public String getOnvalueChanged() {
      return this.onvalueChanged;
    }
  }

  public static class Browser implements Serializable {
    @Json(name = "sequence")
    private Integer sequence;
    @Json(name = "width")
    private Integer width;
    @Json(name = "label")
    private String label;
    @Json(name = "align")
    private String align;

    public Integer getSequence() {
      return this.sequence;
    }


    public Integer getWidth() {
      return this.width;
    }

    public String getLabel() {
      return this.label;
    }


    public String getAlign() {
      return this.align;
    }

  }

  public static class Action implements Serializable {
    @Json(name = "sequence")
    private Integer sequence;


    @Json(name = "type")
    private String type;
    @Json(name = "subtype")
    private String subtype;

    @Json(name = "name")
    private String name;


    @Json(name = "text")
    private String text;


    @Json(name = "col")
    private int col;

    @Json(name = "row")
    private int row;

    @Json(name = "width")
    private int width;

    @Json(name = "height")
    private int height;

    @Json(name = "color")
    private String color;

    @Json(name = "bold")
    private Boolean bold;

    @Json(name = "ongo")
    private String onGo;

    @Json(name = "ongs1")
    private String onGS1;

    @Json(name = "mand")
    private Boolean mandatory;


    @Json(name="backaction")
    private String backAction;

    @Json(name = "align")
    private String align;


    @Json(name = "reported")
    private boolean isReported;

    public String getBackAction() {
      return backAction;
    }

    public Integer getSequence() {
      return this.sequence;
    }

    public String getAlign() {
      return this.align;
    }



    public String getSubtype() {
      return this.subtype;
    }


    public String getType() {
      return this.type;
    }



    public int getColDP() {
      return convP2DP(col);
    }


    public int getRowDP() {
      return convP2DP(row);
    }

    public String getText() {
      return text;
    }

    public int getWidthDP() {
      return convP2DP(width);
    }

    public int getHeightDP() {
      return convP2DP(height);
    }


    public String getName() {
      return name;
    }


    public String getColor() {
      return color;
    }


    public Boolean getBold() {
      return bold;
    }


    public String getOnGo() {
      return onGo;
    }

    public boolean isReported() {
      return isReported;
    }

    public String getOnGS1() {
      return onGS1;
    }

    public void setOnGS1(String onGS1) {
      this.onGS1 = onGS1;
    }

    public Boolean getMandatory() {
      return mandatory;
    }

  }

  public static class Menu implements Serializable {
    @Json(name="sequence")
    private Integer sequence;

    @Json(name="action")
    private String action;

    @Json(name="action_args")
    private String actionArgs;

    @Json(name="label")
    private String label;


    public Integer getSequence() {
      return this.sequence;
    }

    public String getAction() {
      return this.action;
    }

    public String getActionArgs() {
      return actionArgs;
    }

    public String getLabel() {
      return this.label;
    }
  }

  static class ActionComparator implements Comparator<Action> {
      @Override
      public int compare(Action a, Action b) {
          return a.sequence - b.sequence;
      }
  }

  static class BrowserComparator implements Comparator<Browser> {
      @Override
      public int compare(Browser a, Browser b) {
          return a.sequence - b.sequence;
      }
  }

  static class DataComparator implements Comparator<Data> {
      @Override
      public int compare(Data a, Data b) {
          return a.sequence - b.sequence;
      }
  }

  static class MenuComparator implements Comparator<Menu> {
      @Override
      public int compare(Menu a, Menu b) {
          return a.sequence - b.sequence;
      }
  }
}
