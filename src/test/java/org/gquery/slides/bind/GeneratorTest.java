package org.gquery.slides.bind;

import com.google.gwt.junit.client.GWTTestCase;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.gquery.slides.client.SlidesDeferred;

import java.io.InputStream;

public class GeneratorTest extends GWTTestCase {

  public String getModuleName() {
    return null;
  }

  public void test1() throws Exception {
    pp(SlidesDeferred.class);
//     parse("/Users/manolo/git/gwtquery/slides/src/main/java/org/gquery/slides/client/SlidesDeferred.java");
  }
  
  
  public void pp(Class<?> clazz) throws ParseException {
    String file = clazz.getName().replace(".", "/") +  ".java";
    System.out.println(file);
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
    CompilationUnit cu = null;
    cu = JavaParser.parse(in);
    VoidVisitorAdapter a = new VoidVisitorAdapter() {
      public void visit(MethodDeclaration n, Object arg) {
        System.out.println(n.getBody().toString()
            .replaceFirst("^\\s*\\{ *\n*", "")
            .replaceFirst("\\s*\\}\\s*$", "")
            .replaceAll("\n+", "\n")
            .replaceAll("(?m)^    ", "")
          );
//
//        if(n.getComment() != null)
//        System.out.println(n.getComment().toString()
//            .replaceAll("(?m)^\\s*(/\\*\\*|\\*/|\\*)", "")
//            .replaceAll("(?m)^\\s*-\\s(.*)$", "<li>$1</li>")
//            .replaceAll("(?m)^\\s*@t(.+)\\s*$", "<h4>$1</h4>")
//            .replaceFirst("<li>", "<ul><li>")
//            .replaceFirst("(?s)(.*)</li>", "$1</li></ul>\n")
//            );

      }
    };
    
    a.visit(cu, null);

  }

 

}
