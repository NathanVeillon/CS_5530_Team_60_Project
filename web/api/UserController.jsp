<%@ page import="java.io.PrintWriter" %>
<%--
  Created by IntelliJ IDEA.
  User: Student Nathan
  Date: 4/19/2017
  Time: 3:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%
  String method = request.getMethod();
    response.setContentType("application/json");
    PrintWriter writer = response.getWriter();

    switch (method){
      default:
          response.setStatus(405);
          writer.append("The Method ("+method+") Is Not Supported");
  }
%>
