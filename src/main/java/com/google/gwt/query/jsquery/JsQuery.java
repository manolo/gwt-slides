package com.google.gwt.query.jsquery;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportAfterCreateMethod;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.ExportInstanceMethod;
import org.timepedia.exporter.client.ExportJsInitMethod;
import org.timepedia.exporter.client.ExportOverlay;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.GQuery.Offset;
import com.google.gwt.query.client.Predicate;
import com.google.gwt.query.client.Promise;
import com.google.gwt.query.client.Promise.Deferred;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.js.JsCache;
import com.google.gwt.query.client.js.JsUtils;
import com.google.gwt.query.client.plugins.Effects;
import com.google.gwt.query.client.plugins.Events;
import com.google.gwt.query.client.plugins.ajax.Ajax;
import com.google.gwt.query.client.plugins.effects.PropertiesAnimation.Easing;
import com.google.gwt.query.client.plugins.effects.PropertiesAnimation.EasingCurve;
import com.google.gwt.user.client.Event;

/**
 * Class used to expose GQuery methods to Javascript using gwt-exporter
 * annotations and other tweaks.
 *
 * We prefer to overlay the original GQuery object instead of adding
 * gwt-exporter annotations to the core project.
 *
 * Because of the differences between java and js apis, we need to
 * replace some methods in order to deal with complex cases.
 *
 * Exported classes:
 *
 *   JsQuery: used to export static Methods an run after create scripts
 *   GQueryOverlay: used to export gQuery prototype
 *   FunctionOverlay: used to convert js-functions to gQuery-Function
 *   PredicateOverlay: export Predicates
 *   DeferredOverlay: export Deferred interface
 *   PromiseOverlay : export Promise interface
 */
@Export(value="jsQuery", all=false)
public class JsQuery implements Exportable {

  /**
   * After exporting all classes under the c.g.g.q.j name space configure
   * necessary links so as they seem similar to jQuery.
   *
   * It also adds some JS hacks or stub methods when we don't offer the feature
   * via exported methods.
   */
  @ExportAfterCreateMethod
  public static native void afterCreate() /*-{
    var window = $wnd;
    var document = $doc;

    var ns = $wnd.com.google.gwt.query.jsquery;
    var obj = ns.jsQuery;
    var fnc = obj.$;
    for(i in obj) fnc[i] = obj[i];

    fnc.fn = ns.gQuery.prototype;
    fnc.fn.extend = fnc.extend;

    $ = $wnd.$ = $wnd.jQuery = $wnd.gQuery = fnc;

    $.fx = {};
    $.fx.step = {};
    $.cssHooks = {};
    $.cssHooks.opacity = {};
    $.Tween = {};
    $.Tween.propHooks = {};

    $.expr = function(){};
    $.Event = function(e){return {type: e}};
    $.each = function(arr, f) {return Array.prototype.forEach.call(arr, f)};
    $.inArray = function(arr, el, i) {return  Array.prototype.indexOf.call(arr, el, i)};
  }-*/;

  /**
   * jQuery plugins use this intensively, but we don't have it in gQuery since
   * we use java specific mechanisms.
   */
  public static JavaScriptObject extend(Object... objs) {
    int i = 0, l = objs.length;
    boolean deep = false;
    JavaScriptObject ctx = null;
    Object target = objs[i];
    if (target instanceof Boolean) {
      deep = (Boolean) target;
      if (l == 1)
        return ctx;
      target = objs[i++];
    }
    if (l - i == 1) {
      i--;
    } else {
      ctx = (JavaScriptObject) target;
    }

    for (++i; i < l; i++) {
      if (objs[i] != null) {
        ctx = extendImpl(deep, ctx, objs[i]);
      }
    }
    return ctx;
  }

  private static native JavaScriptObject extendImpl(boolean deep,
      JavaScriptObject ctx, Object s) /*-{
        console.log("extend" + $wnd.$.fn);
        var d = ctx ? ctx : $wnd.$.fn.prototype || {};
        for (k in s) {
            d[k] = s[k];
            if (!ctx)
                $wnd.$[k] = s[k];
        }
        return d;
  }-*/;

