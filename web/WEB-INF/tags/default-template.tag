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

<style>
    .navbar-toggle .icon-bar {
        display: block;
        width: 22px;
        height: 2px;
        background-color: #cccccc;
        border-radius: 1px;
    }
</style>

<t:generic>
    <jsp:attribute name="header"></jsp:attribute>
    <jsp:attribute name="header-collapse">
                        <ul class="nav navbar-nav nav-pills">
                            <li role="presentation" id="hdrReservations" ><a href="#">Reservations</a></li>
                            <li role="presentation" id="hdrVisits" ><a href="#">Visits</a></li>
                            <li role="presentation" id="hdrOwnedHousing"><a href="#">Owned Housing</a></li>
                        </ul>

                        <p class="navbar-text navbar-right">Signed in as ${pageContext.session.getAttribute("CurrentUser").getName()}. <a href="/logout.jsp" class="navbar-link">Logout</a></p>
    </jsp:attribute>
    <jsp:attribute name="footer">
      <p id="copyright">&copy; 1927, Future Bits When There Be Bits Inc.</p>
    </jsp:attribute>
    <jsp:attribute name="title">
      <jsp:invoke fragment="defaultTitle"/>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</t:generic>