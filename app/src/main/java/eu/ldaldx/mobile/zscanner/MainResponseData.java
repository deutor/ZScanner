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
      this.screenPixelDensity = screenPixelDensity;
  }

  public static class Data implements Serializable {
    @Json(name = "sequence")
    protected int sequence;
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

    public void setColumn1(String column1) {
      this.column1 = column1;
    }

    public String getOnGo() {
      return this.ongo;
    }

    public void setOngo(String ongo) {
      this.ongo = ongo;
    }

    public String getColumn4() {
      return this.column4;
    }

    public void setColumn4(String column4) {
      this.column4 = column4;
    }

    public String getColumn3() {
      return this.column3;
    }

    public void setColumn3(String column3) {
      this.column3 = column3;
    }

    public String getColumn2() {
      return this.column2;
    }

    public void setColumn2(String column2) {
      this.column2 = column2;
    }

    public String getOnvalueChanged() {
      return this.onvalueChanged;
    }

    public void setOnvalueChanged(String onvalueChanged) {
      this.onvalueChanged = onvalueChanged;
    }
  }

  public static class Browser implements Serializable {
    @Json(name = "sequence")
    protected Integer sequence;
    @Json(name = "width")
    private Integer width;
    @Json(name = "label")
    private String label;
    @Json(name = "align")
    private String align;

    public Integer getSequence() {
      return this.sequence;
    }

    public void setSequence(Integer sequence) {
      this.sequence = sequence;
    }

    public Integer getWidth() {
      return this.width;
    }

    public void setWidth(Integer width) {
      this.width = width;
    }

    public String getLabel() {
      return this.label;
    }

    public void setLabel(String label) {
      this.label = label;
    }

    public String getAlign() {
      return this.align;
    }

    public void setAlign(String align) {
      this.align = align;
    }
  }

  public static class Action implements Serializable {
    @Json(name = "sequence")
    protected Integer sequence;


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

    public Integer getSequence() {
      return this.sequence;
    }

    public void setSequence(Integer sequence) {
      this.sequence = sequence;
    }

    public String getSubtype() {
      return this.subtype;
    }

    public void setSubtype(String subtype) {
      this.subtype = subtype;
    }

    public String getType() {
      return this.type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public int getColDP() {
      return convP2DP(col);
    }

    public void setCol(int col) {
      this.col = col;
    }

    public int getRowDP() {
      return convP2DP(row);
    }

    public void setRow(int row) {
      this.row = row;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }


    public int getWidthDP() {
      return convP2DP(width);
    }

    public void setWidth(int width) {
      this.width = width;
    }

    public int getHeightDP() {
      return convP2DP(height);
    }

    public void setHeight(int height) {
      this.height = height;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getColor() {
      return color;
    }

    public void setColor(String color) {
      this.color = color;
    }

    public Boolean getBold() {
      return bold;
    }

    public void setBold(Boolean bold) {
      this.bold = bold;
    }

    public String getOnGo() {
      return onGo;
    }

    public void setOnGo(String onGo) {
      this.onGo = onGo;
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

    public void setMandatory(Boolean mandatory) {
      this.mandatory = mandatory;
    }
  }

  public static class Menu implements Serializable {
    protected Integer sequence;

    private String action;

    private String label;

    public Integer getSequence() {
      return this.sequence;
    }

    public void setSequence(Integer sequence) {
      this.sequence = sequence;
    }

    public String getAction() {
      return this.action;
    }

    public void setAction(String action) {
      this.action = action;
    }

    public String getLabel() {
      return this.label;
    }

    public void setLabel(String label) {
      this.label = label;
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
