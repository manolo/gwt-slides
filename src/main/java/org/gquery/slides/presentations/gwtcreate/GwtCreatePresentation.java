package org.gquery.slides.presentations.gwtcreate;

import static com.google.gwt.query.client.GQuery.*;
import static org.gquery.slides.client.Utils.getRandom;
import static org.gquery.slides.client.Utils.setTimeout;

import org.gquery.slides.client.SlidesSource;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Promise;
import com.google.gwt.query.client.Promise.Deferred;
import com.google.gwt.query.client.js.JsUtils;
import com.google.gwt.query.client.plugins.deferred.FunctionDeferred;
import com.google.gwt.query.client.plugins.deferred.PromiseFunction;
import com.google.gwt.query.client.plugins.effects.PropertiesAnimation.EasingCurve;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author manolo
 *
 */
public class GwtCreatePresentation extends SlidesSource {

  GQuery viewPort = $("#viewport");
  Widget resizeWidget;

  public void beforeBindEvent() {
    leaveBindEvent();
    viewPort.show().height(250);
  }

  /**
   * @ Event binding
   */
  public void testBindEvent() {
    // handle the click event on the console
    $("#console").bind("click", new Function() {
      public void f() {
        console.log("It's awesome, you click on the console!!");
      }
    });
    //
    // you can handle any type of event supported by the browser
    $("#console").bind("transitionend", new Function() {
      public void f() {
        console.log("Resizing of the console done !");
      }
    });
    //
    resizeWidget = new HTML("Hover me to resize the console");
    RootPanel.get("viewport").add(resizeWidget);
    //
    // works with widget
    $(resizeWidget).mouseover(new Function() {
      public void f() {
        $("#console").css("width", "50%");
      }
    }).mouseout(new Function() {
      public void f() {
        $("#console").css("width", "96%");
      }
    });
  }

  public void afterBindEvent() {
    console.log("");
  }

  public void leaveBindEvent() {
    viewPort.hide();
    if (resizeWidget != null) {
      resizeWidget.removeFromParent();
      resizeWidget = null;
    }
    testUnBindEvent();
  }

  public void enterUnBindEvent() {
    beforeBindEvent();
    testBindEvent();
    afterBindEvent();
  }

  /**
   * @ Unbind Event
   */
  public void testUnBindEvent() {
    // remove all handlers by event types
    $("#console").unbind("click");
    $("#console").unbind("transitionend");
    //
    // you can remove handlers of several types at once.
    $(resizeWidget).unbind("mouseover mouseout");
    //
    // remove a specific handler
    $(resizeWidget).click(new Function() {
      public void f() {
        console.log("Youhou \\o/ /o\\ ");
        $(resizeWidget).unbind("click", this);
      }
    });
  }

  public void leaveUnBindEvent() {
    leaveBindEvent();
  }

  public void beforeCustomEvent() {
    viewPort.show();
  }

  /**
   *  @ Custom event
   */
  public void testCustomEvent() {
    viewPort.append("<input type='text' id='text'></input><button>Send to console</button>");
    //
    // trigger the 'sendToConsole' event when we click on the button
    $("#viewport > button").click(new Function() {
      public void f() {
        $("#console").trigger("sendToConsole", $("#text").val());
      }
    });
    //
    // handle the 'sendToConsole' event
    $("#console").bind("sendToConsole", new Function() {
      @Override
      public boolean f(Event e, Object... data) {
        console.log(data[0]);
        return false;
      }
    });

  }

  public void afterCustomEvent() {
    $("#viewport > button").unbind("click");
    $("#console").unbind("sendToConsole");
    viewPort.empty().hide();
  }

  public void beforeNamespace() {
    viewPort.append("<button id='unbind'>Unbind events</button>").show();
  }

  /**
   *  @ Namespace
   */
  public void testNamespace() {
    // you can specify name space when you bind events
    $("#console").bind("click.ns1 mouseenter.ns1", new Function() {
      public void f() {
        console.log(getEvent().getType() + " from namespace [ns1]");
      }
    });
    //
    $("#console").bind("click.ns2 mouseleave.ns2", new Function() {
      public void f() {
        console.log(getEvent().getType() + " from namespace [ns2]");
      }
    });

    $("#unbind").click(new Function() {
      public void f() {
        // you can decide to unbind only some events of some namespace
        $("#console").unbind("click.ns2");
        //
        // or unbind all events of a certain namespace
        $("#console").unbind(".ns1");
      }
    });
    //
    console.log("Ready !");
  }

  public void leaveNamespace() {
    $("#console").unbind(".ns1");
    viewPort.empty().hide();
  }


