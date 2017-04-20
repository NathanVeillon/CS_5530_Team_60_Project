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
	<jsp:attribute name="loginTitle">
		Login
	</jsp:attribute>
	<jsp:body>
		<div>
			<div>
				<div class="form-group">
					<label for="inpUsername">Username</label>
					<input type="text" class="form-control" id="inpUsername" placeholder="Username">
				</div>
				<div class="form-group">
					<label for="inpPassword">Password</label>
					<input type="password" class="form-control" id="inpPassword" placeholder="Password">
				</div>
				<button id="btnLogin" class="btn btn-default">Log In</button>
			</div>
		</div>

		<script>

			var btnLogin = $("#btnLogin");

			btnLogin.click(handleLoginButtonClicked);
			function handleLoginButtonClicked() {
				btnLogin.text("Logging In...");
				Db.Uotel.Remote.doPost("/api/LoginController.jsp", {Login:"Tester", Password:"pass"}).setHandlers({
					200: function(a){
						window.location = "dashboard.jsp";
					},
					_default: function (a, b) {
						btnLogin.text("Log In");
						alert(b);
					}
				});
            }
		</script>
	</jsp:body>
</t:not-logged-in>