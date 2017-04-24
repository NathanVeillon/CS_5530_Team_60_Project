<%--
  Created by IntelliJ IDEA.
  User: Student Nathan
  Date: 4/8/2017
  Time: 4:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:default-template>
  <jsp:attribute name="defaultTitle">
    All Housing
  </jsp:attribute>
  <jsp:body>
    <div class="row" id="divFilters">
      <h4>Filters</h4>
      <div class="col-md-6">
        <div class="form-group">
          <label for="fltrAddress">Address</label>
          <textarea rows="2" class="form-control infoAddress" id="fltrAddress" placeholder="1234 E. Example Street
City, State 12345"></textarea>
        </div>
        <div class="form-group">
          <label for="fltrKeyword">Keyword</label>
          <input type="text" class="form-control infoCategory" id="fltrKeyword"
                 placeholder="Shiny">
        </div>
      </div>
      <div class="col-md-6">
        <div class="form-group">
          <label for="fltrPriceLow">Price</label>
          <div class="form-inline row">
            <div class="col-xs-5">
            <input type="number" class="form-control" id="fltrPriceLow" placeholder="10">
            </div>
            <div class="col-xs-2">
            <p class="form-control-static text-center" >-</p>

            </div>
            <div class="col-xs-5">
            <input type="number" class="form-control" id="fltrPriceHigh" placeholder="9000">
            </div>
          </div>
        </div>

        <div class="form-group">
          <label for="fltrCategory">Category</label>
          <input type="text" class="form-control infoCategory" id="fltrCategory"
                 placeholder="Category (Ex. House)">
        </div>
      </div>
    </div>
    <hr/>
    <div class="row">
      <div class="col-md-9">
        <div class="form-group">
          <table id="tblTemporaryHousing" class="table table-striped table-bordered table-hover table-condensed"></table>
        </div>
      </div>
      <div class="col-md-3 btn-column">
        <a class="btn btn-default disabled btnSingleItem" href="${pageContext.request.contextPath}/temporary-housing.jsp" role="button">Go To Housing</a>
        <btn class="btn btn-default disabled btnSingleItem" id="btnFavoriteSelectedHousing" role="button">Favorite</btn>

      </div>
    </div>

    <script>
		var currentUserId = ${pageContext.session.getAttribute("CurrentUser").getId()};
		var dataTable = $("#tblTemporaryHousing");
		var table;

		var temporaryHousingCollection;
		var selectedTemporaryHousing;

		initOwnedHousingList();
		$("#btnFavoriteSelectedHousing").click(createFavoriteForCurrentUser);
		$("#divFilters input").keyup(reloadTable);
		$("#divFilters textarea").keyup(reloadTable);
		$("#divFilters select").change(reloadTable);

		function initOwnedHousingList() {

			table = dataTable.DataTable({
				"searching": false,
				"paging":   false,
				"info":     false,
				"aLengthMenu": [[25, 50, 100, 1000], [25, 50, 100, 1000]],
				"scrollY": "400px",
				"serverSide": true,
				"select": 'single',
				"ajax": function (data, callback, settings) {
					var sorters = Db.Uotel.Util.parseSortersFromDataTableData(data);
					var draw = data.draw;
					var relationshipsToPopulate = ["Favorites", "Feedback", "Feedback.User.TargetUserTrust"];

					Db.Uotel.Api.TemporaryHousing.getTemporaryHousing(1, 0, sorters, getFilters(), relationshipsToPopulate, draw).setHandlers({
						success: function(data) {
							temporaryHousingCollection = data.collection;

							callback(data.toDTData([
								'Id',
								"Name",
								"Category",
								"Address",
								"PhoneNumber",
								"ExpectedPrice",
								getAverageFeedbackScore,
								getAverageTrustedFeedbackScore,
								isFavorited
							]));
						},
						error: function(error) {
							// Do nothing for now
						}
					});
				},
				"columnDefs": getDataTableColumnDefinitions()
			});

			table.on( 'select', function ( e, dt, type, index ) {
				selectedTemporaryHousing = temporaryHousingCollection.getItemAt(index)
				handleRowClicked();
			}).on( 'deselect', function ( e, dt, type, index ) {
				selectedTemporaryHousing = null;
				handleRowClicked();
            });
		}

		function getFilters() {
            var filters = [];

            var addressVal = $("#fltrAddress").val();
            if(addressVal){
            	filters.push(new Db.Uotel.Entities.Filter("Address", "%"+addressVal+"%", "LIKE"));
            }

            var lowPriceVal = $("#fltrPriceLow").val();
            if(lowPriceVal){
            	filters.push(new Db.Uotel.Entities.Filter("ExpectedPrice", lowPriceVal, "GREATER_EQUAL"));
            }

            var highPriceVal = $("#fltrPriceHigh").val();
            if(highPriceVal){
            	filters.push(new Db.Uotel.Entities.Filter("ExpectedPrice", highPriceVal, "LESS_EQUAL"));
            }

            var keywordVal = $("#fltrKeyword").val();
            if(keywordVal){
            	filters.push(new Db.Uotel.Entities.Filter("TemporaryHousingKeywordMaps.Keyword.Word", keywordVal+"%", "LIKE"));
            }

            var categoryVal = $("#fltrCategory").val();
            if(categoryVal){
            	filters.push(new Db.Uotel.Entities.Filter("Category", categoryVal+"%", "LIKE"));
            }

            return filters;
		}

		function reloadTable() {
            table.ajax.reload();
		}

		function isFavorited(value, entity) {
            var favoriteCol = entity.Favorites;
            if(favoriteCol === null){
            	return "\u2014";
            }

            var userFavorite = favoriteCol.getFirstByProperty("UserId", currentUserId);

            if(userFavorite){
            	return "Yes";
            }

            return "\u2014";
		}

		function getAverageFeedbackScore(value, entity) {
            var feedbackCol = entity.Feedback;
            if(feedbackCol == null || feedbackCol.getCount() == 0){
            	return "\u2014";
            }

            var totalScore = 0;
            feedbackCol.each(function (feedback) {
            	totalScore += parseInt(feedback.Score);
            });

            return Math.round(totalScore/feedbackCol.getCount()*100)/100;
		}

		function getAverageTrustedFeedbackScore(value, entity) {
            var feedbackCol = entity.Feedback;
            if(feedbackCol == null || feedbackCol.getCount() == 0){
            	return "\u2014";
            }

            var totalScore = 0;
            var totalCount = 0;
            feedbackCol.each(function (feedback) {
            	if(feedback.User == null || feedback.User.TargetUserTrust == null){
            		return;
                }
                var feedbackUserTrust = feedback.User.TargetUserTrust;
            	var currentUserTrust = feedbackUserTrust.getFirstByProperty("SourceUserId", currentUserId);


                if(currentUserTrust && currentUserTrust.SourceTrustsTarget){
					totalScore += parseInt(feedback.Score);
					totalCount++;
				}
            });

            if(totalCount == 0){
				return "\u2014";
            }

            return Math.round(totalScore/totalCount*100)/100;
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
				"PhoneNumber",
				"ExpectedPrice",
				["AverageFeedbackScore", "Score"],
				["AverageTrustedFeedbackScore", "Trusted Score"],
				["Favorites.Date", "Fav." , {"orderable":false}]
			];

			return tempHousing.getColumnDefinitions(columns);
		}

		function handleRowClicked() {
			var buttonsForSingleItems = $(".btnSingleItem");
			var buttonLinksForSingleItems = $("a.btnSingleItem");
			if(selectedTemporaryHousing == null){
				buttonsForSingleItems.addClass("disabled");
				return;
			}
			buttonsForSingleItems.removeClass("disabled");
			buttonLinksForSingleItems.each(function () {
				this.search = "?Id="+selectedTemporaryHousing.Id;
			});
		}

		function createFavoriteForCurrentUser() {
			if(!selectedTemporaryHousing){
				alert("No Selected Housing");
				return
            }
            var btnFav = $("#btnFavoriteSelectedHousing");
			btnFav.addClass("disabled");
			btnFav.text("Favoriting");

			var fav = new Db.Uotel.Entities.Favorite();
			var now = new Date();
			fav.Date = now.format("Y-m-d");
			fav.UserId = currentUserId;
			fav.TemporaryHousingId = selectedTemporaryHousing.Id;

			Db.Uotel.Api.Favorite.createFavorite(fav).setHandlers({
                success: function () {
					btnFav.text("Favorite");
                    table.ajax.reload();
				},
                error: function (error) {
                    alert(error.message);
                    btnFav.text("Favorite");
                    btnFav.removeClass("disabled");
				}
            })
		}

    </script>
  </jsp:body>
</t:default-template>