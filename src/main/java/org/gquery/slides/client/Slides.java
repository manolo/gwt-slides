package org.gquery.slides.client;
import static com.google.gwt.query.client.GQuery.$;
import static org.gquery.slides.client.GQ.hash;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
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

    GQuery slides = $(".slide")
      .each(new Function(){public void f(){
        final String id = $(this).id().toLowerCase();
        String javadoc = examplesClass.docs.get(id);
        String html = javadoc == null ? "" : javadoc;
        String code = examplesClass.snippets.get(id);
        if (code != null && !code.matches("\\s*")) {
          html += SNIPPET_TPL.replace("%%", Prettify.prettify(code));
        }
        $(this).html(html);
      }})
      .hide()
      .click(new Function(){public void f(){
        currentPage += show($(this), $(this).next().filter(".slide")) ? 1 : 0;
        updateMarker();
      }})
      .bind(Event.ONCONTEXTMENU, new Function(){public boolean f(Event e){
        currentPage -= show($(this), $(this).prev().filter(".slide")) ? 1 : 0;
        updateMarker();
        return false;
      }});
    
    $("#play").click(new Function(){public void f(){
      clearConsole();
      examplesClass.exec(currentExecId);
    }});
    
    String hash = hash();
    currentPage = hash.matches("\\d+") ? Integer.parseInt(hash) : 1;
    currentPage = slides.size() >= currentPage ? currentPage : 1;
    show(null, slides.eq(currentPage - 1));
    updateMarker();
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
  
   private boolean show(GQuery actual, GQuery next) {
     if (!next.isEmpty()) {
       $(".ball").hide();
       if (actual != null) {
         actual.hide();
       }
       clearConsole();
       next.show();
       currentExecId = next.id();
       return true;
     }
     return false;
   }
}
