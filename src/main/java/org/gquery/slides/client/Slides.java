package org.gquery.slides.client;

import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.console;
import static com.google.gwt.query.client.GQuery.document;
import static com.google.gwt.query.client.GQuery.lazy;
import static com.google.gwt.query.client.GQuery.window;
import static org.gquery.slides.client.SlidesUtils.hash;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Predicate;
import com.google.gwt.query.client.plugins.gestures.Gesture;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Main class to execute a presentation
 */
public class Slides {

  // For testing purposes, filter the slides to show: string list separated with comma.
  private String onlySlide = null;

  private static final String DISPLAY_PLAY_BUTTON = "displayPlayButton";
  private static final String CODE_SNIPPET =
    "<div class='jCode'>" +
    " <div class='jCode-scroll jCode-div'>" +
    "  <div class='jCode-lines'>" +
    "   <pre>%code%</pre>" +
    "</div></div></div>";

  private int currentPage = 1;
  private SlidesSource slidesSrc;
  private GQuery slides;
  private GQuery currentSlide = $();

  public Slides(SlidesSource presentation) {

    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      public void onUncaughtException(Throwable e) {
        console.error(e);
      }
    });

    // Use the root panel, so as GWT does not remove our handlers
    // in the case we use it later.
    RootPanel.get().add(new Label());

    if (Gesture.hasGestures) {
      $(window).as(Gesture.Gesture).fullScreen();
    }

    slidesSrc = presentation;
    for (String id : presentation.ids()) {
      GQuery s = $("#" + id);
      if (s.isEmpty()) {
        s = $("<section></section>").id(id);
        s.appendTo($(".slides"));
      }
    }

    slides = $(".slides > section")
    // build slide
    .each(new Function() {
      public void f(Element e) {
        buildSlide($(this));
      }
    })
    .filter(new Predicate() {
      public boolean f(Element e, int index) {
        if (onlySlide != null) {
          if (onlySlide.contains($(e).id())) {
            return true;
          }
          $(e).remove();
          return false;
        }
        boolean empty = $(e).html().trim().isEmpty();
        if (empty) {
          $(e).remove();
        }
        return (!empty);
      }
    });

    presentation.bind();
    bindEvents();

    showCurrentSlide();
    // slides have an initial opacity of 0
    $(slides).css("opacity", "1");

    if (Gesture.hasGestures) {
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
    SlidesSource.console.clear();
    $("#play, #viewport").hide();
    $("#marker").text("" + (1 + currentPage) + "/" + slides.size());

    // notify last slide to execute it's clean up code
    currentSlide.trigger(SlidesSource.LEAVE_EVENT_NAME);

    // Reset the appropriate classnames depending on slide position
    slides.removeClass("left", "right", "next", "prev", "current");
    // slides at left
    if (currentPage - 1 >= 0) {
      slides.lt(currentPage).addClass("left");
      slides.eq(currentPage - 1).addClass("prev");
    }
    // slides at right
    if ((currentPage + 1) <= totalPages) {
      slides.gt(currentPage).addClass("right");
      slides.eq(currentPage + 1).addClass("next");
    }
    // current slide
    currentSlide = slides.eq(currentPage).addClass("current");

    // notify new slide to execute it's start up code
    currentSlide.delay(0, lazy().trigger(SlidesSource.ENTER_EVENT_NAME).done());

    // display the button to execute the snippet
    if (currentSlide.data(DISPLAY_PLAY_BUTTON, Boolean.class)) {
      currentSlide.find(".jCode").prepend($("#play").show());
    }
  }

  boolean excludedElements(Event e) {
    return $(e.getEventTarget()).is("input, textarea, a, button, paper-slider");
  }

  private void bindEvents() {
    $(document.getBody()).as(Gesture.Gesture)
    // handle key events to move slides back/forward
    .on("keydown", new Function() {
      public boolean f(Event e) {
        // By pass if the cursor is in an input widget
        if (!excludedElements(e)) {
          switch (e.getKeyCode()) {
            case KeyCodes.KEY_RIGHT:
            case KeyCodes.KEY_SPACE:
              return show(true);
            case KeyCodes.KEY_LEFT:
            case KeyCodes.KEY_BACKSPACE:
              return show(false);
            case KeyCodes.KEY_E:
              $("#play").trigger("tap");
              break;
            case KeyCodes.KEY_F:
              $(window).as(Gesture.Gesture).fullScreenNow();
              break;
            case KeyCodes.KEY_C:
              console.clear();
              break;
          }
        }
        return true;
      }
    });
    $(".slides").as(Gesture.Gesture)
    .on("swipeleft", new Function(){
      public boolean f(Event e) {
        return excludedElements(e) ? true : show(true);
      }
    })
    .on("swiperight", new Function(){
      public boolean f(Event e) {
        return excludedElements(e) ? true : show(false);
      }
    })
    .on("mousewheel", new Function(){
      public boolean f(Event e) {
        return false;
      }
    })
    ;
    // Navigate through url changes.
    $(window).on("hashchange", new Function() {
      public void f() {
        showCurrentSlide();
      }
    });
    // Play button
    $("#play").on("tap", new Function() {
      public boolean f(Event e) {
        slidesSrc.exec(currentSlide.id());
        return false;
      }
    });
    // Clear the console if we push mouse right
    $("#console").on("tapone", new Function() {
      public boolean f(Event e) {
        console.clear();
        console.log("");
        return false;
      }
    })
    ;
  }

  private void buildSlide(GQuery slide) {
    String html = slide.html();
    String id = slide.id().toLowerCase();

    String javadoc = slidesSrc.docs.get(id);
    String code = slidesSrc.snippets.get(id);

    if (javadoc != null){
      html += javadoc;
    }

    boolean displayPlayButton = false;
    if (code != null && code.trim().length() > 0) {
      html += CODE_SNIPPET.replace("%code%", Prettify.prettify(code));
      if (html.contains("@noplay")) {
        html = html.replace("@noplay", "");
      } else {
        displayPlayButton = true;
      }
    }
    slide.data(DISPLAY_PLAY_BUTTON, displayPlayButton);
    slide.html(html);
  }

  private boolean show(boolean forward) {
    int incr = forward ? 1 : -1;
    int nextPage = Math.min(Math.max(currentPage + incr, 0), slides.size() - 1);
    if (nextPage != currentPage) {
      hash(nextPage);
      return false;
    }
    return true;
  }
}
