package org.gquery.slides.client;


/**
 * A very simple prettify class for java code which can be run in gwt.
 */
public class Prettify {

  public static final String FLOW_CONTROL_KEYWORDS = "break|continue|do|else|for|if|return|while";
  public static final String C_KEYWORDS = FLOW_CONTROL_KEYWORDS + "|" + "auto|case|char|const|default|"
          + "[Dd]ouble|enum|extern|float|goto|inline|int|[Ll]ong|register|[Sh]hort|signed|"
          + "sizeof|static|struct|switch|typedef|union|unsigned|void|volatile";
  public static final String COMMON_KEYWORDS = C_KEYWORDS + "|" + "catch|class|delete|false|import|"
          + "new|operator|private|protected|public|this|throw|true|try|typeof";
  public static final String JAVA_KEYWORDS = COMMON_KEYWORDS + "|"
      + "abstract|assert|boolean|byte|extends|final|finally|implements|import|"
      + "instanceof|interface|null|native|package|strictfp|super|synchronized|"
      + "throws|transient|String|Integer|Character";

  public static final String GQUERY_KEYWORDS = JAVA_KEYWORDS + "|"
      + "GWT|GQuery|Function|Promise|PromiseFunction|FunctionDeferred";

  public static final String CONTROL_CHARS = "([\\{\\}\\(\\)\\[\\]\\;\\,\\+\\-\\*\\|\\&]+)";

  public static final String JAVA_ANNOTATIONS = "(@)([A-Z]\\w+)";

  public static String prettify(String s) {
    s = s
          .replaceAll("<", "&lt;")
          .replaceAll(">", "&gt;")
          .replaceAll("([^\\w])(" + GQUERY_KEYWORDS + ")([^\\w])", "$1_$2$3")
          .replaceAll("([^\\w])(" + GQUERY_KEYWORDS + ")([^\\w])", "$1_$2$3")
          .replaceAll("(^|_)(" + GQUERY_KEYWORDS + ")([^\\w])", "<span class='jKey'>$2</span>$3")
          .replaceAll(JAVA_ANNOTATIONS, "$1<span class='jAnn'>$2</span>")
          .replaceAll("([^\\w])(\".*?[^\\\\]\")([^\\w])", "$1<span class='jLiteral'>$2</span>$3")
          .replaceAll("()([\\w\\$]+)(\\(.*?)", "$1<span class='jMethod'>$2</span>$3")
          .replaceAll("(//.+?)\n", "<span class='jComment'>$1\n</span>");

    return s;
  }
}