  public static GQuery $(Object o) {
    return GQuery.$(o);
  }

  public static GQuery $(String s, Element ctx) {
    return GQuery.$(s, ctx);
  }

  public static boolean isArray(JavaScriptObject o) {
    return JsUtils.isArray(o);
  }

  public static Deferred Deferred() {
    return GQuery.Deferred();
  }

  public static Promise getScript(String url) {
    return Ajax.getScript(url);
  }

  public static GQuery ready(GQuery g, Function f) {
    f.fe();
    return g;
  }

  public static Promise loadScript(String url) {
    return Ajax.loadScript(url);
  }

  @Export("gFunction")
  @ExportClosure()
  public interface FunctionOverlay extends ExportOverlay<Function>  {
    public void f();
    public boolean f(Event e);
    public Object f(Element e, int i);
  }

  @Export("gPredicate")
  @ExportClosure()
  public interface PredicateOverlay extends ExportOverlay<Predicate>  {
    public boolean f(Element e, int i);
  }

  @Export("gPromise")
  public interface PromiseOverlay extends ExportOverlay<Promise>  {
    Promise always(Function... o);
    Promise done(Function... o);
    Promise fail(Function... o);
    Promise pipe(Function... f);
    Promise progress(Function... o);
    String state();
    Promise then(Function... f);
    boolean isResolved();
    boolean isRejected();
  }

  @Export("gDeferred")
  public interface DeferredOverlay extends ExportOverlay<Promise.Deferred>  {
    Deferred notify(Object... o);
    Promise promise();
    Deferred reject(Object... o);
    Deferred resolve(Object... o);
  }

  @Export("gQuery")
  public static abstract class GQueryOverlay implements ExportOverlay<GQuery> {

    /**
     * Although we dont use it, gwt-exporter needs a default constructor
     */
    private GQueryOverlay(){}

    /**
     * In js a GQuery object represents a Nodelist.
     * gwt-exporter will use the object returned by get() to wrap
     * the GQuery object
     */
    @ExportJsInitMethod
    public abstract NodeList<Element> get();

    @ExportInstanceMethod
    // TODO: normally plugins adds new easing functions to jquery.easing array
    public static GQuery animate(GQuery g, Object stringOrProperties, int duration, String easing, Function... funcs) {
      Easing e = EasingCurve.valueOf(easing);
      return g.animate(stringOrProperties, duration, e);
    }
    // public GQuery animate(Object stringOrProperties, int duration, Easing easing, Function... funcs);
    public abstract GQuery animate(Object stringOrProperties, Function... funcs);
    public abstract GQuery animate(Object stringOrProperties, int duration, Function... funcs);

    @ExportInstanceMethod
    public static Object css(GQuery g, Object o) {
      if (o instanceof String) {
        return g.css((String)o, false);
      } else {
        return ExporterUtil.wrap(g.css((Properties)o));
      }
    }

    @ExportInstanceMethod
    public static GQuery css(GQuery g, String k, Object v) {
      return g.css(k, String.valueOf(v));
    }

    @ExportInstanceMethod
    public static JavaScriptObject offset(GQuery instance) {
      Offset o = instance.offset();
      return Properties.create("left: " + o.left + ", top:" + o.top);
    }

    @ExportInstanceMethod
    public static GQuery offset(GQuery instance, JsCache o) {
      return instance.offset(new Offset(o.getInt("left"), o.getInt("top")));
    }

    @ExportInstanceMethod
    public static Element[] toArray(GQuery g) {
      return g.elements();
    }

    @ExportInstanceMethod
    public static GQuery trigger(GQuery g, String name, Function f) {
      g.as(Events.Events).triggerHtmlEvent(name, f);
      return g;
    }

    @ExportInstanceMethod
    public static GQuery unbind(GQuery g, String s, Function o) {
      return g.unbind(s);
    }

