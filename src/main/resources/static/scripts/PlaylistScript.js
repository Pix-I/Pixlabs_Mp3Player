/**
 * Created by pix-i on 13/12/2016.
 */

var draggedSong;


function allowDrop(ev) {
    ev.preventDefault();
}

function songDrag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
    console.log(ev.dataTransfer.getData("text"));
}

function playlistDrop(ev, playlist, table) {
    ev.preventDefault();
    var data = ev.dataTransfer.getData("text");
    console.log("Playlist drop:" + playlist.id + "\\" + data);
    if (table == 'lower') {
        console.log("Lower table..");
        $("#LowerPlaylistPanel").load('/playlists/addSong/lower/' + playlist.id + '/' + data);
    } else if (table == 'upper') {
        $("#UpperPlaylistPanel").load('/playlists/addSong/upper/' + playlist.id + '/' + data);

    }
}


function loadUpperPlaylist(select) {
    var selectId = select.options[select.selectedIndex].value;
    // toSearch = toSearch.replace("_","U238183");
    selectId = selectId.replace(/\s/g, "_");
    upperSelect = select;
    $("#UpperPlaylistPanel").load('/playlists/loadUpper/' + select.id + '/' + selectId);
}
function loadLowerPlaylist(select) {
    var selectId = select.options[select.selectedIndex].value;
    selectId = selectId.replace(/\s/g, "_");
    lowerSelect = select;
    $("#LowerPlaylistPanel").load('/playlists/loadLower/' + select.id + '/' + selectId);
}


function playlistClickPlaySong(ev) {
    songname = ev.id;
    $.post('/player/playsong/' + songname);
    setTimeout(loadSong(), 240);
}


function searchUpperPlaylist(event) {
    var toSearch = event.value;

    toSearch = toSearch.replace("_", "U238183");
    toSearch = toSearch.replace(/\s/g, "_");
    upperSelect = 'search:' + toSearch;
    $("#UpperPlaylistPanel").load('/playlists/search/upper/' + toSearch);

}
function searchLowerPlaylist(event) {
    console.log("Lower");
    var toSearch = event.value;
    lowerSelect = 'search:' + toSearch;

    toSearch = toSearch.replace("_", "U238183");
    toSearch = toSearch.replace(/\s/g, "_");
    $("#LowerPlaylistPanel").load('/playlists/search/lower/' + toSearch);

}

function createNewPlaylist() {

    $("#LowerPlaylistPanel").load('/playlists/create');
}

function playlistControl(action) {
    console.log("Playlist control");
    if (lowerSelect == null) {
        lowerSelect = document.getElementsByName("lowerSelect")[0];
    }


    var selectId = lowerSelect.options[lowerSelect.selectedIndex].value;
    selectId = selectId.replace("_", "U238183");
    selectId = selectId.replace(/\s/g, "_");
    if (action == 'play') {
        $("#PlayerPanel").load('/playlists/play/' + lowerSelect.id + '/' + selectId);
    } else {
        $("#LowerPlaylistPanel").load('/playlists/' + action + '/' + lowerSelect.id + '/' + selectId);
    }

}