  /**
   * @ Avoiding JSNI
   * @@ calling external code.
   * <pre>
   pushStateImpl("aStateObject", "aTitle", "foo/bar");
   ...
   native void pushStateImpl(Object stateObject, String title, String url) /*-{
       if ($wnd.history && $wnd.history.pushState) {
          $wnd.history.pushState(stateObject, title, url);
       }
   }-* /;

   NodeList nodes = querySelectorAll("div")
   ...
   native JavaScriptObject querySelectorAll(String selector) /*-{
       if ($doc.querySelectorAll) {
           return $doc.querySelectorAll(selector);
       }
   }-* /;
   * </pre>
   */
  public void testJsnicall() {
    JavaScriptObject jso = JsUtils.prop(window, "history");
    JsUtils.runJavascriptFunction(jso, "pushState", "aStateObject", "aTitle", "foo/bar");
    //
    NodeList<?> nodes = JsUtils.runJavascriptFunction(document, "querySelectorAll", "div");
    //
    console.log(Window.Location.getHref());
    console.log(nodes.getLength());
  }

  private String url;
  public void enterJsnicall() {
    // save pushState before changing it in the snippet
    url = Window.Location.getPath() + Window.Location.getQueryString();
  }

  public void leaveJsnicall() {
    // restore pushState
    JavaScriptObject jso = JsUtils.prop(window, "history");
    JsUtils.runJavascriptFunction(jso, "pushState", null, null, url + Window.Location.getHash());
  }

  /**
   * @ Avoiding JSNI
   * @@ Exporting Java methods
   * <pre>
  // Call the native method to export the function
  exportBar();
  ...
  // Write a native JSNI method to set a window property
  native void exportBar() /*-{
    $wnd.bar =
      $entry(
         @org.gquery.slides.presentations.gwtcreate.GwtCreatePresentation::bar(Ljava/lang/Object;)
      );
  }-* /;
  // Write a Jave method to handle the call
  public static void bar(final Object args) {
    console.log(args);
    return "Hello from JSNI exported java";
  }
   * </pre>
   */
  public void testJsniexport() {
    // Just set a window property with a wrapped function
    JsUtils.prop(window, "foo", JsUtils.wrapFunction(new Function() {
      public Object f(Object... args) {
        console.log(dumpArguments());
        return "Hello from gQuery exported java";
      }
    }));
  }

  // TODO: maybe we could figure out a way to copy these methods to
  // the javadoc <pre></pre> block somehow
  public static Object bar(final Object args) {
    console.log(args);
    return "Hello from JSNI exported java";
  }
  // TODO: To copy this method in the <pre> block should be more
  // difficult because the AST would consider the body as a comment
  native void exportBar() /*-{
    $wnd.bar =
      $entry(
         @org.gquery.slides.presentations.gwtcreate.GwtCreatePresentation::bar(Ljava/lang/Object;)
      );
  }-*/;

  // Show the console, and run a JavaScript console emulator.
  public void beforeJsniexport() {
    console.log("Ready");
    exportBar();
    viewPort.height(250).append("<div>Try this javascript code:</div>" +
        "<pre>bar('hello', 'bye');\nbar(1);\nfoo('hello','bye');\nfoo('hi', 2, {a: 1, b:true, c:'foo'});</pre>" +
    "<input type=text id='evaljs' placeholder='Javascript console' >").fadeIn(2000);

    viewPort.find("input").bind(Event.ONKEYDOWN, new Function() {
      public boolean f(Event e) {
        if (e.getKeyCode() == KeyCodes.KEY_ENTER) {
          String js = $(this).val();
          Object o;
          try {
            o = JsUtils.runJavascriptFunction(window, "eval", js);
          } catch (Exception e2) {
            o = e2.getMessage().replaceFirst("^.*:\\s+", "");
          }
          if (o != null) {
            console.log(o);
          }
          return false;
        }
        return true;
      }
    });
    $("#play").hide();
  }

  public void leaveJsniexport() {
    viewPort.empty().hide();
  }


  /**
   * @ What is the Deferred object?
   * 
   * - It is a chainable object that holds our callbacks into queues.
   * - It can invoke callback queues, and relay the success or failure state of any synchronous or asynchronous function.
   * - We can easily resolve or reject that deferred
   * - We can return, pass around, and store Promises, an interface for the deferred which prevents change the state.
   * - Resolved or rejected data is passed to the callbacks no matter where/when they were assigned
   * - It is Based on the CommonJS Promises/A and Promise/A+ specs.
   */
  public void slide1() {
  }

