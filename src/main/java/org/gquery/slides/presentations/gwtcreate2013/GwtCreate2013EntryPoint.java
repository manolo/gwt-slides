package org.gquery.slides.presentations.gwtcreate2013;

import org.gquery.slides.client.Slides;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point for the GwtCreate presentation
 */
public class GwtCreate2013EntryPoint implements EntryPoint {

  public void onModuleLoad() {
     GwtCreate2013Presentation source = GWT.create(GwtCreate2013Presentation.class);
     new Slides(source);
  }

}
