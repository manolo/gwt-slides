package org.gquery.slides.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Predicate;
import com.google.gwt.query.client.Selector;
import com.google.gwt.query.client.Selectors;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;

import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.window;
import static org.gquery.slides.client.GQ.hash;

/**
 * Entry point for the presentation
 */
public class Slides implements EntryPoint {

  interface SlidesSelector extends Selectors {
    @Selector(".slides > section")
    GQuery slides();
  }

  interface Templates extends SafeHtmlTemplates {
    @Template("<div class='CodeMirror'><div class='code-scroll code-div'><div class='code-lines'>" +
        "<pre>{0}</pre></div></div></div>")
    SafeHtml codeSnippet(SafeHtml code);
  }

  private static final String PRESENT = "present";
  private static final String PAST = "past";
  private static final String FUTURE = "future";

  private int currentPage = 1;
  private String currentExecId = null;
  private SlidesDeferred examplesClass;
  private SlidesSelector selector;
  private Templates html;

  public void onModuleLoad() {
    html = GWT.create(Templates.class);
    selector = GWT.create(SlidesSelector.class);
    examplesClass = GWT.create(SlidesDeferred.class);

    // build slide
    selector.slides().each(
        new Function() {
          public void f(Element e) {
            GQuery slide = $(e);
            String id = slide.id().toLowerCase();
            buildSlide(id, slide);
          }
        });

    bindEvents();

    showCurrentSlide();
  }

  private void showCurrentSlide() {
    String hash = hash();
    currentPage = hash.matches("\\d+") ? Integer.parseInt(hash) - 1 : 0;

    selector.slides().hide().each(new Function() {
      @Override
      public Object f(Element e, int i) {
        GQuery slide = $(e).removeClass(PAST, PRESENT, FUTURE);

        if (i < currentPage) {
          slide.addClass(PAST);
        } else if (i > currentPage) {
          slide.addClass(FUTURE);
        } else {
          slide.addClass(PRESENT);
        }
        return null;
      }
    });

    // presentation is ready show the slides
    Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {
      @Override
      public boolean execute() {
        selector.slides().show();
        return false;
      }
    }, 1000);
  }

  private void bindEvents() {
    $(window).bind(Event.ONKEYDOWN, new Function() {
      public boolean f(Event e) {
        if (e.getKeyCode() == KeyCodes.KEY_RIGHT) {
          return show(true);
        }
        if (e.getKeyCode() == KeyCodes.KEY_LEFT) {
          return show(false);
        }
        return true;
      }
    });

    $("#play").click(new Function() {
      public boolean f(Event e) {
        run();
        return false;
      }
    });
  }

  private void buildSlide(String id, GQuery slide) {
    SafeHtmlBuilder builder = new SafeHtmlBuilder();

    String javadoc = examplesClass.docs.get(id);
    String code = examplesClass.snippets.get(id);

    if (javadoc != null){
      builder.appendHtmlConstant(javadoc);
    }

    if (code != null && !code.matches("\\s*")) {
      builder.append(html.codeSnippet(SafeHtmlUtils.fromSafeConstant(Prettify.prettify(code))));
    }

    String htmlToInject = builder.toSafeHtml().asString();

    if (htmlToInject != null && htmlToInject.trim().length() > 0) {
      slide.html(htmlToInject);
    }
  }

  private void clearConsole() {
    $("#console").html("").hide();
  }

  private void run() {
    clearConsole();
    examplesClass.exec(currentExecId);
  }

  private boolean show(boolean forward) {
    // filter the empty slides
    GQuery slides = selector.slides().filter(new Predicate() {
      @Override
      public boolean f(Element e, int index) {
        return (!$(e).text().trim().isEmpty());
      }
    });

    int incr = forward ? 1 : -1;
    int nextPage = Math.min(Math.max(currentPage + incr, 0), slides.size());
    String classForOldSlide = forward ? PAST : FUTURE;

    slides.eq(currentPage).removeClass(PRESENT).addClass(classForOldSlide);
    slides.eq(nextPage).removeClass(FUTURE, PAST).addClass(PRESENT);

    currentPage = nextPage;
    currentExecId = slides.eq(currentPage).id();

    updateMarker();

    return false;
  }

  private void updateMarker() {
    // Update page number
    String page = "" + currentPage;
    $("#marker").text(page);
    hash(page);
  }
}
