package org.gquery.slides.client;
import static com.google.gwt.query.client.GQuery.*;
import static com.google.gwt.query.client.GQuery.window;
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
 * Example code for a GwtQuery application
 */
public class Slides implements EntryPoint {
  private final String SNIPPET_TPL = "<div class='CodeMirror'><div class='code-scroll code-div'><div class='code-lines'><pre>%%</pre></div></div></div>";
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
      .bind(Event.ONKEYDOWN /*| Event.ONCLICK | Event.ONCONTEXTMENU*/, new Function(){public boolean f(Event e){
        if (e.getKeyCode() == KeyCodes.KEY_RIGHT || e.getTypeInt() == Event.ONCLICK) return show(true);
        if (e.getKeyCode() == KeyCodes.KEY_LEFT || e.getTypeInt() == Event.ONCONTEXTMENU) return show(false);
        return true;
    }});
    
    $("#play").click(new Function(){public boolean f(Event e){
      run();
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
   
   private void run() {
     clearConsole();
     examplesClass.exec(currentExecId);
   }
  
   private boolean show(boolean fw) {
     GQuery slides = $(".slide").filter(new Predicate(){
       @Override public boolean f(Element e, int index) {
         return (!$(e).text().trim().isEmpty());
       }
     });
     
     int incr = fw ? 1 : -1;
     int page = Math.min(Math.max(currentPage + incr, 0), slides.size());
     
     String c_current = "current";
     String c_animate = "animate";
     String c_direct = incr == 1 ? "right" : "left";
     String c_oposite = incr == 1 ? "left" : "right";
     slides.hide();
     
     GQuery next = slides.eq(page).show().removeClass(c_animate, c_direct).addClass(c_oposite);
     GQuery current = slides.eq(currentPage).show().removeClass(c_animate);
     $(document).get(0).getOffsetWidth();
     next.addClass(c_animate);
     current.addClass(c_animate);
     current.addClass(c_direct).removeClass(c_current);
     next.addClass(c_current).removeClass(c_oposite, c_direct);
     currentExecId = current.id();
     
     currentPage = page;
     updateMarker();
     
     
     clearConsole();
     $(".ball").hide();
     
     $(window).stop(true, true).delay(1000, new Function(){
       public void f() {
         run();
       };
     });
     
     return false;
   }
   
}
