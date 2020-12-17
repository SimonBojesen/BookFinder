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

function postReview() {

}