  /**
   * @ What does it look like?
   */
  public void testN1() throws Exception {
    // create a Deferred
    Deferred dfd = Deferred();
    // do something when it's done
    dfd.promise()
    .done(new Function(){public void f(){
      console.log( "dun dun dum" );
    }});
    // resolve (tip the done bucket)
    dfd.resolve();
  }

  /**
   * @ Handling success and failures
   */
  public void testN2() throws Exception {
    // create a Deferred
    Deferred dfd = Deferred();
    // do something when it's done
    dfd.promise()
    .done(new Function(){public void f(){
      console.log("success!");
    }})
    .fail(new Function(){public void f(){
      console.log("broked!");
    }});
    // resolve (tip the done bucket)
    dfd.reject();
  }

  Promise doSomethingAsync(final boolean ok) {
    return new PromiseFunction() {public void f(final Deferred dfd) {
      setTimeout(new Function(){public void f(){
        if (ok) dfd.resolve("OK");
        else dfd.reject("ERR");
      }}, 1000);
    }};
  }

  /**
   * @ Receiving succeed or failed data
   */
  public void testN3() throws Exception {
    // Call an asynchronous method which will succeed
    doSomethingAsync(true)
    .done(new Function(){public void f(){
      // get the succeed data
      console.log("we " + arguments(0));
    }});
    // Call an asynchronous method which will fail
    doSomethingAsync(false)
    .fail(new Function(){public void f(){
      // get the failed message
      console.log("we " + arguments(0));
    }});
  }

  /**
   * @ Chaining
   */
  public void testN4_1() throws Exception {
    Function didIt = new Function(){public void f(){
      console.log("did it!");
    }};
    Function failed = new Function(){public void f(){
      console.log("failed!");
    }};

    // Call a failed asynchronous
    doSomethingAsync(false)
    .done(didIt)
    .fail(failed)
    .always(didIt);
  }

  /**
   * @ Pipelining
   */
  public void testN4_2() throws Exception {
    Function didIt = new Function(){public void f(){
      console.log("did it!");
    }};
    Function failed = new Function(){public void f(){
      console.log("failed!");
    }};

    // Call a failed asynchronous
    doSomethingAsync(true)
    .then(didIt, failed)
    .done(didIt)
    .fail(failed);

    // Call a failed asynchronous
    doSomethingAsync(false)
    .then(didIt, failed)
    .done(didIt)
    .fail(failed);
  }

  /**
   * @ Promises maintain status and data.
   */
  public void testN5() throws Exception {
    // create a Deferred
    final Deferred dfd = Deferred();
    // resolve it
    dfd.resolve( "OH NO YOU DIDNT");

    // Check that the promise status is resolved
    console.log("Promise status is: " + dfd.promise().state());

    // Add a handle to the promise in the future.
    setTimeout( new Function(){public void f(){
      dfd.promise().done( new Function(){public void f() {
        console.log(arguments(0));
      }});
    }}, 1000 );
  }

  public void testN5_2() throws Exception {
    final Promise customDfd = getRandom();
    when(customDfd)
    .done(new Function(){public void f(){
      console.log(dumpArguments());
      when(customDfd).done(new Function(){public void f(){
        console.log(dumpArguments());
      }});
    }});
  }

  public void testN6() throws Exception {
    // call an async random generator
    getRandom().done(new Function(){public void f(){
      // We get an array of arguments
      console.log(arguments(0));
    }});
  }

  /**
   * @ Joining multiple calls
   */
  public void testN7() throws Exception {
    // We can join simultaneous promises, functions or data into a single promise which
    // will be resolved only in the case all of them succeed.
    when( getRandom(), "JQ", getRandom(), true, new Button())
    .done( new Function(){public void f(){
      // We get a bi-dimensional array with the output of each call
      console.log(arguments(0, 0));
      console.log(arguments(1, 0));
      console.log(arguments(2, 0));
      console.log(arguments(3, 0));
      console.log(arguments(4, 0));
    }});
  }

  /**
   * @ The helper method `dumpArguments`
   */
  public void testN7_2() throws Exception {
    // Join different calls
    when( getRandom(), "JQ", new Boolean[]{true, false})
    .done( new Function(){public void f(){
      // helper method to inspect the content of the arguments array
      console.log(dumpArguments());
    }});
  }

