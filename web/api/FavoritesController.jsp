<%@ page import="java.io.PrintWriter" %>
<%@ page import="main.java.managers.ConnectionManager" %>
<%@ page import="main.java.models.base.ListResult" %>
<%@ page import="main.java.models.*" %>
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
                FavoriteQuery query = new FavoriteQuery();
                query.sortFromFlatJsonMap(request.getParameterMap())
                        .filterFromFlatJsonMap(request.getParameterMap())
                        .populateFromFlatJsonMap(request.getParameterMap())
                        .paginate(pageNum, perPage);

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
                Favorite newFavorite = new Favorite();

                try {
                    newFavorite.fromFlatJsonMap(request.getParameterMap());
                } catch (Exception e) {
                    response.setStatus(400);
                    writer.append("Input Error : "+ e.getMessage()+"\n");
                    e.printStackTrace(writer);
                    return;
                }

                UserQuery userQuery = new UserQuery();
                TemporaryHousingQuery temporaryHousingQuery = new TemporaryHousingQuery();

                User user = userQuery.filterByField("Id", newFavorite.getUserId()).findOne();
                TemporaryHousing temporaryHousing = temporaryHousingQuery
						.filterByField("Id", newFavorite.getTemporaryHousingId())
						.findOne();

                if(user == null){
                    response.setStatus(400);
                    writer.append("User Not Found");
                    return;
                }
                if(temporaryHousing == null){
                    response.setStatus(400);
                    writer.append("Temporary Housing Not Found");
                    return;
                }

                if(!user.equals(session.getAttribute("CurrentUser"))){
                    response.setStatus(400);
                    writer.append("You can only make a Reservation for Yourself");
                    return;
                }

                writer.append(newFavorite.toJson());

                ConnectionManager.startTransaction();
                newFavorite.save();
                ConnectionManager.commit();

                response.setStatus(201);
                writer.append(newFavorite.toJson());
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
