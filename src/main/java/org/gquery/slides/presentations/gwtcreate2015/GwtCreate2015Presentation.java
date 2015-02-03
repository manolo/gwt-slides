package org.gquery.slides.presentations.gwtcreate2015;

import static com.google.gwt.query.client.$.when;
import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.$$;
import static com.google.gwt.query.client.GQuery.Widgets;
import static com.google.gwt.query.client.GQuery.browser;
import static com.google.gwt.query.client.GQuery.document;
import static com.google.gwt.query.client.GQuery.window;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.query.client.$;
import com.google.gwt.query.client.Console;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQ;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.GqFunctions.IsElementFunction;
import com.google.gwt.query.client.GqFunctions.IsEventFunction;
import com.google.gwt.query.client.Promise.Deferred;
import com.google.gwt.query.client.Promise;
import com.google.gwt.query.client.impl.ConsoleBrowser;
import com.google.gwt.query.client.js.JsUtils;
import com.google.gwt.query.client.plugins.ajax.Ajax;
import com.google.gwt.query.client.plugins.ajax.Ajax.Settings;
import com.google.gwt.query.client.plugins.deferred.PromiseFunction;
import com.google.gwt.query.client.plugins.effects.Transitions;
import com.google.gwt.query.client.plugins.gestures.Gesture;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * All slideXxxx methods in this class will be merged in the main html
 * page. Java doc of the methods will be written as title, subtitle
 * and sections in the slides. The body of the the function will be
 * included in the code section of the slide.
 *
 * There are some conventions in the body, like preceding a
 * line with '@ ' which means a '<h1>', '@@ ' a <h4>, or '- ' which will
 * be replaced with <ul> sections.
 *
 * You can include any method or inner class in the slide just
 * writing '@include: methodName' or '@include: className' anywhere
 * (javadoc, code). Also extra html is allowed in javadocs.
 *
 * The body code will be executed when clicking on the '#play' button,
 * but additionally, you can write extra functions which will be executed
 * when entering the slide, leaving it, before running the code or after.
 *
 * So the convention for method names are:
 * enterMethod, beforeMethod, slideMethod, afterMetod, leaveMethod.
 *
 * In your hosted html page you can define each slide, and any
 * extra content in the slide apart from the content automatically merged
 * from this class. Each section should have an unique 'id' matching the
 * name of the slideMethodName, but in lower-case. The order of
 * these sections will be the order of the slides despite the order of
 * test methods in this class.
 *
 * <section id='slidemehodname'>Extra Content</section>
 *
 * @author manolo
 *
 */
public class GwtCreate2015Presentation extends GwtCreate2015PresentationBase {

  /**
   * @ Speed up your GWT coding with gQuery
   * @@ Manuel Carrasco Moñino
   * GWT.create 2015
   *
   *
   * class='right bottom semi none'- To move slides use either:
   * -- left/right/space/back keys
   * -- tap-one/two touches
   * -- swipe-right/left gestures
   * - To run examples:
   * -- click/tap on the play button
   */
  public void slideTitle() {
  }

  /**
   * @ About me...
   * class='right none'- Open Source advocate
   * -- Vaadin R&D
   * -- GWT Maintainer
   * -- Apache James PMC
   * -- Jenkins Committer
   * - &nbsp;
   * -- <i class="twitter"></i><a href="https://twitter.com/dodotis" target="_blank">@dodotis</a>
   * -- <i class="gplus"></i><a href="http://google.com/+ManuelCarrascoMonino/posts" target="_blank">+ManuelCarrascoMoñino</a>
   *
   * <img src="img/manolo-business-card.png" style='width:400px;transform:rotateZ(-2deg)'><br/>
   */
  public void slideAbout() {
  }

  /**
   * @ What is gQuery?
   * @@ Write less. Do more!
   * - An entire GWT rewrite of the famous jQuery javascript library.
   * - Use the jQuery API in GWT applications without including the jQuery, while leveraging the optimizations and type safety provided by GWT.
   */
  public void slideWhatIsIt() {
  }

  /**
   * @ It looks like jQuery
   * - The API and syntax of GQuery is almost identical to jQuery.
   * - Existing jQuery code can be easily adapted into GQuery and used in your GWT applications.
   */
  public void slideComparecode() {}

