package org.gquery.slides.client;

import static org.gquery.slides.client.GQ.setTimeout;

import java.util.Random;

import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Promise;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.Promise.Deferred;
import com.google.gwt.query.client.plugins.deferred.PromiseFunction;
import com.google.gwt.user.client.Timer;

public abstract class GQ extends GQuery {
  protected GQ(GQuery gq) {
    super(gq);
  }
  public static class C {
    public static void clear() {
      $("#console").hide().text("");
    }
    public static void log(Object o) {
      System.out.println(o);
      $("#console").show().append("<div>" + o + "</div>");
    }
  }

  public static C console = new C();
  
  public static void setTimeout(final Function f, int t) {
    new Timer() {
      public void run() {
        f.f();
      }
    }.schedule(t);
  }
  
  public static GQuery $ = $();
  
  public static Promise getRandom() {
    return new PromiseFunction() {
      public void f(Deferred dfd) {
        dfd.resolve(new Random().nextInt(30));
      }
    };
  }
  
  public static boolean fail = false, succeed = true;
  

  Promise getRandomAjax() {
    return $ajax($$("url: '/mock-ajax/echo', timeout: 1000, data: " + new Random().nextInt(30)));
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
}
