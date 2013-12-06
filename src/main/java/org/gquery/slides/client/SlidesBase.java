package org.gquery.slides.client;

import java.util.HashMap;

public abstract class SlidesBase implements SlidesSource {
  protected HashMap<String, String> snippets = new HashMap<String, String>();
  protected HashMap<String, String> docs = new HashMap<String, String>();
  public void exec(String id){};
  public void bind(){};
}
