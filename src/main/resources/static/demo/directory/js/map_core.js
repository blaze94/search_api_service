/**
 * Created by jihoon on 2015. 7. 22..
 */
"use strict";
var $ = jQuery.noConflict();

function createHomepageGoogleMap(pt,d,zoom,data){
    var locations = new Array();
    var contents = new Array();
    var types = new Array();
    var images = new Array();
    var _latitude='';
    var _longitude='';
    if (pt != '') {
        var arrPt=pt.toString().split(',');
        _latitude=arrPt[0];
        _longitude=arrPt[1];
    } else {
        _latitude = 37.498004414546934;
        _longitude = 127.02770621963765;
    }

    var map = new L.Map('google-map', {center: new L.LatLng(_latitude,_longitude), zoom: zoom,zoomControl: false});
    var control=L.control.zoom( {position:'bottomleft'});
    map.scrollWheelZoom.disable();
    map.addControl(control);

    var markers = L.markerClusterGroup();
    var fitMarkers = L.markerClusterGroup();
    var googleLayer = new L.Google('ROADMAP');
    map.addLayer(googleLayer,true);

    if(data){
        var icon_name = "";
        $.each(data, function(i, val){
            var lat=data[i].lat;
            var lng=data[i].lng;
            var title=data[i].title;
            var category = data[i].category;
            var telephone = data[i].telephone;
            var address = data[i].address;
            var itemImgPath = '';
            if(!data[i].itemThumImgPath){
                itemImgPath=data[i].resultThumImgPath;
            } else {
                itemImgPath=data[i].itemThumImgPath;
            }

            //alert(data[i].itemThumImgPath);
            var categoryCode=data[i].categoryCode;
            if (categoryCode == '005001') {
                icon_name = "coffee";
            } else if(categoryCode == '001004') {
                icon_name = "shirt";
            } else if(categoryCode='005002'){
                icon_name = "restaurant";
            }
            //alert(geoLocation[0]+geoLocation[1]);
            var title = '<div width="200"><ul><li>' + title + '</li><li>' + category + '</li><li>' + telephone + '</li><li>' + address + '</li></ul></div>';
            var marker = L.marker(L.latLng(lat, lng), { title: title });

            marker.bindPopup(title);
            markers.addLayer(marker);
            fitMarkers.addLayer(marker);
        })
        if (pt !='' && pt != null) {
            var humanIcon = L.icon({
                iconUrl: 'http://maps.google.com/mapfiles/ms/micons/man.png',
                iconRetinaUrl: 'http://maps.google.com/mapfiles/ms/micons/man.png'
            });
            var myMarker = L.marker(L.latLng(_latitude, _longitude), {
                icon: humanIcon
            });
            L.circle(L.latLng(_latitude, _longitude), d*1000, {opacity: 0.1, fillOpacity: 0.1}).addTo(map);
            map.addLayer(myMarker);
            fitMarkers.addLayer(myMarker);
        }
        map.addLayer(markers);
        setTimeout(function() {
            map.invalidateSize(false);
            map.fitBounds(fitMarkers.getBounds(),{padding:L.point(30, 30)});

        }, 1000);



        //markers.addLayers(markerList);
    }
}

function doSearch() {
    var q=document.getElementById('q').value;
    var fq=document.getElementById('fq').value;
    var start=document.getElementById('start').value;
    var pt=document.getElementById('pt').value;
    var d=document.getElementById('d').value;

    if (start == "") {
        start = 0;
    }
    if (pt == "") {
        pt = "37.498004414546934,127.02770621963765";
        document.getElementById('pt').value = pt;
    }

    if (start == "") {
        start = 0;
    }

    if (d == "") {
        d = 10;
    }

    var jsonPath = '/web/api/select?q='+q+'&fq='+fq+'&start='+start+'&pt='+pt+'&d='+d;
    //history.pushState("", document.title, "/search?q="+q+"&fq="+fq+"&location="+location+"&pt="+pt);
    $.getJSON(jsonPath)
        .done(function (data) {
            createHomepageGoogleMap(pt,d,13,data);
        })
        .fail(function (jqxhr, textStatus, error) {
            console.log(error);
        })
    ;
}


function redrawMap(map){
    map._onResize();
    //
    // $('#map_view').click(function() {
    //     map._onResize();
    // });

}