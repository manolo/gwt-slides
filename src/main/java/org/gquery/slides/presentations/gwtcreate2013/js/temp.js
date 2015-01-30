$("p:last")
    .text("innerWidth:" + $("p:first"))
    .addClass("myClass");

$("#clickme").click(function () {
    $("#book").animate({
        opacity: 0.25,
        left: "+=50",
        height: "toggle"
    }, 5000, function () {
        // Animation complete.
    });
});


$("div").click(function () {
    var color = $(this).css("background-color");
    $("#result").html("That div is" +
        " <span style='color:" + color +
        ";'>" + color + "</span>.");
});


$(".article:not(.hidden) > .header")
    .width($("#mainHeader").innerWidth())
    .click(function () {
        $(this)
            .siblings(".content")
            .load("/article/" + $(this).parent().id(), null, function () {
                $(this).fadeIn(250);

            });
    });
