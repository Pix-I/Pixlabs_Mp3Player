/**
 * Created by pix-i on 06/12/2016.
 */

var my_canvas = document.getElementById("soundVisCanvas");
var context = my_canvas.getContext("2d");

function drawSong() {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", '/test', true);
    xhttp.send();
    //

    context.beginPath();
    context.moveTo(10, 100);
    context.lineTo(1600, 100);

    for (i = 0; i < song.length; i++) {
        context.moveTo(10 + i, 100);
        context.lineTo(10 + i, song[i] / 200);
    }
    context.stroke();
}

/*<![CDATA[*/
var song = /*[[${draw}]]*/ null;

/*]]>*/