    public abstract String toString();
    public abstract GQuery add(GQuery previousObject);
    public abstract GQuery add(String selector);
    public abstract GQuery addClass(String... classes);
    public abstract GQuery after(GQuery query);
    public abstract GQuery after(Node n);
    public abstract GQuery after(String html);
    public abstract GQuery andSelf();

  // public GQuery attr(Properties properties);
    public abstract String attr(String name);
  // public GQuery attr(String key, Function closure);
    public abstract GQuery attr(String key, Object value);
    public abstract int size();

    public abstract GQuery append(GQuery query);
    public abstract GQuery append(Node n);
    public abstract GQuery append(String html);
    public abstract GQuery appendTo(GQuery other);
    public abstract GQuery appendTo(Node n);
    public abstract GQuery appendTo(String html);
  //    public abstract <T extends GQuery> T as(Class<T> plugin);

    public abstract GQuery before(GQuery query);
    public abstract GQuery before(Node n);
    public abstract GQuery before(String html);
  // public GQuery bind(int eventbits, Object data, Function... funcs);
  // public GQuery bind(String eventType, Object data, Function... funcs);
    @ExportInstanceMethod
    public static GQuery bind(GQuery g, String events, Function func) {
      return g.bind(events, null, func);
    }
    public abstract GQuery blur(Function... f);
    public abstract GQuery change(Function... f);
    public abstract GQuery children();
    public abstract GQuery children(String... filters);
    public abstract GQuery clearQueue();
    public abstract GQuery clone();
    public abstract GQuery clearQueue(String queueName);
    public abstract GQuery click(Function... f);
    public abstract GQuery closest(String selector);
  //    public abstract JsNamedArray<NodeList<Element>> closest(String[] selectors);
  //    public abstract JsNamedArray<NodeList<Element>> closest(String[] selectors, Node context);
    public abstract GQuery closest(String selector, Node context);
    public abstract GQuery contains(String text);
    public abstract GQuery contents();
  //    public abstract GQuery css(Properties properties);
  //    public abstract String css(String name);
  //    public abstract String css(String name, boolean force);
    public abstract GQuery css(String prop, String val);
    public abstract double cur(String prop);
    public abstract double cur(String prop, boolean force);
    public abstract Object data(String name);
  //    public abstract <T> T data(String name, Class<T> clz);
    public abstract GQuery data(String name, Object value);
    public abstract GQuery dblclick(Function... f);

    @ExportInstanceMethod
    public static GQuery delay(GQuery g, int milliseconds) {
      return g.delay(milliseconds);
    }

