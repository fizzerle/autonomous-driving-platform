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
        crashes: [
            { active: true, oem: "Audi", model: "A8", chassis: "B567GK", occupants: 4, timestamp: "1.5.19 20:44", location: "40.731444, -73.996990" },
            { active: false, oem: "Fiat", model: "Punto", chassis: "ASDF1", occupants: 1, timestamp: "8.5.19 20:44", location: "40.721444, -73.986990" },
            { active: false, oem: "Audi", model: "A6", chassis: "XXX7", occupants: 2, timestamp: "2.5.19 20:44", location: "40.791444, -73.986990" },
            { active: true, oem: "Ford", model: "Fokus", chassis: "QWERT5", occupants: 3, timestamp: "3.5.19 20:44", location: "40.701444, -74.006990" },
            { active: false, oem: "BMW", model: "Z4", chassis: "ZZZDF73", occupants: 4, timestamp: "5.5.19 20:44", location: "40.731444, -73.896990" }
        ],
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
        locationFromString: function(string) {
            let help = string.split(', ');
            let lat = parseFloat(help[0]);
            let lng = parseFloat(help[1]);
            return createLocation(lat,lng,'LatLng')
        },

        receivedCrashData: function(data) {
            let crash = JSON.parse(data.body);
            crash.timestamp = new Date(crash.timestamp);
            if (typeof(crash.location) === "string") {
                // Convert to LatLng
            }
            if (crash.active) {
                this.crashes.push(crash);
            } else {
                let cr = this.crashes.filter(cr => cr.location === crash.location).pop();
                if (cr !== undefined) {
                    this.crashes.splice(this.crashes.indexOf(cr), 1);
                }
            }
            console.info("Received Socket Data", crash);
            this.updateView();            
        }

    },
    mounted:function() {
        this.updateView();
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