  /**
   * @ Wait until everything is resolved.
   */
  public void testN8() throws Exception {
    // customized deferred will be resolved after a delay
    Function customDfd = new Function(){public Object f(Object...args){
      final Deferred dfd = Deferred();
      setTimeout(new Function(){public void f(){
        dfd.resolve("all done!");
      }}, 4000);
      return dfd.promise();
    }};
    // run simultaneous asynchronous callbacks
    when( getRandom(), customDfd)
    .done(new Function(){public void f(){
      // Done will be fired when all promises are resolved
      console.log(arguments(0, 0));
      console.log(arguments(1, 0));
    }});
  }

  public void testN9() throws Exception {
    Function customDfd = new Function(){public Object f(Object...args){
      final Deferred dfd = Deferred();
      setTimeout(new Function(){public void f(){
        dfd.resolve("all done!");
      }}, 4000);
      return dfd.promise();
    }};

    when( getRandom(), customDfd)
    .done(new Function(){public void f(){
      console.log(dumpArguments());
    }});
  }

  /**
   * @ gQuery Helper Functions
   */
  public void testN10() throws Exception {
    // The normal way create a function returning a promise
    Function customDfd = new Function(){public Object f(Object...args){
      Deferred dfd = Deferred();
      dfd.resolve("all done 1 !");
      return dfd.promise();
    }};
    // Simplified way to create a promise
    Promise customPrms = new PromiseFunction() {public void f(final Deferred dfd) {
      dfd.resolve("all done 2 !");
    }};
    // Simplified way to create a deferred function
    Function customFncDfd = new FunctionDeferred() {protected void f(final Deferred dfd) {
      dfd.resolve("all done 3 !");
    }};
    // We can join at the same time Functions and Promises
    when(customDfd, customPrms, customFncDfd)
    .done(new Function(){public void f(){
      console.log(dumpArguments());
    }});
  }

  /**
   * @ How to use deferred for caching calls
   */
  public void testN12() throws Exception {
    // Define a customized deferred function
    final Function customDfd = new Function() {
      // Response cache
      Integer cache;
      public Object f(Object...o) {
        if (cache != null) {
          return cache;
        } else {
          // If the call is not cached return the callback promise
          return getRandom().done(new Function(){public void f(){
            cache = arguments(0);
          }});
        }
      }
    };

    // Calling the function always returns the same value
    when(customDfd)
    .done(new Function(){public void f(){
      console.log(dumpArguments());
      when(customDfd).done(new Function(){public void f(){
        console.log(dumpArguments());
      }});
    }});
  }

  Function drop(final GQuery ball, final int timeout) {
    return new Function(){public Object f(Object...o){
      System.out.println(ball);
      return ball.animate($$("bottom: 0"), timeout, EasingCurve.custom.with(.31,-0.37,.47,1.5)).promise();
    }};
  }

  GQuery red = $(".red"), blue = $(".blue"), yellow = $(".yellow");
  void drawBalls() {
    red.css($$("bottom:8em;right:1em;display:block"));
    blue.css($$("bottom:4em;right:2em;display:block"));
    yellow.css($$("bottom:6em;right:2.7em;display:block"));
  }
  public void testSomething() {
    drawBalls();
    when(drop(blue,500)).then(drop(yellow,2000)).then(drop(red,4000))
    .done(new Function(){public void f(){
      System.out.println(arguments(3));
    }});
  }

  public void testSomething1() {
    drawBalls();
    when($(".ball").animate($$("bottom: 0"), 1700, EasingCurve.custom.with(.31,-0.37,.47,1.5)))
    .done(new Function(){public void f(){
      console.log("all done");
    }});
  }

  public void testSomething2() {
    drawBalls();
    final Function $a = drop(blue,500),
    $b = drop(yellow,2000),
    $c = drop(red,4000);

    when($a, $b, $c)
    .done(new Function(){public void f(){
      console.log("all done");
    }});
  }
  public void testSomething3() {
    drawBalls();
    final Function
    $a = drop(blue,4000),
    $b = drop(yellow,2000),
    $c = drop(red,4000);

    setTimeout(new Function(){public void f(){
      red.stop();
    }}, 1000);
    when($a, $b, $c)
    .done(new Function(){public void f(){
      console.log("all done");
    }});
  }
  public void testSomething4() {
    drawBalls();
    final Function $a = drop(blue,500),
    $b = drop(yellow,2000),
    $c = drop(red,4000);

    when($a, $b, $c, getRandom())
    .done(new Function(){public void f(){
      System.out.println(arguments(3));
    }});
  }

  public void testSomething5() {
    drawBalls();
    Promise promise = getRandom()
    .then(new Function(){public Object f(Object...args){
      console.log(dumpArguments());
      return getRandom();
    }});

    when(promise).then(new Function(){public void f(){
      console.log(dumpArguments());
    }});
  }


}
