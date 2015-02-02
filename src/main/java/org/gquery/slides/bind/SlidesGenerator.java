package org.gquery.slides.bind;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.gquery.slides.client.SlidesSource;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
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

  Set<String> slideIds = new HashSet<String>();
  Map<String, String> methodBodies = new LinkedHashMap<String, String>();
  Map<String, String> methodDoc = new LinkedHashMap<String, String>();
  Map<String, String> enterMethods = new LinkedHashMap<String, String>();
  Map<String, String> beforeMethods = new LinkedHashMap<String, String>();
  Map<String, String> execMethods = new LinkedHashMap<String, String>();
  Map<String, String> afterMethods = new LinkedHashMap<String, String>();
  Map<String, String> leaveMethods = new LinkedHashMap<String, String>();
  Map<String, String> innerClasses = new LinkedHashMap<String, String>();

  @Override
  public String generate(TreeLogger treeLogger,
      GeneratorContext generatorContext, String requestedClass) throws UnableToCompleteException {

    JClassType clazz =  generatorContext.getTypeOracle().findType(requestedClass);
    
    if (clazz.isAbstract() || clazz.isInterface() != null) {
      return null;
    }

    JClassType c = clazz.isClassOrInterface();
    String generatedPkgName = c.getPackage().getName();
    String generatedClzName = c.getName().replace('.', '_') + "_Slide";
    String generatedClzFullName = generatedPkgName + "." + generatedClzName;
    
    while (clazz != null && clazz.isInterface() == null) {
      String file = clazz.getPackage().getName().replace(".", "/") + "/" + clazz.getName() + ".java";
      try {
        parseJavaFile(file);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      clazz = clazz.getSuperclass();
    }

    SourceWriter sw = getSourceWriter(treeLogger, generatorContext, generatedPkgName, generatedClzName, requestedClass);

    if (sw != null) {
      sw.println("public " + generatedClzName + "() {");
      for (String id : methodBodies.keySet()) {
        String s = methodBodies.get(id);
        s = resolveIncludes(s);
        sw.println("  snippets.put(\"" + id + "\", \"" + escape(s) + "\");");

        s = methodDoc.get(id);
        if (s != null) {
          s = resolveIncludes(s);
          sw.println("  docs.put(\"" + id + "\", \"" + escape(s) + "\");");
        }
      }
      for (String id : slideIds) {
        sw.println("  slideIds.add(\"" + id + "\");");
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
    if (in == null) {
      return;
    }

    // use japa.parser.ast to parse the source. We could use the classes in the
    // gwt compiler, but it seems this info is not available when running generators.
    CompilationUnit cu = null;
    cu = JavaParser.parse(in);

    final VoidVisitorAdapter<?> memberVisitor = new VoidVisitorAdapter<Object>() {
      public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        innerClasses.put(n.getName(), formatBody(n.toString()));
      }

      public void visit(MethodDeclaration n, Object arg) {
        if (n.getName().startsWith("enter")) {
          handleSpecialMethod(n, "enter", enterMethods);
        } else if (n.getName().startsWith("before")) {
          handleSpecialMethod(n, "before", beforeMethods);
        } else if (n.getName().startsWith("after")) {
          handleSpecialMethod(n, "after", afterMethods);
        } else if (n.getName().startsWith("leave")) {
          handleSpecialMethod(n, "leave", leaveMethods);
        } else {
          handleSimpleMethod(n, arg);
        }
      }
    };

    final VoidVisitorAdapter<?> mainVisitor = new VoidVisitorAdapter<Object>() {
      public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        for (BodyDeclaration b : n.getMembers()) {
          b.accept(memberVisitor, null);
        }
      }
    };

    mainVisitor.visit(cu, null);
  }

  private void handleSimpleMethod(MethodDeclaration n, Object arg) {
    String id = n.getName().replaceFirst("^(test|slide|case)","").toLowerCase();
    String doc = n.getComment() == null ? null : n.getComment().toString();
    if (doc != null) {
      if (doc.contains("@disabled")) {
        return;
      }
      methodDoc.put(id, formatComment(n.getComment().toString()));
    }
    boolean noParameters = n.getParameters() == null;
    if (n.getBody() != null) {
      String s;
      if (noParameters) {
        // Body comes wrapped by curly brackets.
        s = n.getBody().toString()
          // remove method declaration line
          .replaceFirst("^\\s*\\{ *\n*", "")
          // remove last close method line
          .replaceFirst("\\s*\\}\\s*$", "")
          // remove 4 spaces indentation
          .replaceAll("(?m)^    ", "")
          // java 8 lambda arguments  ( o) -> (o)
          .replaceAll("\\( (\\w)", "($1")
          .replaceAll("\\)->", ") -> ")
          .replaceAll("\\n//", "\n//");
      } else {
        s = n.toString();
      }
      methodBodies.put(id, formatBody(s));
    }
    if (noParameters) {
      execMethods.put(id, n.getName());
    }
    if (n.getName().matches("^(test|slide|case).*")) {
      slideIds.add(id);
    }
  }

  private String formatComment(String comment) {
    return comment
      .replaceAll("(?m)^\\s*(/\\*\\*|\\*/|\\*)", "")
      .replaceAll("(?m)^\\s*(\\w+=[\\w ']+)?-\\s(.*)$", "<li $1>$2</li>")
      .replaceAll("(?m)^\\s*--\\s(.*)$", "<li class='level1'>$1</li>")
      .replaceAll("(?m)^\\s*--\\s(.*)$", "<li class='level2'>$1</li>")
      .replaceAll("(?m)^\\s*(\\w+=[\\w ']+)?@\\s(.+)\\s*$", "<h1 $1>$2</h1>")
      .replaceAll("(?m)^\\s*(\\w+=[\\w ']+)?@@\\s(.+)\\s*$", "<h4 $1>$2</h4>")
      .replaceFirst("<li(.*?)>", "<section$1><ul$1><li$1>")
      .replaceFirst("(?s)(.*)</li>", "$1</li></ul></section>\n")
      .replaceAll("\\*([\\w ]+?)\\*", "<b>$1</b>")
//      .replaceAll("(?m)<li[^>]*>\\s+</li>$", "")
      .replace("* /", "*/") // If we write jsni in javadoc
      .trim();
  }

  // Replace all occurrences of '@include: method_name or inner_class_name' by
  // the content of method or class present in the source class
  private String resolveIncludes(String body) {
    String regex = "(?s)(?:|.*\n)([\\s/]*@include:\\s*)(\\w+).*";
    while (body.matches(regex)) {
      String id = body.replaceFirst(regex, "$2");
      String replace = body.replaceFirst(regex, "$1$2");
      String s = innerClasses.get(id);
      if (s == null) {
        s = methodBodies.get(id);
      }
      if (s == null) {
        s = methodBodies.get(id.toLowerCase().replaceFirst("^test", ""));
      }
      if (s == null) {
        s = "\n// " + id + " class/method not found";
        for (Entry e : methodBodies.entrySet()) {
          System.out.println(e.getKey());
        }
      }
      if (!s.endsWith("\n")) {
        s += "\n";
      }
      if (!s.startsWith("\n")) {
        s = "\n" + s;
      }
      body = body.replace(replace, s);
    }
    return body;
  }

  private String formatBody(String body) {
    return body
      // remove double new line after anonymous class definition
      .replace("\n\n", "\n")
      // remove empty comments, forces a new line
      .replaceAll("(?m)^\\s*//\\s*$", "")
      // replace 4 spaces indentation with 2 spaces
      .replace("    ", "  ")
      // put in new lines split lines
      .replace("\" + \"", "\"\n       + \"");
  }

  private void handleSpecialMethod(MethodDeclaration n, String prefix, Map<String, String> map) {
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
