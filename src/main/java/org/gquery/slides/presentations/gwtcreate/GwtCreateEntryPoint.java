package org.gquery.slides.presentations.gwtcreate;

import org.gquery.slides.client.Slides;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point for the GwtCreate presentation
 */
public class GwtCreateEntryPoint implements EntryPoint {

  public void onModuleLoad() {
//    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//      public void onUncaughtException(Throwable e) {
//        if (!(e instanceof ClassCastException)) {
//          e.printStackTrace();
//        }
//      }
//    });

     GwtCreatePresentation source = GWT.create(GwtCreatePresentation.class);
     new Slides(source);
  }

}