  /**
   * @ But it's much more
   * - It integrates gently with GWT
   * -- shares and enhances its event mechanism
   * -- knows about widgets hierarchy
   * - GWT Optimizers
   * -- Provides a simpler deferred binding mechanism
   * - Provide many utilities for GWT.
   * -- Plenty of useful methods
   */
  public void slideMuchMore() {}

  /**
   * @ Current gQuery status
   * - Very active development
   * - 3000 downloads per month
   * - In the top 5 of the most popular GWT libraries
   * - Stable versions since 2010
   * - Official sponsored & supported by ArcBees
   */
  public void slideStatus() {
  }

  /**
   * @ gQuery Fundamentals
   */
  public void slideBestFundamentals(){};

  /**
   * @ Easy DOM manipulation
   * - GQuery eases traversal and manipulation of the DOM.
   * -- Use $() to wrap, find and create elements or widgets
   * - Chaining methods: select & modify several elements in just one line of code.
   * - friendly css style properties syntax.
   * -- Use $$() to set style properties or for animations
   * -- It also supports type safe CSS
   */
  public void slideDom() {
    //\.animate //\n .animate
    $(".slides > section").css("background-color", "green").animate($$("top: +80%")).animate($$("top: 0, background-color: transparent")).size();
    $("<p>Hello</p>").appendTo(".current");
  }

