package org.gquery.slides.client;

import java.util.HashMap;

/**
 * 
 * Extend this class to create a set of Slides using the SlidesGenerator.
 * 
 */
public abstract class SlidesSource implements SlidesSourceMarker {
  public static final String ENTER_EVENT_NAME = "slideEnter";
  public static final String LEAVE_EVENT_NAME = "slideLeave";

  public SlidesSource() {
    bind();
  }

  protected HashMap<String, String> snippets = new HashMap<String, String>();
  protected HashMap<String, String> docs = new HashMap<String, String>();
  public void exec(String id){};
  public void bind(){};
}

/**
 * The marker interface for deferred binding.
 * Only used in SlidesSources.
 */
interface SlidesSourceMarker {
}
