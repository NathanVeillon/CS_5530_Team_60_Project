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
                AvailablePeriodQuery query = new AvailablePeriodQuery();
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
                AvailablePeriod newAvailablePeriod = new AvailablePeriod();

                try {
                    newAvailablePeriod.fromFlatJsonMap(request.getParameterMap());
                } catch (Exception e) {
                    response.setStatus(400);
                    writer.append("Input Error : "+ e.getMessage());
                    return;
                }

                TemporaryHousingQuery temporaryHousingQuery = new TemporaryHousingQuery();
				temporaryHousingQuery.populateRelation("AvailablePeriods.Period").filterByField("Id", newAvailablePeriod.getTemporaryHousingId());

                TemporaryHousing theTempHousing = temporaryHousingQuery.findOne();

                if(theTempHousing == null){
					response.setStatus(400);
					writer.append("No Temporary Housing Found By Given Id.");
					return;
				}

				if(!theTempHousing.getOwnerId().equals(((User)session.getAttribute("CurrentUser")).getId())){
					response.setStatus(400);
					writer.append("Cannot Update Housing You Don't Own.");
					return;
				}

				ObjectCollection relatedPeriods = new ObjectCollection();
                for(BaseObject object: theTempHousing.getAvailablePeriods()){
					AvailablePeriod availablePeriod = (AvailablePeriod) object;
					relatedPeriods.add(availablePeriod.getPeriod());
				}

                String errorMessage = newAvailablePeriod.getPeriod().validateAvailablePeriod(relatedPeriods);

				if(errorMessage != null){
					response.setStatus(400);
					writer.append(errorMessage);
					return;
				}

                ConnectionManager.startTransaction();

				Period newPeriod = newAvailablePeriod.getPeriod();
				newPeriod.save();
				newAvailablePeriod.setPeriod(newPeriod);
                newAvailablePeriod.save();
                ConnectionManager.commit();

                response.setStatus(201);
                writer.append(newAvailablePeriod.toJson());
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
