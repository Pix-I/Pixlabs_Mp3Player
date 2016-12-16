/**
 * Created by pix-i on 13/12/2016.
 */
var songname = "__STOPPED__";

var origin = '__SOUNDPATH__';

var upperSelect;
var lowerSelect;

function loadSong() {
    $("#PlayerPanel").load('/getPlayerPanel');
}

function optionButtonF() {


    if (document.getElementById("OptionSpan").innerHTML == "Playlist") {

        document.getElementById("OptionSpan").innerHTML = "Files";

        $("#LowerPlaylistPanel").load('/playlists/loadLower/playlists/history');
    } else {
        document.getElementById("OptionSpan").innerHTML = "Playlist";
        $("#LowerPlaylistPanel").load('/options');

        // Load playlist stuff


    }
}

function player(action) {
    $("#PlayerPanel").load(action);
}

function playerRepeat() {
    $("#thRepeatSpan").load('/repeat');
}


window.onload = function () {
    getTime();
};

var timeLeft;


function getTime() {
    timeLeft = 30000;
    $.get("/getTimeLeft", function (data) {

        if (data.timeLeft > 100 && songname != data.song) {
            $("#PlayerPanel").load('/getPlayer');
            songname = data.song;
            timeLeft = data.timeLeft;
        } else {
            timeLeft = 30000;
        }

    }, "json");
    setTimeout(getTime, timeLeft);
}

