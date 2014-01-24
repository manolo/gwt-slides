package org.gquery.slides.client;

import static com.google.gwt.query.client.GQuery.*;
import static org.gquery.slides.client.Utils.hash;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Predicate;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.js.JsUtils;
import com.google.gwt.query.client.plugins.effects.PropertiesAnimation.Easing;
import com.google.gwt.query.client.plugins.effects.PropertiesAnimation.EasingCurve;
import com.google.gwt.user.client.Event;

/**
 * Main class to execute a presentation
 */
public class Slides {
  
  // For testing purposes, filter the slides to show: string list separated with comma.
  private String onlySlyde = null;

  private static final String DISPLAY_PLAY_BUTTON = "displayPlayButton";
  private static final String CODE_SNIPPET =
    "<div class='jCode'>" +
    " <div class='jCode-scroll jCode-div'>" +
    "  <div class='jCode-lines'>" +
    "   <pre>%code%</pre>" +
    "</div></div></div>";

  private Easing easing = EasingCurve.easeInOutSine;

  private int currentPage = 1;
  private SlidesSource slidesSrc;
  private GQuery slides;
  private GQuery currentSlide = $();
  private boolean isMobile = JsUtils.hasProperty(window, "orientation");

  public Slides(SlidesSource presentation) {
    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      public void onUncaughtException(Throwable e) {
        console.log(e.getMessage());
        e.printStackTrace();
      }
    });

    slidesSrc = presentation;

    slides = $(".slides > section")
    // build slide
    .each(new Function() {
      public void f(Element e) {
        buildSlide($(this));
      }
    })
    .css("right", "-150%")
    // remove empty slides
    .filter(new Predicate() {
      public boolean f(Element e, int index) {
        if (onlySlyde != null) {
          if (onlySlyde.contains($(e).id())) {
            return true;
          }
          $(e).remove();
          return false;
        }
        return (!$(e).html().trim().isEmpty());
      }
    });

    bindEvents();
    
    showCurrentSlide();
    // slides have an initial opacity of 0
    $(slides).fadeTo(1);
    
    if (isMobile) {
      // make play button big enough to be clickable in mobile
      $("#play").css("font-size", "5em");
    }
  }
  
  private void showCurrentSlide() {
    // compute current page based on hash
    String hash = hash();

    currentPage = hash.matches("\\d+") ? Integer.parseInt(hash) : 0;
    int totalPages = slides.size() -1;

    // prevent going to any page out of our limits
    if (currentPage < 0 || currentPage > totalPages) {
      hash(0);
      return;
    }

    // update page elements
    console.clear();
    $("#play, #viewport").hide();
    $("#marker").text("" + (1 + currentPage) + "/" + slides.size());

    // stop any pending animation
    slides.stop(true);

    // notify last slide to execute it's clean up code
    currentSlide.trigger(SlidesSource.LEAVE_EVENT_NAME);

    // FIXME: gQuery animations seems not working with percentages, it should be -150% and 150%
    int w = (int)($(window).width() * 1.5);
    Properties pLeft = $$("scale:0, left: -" + w);
    Properties pRight = $$("scale:0, left: " + w);

    // move slides to left out of the window view port
    if (currentPage - 2 >= 0) {
      slides.lt(currentPage - 1).css(pLeft);
    }
    if (currentPage - 1 >= 0) {
      slides.eq(currentPage - 1).animate(pLeft, 1000, easing);
    }

    // move slides to right out of the window view port
    if ((currentPage + 2) <= totalPages) {
      slides.gt(currentPage + 1).css(pRight);
    }
    if ((currentPage + 1) <= totalPages) {
      slides.eq(currentPage + 1).animate(pRight, 1000, easing);
    }

    // move current slide to the window view port
    currentSlide = slides.eq(currentPage)
      .animate($$("scale: 1, rotateX: 0deg, rotateY: 0deg, left: 0"), 1000, easing)
      // notify new slide to execute it's start up code
      .delay(0, lazy().trigger(SlidesSource.ENTER_EVENT_NAME).done());

    // display the button to execute the snippet
    if (currentSlide.data(DISPLAY_PLAY_BUTTON, Boolean.class)) {
      currentSlide.find(".jCode").prepend($("#play").show());
    }
  }

  private void bindEvents() {
    final String touchStart = isMobile ? "touchstart" : "mousedown";
    final String touchEnd = isMobile ? "touchend" : "mouseup";

    $(window)
    // handle key events to move slides back/forward
    .on("keydown", new Function() {
      public boolean f(Event e) {
        // By pass if the cursor is in an input widget
        if (!$(e.getEventTarget()).is("input, textarea")) {
          int code = e.getKeyCode();
          if (code == KeyCodes.KEY_RIGHT || code == ' ') {
            show(true);
            return false;
          }
          if (code == KeyCodes.KEY_LEFT || code == KeyCodes.KEY_BACKSPACE) {
            show(false);
            return false;
          }
        }
        return true;
      }
    })
    // handle change slide with mouse move or slide gesture in mobile
    .on(touchStart + " " + touchEnd, new Function() {
      int x = 0;
      int xThreshold = 30;
      public boolean f(Event e) {
        String eventName = e.getType();
        String tagName = e.getEventTarget().<Element>cast().getTagName().toLowerCase(); 
        if (!tagName.matches("a|input") && touchStart.equals(eventName)) {
          x = isMobile ? e.getChangedTouches().get(0).getClientX() : e.getClientX();
          console.log(tagName + " " + eventName + " false");
          return false;
        } else if (x != 0) {
          int d = x - (isMobile ? e.getChangedTouches().get(0).getClientX() : e.getClientX());
          x = 0;
          if (d > xThreshold || d < -xThreshold) {
            show(d > 30);
            return false;
          }
        }
        console.log(tagName + " " + eventName + " true");
        return true;
      }
    })
    // handle hash change to select the appropriate slide
    .on("hashchange", new Function() {
      public void f() {
        showCurrentSlide();
      }
    })
    // on resize we change slide sizes and left properties
    .on("resize", new Function() {
      public void f() {
        int w = (int)($(window).width() * 1.5);
        slides.lt(currentPage).css("left", "-" + w);
        slides.gt(currentPage).css("left", "+" + w);
        slides.css("width", "" + w);
      }
    });

    $("#play").on(touchStart, new Function() {
      public void f() {
        slidesSrc.exec(currentSlide.id());
      }
    });
    
    // Clear the console if we push mouse right
    $("#console").on("contextmenu", new Function() {
      public boolean f(Event e) {
        console.clear();
        console.log("");
        return false;
      }
    });
  }
  
  private void buildSlide(GQuery slide) {
    String html = slide.html();
    String id = slide.id().toLowerCase();

    String javadoc = slidesSrc.docs.get(id);
    String code = slidesSrc.snippets.get(id);

    if (javadoc != null){
      html += javadoc;
    }

    boolean displayPlayButton = true;
    if (code != null && code.trim().length() > 0) {
      html += CODE_SNIPPET.replace("%code%", Prettify.prettify(code));
    } else {
      displayPlayButton = false;
    }

    slide.data(DISPLAY_PLAY_BUTTON, displayPlayButton);
    slide.html(html);
  }

  private void show(boolean forward) {
    int incr = forward ? 1 : -1;
    int nextPage = Math.min(Math.max(currentPage + incr, 0), slides.size() - 1);
    if (nextPage != currentPage) {
      hash(nextPage);
    }
  }
}
