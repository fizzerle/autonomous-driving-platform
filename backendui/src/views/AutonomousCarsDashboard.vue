<template>
    <v-container fill-height fluid grid-list-xl>
        <v-layout justify-center wrap>
            <v-flex>
                <material-card>
                <div class="gmap_canvas">
                    <gmap-map
                            :center="center"
                            :zoom="12"
                            style="width:100%; height: 100%;">
                        <gmap-marker
                                :key="index"
                                v-for="(m, index) in markers"
                                :position="m.crash.location"
                                :icon="m.icon"
                        ></gmap-marker>
                    </gmap-map>
                </div>
                </material-card>
                
            </v-flex>
        </v-layout>
    </v-container>

</template>
<script>
import {createLocation,toLatLng, moveTo, headingTo, distanceTo, getLatitude, getLongitude} from 'geolocation-utils'
import WebSocketClient from '../utils/WebSocketClient';
export default {
    data: () => ({
        center: { lat: 40.756, lng: -73.978 },
        crashes: [],
        list: [],
        markers: [],

        wsClient: null
    }),
    methods: {

        updateView: function() {
            this.list = [];
            this.crashes.forEach(cr => {
                this.list.push({
                    location: this.locationFromString(cr.location)
                });
            });
            this.updateMarkers();
        },
        updateMarkers: function() {
            this.markers = [];
            for (const cr of this.list) {
                this.markers.push({
                    crash: cr,
                    icon: {
                        url: "http://maps.google.com/mapfiles/ms/icons/red-dot.png"
                    }
                });
            }
        },
        locationFromString: function(loc) {
            if (typeof(loc) === "object") {
                return createLocation(loc.lat, loc.lng, 'LatLng');
            }
            let help = loc.split(', ');
            let lat = parseFloat(help[0]);
            let lng = parseFloat(help[1]);
            return createLocation(lat, lng, 'LatLng')
        },

        loadCrashData: function() {
            fetch('/notificationstorage/notifications', {
                headers: {
                    'X-Client-Type': 'Car'
                }
            })
            .then(resp => {
                resp.json().then(data => {
                    console.info("Received car crash events", data);
                    this.$data = data;
                    this.updateView();
                });
            });
        },

        receivedCrashData: function(data) {
            let crash = JSON.parse(data.body);
            if (crash.active) {
                this.crashes.push(crash);
            } else {
                let cr = this.crashes.filter(cr => cr.location.lat === crash.location.lat && cr.location.lng === crash.location.lng).pop();
                if (cr !== undefined) {
                    this.crashes.splice(this.crashes.indexOf(cr), 1);
                }
            }
            console.info("Received Socket Data", crash);
            this.updateView();            
        }

    },
    mounted: function() {
        this.loadCrashData();
        this.wsClient = new WebSocketClient();
        this.wsClient.connectCar(this.receivedCrashData);
    },
    beforeDestroy: function() {
        this.wsClient.close();
    }
}

</script>
<style>
    .gmap_canvas {
        height: 40em;
        width: auto;
    }
    .simConsole{
        border: lightgray;
        border-width: 1px;
        border-style: solid;
        border-radius:2px;
        width: auto;
    }
</style>
