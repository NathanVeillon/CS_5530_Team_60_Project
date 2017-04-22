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
        <a class="btn btn-default navbar-btn navbar-right" href="${pageContext.request.contextPath}/login.jsp" role="button">Login</a>
	</jsp:attribute>
	<jsp:attribute name="loginTitle">
		Create New User
	</jsp:attribute>
	<jsp:body>
		<div class="container col-md-6">
			<p id="infoCreateUserError" class="bg-danger text-danger hidden">Unable to Create User:  </p>
			<div>
				<div class="form-group">
					<label for="inpLogin">Username</label>
					<input type="text" class="form-control" id="inpLogin" placeholder="Username">
				</div>
				<div class="form-group">
					<label for="inpPassword">Password</label>
					<input type="password" class="form-control" id="inpPassword" placeholder="Password">
				</div>
				<div class="form-group">
					<label for="inpName">Name</label>
					<input type="text" class="form-control" id="inpName" placeholder="Name">
				</div>
				<div class="form-group">
					<label for="inpPhoneNumber">Phone Number</label>
					<input type="text" class="form-control" id="inpPhoneNumber" placeholder="(555) 555-5555">
				</div>
				<div class="form-group">
					<label for="inpAddress">Address</label>
					<textarea rows="2" class="form-control" id="inpAddress" placeholder="1234 E. Example Street
City, State 12345"></textarea>
				</div>
				<button id="btnCreateUser" class="btn btn-default">Create</button>
			</div>
		</div>

		<script>

			var btnCreateUser = $("#btnCreateUser");
			var infoCreateUserError = $("#infoCreateUserError");

			btnCreateUser.click(handleLoginButtonClicked);
			function handleLoginButtonClicked() {
				var newUser = new Db.Uotel.Entities.User();
				newUser.Login = $("#inpLogin").val();
				newUser.Password = $("#inpPassword").val();
				newUser.Name = $("#inpName").val();
				newUser.PhoneNumber = $("#inpPhoneNumber").val();
				newUser.Address = $("#inpAddress").val();
				newUser.IsAdmin = false;

				infoCreateUserError.addClass("hidden");
				btnCreateUser.addClass("disabled");
				btnCreateUser.text("Creating User...");
				Db.Uotel.Api.User.createUser(newUser).setHandlers({
					success: function(a){
						window.location = "dashboard.jsp";
					},
					error: function (error) {
						btnCreateUser.text("Create");
						btnCreateUser.removeClass("disabled");
						infoCreateUserError.text("Unable to Create User: "+error.message);
						infoCreateUserError.removeClass("hidden");
					}
				});
            }
		</script>
	</jsp:body>
</t:not-logged-in>