/**
 * Created by pix-i on 13/12/2016.
 */

var opacity = 0.5;

function volumeChange() {
    var slider = document.getElementById("vRange").value;
    opacity = (parseFloat(slider) + 40.00) / 60.00;
    document.getElementById("vol").style.opacity = 1.00;
    document.getElementById("volPercentage").style.opacity = 1.00;
    document.getElementById("CoverImage").style.opacity = opacity;

    document.getElementById("volPercentage").innerHTML = ":" + parseInt((opacity * 100) - 16);

    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", '/volume/' + slider, true);
    xhttp.send();

}


function volumeChangeDrop() {
    document.getElementById("vol").style.opacity = 0.00;
    document.getElementById("volPercentage").style.opacity = 0.00;
}

function hoverScript() {

    document.getElementById("CoverImage").style.opacity = 1.00;
    document.getElementById("vol").style.opacity = 1.00;
    document.getElementById("volPercentage").style.opacity = 1.00;
}
function hoverEndScript() {
    document.getElementById("CoverImage").style.opacity = opacity;
    document.getElementById("vol").style.opacity = 0.00;
    document.getElementById("volPercentage").style.opacity = 0.00;
}