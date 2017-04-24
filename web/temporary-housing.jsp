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
		<style>
			#divFeedback > ul > li{
				border-style: solid;
				border-width: 1px;
				border-color: #eee;
				padding: 5px;
				margin: 5px;
			}
		</style>

		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#tabInfo" aria-controls="info" role="tab" data-toggle="tab">Info</a>
			</li>
			<li role="presentation"><a id="tabLinkAvailablePeriods" href="#tabAvailablePeriods" aria-controls="profile" role="tab" data-toggle="tab">Available
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
				<div class="clearfix center-block"></div>
				<hr>
				<h3 style="margin-top: 10px; margin-bottom: 10px">Feedback</h3>
				<div class="row">
					<div class="col-sm-offset-1 col-sm-11">
						<div id="divFeedback"></div>
						<button id="btnShowFeedbackDialog" class="btn btn-default" role="button">Add Feedback</button>
					</div>
				</div>
			</div>
			<div role="tabpanel" class="tab-pane" id="tabAvailablePeriods">
				<div class="container">
					<div class="col-md-9">
						<div class="form-group">
							<table id="tblAvailablePeriods" class="table table-striped table-bordered table-hover table-condensed">
							</table>
						</div>
					</div>
					<div class="divOwner col-md-3 btn-column">
						<button class="btn btn-default" id="btnShowAddAvailablePeriod" role="button">Add Available Period</button>
					</div>
				</div>
			</div>
		</div>

		<div class="modal fade" id="modalNewAvailablePeriod" tabindex="-1" role="dialog">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title">New Available Period</h4>
					</div>
					<div class="modal-body">

						<div class="row">
							<div class="col-md-6">
								<div class="form-group">
									<label for="inpPeriodFrom">From</label>
									<input type="date" class="form-control" id="inpPeriodFrom" placeholder="From">
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="inpPeriodTo">To</label>
									<input type="date" class="form-control" id="inpPeriodTo" placeholder="To">
								</div>
							</div>
							<div class="col-md-12">
								<div class="form-group">
									<label for="inpPricePerNight">Price Per Night</label>
									<input type="number" class="form-control" id="inpPricePerNight" placeholder="140">
								</div>
							</div>
						</div>

					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" id="btnCreateAvailablePeriod">Create</button>
					</div>
				</div><!-- /.modal-content -->
			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->

		<div class="modal fade" id="modalNewFeedback" tabindex="-1" role="dialog">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title">Feedback</h4>
					</div>
					<div class="modal-body">

						<div class="row">
							<div class="col-md-6">
								<div class="form-group">
									<label for="inpScore">Score</label>
									<input type="number" max="10" min="0" class="form-control" id="inpScore" placeholder="0-10">
								</div>
							</div>
							<div class="col-xs-12">
								<div class="form-group">
									<label for="inpText">Message</label>
									<textarea rows="3" class="form-control" id="inpText"></textarea>
								</div>
							</div>
						</div>

					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" id="btnCreateFeedback">Create</button>
					</div>
				</div><!-- /.modal-content -->
			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->

		<script>
			var currentUserId = ${pageContext.session.getAttribute("CurrentUser").getId()};
			var dataTable = $("#tblAvailablePeriods");
			var table;

			var theTemporaryHousingId;
			var theTemporaryHousing;

			var feedbackCollection;

			var btnShowFeedbackDialog = $("#btnShowFeedbackDialog");
			var btnCreateFeedback = $("#btnCreateFeedback");

			var tabsInitialized;

			init();


			function init() {
				getTheTemporaryHousing();
			}

			function getTheTemporaryHousing() {
				theTemporaryHousingId = Db.Uotel.Util.UrlParam("Id");
				var filters = [new Db.Uotel.Entities.Filter("Id", theTemporaryHousingId)];

				Db.Uotel.Api.TemporaryHousing.getTemporaryHousing(1, 1, [], filters, []).setHandlers({
					success: function (data) {
						if (data.totalItemCount == 0) {
							alert("Temporary Housing Not Found");
							window.location = "";
						}


						getTheFeedbackCollection();

						theTemporaryHousing = data.collection.getItemAt(0);
						initTabs();

					},
					error: function (error) {
						// Do nothing for now
					}
				});
			}

			function getTheFeedbackCollection() {
				theTemporaryHousingId = Db.Uotel.Util.UrlParam("Id");
				var filters = [new Db.Uotel.Entities.Filter("TemporaryHousingId", theTemporaryHousingId)];

				Db.Uotel.Api.Feedback.getFeedback(1, 0, [], filters, ["User"]).setHandlers({
					success: function (data) {
						feedbackCollection = data.collection;
						console.log(feedbackCollection);
						var collectionRenderer = Db.Uotel.Renderers.FeedbackCollectionRender(feedbackCollection);
						var container = $("#divFeedback");

						btnShowFeedbackDialog.click(showAddFeedbackModal);
						btnCreateFeedback.click(handleCreateFeedbackButtonClicked);

						container.empty();
						container.append(collectionRenderer.Container);
					},
					error: function (error) {
						// Do nothing for now
					}
				});
			}

			function initTabs() {
				initTabEventHandlers();
				populateHousingInfo();
				initInfoTab();
				initAvailablePeriodsTab();
			}

			function initInfoTab() {
			}

			function initAvailablePeriodsTab() {
				initAvailablePeriodsList()
			}


			function initTabEventHandlers() {
				$("#btnHousingSave").click(handleHousingSaveButtonClicked);
				$("#btnShowAddAvailablePeriod").click(showAddAvailablePeriodModal);
				$("#btnCreateAvailablePeriod").click(handleAvailablePeriodCreateButtonClicked);
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


			function initAvailablePeriodsList() {

				table = dataTable.DataTable({
					"sDom": 'tr' +
					'<"footer-flex"' +
					'	<l>' +
					'	<"hidden-xs hidden-sm"i>' +
					'	<p>' +
					'>',
					"searching": false,
					"order": [[ 0, "desc" ]],
					"aLengthMenu": [[25, 50, 100, 1000], [25, 50, 100, 1000]],
					"scrollY": "400px",
					"processing": true,
					"serverSide": true,
					"ajax": function (data, callback, settings) {
						var filters = [new Db.Uotel.Entities.Filter("TemporaryHousingId", theTemporaryHousing.Id)];
						var sorters = Db.Uotel.Util.parseSortersFromDataTableData(data);
						var relationsToPopulate = ["Period"];
						var page = Db.Uotel.Util.calculatePage(data.start, data.length);
						var perPage = data.length;
						var draw = data.draw;

						Db.Uotel.Api.AvailablePeriods.getAvailablePeriods(page, perPage, sorters, filters, relationsToPopulate, draw).setHandlers({
							success: function (data) {
								temporaryHousingCollection = data.collection;

								callback(data.toDTData([
									['Period.From', formatDate],
									['Period.To', formatDate],
									'PricePerNight'
								]));
							},
							error: function (error) {
								// Do nothing for now
							}
						});
					},
					"columnDefs": getDataTableColumnDefinitions()
				});


				$("#tabLinkAvailablePeriods").on('show.bs.tab', function() {table.columns.adjust().draw();});
			}

			function resetAvailablePeriodsList() {
				table.ajax.reload();
			}

			function formatDate(value, entity) {
				return (value && value !== "null") ?((value instanceof Date) ? value.formatUtc('n/j/Y') : new Date(value).formatUtc('n/j/Y')) : "\u2014";
			}

			function getDataTableColumnDefinitions() {
				var AvailablePeriod = new Db.Uotel.Entities.AvailablePeriod();
				var columns = [
					'Period.To',
					"Period.From",
					"PricePerNight"
				];

				return AvailablePeriod.getColumnDefinitions(columns);
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

			function getInputNewAvailablePeriodTempHousing() {
				var newPeriod = new Db.Uotel.Entities.Period();
				newPeriod.From = $("#inpPeriodFrom").val();
				newPeriod.To = $("#inpPeriodTo").val();

				var newAvailablePeriod = new Db.Uotel.Entities.AvailablePeriod();
				newAvailablePeriod.TemporaryHousing = theTemporaryHousing;
				newAvailablePeriod.TemporaryHousingId = theTemporaryHousing.Id;
				newAvailablePeriod.Period = newPeriod;
				newAvailablePeriod.PricePerNight = $("#inpPricePerNight").val();

				return newAvailablePeriod;
			}

			function handleAvailablePeriodCreateButtonClicked() {
				var saveBtn = $("#btnCreateAvailablePeriod");
				saveBtn.addClass("disabled");
				saveBtn.text("Creating");
				Db.Uotel.Api.AvailablePeriods.createAvailablePeriod(getInputNewAvailablePeriodTempHousing()).setHandlers({
					success: function (updatedTempHousing) {
						resetAvailablePeriodsList();
						$("#modalNewAvailablePeriod").modal('hide');
						$("#inpPeriodFrom").empty();
						$("#inpPeriodTo").empty();
						$("#inpPricePerNight").empty();
						saveBtn.removeClass("disabled");
						saveBtn.text("Create");
					},
					error: function (error) {
						alert(error.message);
						saveBtn.removeClass("disabled");
						saveBtn.text("Create");
					}
				});
			}

			function showAddAvailablePeriodModal() {
				$("#modalNewAvailablePeriod").modal();
			}

			function showAddFeedbackModal() {
				$("#modalNewFeedback").modal();
			}

			function handleCreateFeedbackButtonClicked() {
				var feedback = new Db.Uotel.Entities.Feedback();
				feedback.TemporaryHousingId = theTemporaryHousingId;
				feedback.UserId = currentUserId;
				feedback.Score = $("#inpScore").val();
				feedback.Text = $("#inpText").val();

				var now = new Date();
				feedback.Date = now.format("Y-m-d");

				btnCreateFeedback.addClass("disabled");
				btnCreateFeedback.text("Creating");
				Db.Uotel.Api.Feedback.createFeedback(feedback).setHandlers({
					success: function () {
						getTheFeedbackCollection();
						$("#modalNewFeedback").modal('hide');
						$("#inpScore").val("");
						$("#inpText").val("");
						btnCreateFeedback.text("Created");
					},
					error: function (error) {
						alert(error.message);
						btnCreateFeedback.removeClass("disabled");
						btnCreateFeedback.text("Create");
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