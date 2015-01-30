package org.gquery.slides.presentations.gwtcreate2015;

import org.gquery.slides.client.Slides;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point for the presentation
 */
public class GwtCreate2015EntryPoint implements EntryPoint {


  public void onModuleLoad() {
    GwtCreate2015Presentation source = GWT.create(GwtCreate2015Presentation.class);
     new Slides(source);
  }

}
