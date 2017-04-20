<%@tag description="Login Page template" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="loginTitle" fragment="true" %>

<%
    if(session.getAttribute("CurrentUser") != null){
        response.sendRedirect(response.encodeRedirectURL("dashboard.jsp"));
        return;
    }

%>

<t:generic>
    <jsp:attribute name="header">
        <a class="btn btn-default navbar-btn navbar-right" href="${pageContext.request.contextPath}/login.jsp" role="button">Create New User</a>
    </jsp:attribute>
    <jsp:attribute name="header-collapse"></jsp:attribute>
    <jsp:attribute name="title">
      <jsp:invoke fragment="loginTitle" />
    </jsp:attribute>
    <jsp:attribute name="footer">
      <p id="copyright">Copyright 1927, Future Bits When There Be Bits Inc.</p>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</t:generic>