package org.gquery.slides.client;

import static com.google.gwt.query.client.GQuery.*;
import static org.gquery.slides.client.GQ.hash;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Predicate;
import com.google.gwt.user.client.Event;

/**
 * Entry point for the presentation
 */
public class Slides implements EntryPoint {

  private static final String PRESENT = "present";
  private static final String PAST = "past";
  private static final String FUTURE = "future";
  private static final String DISPLAY_PLAY_BUTTON = "displayPlayButton";
  private static final String CODE_SNIPPET = "<div class='CodeMirror'><div class='code-scroll " +
      "code-div'><div class='code-lines'><pre>%code%</pre></div></div></div>";

  private int currentPage = 1;
  private String currentExecId = null;
  private SlidesDeferred examplesClass;
  private GQuery slides;

  public void onModuleLoad() {
    examplesClass = GWT.create(SlidesDeferred.class);

    slides = $(".slides > section")
     // build slide
     .each(new Function() {
        public void f(Element e) {
          buildSlide($(this));
        }
     })
     // remove empty slides
     .filter(new Predicate() {
        public boolean f(Element e, int index) {
          return (!$(e).html().trim().isEmpty());
        }
     });

    bindEvents();

    showCurrentSlide();
  }

  private void showCurrentSlide() {
    String hash = hash();
    currentPage = hash.matches("\\d+") ? Integer.parseInt(hash) : 0;

    slides.removeClass(PAST, PRESENT, FUTURE);

    slides.lt(currentPage).addClass(PAST);
    slides.gt(currentPage).addClass(FUTURE);

    GQuery slide = slides.eq(currentPage).addClass(PRESENT);

    currentExecId = slide.id();

    console.clear();

    hideOrShowPlayButton(slide);

    // Update page number
    String page = "" + currentPage;
    $("#marker").text(page);
  }

  private void bindEvents() {
    $(window)
      // handle key events to move slides back/forward
      .bind(Event.ONKEYDOWN, new Function() {
        public boolean f(Event e) {
          int code = e.getKeyCode();
          if (code == KeyCodes.KEY_RIGHT || code == ' ') {
            show(true);
          }
          if (code == KeyCodes.KEY_LEFT || code == KeyCodes.KEY_BACKSPACE) {
            show(false);
          }
          return false;
        }
      })
      // handle hash change to select the appropriate slide
      .bind("hashchange", new Function() {
        public void f() {
          showCurrentSlide();
        }
      });

    $("#play").click(new Function() {
      public void f() {
        console.clear();
        examplesClass.exec(currentExecId);
      }
    });
  }

  private void buildSlide(GQuery slide) {
    String html = slide.html();
    String id = slide.id().toLowerCase();

    String javadoc = examplesClass.docs.get(id);
    String code = examplesClass.snippets.get(id);

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

  private void hideOrShowPlayButton(GQuery slide) {
    if (slide.data(DISPLAY_PLAY_BUTTON, Boolean.class)) {
      $("#play").show();
    } else {
      $("#play").hide();
    }
  }
}