    public abstract GQuery delay(int milliseconds, Function... f);
    public abstract GQuery delay(int milliseconds, String queueName, Function... f);
    public abstract GQuery delegate(String selector, String eventType, Function... handlers);
    public abstract GQuery delegate(String selector, String eventType, Object data, Function... handlers);
    public abstract GQuery delegate(String selector, int eventbits, Function... handlers);
    public abstract GQuery delegate(String selector, int eventbits, Object data, Function... handlers);
    public abstract GQuery dequeue();
    public abstract GQuery dequeue(String queueName);
    public abstract GQuery detach();
    public abstract GQuery detach(String filter);
    public abstract GQuery die();
    public abstract GQuery die(String eventName);
  //    public abstract GQuery die(int eventbits);
    public abstract GQuery each(Function... f);
  //    public abstract Element[] elements();
    public abstract GQuery empty();
  //    public abstract GQuery end();
    public abstract GQuery eq(int pos);
    public abstract GQuery error(Function... f);
    public abstract GQuery fadeIn(Function... f);
    public abstract GQuery fadeIn(int millisecs, Function... f);
    public abstract GQuery fadeOut(Function... f);
    public abstract GQuery fadeOut(int millisecs, Function... f);
    public abstract GQuery fadeTo(double opacity, Function... f);
    public abstract GQuery fadeTo(int millisecs, double opacity, Function... f);
    public abstract Effects fadeToggle(int millisecs, Function... f);
    public abstract GQuery filter(Predicate filterFn);
  //    public abstract GQuery filter(String... filters);
    public abstract GQuery find(String... filters);
    public abstract GQuery first();
    // TODO: focusIn
    // TODO: focusOut
    public abstract GQuery focus(Function... f);
    public abstract Element get(int i);
    public abstract Node getContext();
  //    public abstract GQuery getPreviousObject();
  //    public abstract String getSelector();
  //    public abstract GQuery gt(int pos);
    public abstract GQuery has(String selector);
    public abstract GQuery has(Element elem);
    public abstract boolean hasClass(String... classes);
    public abstract int height();
    public abstract GQuery height(int height);
    public abstract GQuery height(String height);
    public abstract GQuery hide();
    public abstract GQuery hover(Function fover, Function fout);
    public abstract String html();
    public abstract GQuery html(String html);
  //    public abstract String id();
  //    public abstract GQuery id(String id);
    public abstract int index(Element element);
    // TODO: init
    public abstract int innerHeight();
    public abstract int innerWidth();
    public abstract GQuery insertAfter(Element elem);
    public abstract GQuery insertAfter(GQuery query);
    public abstract GQuery insertAfter(String selector);
    public abstract GQuery insertBefore(Element item);
    public abstract GQuery insertBefore(GQuery query);
    public abstract GQuery insertBefore(String selector);
    public abstract boolean is(String... filters);
  //    public abstract boolean isEmpty();
    public abstract GQuery keydown(Function... f);
    public abstract GQuery keydown(int key);
    public abstract GQuery keypress(Function... f);
    public abstract GQuery keypress(int key);
    public abstract GQuery keyup(Function... f);
    public abstract GQuery keyup(int key);
    public abstract GQuery last();
  //    public abstract int left();
  //    public abstract int length();
    public abstract GQuery live(String eventName, Function... funcs);
  //    public abstract GQuery live(int eventbits, Function... funcs);
  //    public abstract GQuery live(int eventbits, Object data, Function... funcs);
    public abstract GQuery live(String eventName, Object data, Function... funcs);
  //    TODO: public abstract GQuery load(Function f);
  //    public abstract GQuery lt(int pos);
  //    TODO: public abstract <W> List<W> map(Function f);
    public abstract GQuery mousedown(Function... f);
    public abstract GQuery mouseenter(Function... f);
    public abstract GQuery mouseleave(Function... f);
    public abstract GQuery mousemove(Function... f);
    public abstract GQuery mouseout(Function... f);
    public abstract GQuery mouseover(Function... f);
    public abstract GQuery mouseup(Function... f);
    public abstract GQuery next();
    public abstract GQuery next(String... selectors);
    public abstract GQuery nextAll();
    public abstract GQuery nextUntil(String selector);
    public abstract GQuery not(Element elem);
    public abstract GQuery not(GQuery gq);
    public abstract GQuery not(String... filters);
    public abstract GQuery offsetParent();
    public abstract GQuery one(int eventbits, Object data, Function f);
    public abstract int outerHeight();
    public abstract int outerHeight(boolean includeMargin);
    public abstract int outerWidth();
    public abstract int outerWidth(boolean includeMargin);
    public abstract GQuery parent();
    public abstract GQuery parent(String... filters);
    public abstract GQuery parents();
    public abstract GQuery parents(String... filters);
    public abstract GQuery parentsUntil(String selector);
    public abstract GQuery prepend(GQuery query);
    public abstract GQuery prepend(Node n);
    public abstract GQuery prepend(String html);
    public abstract GQuery prependTo(GQuery other);
    public abstract GQuery prependTo(Node n);
    public abstract GQuery prependTo(String html);
    public abstract GQuery prev();
    public abstract GQuery prev(String... selectors);
    public abstract GQuery prevAll();
    public abstract GQuery prevUntil(String selector);
    // TODO: pushStack
    public abstract Object prop(String key);
    public abstract GQuery prop(String key, Object value);
    public abstract GQuery prop(String key, Function closure);
    public abstract Promise promise();
    public abstract int queue();

