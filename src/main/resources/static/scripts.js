function getAverageRating(ratingElement,amountElement,bookId) {
    $.ajax({
        url: '/review/' + bookId,
        type: "GET",
        data: bookId,
        success: function (data) {
            if(data.model.averageRating != 0){
                ratingElement.html("Average rating "+data.model.averageRating);
                ratingElement.show();

            }else{
                ratingElement.hide();
            }
            amountElement.html("Amount of reviews: "+data.model.totalAmount);


        }, error: function (data) {
            let errorMessage = "";
            if (data.responseJSON.error) {
                $(ratingElement).hide();
                $(amountElement).hide();
            } else if (data.responseJSON.failed) {
                $(ratingElement).hide();
                $(amountElement).hide();
            }

        }
    })
}

function ajaxSubscribe(requestType) {
    var id = $("#bookId").val();
    var bookId = {bookId: id};
    $.ajax({
        url: '/book/subscribe',
        type: requestType,
        data: bookId,
        success: function (data) {
            $("#subscribe").toggle();
            $("#unsubscribe").toggle();
        }, error: function (data) {
            let errorMessage = "";
            if (data.responseJSON.error)
                errorMessage = data.responseJSON.error;
            else if (data.responseJSON.failed)
                errorMessage = data.responseJSON.failed;
            alert(errorMessage);
        }
    })
}
function ajaxRating(isbn13, rating, id) {
    var postData = {isbn13: isbn13, rating: rating};
    $.ajax({
        url: '/review',
        type: "POST",
        data: postData,
        success: function (data) {
            findBookDetails(id)
        }, error: function (data) {
            let errorMessage = "";
            if (data.responseJSON.error)
                errorMessage = data.responseJSON.error;
            else if (data.responseJSON.failed)
                errorMessage = data.responseJSON.failed;
            alert(errorMessage);
        }
    })
}

function findBookDetails(id, divId) {
    $.get("/bookdetails/" + id, function (data) {
        $(divId).html(data);
        var exist = $("#bookId").data("exists");
        if (exist) {
            $("#subscribe").hide();
            $("#unsubscribe").show();
        }
    }).done(function () {
        var rating = $(".rating");
        var amount = $(".ratingamount");
        getAverageRating(rating, amount, id);
        if ($("#ISBN_13").length == 0) {
            $("#ratingForm").hide();
        }
    });
}

function searchForBooks(query, maxResults, startIndex) {
    if (query == "") {
        alert("You must type a query")
    } else {
        $("#savedQuery").val(query);
        $.get("/booklist/" + query + "/" + startIndex + "/" + maxResults, function (data) {
            $("#searchResults").html(data);
        }).done(function () {
            var idElements = $(".bookid");

            for (let i = 0; i < idElements.length; i++) {
                let id = idElements[i].value;
                var rating = $(".bookid[value=" + id + "]").parent().find(".rating");
                var amount = $(".bookid[value=" + id + "]").parent().find('.ratingamount');
                getAverageRating(rating, amount, id);
            }

        });
    }
}

function paginationForward() {
    const currentIndex = parseInt($("#currentStartIndex").val());
    const maxResults = parseInt($("#maxResults").val());
    let newIndex = currentIndex + maxResults;
    let query = $("#savedQuery").val();
    searchForBooks(query, maxResults, newIndex);
}

function paginationBackwards() {
    const currentIndex = parseInt($("#currentStartIndex").val());
    const maxResults = parseInt($("#maxResults").val());
    let newIndex = currentIndex - maxResults;
    let query = $("#savedQuery").val();
    searchForBooks(query, maxResults, newIndex);
}