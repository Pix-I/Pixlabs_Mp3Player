/**
 * Created by pix-i on 08/12/2016.
 */
var crt;

var media;


$(function () {


    var parentElem = document.querySelectorAll("[id^='PARENT__']");
    for (var i = 0; i < parentElem.length; i++) {
        parentElem[i].style.backgroundColor = 'rgb(0, 71, 179)';
    }
    var fileElem = document.querySelectorAll("[id^='F__']");
    for (var j = 0; j < fileElem.length; j++) {
        fileElem[j].style.backgroundColor = '#257C99';
    }


    var mediapathElem = document.querySelectorAll("[id^='MEDIAPATH__']")[0];
    mediapathElem.style.backgroundColor = 'crimson';
    var soundPathElem = document.querySelectorAll("[id^='SOUNDPATH__']")[0];
    soundPathElem.style.backgroundColor = '#44448B';


});


function dragStart(event) {
    event.dataTransfer.setData('text', event.target.id);
    media = event.target.id;

}


function mediaPathDrop(event, object) {
    event.preventDefault();
    $("#LowerPlaylistPanel").load('/newPath/' + media + '/' + object.id);
}


function refresh() {
    //  $("#soundfilesTable").load('/loadTopTable');
}

function folderClick(id) {
    $("#LowerPlaylistPanel").load('/paths/' + id);
}

