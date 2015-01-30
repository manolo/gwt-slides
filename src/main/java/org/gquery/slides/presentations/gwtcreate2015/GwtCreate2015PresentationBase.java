package org.gquery.slides.presentations.gwtcreate2015;

import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.$$;
import static com.google.gwt.query.client.GQuery.document;
import static com.google.gwt.query.client.GQuery.window;
import static com.google.gwt.query.client.plugins.effects.Transitions.Transitions;

import java.util.List;

import org.gquery.slides.client.SlidesSource;
import org.gquery.slides.client.SlidesUtils;

import com.google.gwt.core.client.Duration;
import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Console;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.builders.JsonBuilder;
import com.google.gwt.query.client.plugins.Plugin;
import com.google.gwt.query.client.plugins.effects.Animations;
import com.google.gwt.query.client.plugins.effects.Transitions;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 */
public abstract class GwtCreate2015PresentationBase extends SlidesSource {

  final static Console console = new SlidesUtils.Console();

  final String uploadUrl = "http://gwtupload.alcala.org/gupld/servlet.gupld?nodelay&" + Duration.currentTimeMillis();

  GQuery viewPort = $("#viewport");
  GQuery play = $("#play");
  Widget resizeWidget;

  static GQuery dollar(Object o) {
    return GQuery.$(o);
  }

  public void enterWhatIsIt() {
    $(".gquery-logo").animate($$("scale: 4, x: 50px, y: -18px, rotateY: 360deg"), 1500);
  }
  public void leaveWhatIsIt() {
    $(".gquery-logo").animate($$("scale: 1, x: 0px, y: 0px, rotateY: 0deg"), 1000);
  }
  public void enterMuchMore() {
    $(".gwt-logo").animate($$("scale: 3, x: 120px, y: -18px, rotateX: 360deg"), 2000);
  }
  public void leaveMuchMore() {
    $(".gwt-logo").animate($$("scale: 1, x: 0px, y: 0px, rotateX: 0deg"), 1000);
  }

  public void enterCompareCode() {
    $("#comparecode > div").appendTo($("#comparecode"));
  }

  public void leaveDom() {
    $(".current p").remove();
  }

  Transitions balls = $(".ball").as(Transitions);
  Transitions red = $(".red").as(Transitions), blue = $(".blue").as(Transitions), yellow = $(".yellow").as(Transitions), green = $(".green").as(Transitions);

  void drawBalls() {
    if (!balls.isVisible() || balls.cur("left", true) > 350 ) {
      balls.stop().show().each(new Function() {
        int h = $(window).height() - 100;
        public void f(Element e) {
          $(e).css("bottom", (h - Random.nextInt(100)) + "px").css("left", Random.nextInt(140) + "px");
        }
      });
    }
    balls.css("transform", "");
  }

  public void enterPromisesPipeline() {
    drawBalls();
  }
  public void beforePromisesPipeline() {
    drawBalls();
  }
  public void leavePromisesPipeline() {
    balls.hide();
  }

  public void enterCreatePlugin() {
    drawBalls();
  }
  public void beforeCreatePlugin() {
    drawBalls();
  }
  public void leaveCreatePlugin() {
    balls.hide();
  }

  public void enterLambdas() {
    drawBalls();
  }
  public void beforeLambdas() {
    drawBalls();
  }
  public void leaveLambdas() {
    balls.hide();
  }


  public void leaveEnhanceWidgets() {
    $($("*").widgets(Label.class)).remove();
  }

  class MyBreadCrumb extends Label {
    public MyBreadCrumb addCrumb(String s) {
      setText(getText() + " > " + s);
      return this;
    };
  };

  private MyBreadCrumb w = new MyBreadCrumb().addCrumb("Gwt").addCrumb("GQuery");

  public void enterFindWidgets() {
    RootPanel.get().add(w);
    $(w).css($$("position: absolute, color: cornsilk"));
  }

  public void leaveFindWidgets() {
    w.removeFromParent();
  }

