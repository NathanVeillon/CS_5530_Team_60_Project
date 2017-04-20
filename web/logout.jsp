<%
	session.setAttribute("CurrentUser", null);
	response.sendRedirect(response.encodeRedirectURL("login.jsp"));
%>