<%@ page import="java.io.PrintWriter" %>
<%@ page import="main.java.models.User" %>
<%@ page import="main.java.managers.ConnectionManager" %>
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

    switch (method) {
        case "POST":
            try {
                ConnectionManager.init("5530u60", "jure0kku", "jdbc:mysql://georgia.eng.utah.edu", "5530db60");
                User newUser = new User();

                try {
                    newUser.fromFlatJsonMap(request.getParameterMap());
                } catch (Exception e) {
                    response.setStatus(400);
                    writer.append("Input Error : "+ e.getMessage()+"\n");
                    e.printStackTrace(writer);
                    return;
                }
                newUser.setIsAdmin(false);

                writer.append(newUser.toJson());

                ConnectionManager.startTransaction();
                newUser.save();
                ConnectionManager.commit();

                session.setAttribute("CurrentUser", newUser);
                response.setStatus(201);
                writer.append(newUser.toJson());
            } catch (Exception e) {
                response.setStatus(400);
                writer.append("Unexpected Error:");
                e.printStackTrace(writer);
                return;

            } finally {
                if (ConnectionManager.isInitialized()) {
                    ConnectionManager.closeConnection();
                }
            }
            break;
        default:
            response.setStatus(405);
            writer.append("The Method (" + method + ") Is Not Supported");
    }
%>
