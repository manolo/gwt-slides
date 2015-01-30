package org.gquery.slides.presentations.gwtcreate2013.js.Temp;

import com.google.gwt.query.client.Function;
import com.google.gwt.user.client.ui.Label;

import static com.google.gwt.query.client.GQuery.$;

public class java {
  private void test() {

$("p:last")
    .text("innerWidth:" + $("p:first"))
    .addClass("myClass");

$("#clickme").click(new Function() {
  public void f() {
    $("#book").animate("{" +
        "opacity: 0.25," +
        "left: '+=50'," +
        "height: 'toggle'}"
        , 5000, new Function() {
      public void f() {
        // Animation complete.
      }
    });
  }
});

$("div").click(new Function() {
  public void f() {
    String color = $(this).css("background-color");
    $("#result").html("That div is " +
        "<span style='color:" + color +
        ";'>" + color + "</span>.");
  }
});

$(".article:not(.hidden) > .header")
    .width($("#mainHeader").innerWidth())
    .click(new Function() {
      public void f() {
        $(this)
            .siblings(".content")
            .load("/article/" + $(this).parent().id(), null, new Function() {
              public void f() {
                $(this).fadeIn(250);
              }
            });
      }
    });

Label label = new Label("GQuery is awesome!");

$(label).addClass("myLabel").css("background-color", "#fefefe");

// ...
Label myLabel = $(".label").widget();

  }
}
