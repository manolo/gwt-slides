package org.gquery.slides.client;
import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.$$;
import static com.google.gwt.query.client.GQuery.window;
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
  
  String snippet = "<div class='CodeMirror'><div class='CodeMirror-scroll cm-s-monokai'><div class='CodeMirror-lines'><pre>%%</pre></div></div></div>";
  int current = 1;
  String color = "$1<span style='color: %%'>$2</span>$3";
  SlidesDeferred sld;
  
  public void onModuleLoad() {
    sld = GWT.create(SlidesDeferred.class);
    
    GQuery slides = $(".slide")
    .css($$("width: 100%, height: 100%"))
    .each(new Function(){public void f(){
      final String id = $(this).id().toLowerCase();
      String d = sld.docs.get(id);
      String html = d == null ? "" : d;
      String h = sld.snippets.get(id);
      if (h != null && !h.replaceAll("\\s+", "").isEmpty()) {
        h = h
//             .replaceAll("([^\\w])(if|null|new|public|void|else|static|return)([^\\w])", "$1<span style='color: red'>$2</span>$3")
             .replaceAll("([^\\w])(\".*\\w\")([^\\w])", "$1<span style='color: cian'>$2</span>$3")
             .replaceAll("(//.+?)\n", "<span style='color: green'>$1\n</span>")
            ;
        html += snippet.replace("%%", h);
      }
      $(this).html(html);
      
    }})
    .hide()
    .click(new Function(){public void f(){
      current += show($(this), $(this).next().filter(".slide")) ? 1 : 0;
    }}).bind(Event.ONCONTEXTMENU, new Function(){public boolean f(Event e){
      current -= show($(this), $(this).prev().filter(".slide")) ? 1 : 0;
      return false;
    }});
    
    String hash = hash();
    current = hash.matches("\\d+") ? Integer.parseInt(hash) : 1;
    current = slides.size() < current ? current : 1;
    show(null, slides.eq(current));
   }
  
   private boolean show(GQuery actual, GQuery next) {
     if (!next.isEmpty()) {
       $(".ball").hide();
       $("#console").html("").hide();
       if (actual != null) {
         actual.hide();
       }
       next.show();
       updateMarker(next.id());
       return true;
     }
     return false;
   }
   
   private void updateMarker(final String id) {
     $(window).delay(100, new Function(){public void f(){
       String page = "" + current;
       $("#marker").text(page);
       hash(page);
     }}).delay(1000, new Function(){public void f(){
       try {
         sld.exec(id);
      } catch (Exception e) {
        e.printStackTrace();
      }
     }});
   }

}