  /**
   * @ Full GWT Widget integration
   * - Whatever you do with elements, you can do with widgets
   * -- Query, Enhance, Manipulate, Modify
   * - Promote elements to widgets inserting them in gwt hierarchy
   * - Improve widgets events
   * -- Adding events not implemented by the widget
   */
  public void slideWidgets() {
    //
    Button b = new Button("Click me");
    b.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        console.log("Clicked on Button");
      }
    });
    // Promote any DOM element to a GWT panel
    Panel p = $("section.current .jCode-div").as(Widgets).panel().widget();
    // Use it as any other widget in GWT hierarchy
    p.add(b);
    // Enhance Widget DOM
    $("<a href='javascript:'>Click me</a>")
    .prependTo($(b)).on("click", new Function(){
      public boolean f(Event e) {
        console.log("Clicked on Link");
        return false;
      }
    });
  }
  /**
   * @ Event Handling
   * - Handle and fire any native event.
   * - Create your own events.
   * - Support for event name spaces.
   * - Delegate events.
   * - Pass data to listeners using events
   * -- Tip: Replacement to EventBus
   */
  public void slideEvents() {
    console.log("Click on code text to see the font color.");
    $("#console").on("foo", new Function(){
      public boolean f(Event e, Object... args) {
        console.log(args[0]);
        return true;
      }
    });
    //
    $(".current .jCode").on("tap", "span", new Function(){
      public boolean f(Event e) {
        $("#console").trigger("foo", $(e).css("color"));
        return false;
      }
    });
  }

  /**
   * @ Promises
   * - GQuery implements the promises API existing in jQuery.
   * -- when, then, and, or, progress, done, fail, always
   * - Use it as an alternative to nested callback based code.
   * -- Declarative syntax.
   * - Can be used in the JVM
   */
  public void slidePromisesPipeline() {
    //(\(|, |\))(\$\(|\)\.done) //$1\n$2//Launch things at the same time with when
    GQuery.when(
        $(".ball.yellow").animate($$("bottom:0"), 1000),
        $(".ball.red").animate($$("bottom:0"), 2000)
        )
        .done(new Function(){
          public void f() {
            $(".ball").fadeOut();
          }
        });
  }

  /**
   * @disabled
   */
  public void slidePromisesPipelineAdvanced() {
    //((?:\))|(?:,|\())( \$\(|\)\.then) //$1\n$2//Anything can be simultaneously executed with when
    GQuery.when(null,
        $(".ball.yellow").animate($$("bottom:0"), 1000),
        $(".ball.red").animate($$("bottom:0"), 2000)
        )
        .then(new Function() {
          public Object f(Object... args) {
            return $(".ball.blue").animate($$("bottom: 0"), 2000);
          }
        })
        .done(new Function(){
          public void f() {
            $(".ball").fadeOut();
          }
        });
  }

  /**
   * @ Ajax
   * class='none'- GQuery provides an easy API for Ajax.
   * -- Syntax sugar
   * -- Promises
   * -- Progress
   * -- Works in JVM
   * <div id='response' class='right' style='width: 20%; height: 60px; margin: 10px; border: 1px solid; padding:20px; background: #E43333; border-radius: 4px;'></div>
   */
  public void slideBasicAjax() {
    $("#response").load("gwtcreate2015.html #hello > div");
  }

  /**
   * @ Data Binding
   * - GQuery ships an easy data binding system to and from *JSON* or *XML*
   * - Light weight implementation: each object wraps a JS object
   * - JVM compatible.
   */
  public void slideDataBinding0() {
    // @include: Person//.set //\n\s .set
    // @include: Person
    Person me = GQ.create(Person.class)
        .setAge(10)
        .setName("Manolo");
    console.log(me);
  }

  /**
   * @ Utilities
   * class=none- *GQuery ships a set of GWT utilities which makes your live easier*
   * <br/>
   * - Avoiding writing GWT JSNI
   * -- export, import
   * - Simplifying GWT Deferred binding
   * -- browser.isIe, browser.isWebkit ... flags
   * - Browser syntax logging
   * -- console.log, console.err ...
   * - Import external JS in JSNIBlocks
   * -- JsniBundle
   */
  public void slideUtilites() {
  }

  /**
   * @ All Right, but how can gQuery help in my GWT project ?
   * - Writing less code !
   * -- For certain actions you can save 60-90% of code
   * - Making your code more expressive
   * -- Chaining is more declarative.
   * -- Promises try to solve nested and complex asynchronous blocks.
   * - Using pure DOM elements instead of creating widgets for everything.
   * -- The tendency is to avoid Widgets
   * -- Web-components are here
   * -- Reusing existing code (js, html)
   */
  public void slideHow(){};

  /**
   * @ When can I use gQuery in my GWT project?
   * - 1.- Doesn't matter the final GWT architecture of your project
   * -- Plain GWT, MVP, GWTP
   * -- GXT, MGWT, Vaadin
   * - 2.- As usual separate Views from your Business logic.
   * -- In your Views: gQuery will help you to enhance & manipulate the DOM
   * -- In your Logic: you can use Ajax, Promises, and Json binding and test in the JVM
   */
  public void slideWhen(){};


  /**
   * @ I want to write less and do more...
   * @ Show me the code
   */
  public void slideWriteLess(){}

  /**
   * @ Don't extend GWT widgets to change it's behavior.
   * - Modify the widget when it attaches or detaches.
   * -- Modify its DOM
   * -- Add effects
   * -- Add events
   * -- Tip: when GWT attaches/detaches you must set events again
   * - Easy to use in UiBinder classes
   */
  public void slideEnhanceWidgets(){
    Widget widget = new Label("Hello");
    widget.addAttachHandler(new Handler() {
      public void onAttachOrDetach(AttachEvent event) {

        //
        //\)\.(append|animate|on) //)\n\s .$1
        $(event.getSource()).append("<span> GWT</span><span> Create</span>").animate($$("y: +200px, x: +50px, color: yellow "), 3000)
        .on("click", "span", new Function(){
          public void f() {
            console.log($(this).text());
          }
        });
      }
    });
    RootPanel.get().add(widget);
  }

  /**
   * @ Decouple your widgets
   * - Find any widget of a specific java class.
   * -- By default it looks for widgets attached to the RootPanel
   * -- Enclose your search using especific selectors like '.gwt-Label' or specific contexts.
   * - Then you can use those widgets as usual in your GWT application
   */
  public void slideFindWidgets(){
    // You get a list with all MyBreadCrumb matching the selector
    List<MyBreadCrumb> list = $(".gwt-Label").widgets(MyBreadCrumb.class);
    MyBreadCrumb crumbs = list.get(0);
    //
    crumbs.addCrumb("Rocks");
  }

  /**
   * @ GWT Ajax never was simpler.
   * - Just one line of code vs dozen of lines using RequestBuilder
   * - Reusable responses via Promises
   * - Advanced features
   * --  Upload/Download progress, FormData, CORS
   * - Usable in the JVM
   */
  public void slideAjax() {
    // Configurable via Properties syntax
    Ajax.get("/my-rest-service/items", $$("customer: whatever"));
    Ajax.post("/my-rest-service/save", $$("id: foo, description: bar"));
    Ajax.loadScript("/my-cdn-host/3party.js");
    Ajax.importHtml("/bower_components/3party-web-component.html");
    //\.((?:set\w+|fail)) //\n\s .$1// Or via the Settings interface
    Ajax.ajax(Ajax.createSettings()
        .setUrl("/my-3party-site/service")
        .setWithCredentials(true)
        .setData(GQ.create().set("parameter1", "value"))
        ).fail(new Function(){
          public void f() {
            console.log("The call should fail because url's are invalid");
          }
        });
  }

  /**
   * @ Make your async code more declarative and simpler
   * - Avoid nesting callbacks
   * - Chain promises methods to add callbacks
   * - Reuse resolved promises to avoid requesting twice the same service
   * -- resolved status is maintained for ever
   * - Pipe your callbacks
   */
  public void slideDeclarativePromises(){
    // We can reuse the login promise any time
    Promise loginDone = Ajax.post("/my-login-service", $$("credentials: whatever"));
    //\.(then|done) //\n\s .$1//
    GQuery.when(loginDone)
      .then(Ajax.post("/my-rest-service", $$().set("param", "value")))
      .done(new Function(){
        public Object f(Object... args) {
          return super.f(args);
        }
      }).fail(new Function() {
        public void f() {
          console.log("Login Error");
        }
      });
  }

  /**
   * @ JSNI sucks, how does gQuery help?
   * - Export java functions to JS objects
   * -- This will be covered by JsInterop
   * -- But sometimes you will be interested on simply export one method
   * - Execute external functions
   * -- Automatically boxes and un-boxes parameters and return values
   * - Wrap any JS object with *JsonBuilders*
   * -- It supports functions
   * - Load external libraries via *JsniBundle*
   */
  public void slideGWTStuff() {
    JsUtils.export(window, "foo", new Function(){
      public Object f(Object... args) {
        return args[0];
      }
    });
    //
    String response = JsUtils.jsni(window, "foo", "Bye Bye JSNI");
    console.log(response);
  }

  /**
   * @ Simplifying deferred binding
   * - gQuery Browser flags are set in compilation time.
   * -- They return true or false, making the compiler get rid of other borwsers code
   * - Not necessity of create classes nor deal with module files
   */
  public void slideDataBinding2(){
    if (browser.webkit) {
      console.log("WebKit");
    } else if (browser.ie6) {
      // This code will never be in chrome permutation
      Window.alert("IE6 does not have console");
    } else {
      // This code will never be in chrome permutation
      console.log("Not webkit nor IE. Maybe mozilla? " + browser.mozilla);
    }
  }

  /**
   * @ Extending gQuery
   */
  public void slidePlugins(){
  }

  /**
   * @ How to extend gQuery?
   * - Plugins add new methods to GQuery objects
   * - It's easy to call new methods via the *as(Plugin)* method
   * - Plugins also could modify certain behaviors of gQuery:
   * -- support for new selectors
   * -- synthetic events
   * -- new css properties ...
   */
  public void slideCreatePlugin(){
    // FIXME, put a comment here
    // @include: Css3Animations
    $(".ball").as(Css3Animations.Css3Animations).animate($$("rotateX: 180deg, rotateY: 180deg"));
  }


  /**
   * @ How to port a jQuery plugin to gQuery
   *
   * - <a href="https://review.gerrithub.io/#/c/14511/1/src/main/java/com/google/gwt/query/client/plugin/Gesture.java" target='_blank'>Gesture Plugin Differences</a>
   * -- Take original JS code
   * -- Create JsBuider interfaces so as syntax is similar to JS
   * -- Set appropriate java types to JS variables
   * - <a href='https://github.com/manolo/gwtquery-gesture-plugin'  target='_blank'>Gesture Plugin Source Gighub</a>
   * - <a href='http://manolo.github.io/gwtquery-gesture-demo/index.html'  target='_blank'>Gesture Plugin Demo</a>
   *
   */
  public void slideGesturePlugin() {
    $(".current .jCode").as(Gesture.Gesture).on("taptwo", new Function(){
      public void f() {
        console.log("Double Tap");
      }
    });
  }

  /**
   * @ What is the future of gQuery
   *
   * - Type safe functions and promises
   * - Full support for java 8 lambdas
   * - Mobile friendly
   * - Integration with JsInterop
   */
  public void slideLambdas(){
    GQuery g = $(".ball");
    g.each(new IsElementFunction(){
      public void run(Element elm) {
        $(elm).animate("scale: 1.2");
      }
    });
    g.on("tapone", new IsEventFunction(){
      public Boolean call(Event evt) {
        return $(evt).animate($$("rotateX: 90deg")).animate($$("rotateX: 0deg")).FALSE;
      }
    });
    // Predefined return types in the gQuery chain
    g.on("taptwo",(e)-> $(e).animate($$("x: +=50")).TRUE);

    //.(done|fail) //\n\s .$1//Get the result of multiple callbacks
    $.when(() -> "aaa", () -> "bbb", Ajax.get("/lambdas.html"))
     .done((Object[] s) -> console.log(s[0], s[1], s[3]))
     .fail((s) -> console.log("Fail"));
  }

  /**
   * @ Show me more cool code ...
   */
  public void slideExamples() {
  }

  /**
   * @ *Example*: Material Design Hierarchical Timing.
   */
  public void slideMaterialDesign() {
    boxes.each(new Function() {
      int scale = boxes.isVisible() ? 0 : 1;
      public void f(Element e) {
        GQuery g = $(this);
        int delay = (int)(g.offset().left + g.offset().top);
        g.animate("duration: 200, delay: " + delay + ", scale: " + scale);
      }
    });
  }

  /**
   * @ *Example*: Material Design Ripple Effect.
   */
  public void slideMaterialDesignRipple() {
    //(\$\$\("posi) //\n\s $1//Create the ripple element to be animated
    final GQuery ripple = $("<div>").as(Transitions.Transitions).css($$("position: absolute, width: 40px, height: 40px, background: white, border-radius: 50%"));
    //Add ripple effects to certain elements when they are tapped
    $(".jCode, .button, h1").on("tap.ripple", new Function(){
      public boolean f(Event e) {
        GQuery target = $(this).css($$("overflow: hidden")).append(ripple);
        int x = e.getClientX() - 20 - target.offset().left;
        int y = e.getClientY() - 20 - target.offset().top;
        int f = Math.max(target.width(), target.height()) / 40 * 3;
        ripple.css($$("opacity:0.8, scale:0.5")).css("left", x + "px").css("top", y + "px");
        ripple.animate($$("opacity: 0, scale:" + f), new Function(){
          public void f() {
            ripple.detach();
          }
        });
        return false;
      }
    });
  }


  /**
   * @ Example: Wrapping Web Components with gQuery
   * - Use *bower* to install components in the public folder
   * -- bower install Polymer/paper-slider
   * - Use Ajax utility methods to load polyfills and import templates
   * - Web Components can be created and manipulated as any other element.
   * - We can change its properties or bind events.
   * <div id='sliders-container' class='right bottom'></div>
   */
  public void slideWebComponents() {
    Ajax.loadScript("bower_components/webcomponentsjs/webcomponents.js");
    Ajax.importHtml("bower_components/paper-slider/paper-slider.html");
    //
    GQuery slider = $("<paper-slider />").appendTo($("#sliders-container"));
    //
    slider.prop("value", 67);
    //(console|return) //  $1
    slider.on("change", (e) -> {
      console.log($(e).prop("value"));
      return true;
    });
    //
    $("#sliders-container").append("<paper-slider value=183 max=255 editable>");
  }

  /**
   * @ Example: Binding Attributes of Web Components.
   * - JsonBuilder can wrap any JavaScript element
   * -- Light Weight wrapper
   * -- Type safe
   * -- Chain setters
   *
   * <div id='slider-container' class='right bottom'></div>
   */
  public void slideWebComponentsBinding() {
    // @include: PaperSlider//(Ajax\.|\).done) //\n\s$1//Wait until the polyfill and the web component has been loaded
    GQuery.when(
      Ajax.loadScript("bower_components/webcomponentsjs/webcomponents.js"),
      Ajax.importHtml("bower_components/paper-slider/paper-slider.html")
    ).done(new Function(){
      public void f() {
        // Create and append the element as usual in gQuery
        GQuery g = $("<paper-slider>").appendTo($("#slider-container"));
        // Wrap the native element in a POJO
        PaperSlider slider = GQ.create(PaperSlider.class).load(g);
        // Use it as a java object
        slider.setValue(300).max(400).step(50).snaps(true).pin(true);
      }
    });
  }

  /**
   * @ Example: Uploading files with progress bar.
   */
  public void slideUpload() {
    final GQuery progress = $("<div>").css($$("height: 12px, width: 0%, background: #75bff4, position: absolute, bottom:0px")).appendTo(document);
    final GQuery fileUpload = $("<input type='file' accept='image/*'>").appendTo(document).hide();
    fileUpload.on("change", new Function() {
      public boolean f(Event e) {
        final JsArray<JavaScriptObject> files = $(e).prop("files");
        JavaScriptObject formData = JsUtils.jsni("eval", "new FormData()");
        for (int i = 0, l = files.length(); i < l; i++) {
          JsUtils.jsni(formData, "append", "file-" + i, files.get(i));
        }
        //\.(progress|setUrl) //\n\s .$1
        Ajax.ajax(Ajax.createSettings().setUrl(uploadUrl).setData(formData).setWithCredentials(true))
            .progress(new Function() {
              public void f() {
                progress.animate("width:" + arguments(2) + "%", 1000);
              }
            })
           .always(new Function() {
              public void f() {
                progress.remove();
              }
            })
           .done(new Function() {
              public void f() {
                uploadImg.attr("src", uploadUrl + "&show=file-0-0");
              }
            })
            ;
        return true;
      }
    })
    .trigger("click");
  }
  /**
   * J8
   */
  public void TODOslideUploadj8() {
    final GQuery progress = $("<div>").css($$("height: 6px, width: 0%, background: #75bff4, position: absolute, top:0px")).appendTo(document);
    fileUpload = $("<input type='file' multiple >").hide().appendTo(document);
    fileUpload.trigger("click");
    $(fileUpload).on("change", (e) -> {
      final JsArray<JavaScriptObject> files = fileUpload.prop("files");
      JavaScriptObject formData = JsUtils.jsni("eval", "new FormData()");
      for (int i = 0, l = files.length(); i < l; i++) {
        JsUtils.jsni(formData, "append", "file-" + i, files.get(i));
      }
      Settings settings = Ajax.createSettings().setUrl(uploadUrl).setData(formData).setWithCredentials(true);
      Ajax.ajax(settings)
         .done((o) -> {
           progress.remove();
           $("<img src='" + uploadUrl + "?show=file-0-0'>").css($$("position: absolute, top: 0")).appendTo(document);
         })
         .progress((a, b) -> {
           progress.animate("width:" + b + "%");
         });
      return true;
    });
  }


  /**
   * @ Real Examples
   * - <a href="http://arcbees.com">www.Arcbees.com<a>
   * - <a href="http://gwtproject.org">www.GwtProject.org</a>
   * - <a href="http://social.talkwheel.com">www.Talkwheel.com</a>
   */
  public void slideExample() {
  }

  public   void TODOslideTouchEvents() {
    $(".current .jCode").as(Gesture.Gesture).on("taptwo", new Function(){
      public void f() {
        console.log("Slide Left");
      }
    });
  }

  /**
   * @ Questions and Answers
   *
   * <br/>
   * @@ Rate this talk: <a href='http://gwtcreate.com/agenda' target='_blank'>http://gwtcreate.com/agenda</a>
   * <br/>
   */
  public void slideQuestionsAnswers() {
    //\)\. //)\n\s .
    when(() -> "talk").then((o) -> "Questions").and((o) -> "Answers").done((o) -> console.log("thanks"));
  }


}
