<%@ page import="java.io.PrintWriter" %>
<%@ page import="main.java.models.UserQuery" %>
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

    switch (method){
        case "POST":
            try {
				ConnectionManager.init("5530u60","jure0kku", "jdbc:mysql://georgia.eng.utah.edu", "5530db60");
				UserQuery query = new UserQuery();

                query.filterByField("Login", request.getParameter("Login"));
                String requestPassword = request.getParameter("Password");

                User user = query.findOne();
                if(user == null){
                    response.setStatus(400);
                    writer.append("No User Found");
                    return;
                }

                boolean passMatch = user.Password.equals(requestPassword);
                if(!passMatch){
                    response.setStatus(400);
                    writer.append("Invalid Password");
                	return;
                }

                session.setAttribute("CurrentUser", user);

                response.setStatus(200);
                writer.append("Successful Login");
            }catch (Exception e){
                response.setStatus(500);
				e.printStackTrace(writer);
                return;

            } finally {
            	if(ConnectionManager.inTransaction()){
					ConnectionManager.closeConnection();
				}
			}
        	break;
        default:
            response.setStatus(405);
            writer.append("The Method ("+method+") Is Not Supported");
  }
%>
