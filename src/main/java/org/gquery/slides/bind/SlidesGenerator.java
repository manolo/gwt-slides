package org.gquery.slides.bind;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.dev.jjs.UnifiedAst.AST;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class SlidesGenerator extends Generator {

  HashMap<String, String> methodBodies = new HashMap<String, String>();
  HashMap<String, String> methodDoc = new HashMap<String, String>();
  HashMap<String, String> exec = new HashMap<String, String>();

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
      parseJava0(file);
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
      for (String id: exec.keySet()) {
        sw.println(" if(id.equals(\"" + id + "\")) " + exec.get(id) + "();\n");
      }
      sw.println(" } catch (Exception e) {e.printStackTrace();}\n}");
      sw.commit(treeLogger);
    }

    return generatedClzFullName;
  }

//  public void parseJava(String file) throws ParseException, IOException {
//    InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
//    String content = IOUtils.toString(in);
//
//    content.format("","");
//
//
//    ASTParser parser = ASTParser.newParser(AST.JLS3);
//
//    @SuppressWarnings( "unchecked" )
//    Map<String,String> options = JavaCore.getOptions();
//    if(VERSION_1_5.equals(targetJdk))
//        JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
//    else if(VERSION_1_6.equals(targetJdk))
//        JavaCore.setComplianceOptions(JavaCore.VERSION_1_6, options);
//    else {
//        if(!VERSION_1_4.equals(targetJdk)) {
//            log.warn("Unknown targetJdk ["+targetJdk+"]. Using "+VERSION_1_4+" for parsing. Supported values are: "
//                    + VERSION_1_4 + ", "
//                    + VERSION_1_5 + ", "
//                    + VERSION_1_6
//            );
//        }
//        JavaCore.setComplianceOptions(JavaCore.VERSION_1_4, options);
//    }
//    parser.setCompilerOptions(options);
//
//    parser.setResolveBindings(false);
//    parser.setStatementsRecovery(false);
//    parser.setBindingsRecovery(false);
//    parser.setSource(source.toCharArray());
//    parser.setIgnoreMethodBodies(false);
//
//    CompilationUnit ast = (CompilationUnit) parser.createAST(null);
//
//    // AstVisitor extends org.eclipse.jdt.core.dom.ASTVisitor
//    AstVisitor visitor = new AstVisitor();
//    visitor.DEBUG = DEBUG;
//    ast.accept( visitor );
//
//
//  }

  public void parseJava0(String file) throws ParseException {
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);

    // use japa.parser.ast to parse the source. We could use the classes in the
    // gwt compiler, but it seems this info is not available when running generators.
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
