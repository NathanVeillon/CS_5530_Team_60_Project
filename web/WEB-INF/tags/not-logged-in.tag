<%@tag description="Login Page template" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="loginTitle" fragment="true" %>
<%@attribute name="headerButton" fragment="true" %>

<%
    if(session.getAttribute("CurrentUser") != null){
        response.sendRedirect(response.encodeRedirectURL("dashboard.jsp"));
        return;
    }

%>

<t:generic>
    <jsp:attribute name="header">
        <jsp:invoke fragment="headerButton" />
    </jsp:attribute>
    <jsp:attribute name="title">
      <jsp:invoke fragment="loginTitle" />
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</t:generic>