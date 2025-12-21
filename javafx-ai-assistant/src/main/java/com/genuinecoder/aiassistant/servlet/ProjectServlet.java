package com.genuinecoder.aiassistant.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/project")
public class ProjectServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Project - NeuroBot Assistant</title>");
            out.println("<style>");
            out.println("body {");
            out.println("  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
            out.println("  margin: 0;");
            out.println("  padding: 20px;");
            out.println("  background-image: url('/icon/feedback.png');");
            out.println("  background-size: cover;");
            out.println("  background-attachment: scroll;");
            out.println("  background-position: center;");
            out.println("  min-height: 100vh;");
            out.println("}");
            out.println(".container {");
            out.println("  max-width: 800px;");
            out.println("  margin: 20px auto;");
            out.println("  background: rgba(255, 255, 255, 0.9);");
            out.println("  padding: 30px;");
            out.println("  border-radius: 10px;");
            out.println("  box-shadow: 0 2px 10px rgba(0,0,0,0.1);");
            out.println("  backdrop-filter: blur(5px);");
            out.println("}");
            out.println("h1 { color: #2c3e50; text-align: center; margin-bottom: 30px; }");
            out.println(".criteria { background: rgba(248, 249, 250, 0.8); padding: 20px; border-radius: 8px; margin-bottom: 25px; }");
            out.println("h3 { color: #34495e; margin-top: 0; }");
            out.println("ul { padding-left: 20px; }");
            out.println("li { margin-bottom: 8px; }");
            out.println(".status { background: rgba(232, 244, 253, 0.8); padding: 15px; border-left: 4px solid #3498db; margin: 20px 0; }");
            out.println(".timestamp { text-align: right; color: #7f8c8d; font-size: 0.9em; margin-top: 30px; }");
            out.println(".feedback-form { margin-top: 40px; border-top: 1px solid #eee; padding-top: 20px; }");
            out.println(".form-group { margin-bottom: 15px; }");
            out.println("label { display: block; margin-bottom: 5px; font-weight: 500; }");
            out.println("input[type='text'], textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }");
            out.println("textarea { min-height: 100px; resize: vertical; }");
            out.println("button { background-color: #4caf50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }");
            out.println("button:hover { background-color: #45a049; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class=\"container\">");
            out.println("<h1>Servlet Implementation - NeuroBot Assistant</h1>");
            out.println("<div class=\"criteria\">");
            out.println("<h3>Marking Criteria (Out of 10):</h3>");
            out.println("<ul>");
            out.println("<li><strong>Servlet Implementation (2 points):</strong> Proper implementation of a Java servlet with correct lifecycle methods.</li>");
            out.println("<li><strong>Request Handling (2 points):</strong> Correct handling of HTTP GET and POST requests with parameters.</li>");
            out.println("<li><strong>Response Generation (2 points):</strong> Proper generation of dynamic HTML content with appropriate status codes.</li>");
            out.println("<li><strong>Code Organization (2 points):</strong> Clean, well-structured code following Java and servlet best practices.</li>");
            out.println("<li><strong>Documentation (2 points):</strong> Clear comments and documentation explaining the code and its purpose.</li>");
            out.println("</ul>");
            out.println("</div>");
            
            out.println("<div class=\"status\">");
            out.println("<p><strong>✅ Project Status:</strong> Successfully running within Spring Boot application</p>");
            out.println("<p>This servlet demonstrates the implementation of a traditional Java servlet within a modern Spring Boot 3.x application.</p>");
            out.println("</div>");
            
            // Feedback Form
            out.println("<div class=\"feedback-form\">");
            out.println("<h3>Submit Feedback</h3>");
            out.println("<form method=\"POST\" action=\"project\">");
            out.println("<div class=\"form-group\">");
            out.println("<label for=\"name\">Your Name (optional):</label>");
            out.println("<input type=\"text\" id=\"name\" name=\"name\" placeholder=\"Enter your name\">");
            out.println("</div>");
            out.println("<div class=\"form-group\">");
            out.println("<label for=\"feedback\">Your Feedback:</label>");
            out.println("<textarea id=\"feedback\" name=\"feedback\" placeholder=\"Enter your feedback here...\" required></textarea>");
            out.println("</div>");
            out.println("<button type=\"submit\">Submit Feedback</button>");
            out.println("</form>");
            out.println("</div>");
            
            out.println("<div class=\"timestamp\">");
            out.println("Page generated on: " + timestamp);
            out.println("</div>");
            
            out.println("</div>"); // Close container
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        // Get form parameters
        String name = request.getParameter("name") != null ? 
                     request.getParameter("name") : "Anonymous";
        String feedback = request.getParameter("feedback") != null ? 
                         request.getParameter("feedback") : "No feedback provided";
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Feedback Received - NeuroBot Assistant</title>");
            out.println("<style>");
            out.println("body {");
            out.println("  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
            out.println("  margin: 0;");
            out.println("  padding: 20px;");
            out.println("  overflow: hidden;");
            out.println("  background-image: url('/icon/admin.png');");
            out.println("  background-size: cover;");
            out.println("  background-attachment: fixed;");
            out.println("  background-position: center;");
            out.println("  min-height: 100vh;");
            out.println("}");
            out.println(".container {");
            out.println("  max-width: 600px;");
            out.println("  margin: 20px auto;");
            out.println("  background: rgba(255, 255, 255, 0.9);");
            out.println("  padding: 30px;");
            out.println("  border-radius: 10px;");
            out.println("  box-shadow: 0 2px 10px rgba(0,0,0,0.1);");
            out.println("  backdrop-filter: blur(5px);");
            out.println("}");
            out.println("h1 { color: #2c3e50; text-align: center; }");
            out.println(".confirmation { background: rgba(232, 245, 233, 0.9); padding: 20px; border-left: 4px solid #4caf50; margin: 20px 0; }");
            out.println(".feedback-item { margin-bottom: 15px; }");
            out.println(".feedback-label { font-weight: bold; color: #2c3e50; }");
            out.println(".back-link { display: inline-block; background-color: #3498db; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 20px; }");
            out.println(".back-link:hover { background-color: #2980b9; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class=\"container\">");
            out.println("<h1>Thank You for Your Feedback!</h1>");
            
            out.println("<div class=\"confirmation\">");
            out.println("<h3>✅ Feedback Successfully Submitted</h3>");
            out.println("<p>We've received your feedback and appreciate you taking the time to share your thoughts with us.</p>");
            out.println("</div>");
            
            out.println("<h3>Your Feedback Details:</h3>");
            out.println("<div class=\"feedback-item\">");
            out.println("<span class=\"feedback-label\">Name:</span> " + escapeHtml(name) + "<br>");
            out.println("</div>");
            out.println("<div class=\"feedback-item\">");
            out.println("<span class=\"feedback-label\">Feedback:</span><br>");
            out.println(escapeHtml(feedback).replace("\n", "<br>"));
            out.println("</div>");
            
            out.println("<div style=\"text-align: center; margin-top: 30px;\">");
            out.println("<a href=\"project\" class=\"back-link\">Back to Project Page</a>");
            out.println("</div>");
            
            out.println("</div>"); // Close container
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}