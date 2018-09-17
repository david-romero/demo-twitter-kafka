// Shorthand for $( document ).ready()
$(function() {
    console.log( "ready!" );
    getStats()
    getActive()
    getTweetCount()
});

function getStats(){
    $.get( "http://localhost:8080/influencer/stats ", function( response ) {
            console.log(response);
            var $table = $("#influencers > tbody")
            $table.html("")
            var html = ""
            $.each(response, function(i, influencer) {
                html += "<tr>"
                    + "<td>" + (i + 1 ) + "</td>"
                    + "<td>" + influencer.username + "</td>"
                    + "<td>" + influencer.tweets + "</td>"
                    + "<td>" + influencer.content + "</td>"
                    + "<td>" + influencer.likes + "</td>"
                    + "</tr>";
            });
            $table.html(html)
        });
}

function getActive(){
    $.get( "http://localhost:8080/influencer/active ", function( response ) {
            console.log(response);
            var $table = $("#active > tbody")
            $table.html("")
            var html = ""
            $.each(response, function(i, influencer) {
                html += "<tr>"
                    + "<td>" + (i + 1 ) + "</td>"
                    + "<td>" + influencer.username + "</td>"
                    + "<td>" + influencer.tweets + "</td>"
                    + "<td>" + influencer.content + "</td>"
                    + "<td>" + influencer.likes + "</td>"
                    + "</tr>";
            });
            $table.html(html)
        });
}

function getTweetCount(){
    $.get( "http://localhost:8080/influencer/count ", function( response ) {
            console.log(response);
            var $span = $("#total")
            $span.html(response)
        });
}

window.setInterval(function(){
  getStats()
  getActive()
  getTweetCount()
}, 5000);