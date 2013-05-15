package org.gquery.slides.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EchoServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String t = req.getParameter("timeout");
    if (t != null && t.matches("\\d+")) {
      try {
        Thread.sleep(Integer.parseInt(t));
      } catch (Exception e) {
      }
    }
    PrintWriter p = resp.getWriter();
    p.print(req.getParameter("data"));
    p.flush();
    p.close();
  }
}
