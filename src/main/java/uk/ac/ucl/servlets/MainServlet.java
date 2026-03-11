package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

@WebServlet({ "/main" })
public class MainServlet extends BaseServlet {
  public MainServlet() {
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      Model model = ModelFactory.getModel();
      // Refresh the model view so that it covers the whole DataFrame
      model.refreshView();
      // Flag to tell JSPs to display table + main menu
      request.setAttribute("pageMode", "main");

      // Dispatch request to the DataServlet
      // 'table' and 'key' request attributes will be set by the DataServlet
      forward(request, response, "/data");
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error: " + e.getMessage());
    }
  }
}