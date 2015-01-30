package org.gquery.slides.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.query.client.Console;

/**
 * 
 * Extend this class to create a set of Slides using the SlidesGenerator.
 * 
 */
public abstract class SlidesSource implements SlidesSourceMarker {
  public static final String ENTER_EVENT_NAME = "slideEnter";
  public static final String LEAVE_EVENT_NAME = "slideLeave";
  public static final  Console console = new SlidesUtils.Console();

  public SlidesSource() {
    bind();
  }
  
  public Set<String> ids() {
    return snippets.keySet();
  }

  public Map<String, String> snippets = new LinkedHashMap<String, String>();
  public Map<String, String> docs = new LinkedHashMap<String, String>();
  public void exec(String id){};
  public void bind(){};
}

/**
 * The marker interface for deferred binding.
 * Only used in SlidesSources.
 */
interface SlidesSourceMarker {
}