  public interface Person extends JsonBuilder {
    Person setName(String s);
    String getName();
    Person setAge(int i);
    int getAge();
    List<Person> getChildren();
    Person getPartner();
    Person setPartner(Person p);
  }

  public interface User extends JsonBuilder {
    public interface Address extends JsonBuilder {
      String street();
      Address street(String s);
    }
    User setName(String s);
    String getName();
    User setAdress(Address a);
    Address getAddress();
  }

  public interface ElementWrapper extends JsonBuilder {
    String getTagName();
    Element parentElement();
    Function querySelector();
  }
  public interface WindowWrapper extends JsonBuilder {
    Function eval();
  }

  public static class Css3Animations extends GQuery {
    // We just need a constructor, and a reference to the new registered plugin
    protected Css3Animations(GQuery gq) {
      super(gq);
    }
    //.registerPlugin //\n\s .registerPlugin
    public static final Class<Css3Animations> Css3Animations = GQuery.registerPlugin(Css3Animations.class, new Plugin<Css3Animations>() {
          public Css3Animations init(GQuery gq) {
            return new Css3Animations(gq);
          }
        });

    // We can add new methods or override existing ones
    public Css3Animations css3Animate(Properties properties) {
      super.animate(properties);
      return this;
    }
  }

  /************** Upload *********************/
  GQuery fileUpload;
//  String uploadUrl = "http://server.cors-api.appspot.com/server?id=123&enable=true&status=200&credentials=true";
  GQuery uploadImg = $("<img class='right bottom'>").css($$("width: 20%, margin: 20px"));
  public void beforeUpload() {
    uploadImg.appendTo(document).attr("src", "");
  }


  public void leaveUpload() {
    uploadImg.remove();
    fileUpload.remove();
  }

  /************** MaterialDesign *********************/
  String boxStyle = Animations.insertStyle($$("scaleX: 1, scaleY: 1, width: 100px, height: 40px, margin: 5px, float: left, position: relative, background: #0F678E"));
  Transitions boxes;
  public void enterMaterialDesign() {
    int l = $(".current").width() / 110 * 3;
    for (int i = 0; i < l; i++) {
      $("<div/>").addClass(boxStyle).appendTo(document);
    }
    boxes = $("." + boxStyle).as(Transitions).css("scale", "1");
  }
  public void leaveMaterialDesign() {
    boxes.remove();
    boxes = null;
  }

  GQuery rippleButtons;
  public void enterMaterialDesignRipple() {
    GQuery slides = $(".slides");
    if (rippleButtons == null) {
      for (String c : new String[]{"red", "blue", "pink", "green" , "grey"}) {
        $("<div class='button'>").css($$("width: 20%, height: 40px; border: none, margin: 10px, overflow: hidden, position: relative, border: solid 1px #BEB084, background-color:" + c)).appendTo(slides);
      }
      rippleButtons = $(".button");
      $(".current .jCode, .current h1").css($$("position:relative, overflow: hidden"));
      slideMaterialDesignRipple();
    } else {
      rippleButtons.appendTo(slides);
    }
    $("#play").hide();
  }
  public abstract void slideMaterialDesignRipple();

  public void leaveMaterialDesignRipple() {
    $(".button").detach();
    $("#play").show();
  }

  public interface PaperSlider extends JsonBuilder {
    // get/set prefixes are optional
    int getValue();
    // Chaining setters is optional
    PaperSlider setValue(int value);
    PaperSlider min(int value);
    PaperSlider max(int value);
    PaperSlider step(int value);
    PaperSlider snaps(boolean value);
    PaperSlider pin(boolean value);
  }

  public void leaveWebComponents() {
    $("paper-slider").remove();
  }
  public void leaveWebComponentsBinding() {
    $("paper-slider").remove();
  }


  /************** QuestionsAnswers *********************/
  public void enterQuestionsAnswers() {
    $("#play").hide();
  }
}
