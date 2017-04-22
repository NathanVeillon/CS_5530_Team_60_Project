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
		Temporary Housing : <span class="infoName"></span>
  	</jsp:attribute>
	<jsp:body>
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#tabInfo" aria-controls="info" role="tab" data-toggle="tab">Info</a>
			</li>
			<li role="presentation"><a href="#tabAvailableDates" aria-controls="profile" role="tab" data-toggle="tab">Available
				Dates</a></li>
		</ul>

		<br/>

		<!-- Tab panes -->
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="tabInfo">
				<div class="divOwner hidden">
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
									   placeholder="450.00">
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
							<button id="btnHousingSave" class="btn btn-default">Save</button>
						</div>
					</div>
				</div>
				<div class="divNonOwner">
					<div class="col-md-6">
						<div class="form-group">
							<label for="infoName">Name</label>
							<p id="infoName" class="form-control-static infoName"></p>
						</div>
						<div class="form-group">
							<label for="infoPhoneNumber">Phone Number</label>
							<p class="form-control-static infoPhoneNumber" id="infoPhoneNumber"></p>
						</div>
						<div class="form-group">
							<label for="infoYearBuilt">Year Built</label>
							<p class="form-control-static infoYearBuilt" id="infoYearBuilt">
						</div>
						<div class="form-group">
							<label for="infoExpectedPrice">Expected Price Per Night</label>
							<p class="form-control-static infoExpectedPrice" id="infoExpectedPrice"></p>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label for="infoCategory">Category</label>
							<p class="form-control-static infoCategory" id="infoCategory"></p>
						</div>
						<div class="form-group">
							<label for="infoUrl">Website Url</label>
							<p class="form-control-static infoUrl" id="infoUrl"></p>
						</div>
						<div class="form-group">
							<label for="inpAddress">Address</label>
							<p class="form-control-static infoAddress" id="infoAddress"></p>
						</div>
					</div>
				</div>
				<div id="divFeedback"></div>
			</div>
			<div role="tabpanel" class="tab-pane" id="tabAvailableDates">
				<div class="container">
					<div class="col-md-9">
						<div class="form-group">
							<table id="tblAvailableDates"
								   class="table table-striped table-bordered table-hover table-condensed"></table>
							<div id="pgrAvailableDates"></div>
						</div>
					</div>
					<div class="col-md-3 btn-column">
						<button class="btn btn-default" role="button">Add Available Date Range</button>
					</div>
				</div>
			</div>
		</div>

		<script>
			var currentUserId = ${pageContext.session.getAttribute("CurrentUser").getId()};
			var dataTable = $("#tbl_ownedHousing");
			var table;

			var theTemporaryHousingId;
			var theTemporaryHousing;

			var tabsInitialized;

			init();


			function init() {
				getTheTemporaryHousing();
			}

			function getTheTemporaryHousing() {
				theTemporaryHousingId = Db.Uotel.Util.UrlParam("Id");
				var filters = [new Db.Uotel.Entities.Filter("Id", theTemporaryHousingId)]

				Db.Uotel.Api.TemporaryHousing.getTemporaryHousing(1, 1, [], filters).setHandlers({
					success: function (data) {
						if (data.totalItemCount == 0) {
							alert("Temporary Housing Not Found");
							window.location = "";
						}

						theTemporaryHousing = data.collection.getItemAt(0);
						initTabs();

					},
					error: function (error) {
						// Do nothing for now
					}
				});


			}

			function initTabs() {
				initTabEventHandlers();
				initInfoTab();
//			initAvailableDatesTab();
			}

			function initInfoTab() {
				populateHousingInfo();
			}


			function initTabEventHandlers() {
				$("#btnHousingSave").click(handleHousingSaveButtonClicked)
			}

			function populateHousingInfo() {
				$(".infoName").val(theTemporaryHousing.Name).text(theTemporaryHousing.Name);
				$(".infoCategory").val(theTemporaryHousing.Category).text(theTemporaryHousing.Category);
				$(".infoAddress").val(theTemporaryHousing.Address).text(theTemporaryHousing.Address);
				$(".infoUrl").val(theTemporaryHousing.URL).text(theTemporaryHousing.URL);
				$(".infoPhoneNumber").val(theTemporaryHousing.PhoneNumber).text(theTemporaryHousing.PhoneNumber);
				$(".infoExpectedPrice").val(theTemporaryHousing.ExpectedPrice).text(theTemporaryHousing.ExpectedPrice);
				$(".infoYearBuilt").val(theTemporaryHousing.YearBuilt).text(theTemporaryHousing.YearBuilt);

				if (currentUserIsOwner()) {
					$(".divNonOwner").addClass("hidden");
					$(".divOwner").removeClass("hidden");
				} else {
					$(".infoAddress").text(theTemporaryHousing.Address.replace("\n", "<br\>"));
					$(".divOwner").addClass("hidden");
					$(".divNonOwner").removeClass("hidden");
				}
			}


			function initAvailableList() {

				table = dataTable.DataTable({
					"sDom": 'tr' +
					'<"footer-flex"' +
					'	<l>' +
					'	<"hidden-xs hidden-sm"i>' +
					'	<p>' +
					'>',
					"searching": false,
					"ordering": false,
					"aLengthMenu": [[25, 50, 100, 1000], [25, 50, 100, 1000]],
					"scrollY": "400px",
					"processing": true,
					"serverSide": true,
					"select": 'single',
					"ajax": function (data, callback, settings) {
						var filters = [new Db.Uotel.Entities.Filter("OwnerId", currentUserId)];
						var page = Db.Uotel.Util.calculatePage(data.start, data.length);
						var perPage = data.length;
						var draw = data.draw;

						Db.Uotel.Api.TemporaryHousing.getTemporaryHousing(page, perPage, [], filters, draw).setHandlers({
							success: function (data) {
								temporaryHousingCollection = data.collection;

								callback(data.toDTData([
									'Id',
									"Name",
									"Category",
									"Address",
									"URL",
									"PhoneNumber",
									"YearBuilt"
								]));
							},
							error: function (error) {
								// Do nothing for now
							}
						});
					},
					"columnDefs": getDataTableColumnDefinitions()
				});

				table.on('select', function (e, dt, type, index) {
					selectedTemporaryHousing = dataCollection.getItemAt(index)
					handleRowClicked();
				}).on('deselect', function (e, dt, type, index) {
					selectedTemporaryHousing = null;
					handleRowClicked();
				});
			}

			function getDataTableColumnDefinitions() {
				var columnDefinitions = [];
				var tempHousing = new Db.Uotel.Entities.TemporaryHousing();
				var properties = tempHousing.getProperties();

				var columns = [
					'Id',
					"Name",
					"Category",
					"Address",
					"URL",
					"PhoneNumber",
					"YearBuilt"
				];

				columns.forEach(function (columnName, i) {
					if (properties.hasOwnProperty(columnName)) {
						var property = properties[columnName];
						columnDefinitions.push({name: property.name, title: property.alias, targets: i});
					}
					else {
						columnDefinitions.push({name: columnName, title: columnName, targets: i});
					}

				});

				return columnDefinitions;
			}

			function handleRowClicked() {
				var buttonsForSingleItems = $(".btnSingleItem");
				var buttonLinksForSingleItems = $(".btnSingleItem a");
				if (selectedTemporaryHousing == null) {
					buttonsForSingleItems.addClass("disabled");
				}
				buttonsForSingleItems.removeClass("disabled");
				buttonLinksForSingleItems.each(function () {
					this.search = "?Id=" + selectedTemporaryHousing.Id;
				});
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

			function handleHousingSaveButtonClicked() {
				var saveBtn = $("#btnHousingSave");
				saveBtn.addClass("disabled");
				saveBtn.text("Saving");
				Db.Uotel.Api.TemporaryHousing.updateTemporaryHousing(getInputTempHousing()).setHandlers({
					success: function (updatedTempHousing) {
						theTemporaryHousing = updatedTempHousing;
						populateHousingInfo();
						saveBtn.removeClass("disabled");
						saveBtn.text("Save");
					},
					error: function (error) {
						alert(error.message);
						saveBtn.removeClass("disabled");
						saveBtn.text("Save");
					}
				});
			}

			function currentUserIsOwner() {
				if (!theTemporaryHousing) {
					return false;
				}

				return theTemporaryHousing.OwnerId === currentUserId;
			}

		</script>
	</jsp:body>
</t:default-template>