<%--
  Created by IntelliJ IDEA.
  User: Student Nathan
  Date: 4/8/2017
  Time: 4:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:default-template>
	<jsp:attribute name="defaultTitle">
		New Temporary Housing
  	</jsp:attribute>
	<jsp:body>

		<div class="container">
			<div class="col-md-6">
				<div class="form-group">
					<label for="inpName">Name</label>
					<input type="text" class="form-control infoName" id="inpName" placeholder="Hotel Name">
				</div>
				<div class="form-group">
					<label for="inpPhoneNumber">Phone Number</label>
					<input type="text" class="form-control infoPhoneNumber" id="inpPhoneNumber"
						   placeholder="(555) 555-5555">
				</div>
				<div class="form-group">
					<label for="inpYearBuilt">Year Built</label>
					<input type="text" maxlength="4" class="form-control infoYearBuilt" id="inpYearBuilt"
						   placeholder="2004">
				</div>
				<div class="form-group">
					<label for="inpExpectedPrice">Expected Price Per Night</label>
					<input type="number" class="form-control infoExpectedPrice" id="inpExpectedPrice"
						   placeholder="140.00">
				</div>
			</div>
			<div class="col-md-6">
				<div class="form-group">
					<label for="inpCategory">Category</label>
					<input type="text" class="form-control infoCategory" id="inpCategory"
						   placeholder="Category (Ex. House)">
				</div>
				<div class="form-group">
					<label for="inpUrl">Website Url</label>
					<input type="url" class="form-control infoUrl" id="inpUrl"
						   placeholder="www.example.com">
				</div>
				<div class="form-group">
					<label for="inpAddress">Address</label>
					<textarea rows="2" class="form-control infoAddress" id="inpAddress" placeholder="1234 E. Example Street
City, State 12345"></textarea>
				</div>
				<button id="btnCreateHousing" class="btn btn-default">Create</button>
			</div>
		</div>

		<script>

			init();

			function init() {
				initEventHandlers();
			}


			function initEventHandlers() {
				$("#btnCreateHousing").click(handleCreateHousingButtonClicked);
			}

			function getInputTempHousing() {
				var newTempHousing = new Db.Uotel.Entities.TemporaryHousing(theTemporaryHousing);
				newTempHousing.Name = $("#inpName").val();
				newTempHousing.Category = $("#inpCategory").val();
				newTempHousing.PhoneNumber = $("#inpPhoneNumber").val();
				newTempHousing.Address = $("#inpAddress").val();
				newTempHousing.URL = $("#inpUrl").val();
				newTempHousing.YearBuilt = $("#inpYearBuilt").val();
				newTempHousing.ExpectedPrice = $("#inpExpectedPrice").val();

				return newTempHousing;
			}

			function handleCreateHousingButtonClicked() {
				var saveBtn = $("#btnCreateHousing");
				saveBtn.addClass("disabled");
				saveBtn.text("Creating");
				Db.Uotel.Api.TemporaryHousing.createTemporaryHousing(getInputTempHousing()).setHandlers({
					success: function (newTempHousing) {
						window.location = '/temporary-housing.jsp?Id='+newTempHousing.Id;
					},
					error: function (error) {
						alert(error.message);
						saveBtn.removeClass("disabled");
						saveBtn.text("Create");
					}
				});
			}

		</script>
	</jsp:body>
</t:default-template>