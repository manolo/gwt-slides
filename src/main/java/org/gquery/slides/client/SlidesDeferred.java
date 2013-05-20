package org.gquery.slides.client;

import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.$$;
import static com.google.gwt.query.client.GQuery.when;
import static org.gquery.slides.client.GQ.*;

import java.util.Random;

import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Promise;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.Promise.Deferred;
import com.google.gwt.query.client.js.JsUtils;
import com.google.gwt.query.client.plugins.deferred.FunctionDeferred;
import com.google.gwt.query.client.plugins.deferred.PromiseFunction;
import com.google.gwt.query.client.plugins.effects.PropertiesAnimation.Easing;

/**
 * @author manolo
 *
 */
@SuppressWarnings("static-access")
public class SlidesDeferred extends SlidesBase {
  
  /**
   * @ so now what?
   * - We can create a deferred object that holds our callbacks
   * - We can return, pass around, store, etc, a public interface for the deffered: a promise
   * - We can easily resolve or reject that deferred
   * - Resolved or rejected data is passed to the callbacks no matter where/when they were assigned
   * - Based on the CommonJS Promises/A and Promise/A+ specs.
   */
  public void slide1() {
  }

  /**
   * @@ What it looks like
   */
  public void testN1() throws Exception {
    // create a Deferred
    Deferred dfd = $.Deferred();
    // do something when it's done
    dfd.promise()
      .done(new Function(){public void f(){
        console.log( "dun dun dum" );
      }});
    // resolve (tip the done bucket)
    dfd.resolve();
  }
  
  /**
   * @@ Handle success and failures
   */
  public void testN2() throws Exception {
    // create a Deferred
    Deferred dfd = $.Deferred();
    // do something when it's done
    dfd.promise()
      .done(new Function(){public void f(){
        console.log("success!" + arguments(0));
      }})
      .fail(new Function(){public void f(){
        console.log("broked!" + arguments(0));
      }});
    // resolve (tip the done bucket)
    dfd.reject("oh noes!");
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
   * @@ Receive succeed data
   */
  public void testN3_1() throws Exception {
    // Call an asynchronous method which will succeed
    doSomethingAsync(succeed)
      .done(new Function(){public void f(){
        // get the succeed data
        console.log("we " + arguments(0));
      }});
  }

  /**
   * @@ Receive failed messages
   */
  public void testN3_2() throws Exception {
    // Call an asynchronous method which will fail
    doSomethingAsync(fail)
      .fail(new Function(){public void f(){
        // get the failed message
        console.log("we " + arguments(0));
      }});
  }

  /**
   * @@ Chaining
   */
  public void testN4() throws Exception {
    Function didIt = new Function(){public void f(){
      console.log("did it!");
    }};
    Function failed = new Function(){public void f(){
      console.log("failed!");
    }};

    // Call a failed asynchronous
    doSomethingAsync(fail)
      .then(didIt, failed)
      .done(didIt)
      .fail(failed)
      .always(didIt);
  }
  
  /**
   * @@ Promises maintain status and data.
   */
  public void testN5() throws Exception {
    // create a Deferred
    final Deferred dfd = $.Deferred();
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
    
    $.when(customDfd)
      .done(new Function(){public void f(){
        console.log(dumpArguments());
        $.when(customDfd).done(new Function(){public void f(){
          console.log(dumpArguments());
        }});
      }});
  }  
  
  
  /**
   * @@ More about arguments
   */
  public void testN6() throws Exception {
    // call an async random generator
    getRandom().done(new Function(){public void f(){
      // We get an array of arguments
      console.log(arguments(0));
    }});
  }
  /**
   * @@ Mixing calls
   */
  public void testN7() throws Exception {
    // We can call simultaneous things
    $.when( getRandom(), "JQ", getRandom(), true, new Random())
    .done( new Function(){public void f(){
      // We get a bidimensional array with the output of each call
      console.log(arguments(0, 0));
      console.log(arguments(1, 0));
      console.log(arguments(2, 0));
      console.log(arguments(3, 0));
      console.log(arguments(4, 0));
    }});
  }
  /**
   * @@ Helper method: dumpArguments()
   */
  public void testN7_2() throws Exception {
    // Join different calls
    $.when( getRandom(), "JQ", new Boolean[]{true, false})
    .done( new Function(){public void f(){
      // dump data returned by each call
      console.log(dumpArguments());
    }});
  }
  
  /**
   * @@ Wait until all is resolved.
   */
  public void testN8() throws Exception {
    // customized deferred will be resolved after a delay
    Function customDfd = new Function(){public Object f(Object...args){
      final Deferred dfd = $.Deferred();
      setTimeout(new Function(){public void f(){
        dfd.resolve("all done!");
      }}, 4000);
      return dfd.promise();
    }};
    // mix two calls
    $.when( getRandom(), customDfd)
      .done(new Function(){public void f(){
        // Done when all promises finish
        console.log(arguments(0, 0));
        console.log(arguments(1, 0));
      }});
  }
  public void testN9() throws Exception {
    Function customDfd = new Function(){public Object f(Object...args){
      final Deferred dfd = $.Deferred();
      setTimeout(new Function(){public void f(){
        dfd.resolve("all done!");
      }}, 4000);
      return dfd.promise();
    }};

    $.when( getRandom(), customDfd)
      .done(new Function(){public void f(){
        console.log(dumpArguments());
      }});
  }
  /**
   * @@ Helper Functions
   */
  public void testN10() throws Exception {
    Function customDfd = new Function(){public Object f(Object...args){
      Deferred dfd = $.Deferred();
      dfd.resolve("all done 1 !");
      return dfd.promise();
    }};
    
    Promise customPrms = new PromiseFunction() {public void f(final Deferred dfd) {
      dfd.resolve("all done 2 !");
    }};

    Function customFncDfd = new FunctionDeferred() {protected void f(final Deferred dfd) {
      dfd.resolve("all done 3 !");
    }};
    
    $.when(customDfd, customPrms, customFncDfd)
      .done(new Function(){public void f(){
        console.log(dumpArguments());
      }});
  }

  /**
   * @@ A cache implementation
   */
  public void testN12() throws Exception {
    final Function customDfd = new Function() {
      Integer cache;
      public Object f(Object...o) {
        if (cache != null) {
          return cache;
        } else {
          return getRandom().done(new Function(){public void f(){
            cache = arguments(0);
          }});
        }
      }
    };
    
    $.when(customDfd)
      .done(new Function(){public void f(){
        console.log(dumpArguments());
        $.when(customDfd).done(new Function(){public void f(){
          console.log(dumpArguments());
        }});
      }});
  }

  Function drop(final GQuery ball, final int timeout) {
    return new Function(){public Object f(Object...o){
      System.out.println(ball);
      return ball.animate($$("bottom: 0"), timeout, Easing.SWING).promise();
    }};
  }
  
  GQuery red = $(".red"), blue = $(".blue"), yellow = $(".yellow");
  void drawBalls() {
    red.css($$("bottom:8em;right:1em"));
    blue.css($$("bottom:4em;right:2em"));
    yellow.css($$("bottom:6em;right:2.7em"));
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
    when($(".ball").animate($$("bottom: 0"), 1700, Easing.SWING))
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
