<%@ page import="java.io.PrintWriter" %>
<%@ page import="main.java.managers.ConnectionManager" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="main.java.models.base.ListResult" %>
<%@ page import="main.java.models.*" %>
<%@ page import="main.java.models.base.ObjectCollection" %>
<%@ page import="main.java.models.base.BaseObject" %>
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
                VisitQuery query = new VisitQuery();
                query.filterFromFlatJsonMap(request.getParameterMap())
						.sortFromFlatJsonMap(request.getParameterMap())
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
                Visit newVisit = new Visit();

                try {
                    newVisit.fromFlatJsonMap(request.getParameterMap());
                } catch (Exception e) {
                    response.setStatus(400);
                    writer.append("Input Error : "+ e.getMessage());
                    return;
                }


				UserQuery userQuery = new UserQuery();
                User user = userQuery
						.populateRelation("Reservation.Period")
						.filterByField("Reservation.TemporaryHousingId", newVisit.getTemporaryHousingId())
						.filterByField("Id", newVisit.getUserId())
						.findOne();

                if(user == null){
					response.setStatus(400);
					writer.append("No User Found By Given Id.");
					return;
				}

                if(!user.equals(session.getAttribute("CurrentUser"))){
					response.setStatus(400);
					writer.append("You can only make a Visit for Yourself");
					return;
				}

                TemporaryHousingQuery temporaryHousingQuery = new TemporaryHousingQuery();
				TemporaryHousing theTempHousing = temporaryHousingQuery
						.filterByField("Id", newVisit.getTemporaryHousingId())
						.findOne();

				if(theTempHousing == null){
					response.setStatus(400);
					writer.append("No Temporary Housing Found By Given Id.");
					return;
				}

				String errorMessage = newVisit.getPeriod().validateVisit(user.getReservations());

				if(errorMessage != null){
					response.setStatus(400);
					writer.append(errorMessage);
					return;
				}

                ConnectionManager.startTransaction();

				Period newPeriod = newVisit.getPeriod();
				newPeriod.save();
				newVisit.setPeriodId(newPeriod.Id);
				newVisit.save();
                ConnectionManager.commit();

                response.setStatus(201);
                writer.append(newVisit.toJson());
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
