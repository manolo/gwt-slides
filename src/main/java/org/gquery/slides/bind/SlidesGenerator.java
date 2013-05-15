package org.gquery.slides.bind;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.builders.JsonBuilder;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class SlidesGenerator extends Generator {
  
  TypeOracle oracle;
  static JClassType functionType;
  static JClassType jsonBuilderType;
  static JClassType jsType;
  static JClassType listType;
  static JClassType stringType;


  @Override
  public String generate(TreeLogger treeLogger, 
      GeneratorContext generatorContext, String requestedClass) throws UnableToCompleteException {
    oracle = generatorContext.getTypeOracle();
    JClassType clazz =  oracle.findType(requestedClass);
    jsonBuilderType = oracle.findType(JsonBuilder.class.getName());
    stringType = oracle.findType(String.class.getName());
    jsType = oracle.findType(JavaScriptObject.class.getName());
    listType = oracle.findType(List.class.getName());
    functionType = oracle.findType(Function.class.getName());

    String t[] = generateClassName(clazz);
    String file = t[0].replace(".", "/") + "/" + clazz.getName() + ".java";

    try {
      parseJava(file);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    
    SourceWriter sw = getSourceWriter(treeLogger, generatorContext, t[0], t[1], requestedClass);
    
    if (sw != null) {
      sw.println("public " + t[1] + "() {");
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
      for (String id: exec.keySet()) {
        sw.println(" if(id.equals(\"" + id + "\")) " + exec.get(id) + "();\n");
      }
      sw.println(" } catch (Exception e) {e.printStackTrace();}\n}");
      sw.commit(treeLogger);
    }
    return t[2];
  }
  
  HashMap<String, String> methodBodies = new HashMap<String, String>();
  HashMap<String, String> methodDoc = new HashMap<String, String>();
  HashMap<String, String> exec = new HashMap<String, String>();
  
  public void parseJava(String file) throws ParseException {
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
    CompilationUnit cu = null;
    cu = JavaParser.parse(in);
    VoidVisitorAdapter<?> a = new VoidVisitorAdapter<Object>() {
      public void visit(MethodDeclaration n, Object arg) {
          String id = n.getName().replaceFirst("^test","").toLowerCase();
          boolean noParameters = n.getParameters() == null;
          if (n.getBody() != null) {
            String s = n.getBody().toString();
            if (noParameters) {
              s = s.replaceFirst("^\\s*\\{ *\n*", "")
                   .replaceFirst("\\s*\\}\\s*$", "")
                   .replaceAll("\n+", "\n")
                   .replaceAll("(?m)^    ", "");
            } else {
              s = n.getName() + "()" + s;
            }
            methodBodies.put(id, s);
          }
          if (noParameters) {
            exec.put(id, n.getName());
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
    };
    a.visit(cu, null);
  }

  
  public String[] generateClassName(JType t) {
    String[] ret = new String[3];
    JClassType c = t.isClassOrInterface();
    ret[0] = c.getPackage().getName();
    ret[1] = c.getName().replace('.', '_') + "_Slide";
    ret[2] = ret[0] + "." + ret[1];
    return ret;
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
    composerFactory.addImport("com.google.gwt.query.client.js.*");
    composerFactory.addImport("com.google.gwt.query.client.*");
    composerFactory.addImport("com.google.gwt.core.client.*");
    composerFactory.addImport("com.google.gwt.dom.client.*");
    composerFactory.addImport("java.util.*");

    //  composerFactory.addImplementedInterface(interfaceName);
    return composerFactory.createSourceWriter(context, printWriter);
  }

}
