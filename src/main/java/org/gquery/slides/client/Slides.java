package org.gquery.slides.client;
import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.window;
import static org.gquery.slides.client.GQ.hash;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Event;

/**
 * Example code for a GwtQuery application
 */
public class Slides implements EntryPoint {
  
  private final String SNIPPET_TPL = "<div class='code'><div class='code-scroll code-div'><div class='code-lines'><pre>%%</pre></div></div></div>";
  private int currentPage = 1;
  private String currentExecId = null;
  private SlidesDeferred examplesClass;
  public void onModuleLoad() {
    examplesClass = GWT.create(SlidesDeferred.class);

    $(".slide")
      .hide()
      .each(new Function(){public void f(){
        final String id = $(this).id().toLowerCase();
        String javadoc = examplesClass.docs.get(id);
        String html = javadoc == null ? "" : javadoc;
        String code = examplesClass.snippets.get(id);
        if (code != null && !code.matches("\\s*")) {
          html += SNIPPET_TPL.replace("%%", Prettify.prettify(code));
        }
        $(this).html(html);
      }});
    
    $(window)
      .bind(Event.ONKEYDOWN | Event.ONCLICK | Event.ONCONTEXTMENU, new Function(){public boolean f(Event e){
        if (e.getKeyCode() == KeyCodes.KEY_RIGHT || e.getTypeInt() == Event.ONCLICK) show(true);
        if (e.getKeyCode() == KeyCodes.KEY_LEFT || e.getTypeInt() == Event.ONCONTEXTMENU) show(false);
        return false;
    }});
    
    $("#play").click(new Function(){public boolean f(Event e){
      clearConsole();
      examplesClass.exec(currentExecId);
      return false;
    }});
    
    String hash = hash();
    currentPage = hash.matches("\\d+") ? Integer.parseInt(hash) - 1  : 0;
    show(true);
   }
  
  
   private void updateMarker() {
     // Update page number
     String page = "" + currentPage;
     $("#marker").text(page);
     hash(page);
   }
   
   private void clearConsole() {
     $("#console").html("").hide();
   }
  
   private void show(boolean right) {
     GQuery slides = $(".slide");
     int incr = right ? 1 : -1;
     int page = Math.max(currentPage + incr, 0);
     
     GQuery actual = slides.eq(currentPage);
     GQuery next = slides.eq(page);
     while (!next.isEmpty() && next.text().trim().isEmpty()) {
       next = slides.eq(page += incr);
     }
     if (!next.isEmpty()) {
       $(".ball").hide();
       actual.hide();
       clearConsole();
       next.show();
       currentExecId = next.id();
       currentPage = page;
       updateMarker();
     }
   }
}
