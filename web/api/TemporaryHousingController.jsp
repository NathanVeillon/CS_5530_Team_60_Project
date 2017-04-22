<%@ page import="java.io.PrintWriter" %>
<%@ page import="main.java.models.User" %>
<%@ page import="main.java.managers.ConnectionManager" %>
<%@ page import="main.java.models.TemporaryHousing" %>
<%@ page import="main.java.models.TemporaryHousingQuery" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="main.java.models.base.ListResult" %>
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

    if(session.getAttribute("CurrentUser") == null){
        response.setStatus(403);
        writer.append("You Must Be Logged In To Use This Controller.");
        return;
    }

    switch (method) {
        case "GET":
            try {
                ConnectionManager.init("5530u60", "jure0kku", "jdbc:mysql://georgia.eng.utah.edu", "5530db60");
                int pageNum = Integer.parseInt(request.getParameter("page"));
                int perPage = Integer.parseInt(request.getParameter("perPage"));
                TemporaryHousingQuery query = new TemporaryHousingQuery();
                query.filterFromFlatJsonMap(request.getParameterMap());
                query.paginate(pageNum, perPage);

                ListResult data = new ListResult();
                data.Page = pageNum;
                data.PerPage = perPage;
                data.Results = query.find();
                data.Count = query.count();

                response.setStatus(200);
                writer.append(data.toJson());
            } catch (Exception e) {
                response.setStatus(400);
                writer.append("Unexpected Error: ");
                e.printStackTrace(writer);
                return;

            } finally {
                if (ConnectionManager.isInitialized()) {
                    ConnectionManager.closeConnection();
                }
            }
            break;
        case "POST":
            try {
                ConnectionManager.init("5530u60", "jure0kku", "jdbc:mysql://georgia.eng.utah.edu", "5530db60");
                TemporaryHousing newHousing = new TemporaryHousing();

                try {
                    newHousing.fromFlatJsonMap(request.getParameterMap());
                } catch (Exception e) {
                    response.setStatus(400);
                    writer.append("Input Error : "+ e.getMessage());
                    return;
                }

                newHousing.setOwner((User)session.getAttribute("CurrentUser"));

                ConnectionManager.startTransaction();
                newHousing.save();
                ConnectionManager.commit();

                response.setStatus(201);
                writer.append(newHousing.toJson());
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
        case "PUT":
            try {
                ConnectionManager.init("5530u60", "jure0kku", "jdbc:mysql://georgia.eng.utah.edu", "5530db60");

                TemporaryHousingQuery query = new TemporaryHousingQuery();
                int idToUpdate = (request.getParameter("Id") == null) ? Integer.parseInt(request.getParameter("Id")) : 0;

                query.filterByField("Id", idToUpdate);

                TemporaryHousing housingToUpdate = query.findOne();

                if(housingToUpdate == null){
                	response.setStatus(404);
                    writer.append("No Housing Found To Update.");
                	return;
                }

                if(!housingToUpdate.OwnerId.equals(((User)session.getAttribute("CurrentUser")).getId())){
                	response.setStatus(400);
                    writer.append("Cannot Update Housing You Don't Own.");
                	return;
                }

                try {
                    housingToUpdate.fromFlatJsonMap(request.getParameterMap());
                } catch (Exception e) {
                    response.setStatus(400);
                    writer.append("Input Error : "+ e.getMessage());
                    return;
                }

                ConnectionManager.startTransaction();
                housingToUpdate.save();
                ConnectionManager.commit();

                response.setStatus(200);
                writer.append(housingToUpdate.toJson());
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