    public abstract int queue(String queueName);
    public abstract GQuery queue(Function... f);
    public abstract GQuery queue(String queueName, Function... f);
    public abstract GQuery remove();
    public abstract GQuery remove(String filter);
    public abstract GQuery removeAttr(String key);
    public abstract GQuery removeClass(String... classes);
    public abstract GQuery removeData(String name);
    public abstract GQuery removeProp(String name);
    public abstract GQuery replaceAll(Element elem);
    public abstract GQuery replaceAll(GQuery target);
    public abstract GQuery replaceAll(String selector);
    public abstract GQuery replaceWith(Element elem);
    public abstract GQuery replaceWith(GQuery target);
    public abstract GQuery replaceWith(String html);
    public abstract GQuery resize(Function... f);
  //    public abstract void restoreCssAttrs(String... cssProps);
  //    public abstract void resize(Function f);
  //    public abstract void saveCssAttrs(String... cssProps);
    public abstract GQuery scroll(Function... f);
  //    public abstract GQuery scrollIntoView();
  //    public abstract GQuery scrollIntoView(boolean ensure);
    public abstract int scrollLeft();
    public abstract GQuery scrollLeft(int left);
  //    public abstract GQuery scrollTo(int left, int top);
    public abstract int scrollTop();
    public abstract GQuery scrollTop(int top);
    public abstract GQuery select();
    // TODO: selector
    // TODO: selector
    // TODO: serialize
    // TODO: serializeArray
    // public abstract GQuery setArray(NodeList<Element> list);
    // public abstract void setPreviousObject(GQuery previousObject);
    // public abstract GQuery setSelector(String selector);
    public abstract GQuery show();
    public abstract GQuery siblings();
    public abstract GQuery siblings(String... selectors);
    public abstract GQuery slice(int start, int end);
    public abstract Effects slideDown(Function... f);
    public abstract Effects slideDown(int millisecs, Function... f);
    public abstract Effects slideToggle(int millisecs, Function... f);
    public abstract Effects slideUp(Function... f);
    public abstract Effects slideUp(int millisecs, Function... f);
    public abstract GQuery stop();
    public abstract GQuery stop(boolean clearQueue);
    public abstract GQuery stop(boolean clearQueue, boolean jumpToEnd);
    public abstract GQuery submit(Function... funcs);
    public abstract String text();
    public abstract GQuery text(String txt);
    public abstract GQuery toggle();
    public abstract GQuery toggle(Function... fn);
    public abstract GQuery toggleClass(String... classes);
    public abstract GQuery toggleClass(String clz, boolean addOrRemove);
    public abstract String toString(boolean pretty);
  //    public abstract GQuery unbind(String eventName);
  //    public abstract GQuery unbind(String eventName, Function f);
    public abstract GQuery undelegate();
    public abstract GQuery undelegate(String selector);
    public abstract GQuery undelegate(String selector, String eventName);
  //    public abstract GQuery undelegate(String selector, int eventBit);
  //    public abstract JsNodeArray unique(NodeList<Element> result);
    // TODO: unload
    public abstract GQuery unwrap();
    public abstract String val();
    public abstract GQuery val(String... values);
  //    public abstract String[] vals();
  //    public abstract boolean isVisible();
    public abstract int width();
    public abstract GQuery width(int width);
    public abstract GQuery wrap(Element elem);
    public abstract GQuery wrap(GQuery query);
    public abstract GQuery wrap(String html);
    public abstract GQuery wrapAll(Element elem);
    public abstract GQuery wrapAll(GQuery query);
    public abstract GQuery wrapAll(String html);
    public abstract GQuery wrapInner(Element elem);
    public abstract GQuery wrapInner(GQuery query);
    public abstract GQuery wrapInner(String html);
  }
}
