package com.google.gwt.query.jsquery;

import static com.google.gwt.query.client.GQuery.window;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportAfterCreateMethod;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.ExportInstanceMethod;
import org.timepedia.exporter.client.ExportJsInitMethod;
import org.timepedia.exporter.client.ExportOverlay;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;
import org.timepedia.exporter.client.NoExport;

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
        return ExporterUtil.wrap((Object)g.css((Properties)o));
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
    @NoExport public abstract GQuery add(GQuery previousObject);
    @NoExport public abstract GQuery add(String selector);
    @NoExport public abstract GQuery addClass(String... classes);
    @NoExport public abstract GQuery after(GQuery query);
    @NoExport public abstract GQuery after(Node n);
    @NoExport public abstract GQuery after(String html);
    @NoExport public abstract GQuery andSelf();

  // public GQuery attr(Properties properties);
    public abstract String attr(String name);
  // public GQuery attr(String key, Function closure);
    @NoExport public abstract GQuery attr(String key, Object value);
    public abstract int size();

    @NoExport public abstract GQuery append(GQuery query);
    @NoExport public abstract GQuery append(Node n);
    @NoExport public abstract GQuery append(String html);
    @NoExport public abstract GQuery appendTo(GQuery other);
    @NoExport public abstract GQuery appendTo(Node n);
    @NoExport public abstract GQuery appendTo(String html);
  //    public abstract <T extends GQuery> T as(Class<T> plugin);

    @NoExport public abstract GQuery before(GQuery query);
    @NoExport public abstract GQuery before(Node n);
    @NoExport public abstract GQuery before(String html);
  // public GQuery bind(int eventbits, Object data, Function... funcs);
  // public GQuery bind(String eventType, Object data, Function... funcs);
    @ExportInstanceMethod
    public static GQuery bind(GQuery g, String events, Function func) {
      // FIXME: For some reason when highcharts bind the onresize event to window
      // we lose previous bound events
      return g.isEmpty() || g.get(0) == window ? g : g.bind(events, null, func);
    }
    @NoExport public abstract GQuery blur(Function... f);
    @NoExport public abstract GQuery change(Function... f);
    @NoExport public abstract GQuery children();
    @NoExport public abstract GQuery children(String... filters);
    @NoExport public abstract GQuery clearQueue();
    @NoExport public abstract GQuery clone();
    @NoExport public abstract GQuery clearQueue(String queueName);
    @NoExport public abstract GQuery click(Function... f);
    @NoExport public abstract GQuery closest(String selector);
  //    public abstract JsNamedArray<NodeList<Element>> closest(String[] selectors);
  //    public abstract JsNamedArray<NodeList<Element>> closest(String[] selectors, Node context);
    @NoExport public abstract GQuery closest(String selector, Node context);
    @NoExport public abstract GQuery contains(String text);
    @NoExport public abstract GQuery contents();
  //    @NoExport public abstract GQuery css(Properties properties);
  //    public abstract String css(String name);
  //    public abstract String css(String name, boolean force);
    @NoExport public abstract GQuery css(String prop, String val);
    public abstract double cur(String prop);
    public abstract double cur(String prop, boolean force);
    public abstract Object data(String name);
  //    public abstract <T> T data(String name, Class<T> clz);
    @NoExport public abstract GQuery data(String name, Object value);
    @NoExport public abstract GQuery dblclick(Function... f);

    @ExportInstanceMethod
    public static GQuery delay(GQuery g, int milliseconds) {
      return g.delay(milliseconds);
    }

    @NoExport public abstract GQuery delay(int milliseconds, Function... f);
    @NoExport public abstract GQuery delay(int milliseconds, String queueName, Function... f);
    @NoExport public abstract GQuery delegate(String selector, String eventType, Function... handlers);
    @NoExport public abstract GQuery delegate(String selector, String eventType, Object data, Function... handlers);
    @NoExport public abstract GQuery delegate(String selector, int eventbits, Function... handlers);
    @NoExport public abstract GQuery delegate(String selector, int eventbits, Object data, Function... handlers);
    @NoExport public abstract GQuery dequeue();
    @NoExport public abstract GQuery dequeue(String queueName);
    @NoExport public abstract GQuery detach();
    @NoExport public abstract GQuery detach(String filter);
    @NoExport public abstract GQuery die();
    @NoExport public abstract GQuery die(String eventName);
  //    @NoExport public abstract GQuery die(int eventbits);
    @NoExport public abstract GQuery each(Function... f);
  //    public abstract Element[] elements();
    @NoExport public abstract GQuery empty();
  //    @NoExport public abstract GQuery end();
    @NoExport public abstract GQuery eq(int pos);
    @NoExport public abstract GQuery error(Function... f);
    @NoExport public abstract GQuery fadeIn(Function... f);
    @NoExport public abstract GQuery fadeIn(int millisecs, Function... f);
    @NoExport public abstract GQuery fadeOut(Function... f);
    @NoExport public abstract GQuery fadeOut(int millisecs, Function... f);
    @NoExport public abstract GQuery fadeTo(double opacity, Function... f);
    @NoExport public abstract GQuery fadeTo(int millisecs, double opacity, Function... f);
    public abstract Effects fadeToggle(int millisecs, Function... f);
    @NoExport public abstract GQuery filter(Predicate filterFn);
  //    @NoExport public abstract GQuery filter(String... filters);
    @NoExport public abstract GQuery find(String... filters);
    @NoExport public abstract GQuery first();
    // TODO: focusIn
    // TODO: focusOut
    @NoExport public abstract GQuery focus(Function... f);
    public abstract Element get(int i);
    public abstract Node getContext();
  //    @NoExport public abstract GQuery getPreviousObject();
  //    public abstract String getSelector();
  //    @NoExport public abstract GQuery gt(int pos);
    @NoExport public abstract GQuery has(String selector);
    @NoExport public abstract GQuery has(Element elem);
    public abstract boolean hasClass(String... classes);
    public abstract int height();
    public abstract GQuery height(int height);
    public abstract GQuery height(String height);
    @NoExport public abstract GQuery hide();
    @NoExport public abstract GQuery hover(Function fover, Function fout);
    public abstract String html();
    @NoExport public abstract GQuery html(String html);
  //    public abstract String id();
  //    @NoExport public abstract GQuery id(String id);
    public abstract int index(Element element);
    // TODO: init
    public abstract int innerHeight();
    public abstract int innerWidth();
    @NoExport public abstract GQuery insertAfter(Element elem);
    @NoExport public abstract GQuery insertAfter(GQuery query);
    @NoExport public abstract GQuery insertAfter(String selector);
    @NoExport public abstract GQuery insertBefore(Element item);
    @NoExport public abstract GQuery insertBefore(GQuery query);
    @NoExport public abstract GQuery insertBefore(String selector);
    public abstract boolean is(String... filters);
  //    public abstract boolean isEmpty();
    @NoExport public abstract GQuery keydown(Function... f);
    @NoExport public abstract GQuery keydown(int key);
    @NoExport public abstract GQuery keypress(Function... f);
    @NoExport public abstract GQuery keypress(int key);
    @NoExport public abstract GQuery keyup(Function... f);
    @NoExport public abstract GQuery keyup(int key);
    @NoExport public abstract GQuery last();
  //    public abstract int left();
  //    public abstract int length();
    @NoExport public abstract GQuery live(String eventName, Function... funcs);
  //    @NoExport public abstract GQuery live(int eventbits, Function... funcs);
  //    @NoExport public abstract GQuery live(int eventbits, Object data, Function... funcs);
    @NoExport public abstract GQuery live(String eventName, Object data, Function... funcs);
  //    TODO: @NoExport public abstract GQuery load(Function f);
  //    @NoExport public abstract GQuery lt(int pos);
  //    TODO: public abstract <W> List<W> map(Function f);
    @NoExport public abstract GQuery mousedown(Function... f);
    @NoExport public abstract GQuery mouseenter(Function... f);
    @NoExport public abstract GQuery mouseleave(Function... f);
    @NoExport public abstract GQuery mousemove(Function... f);
    @NoExport public abstract GQuery mouseout(Function... f);
    @NoExport public abstract GQuery mouseover(Function... f);
    @NoExport public abstract GQuery mouseup(Function... f);
    @NoExport public abstract GQuery next();
    @NoExport public abstract GQuery next(String... selectors);
    @NoExport public abstract GQuery nextAll();
    @NoExport public abstract GQuery nextUntil(String selector);
    @NoExport public abstract GQuery not(Element elem);
    @NoExport public abstract GQuery not(GQuery gq);
    @NoExport public abstract GQuery not(String... filters);
    @NoExport public abstract GQuery offsetParent();
    @NoExport public abstract GQuery one(int eventbits, Object data, Function f);
    public abstract int outerHeight();
    public abstract int outerHeight(boolean includeMargin);
    public abstract int outerWidth();
    public abstract int outerWidth(boolean includeMargin);
    @NoExport public abstract GQuery parent();
    @NoExport public abstract GQuery parent(String... filters);
    @NoExport public abstract GQuery parents();
    @NoExport public abstract GQuery parents(String... filters);
    @NoExport public abstract GQuery parentsUntil(String selector);
    @NoExport public abstract GQuery prepend(GQuery query);
    @NoExport public abstract GQuery prepend(Node n);
    @NoExport public abstract GQuery prepend(String html);
    @NoExport public abstract GQuery prependTo(GQuery other);
    @NoExport public abstract GQuery prependTo(Node n);
    @NoExport public abstract GQuery prependTo(String html);
    @NoExport public abstract GQuery prev();
    @NoExport public abstract GQuery prev(String... selectors);
    @NoExport public abstract GQuery prevAll();
    @NoExport public abstract GQuery prevUntil(String selector);
    // TODO: pushStack
    public abstract Object prop(String key);
    @NoExport public abstract GQuery prop(String key, Object value);
    @NoExport public abstract GQuery prop(String key, Function closure);
    public abstract Promise promise();
    public abstract int queue();

    public abstract int queue(String queueName);
    @NoExport public abstract GQuery queue(Function... f);
    @NoExport public abstract GQuery queue(String queueName, Function... f);
    @NoExport public abstract GQuery remove();
    @NoExport public abstract GQuery remove(String filter);
    @NoExport public abstract GQuery removeAttr(String key);
    @NoExport public abstract GQuery removeClass(String... classes);
    @NoExport public abstract GQuery removeData(String name);
    @NoExport public abstract GQuery removeProp(String name);
    @NoExport public abstract GQuery replaceAll(Element elem);
    @NoExport public abstract GQuery replaceAll(GQuery target);
    @NoExport public abstract GQuery replaceAll(String selector);
    @NoExport public abstract GQuery replaceWith(Element elem);
    @NoExport public abstract GQuery replaceWith(GQuery target);
    @NoExport public abstract GQuery replaceWith(String html);
    @NoExport public abstract GQuery resize(Function... f);
  //    public abstract void restoreCssAttrs(String... cssProps);
  //    public abstract void resize(Function f);
  //    public abstract void saveCssAttrs(String... cssProps);
    @NoExport public abstract GQuery scroll(Function... f);
  //    @NoExport public abstract GQuery scrollIntoView();
  //    @NoExport public abstract GQuery scrollIntoView(boolean ensure);
    public abstract int scrollLeft();
    @NoExport public abstract GQuery scrollLeft(int left);
  //    @NoExport public abstract GQuery scrollTo(int left, int top);
    public abstract int scrollTop();
    @NoExport public abstract GQuery scrollTop(int top);
    @NoExport public abstract GQuery select();
    // TODO: selector
    // TODO: selector
    // TODO: serialize
    // TODO: serializeArray
    // @NoExport public abstract GQuery setArray(NodeList<Element> list);
    // public abstract void setPreviousObject(GQuery previousObject);
    // @NoExport public abstract GQuery setSelector(String selector);
    @NoExport public abstract GQuery show();
    @NoExport public abstract GQuery siblings();
    @NoExport public abstract GQuery siblings(String... selectors);
    @NoExport public abstract GQuery slice(int start, int end);
    public abstract Effects slideDown(Function... f);
    public abstract Effects slideDown(int millisecs, Function... f);
    public abstract Effects slideToggle(int millisecs, Function... f);
    public abstract Effects slideUp(Function... f);
    public abstract Effects slideUp(int millisecs, Function... f);
    public abstract GQuery stop();
    public abstract GQuery stop(boolean clearQueue);
    public abstract GQuery stop(boolean clearQueue, boolean jumpToEnd);
    @NoExport public abstract GQuery submit(Function... funcs);
    public abstract String text();
    @NoExport public abstract GQuery text(String txt);
    @NoExport public abstract GQuery toggle();
    @NoExport public abstract GQuery toggle(Function... fn);
    @NoExport public abstract GQuery toggleClass(String... classes);
    @NoExport public abstract GQuery toggleClass(String clz, boolean addOrRemove);
    public abstract String toString(boolean pretty);
  //    @NoExport public abstract GQuery unbind(String eventName);
  //    @NoExport public abstract GQuery unbind(String eventName, Function f);
    @NoExport public abstract GQuery undelegate();
    @NoExport public abstract GQuery undelegate(String selector);
    @NoExport public abstract GQuery undelegate(String selector, String eventName);
  //    @NoExport public abstract GQuery undelegate(String selector, int eventBit);
  //    public abstract JsNodeArray unique(NodeList<Element> result);
    // TODO: unload
    @NoExport public abstract GQuery unwrap();
    public abstract String val();
    @NoExport public abstract GQuery val(String... values);
  //    public abstract String[] vals();
  //    public abstract boolean isVisible();
    public abstract int width();
    public abstract GQuery width(int width);
    @NoExport public abstract GQuery wrap(Element elem);
    @NoExport public abstract GQuery wrap(GQuery query);
    @NoExport public abstract GQuery wrap(String html);
    @NoExport public abstract GQuery wrapAll(Element elem);
    @NoExport public abstract GQuery wrapAll(GQuery query);
    @NoExport public abstract GQuery wrapAll(String html);
    @NoExport public abstract GQuery wrapInner(Element elem);
    @NoExport public abstract GQuery wrapInner(GQuery query);
    @NoExport public abstract GQuery wrapInner(String html);
  }
}
