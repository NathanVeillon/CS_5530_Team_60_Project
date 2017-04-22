<%--
  Created by IntelliJ IDEA.
  User: Student Nathan
  Date: 4/8/2017
  Time: 4:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:not-logged-in>
	<jsp:attribute name="headerButton">
        <a class="btn btn-default navbar-btn navbar-right" href="${pageContext.request.contextPath}/new-user.jsp" role="button">Create New User</a>
	</jsp:attribute>
	<jsp:attribute name="loginTitle">
		Login
	</jsp:attribute>
	<jsp:body>
		<div class="container col-md-6">
			<p id="infoLoginError" class="bg-danger text-danger hidden">Unable to Login:  </p>
			<div>
				<div class="form-group">
					<label for="inpLogin">Username</label>
					<input type="text" class="form-control" id="inpLogin" placeholder="Username">
				</div>
				<div class="form-group">
					<label for="inpPassword">Password</label>
					<input type="password" class="form-control" id="inpPassword" placeholder="Password">
				</div>
				<button id="btnLogin" class="btn btn-default">Log In</button>
			</div>
			<div class="col-md-6"></div>
		</div>

		<script>

			var btnLogin = $("#btnLogin");
			var infoLoginError = $("#infoLoginError");

			btnLogin.click(handleLoginButtonClicked);
			function handleLoginButtonClicked() {
				var inpLoginVal = $("#inpLogin").val();
				var inpPasswordVal = $("#inpPassword").val();

				infoLoginError.addClass("hidden");
				btnLogin.text("Logging In...");
				Db.Uotel.Api.User.loginUser(inpLoginVal, inpPasswordVal).setHandlers({
					success: function(){
						window.location = "dashboard.jsp";
					},
					error: function (error) {
						btnLogin.text("Log In");
						infoLoginError.text("Unable to Login: "+error.message);
						infoLoginError.removeClass("hidden");
					}
				});
            }
		</script>
	</jsp:body>
</t:not-logged-in>