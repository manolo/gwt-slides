package org.gquery.slides.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.Promise;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.impl.ConsoleBrowser;
import com.google.gwt.query.client.js.JsUtils;
import com.google.gwt.query.client.plugins.deferred.PromiseFunction;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;

import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.$$;
import static com.google.gwt.query.client.GQuery.window;

public abstract class Utils {

  /**
   * Overrides default gQuery console implementation to
   * write out to the slide.
   */
  public static class Console extends ConsoleBrowser {
    @Override public void clear() {
      $("#console").hide().text("");
    }
    @Override public void log(Object o) {
      $("#console").show().append("<div class='consoleItem'>" +
          String.valueOf(o)
          // dumpArguments java.lang is obvious
          .replaceAll("java\\.lang\\.", "")
          // Formating a the dumpArguments output
          .replaceAll("\n\\[","\n  [")
          .replace("\n", "<br/>").replace(" ", "&nbsp;") + "</div>");
    }
  }

  public static void setTimeout(final Function f, int t) {
    new Timer() {
      public void run() {
        f.f();
      }
    }.schedule(t);
  }

  public static Promise getRandom() {
    return new PromiseFunction() {
      public void f(final Deferred dfd) {
        dfd.resolve(Random.nextInt(100));
      }
    };
  }

  public static boolean fail = false, succeed = true;


  Promise getRandomAjax() {
    return $ajax($$("url: '/mock-ajax/echo', timeout: 1000, data: " + Random.nextInt(30)));
  }

  public static Promise $ajax(Properties s) {
    final int t  = s.getInt("timeout");
    final String echo = s.getStr("data");
    return new PromiseFunction() {
      public void f(final Deferred dfd) {
        setTimeout(new Function(){public void f(){
          dfd.resolve(echo);
        }}, t);
      }
    };
  }

  private static JavaScriptObject location  = JsUtils.prop(window, "location");

  public static String hash() {
    String h = JsUtils.prop(location, "hash");
    return h != null ? h.substring(1) : "";
  }

  public static String hash(Object hash) {
    JsUtils.prop(location, "hash", hash);
    return hash();
  }
}
