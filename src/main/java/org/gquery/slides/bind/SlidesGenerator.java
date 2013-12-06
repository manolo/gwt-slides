package org.gquery.slides.bind;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;

import org.gquery.slides.client.SlidesSource;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class SlidesGenerator extends Generator {
  private static final String ID_SELECT = GQuery.class.getName() + ".$(\"#%id%\")";
  private static final String FUNCTION = "new " + Function.class.getName() + "(){public void f(){%call%();}}";

  HashMap<String, String> methodBodies = new HashMap<String, String>();
  HashMap<String, String> methodDoc = new HashMap<String, String>();
  HashMap<String, String> enterMethods = new HashMap<String, String>();
  HashMap<String, String> beforeMethods = new HashMap<String, String>();
  HashMap<String, String> execMethods = new HashMap<String, String>();
  HashMap<String, String> afterMethods = new HashMap<String, String>();
  HashMap<String, String> leaveMethods = new HashMap<String, String>();

  @Override
  public String generate(TreeLogger treeLogger,
      GeneratorContext generatorContext, String requestedClass) throws UnableToCompleteException {

    JClassType clazz =  generatorContext.getTypeOracle().findType(requestedClass);

    JClassType c = clazz.isClassOrInterface();
    String generatedPkgName = c.getPackage().getName();
    String generatedClzName = c.getName().replace('.', '_') + "_Slide";
    String generatedClzFullName = generatedPkgName + "." + generatedClzName;

    String file = generatedPkgName.replace(".", "/") + "/" + clazz.getName() + ".java";

    try {
      parseJavaFile(file);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    SourceWriter sw = getSourceWriter(treeLogger, generatorContext, generatedPkgName, generatedClzName, requestedClass);

    if (sw != null) {
      sw.println("public " + generatedClzName + "() {");
      for (String id : methodBodies.keySet()) {
        String s = methodBodies.get(id).replaceAll("\n", "\\\\\\n").replaceAll("\"", "\\\\\"");
        sw.println("  snippets.put(\"" + id + "\", \"" + s + "\");");

        s = methodDoc.get(id);
        if (s != null) {
          s = s.replaceAll("\n", "\\\\\\n").replaceAll("\"", "\\\\\"");
          sw.println("  docs.put(\"" + id + "\", \"" + s + "\");");
        }
      }

      sw.println("}");
      sw.println("public void exec(String id){\n try {");
      for (String id: execMethods.keySet()) {
        sw.println(" if(id.equals(\"" + id + "\")){ ");
        sw.indent();
        if (beforeMethods.containsKey(id)) {
          sw.println(beforeMethods.get(id) + "();");
        }
        sw.println(execMethods.get(id) + "();");
        if (afterMethods.containsKey(id)) {
          sw.println(afterMethods.get(id) + "();");
        }
        sw.println("\n}");
        sw.outdent();
      }
      sw.println(" } catch (Exception e) {e.printStackTrace();}\n}");

      sw.println("public void bind(){");
      sw.indent();

      for (String id : enterMethods.keySet()) {
        sw.println(ID_SELECT.replace("%id%", id)
            + ".bind(\"" + SlidesSource.ENTER_EVENT_NAME + "\","
            + FUNCTION.replace("%call%", enterMethods.get(id)) + ");");
      }
      for (String id : leaveMethods.keySet()) {
        sw.println(ID_SELECT.replace("%id%", id)
            + ".bind(\"" + SlidesSource.LEAVE_EVENT_NAME + "\","
            + FUNCTION.replace("%call%", leaveMethods.get(id)) + ");");
      }

      sw.outdent();
      sw.println("}");
      sw.commit(treeLogger);
    }

    return generatedClzFullName;
  }

  public void parseJavaFile(String file) throws ParseException {
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);

    // use japa.parser.ast to parse the source. We could use the classes in the
    // gwt compiler, but it seems this info is not available when running generators.
    CompilationUnit cu = null;
    cu = JavaParser.parse(in);

    VoidVisitorAdapter<?> a = new VoidVisitorAdapter<Object>() {
      public void visit(MethodDeclaration n, Object arg) {
        if (n.getName().startsWith("enter")){
          handleSpecialMethod(n, "enter", enterMethods);
        } else if (n.getName().startsWith("before")){
          handleSpecialMethod(n, "before", beforeMethods);
        } else if (n.getName().startsWith("after")){
          handleSpecialMethod(n, "after", afterMethods);
        } else if (n.getName().startsWith("leave")){
          handleSpecialMethod(n, "leave", leaveMethods);
        } else {
          handleSimpleMethod(n, arg);
        }
      }
    };
    a.visit(cu, null);
  }

  private void handleSimpleMethod(MethodDeclaration n, Object arg) {
    String id = n.getName().replaceFirst("^test","").toLowerCase();
    boolean noParameters = n.getParameters() == null;
    if (n.getBody() != null) {
      String s = n.getBody().toString();
      if (noParameters) {
        s = s.replaceFirst("^\\s*\\{ *\n*", "")
        .replaceFirst("\\s*\\}\\s*$", "")
        .replace("\n\n", "\n") // remove double new line after anonymous class definition
        .replace("// nl", "") // The comment forces a new line
        .replaceAll("(?m)^    ", "")
        .replaceAll("    ", "  ");

      } else {
        s = n.getName() + "()" + s;
      }
      methodBodies.put(id, s);
    }
    if (noParameters) {
      execMethods.put(id, n.getName());
    }
    if (n.getComment() != null) {
      methodDoc.put(id,
          n.getComment().toString()
          .replaceAll("(?m)^\\s*(/\\*\\*|\\*/|\\*)", "")
          .replaceAll("(?m)^\\s*-\\s(.*)$", "<li>$1</li>")
          .replaceAll("(?m)^\\s*@\\s(.+)\\s*$", "<h1>$1</h1>")
          .replaceAll("(?m)^\\s*@@\\s(.+)\\s*$", "<h4>$1</h4>")
          .replaceFirst("<li>", "<section><ul><li>")
          .replaceFirst("(?s)(.*)</li>", "$1</li></ul></section>\n")
          .replaceAll("(?m)    ", "")
          .trim()
      );
    }
  }

  private void handleSpecialMethod(MethodDeclaration n, String prefix, HashMap<String, String> map) {
    if (n.getParameters() == null) {
      String id = n.getName().replaceFirst(prefix,"").toLowerCase();
      map.put(id, n.getName());
    }
  }

  protected SourceWriter getSourceWriter(TreeLogger logger,
      GeneratorContext context, String packageName, String className,
      String clazz) {
    PrintWriter printWriter = context.tryCreate(logger, packageName, className);
    if (printWriter == null) {
      return null;
    }
    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
        packageName, className);
    composerFactory.setSuperclass(clazz);

    return composerFactory.createSourceWriter(context, printWriter);
  }
}
