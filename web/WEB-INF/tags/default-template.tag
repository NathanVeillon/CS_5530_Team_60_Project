<%@ tag import="main.java.models.User" %>
<%@tag description="Page Template For Logged-In Users" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="defaultTitle" fragment="true" %>

<%
    if(session.getAttribute("CurrentUser") == null){
        response.sendRedirect(response.encodeRedirectURL("login.jsp"));
        return;
    }
%>

<t:generic>
    <jsp:attribute name="header">
        <ul class="nav navbar-nav nav-pills header-links">
            <li role="presentation" id="hdrReservations" ><a href="/reservations.jsp">Reservations</a></li>
            <li role="presentation" id="hdrVisits" ><a href="/visits.jsp">Visits</a></li>
            <li role="presentation" id="hdrHousingQuery"><a href="/housing-query.jsp">All Housing</a></li>
            <li role="presentation" id="hdrOwnedHousing"><a href="/owned-housing.jsp">Owned Housing</a></li>
        </ul>

        <p class="navbar-text navbar-right">Signed in as ${pageContext.session.getAttribute("CurrentUser").getName()}. <a href="/logout.jsp" class="navbar-link">Logout</a></p>
    </jsp:attribute>
    <jsp:attribute name="title">
      <jsp:invoke fragment="defaultTitle"/>
    </jsp:attribute>
    <jsp:body>
        <script>
            $(".header-links > li > a").each(function (index, element) {
				if($(this).attr("href") == window.location.pathname){
					$(this).parent().addClass("active");
                }
            });
        </script>
        <jsp:doBody/>
    </jsp:body>
</t:generic>