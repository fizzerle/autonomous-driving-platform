<template>
    <v-container fill-height fluid grid-list-xl>
        <v-layout justify-center wrap>
            <v-flex>
                <material-card>
                <div>
                    <v-select
                        v-model="selectedOem"
                        :items="oems"
                        @change="oemChanged"
                        chips
                        label="Please Select a Car Manufacturer"
                        >
                        <template v-slot:selection="data">
                            <v-chip
                            :key="JSON.stringify(data.item)"
                            :selected="data.selected"
                            :disabled="data.disabled"
                            class="v-chip--select-multi"
                            @click.stop="data.parent.selectedIndex = data.index"
                            @input="data.parent.selectItem(data.item)"
                            >
                            <v-avatar class="accent white--text">
                                {{ data.item.slice(0, 1).toUpperCase() }}
                            </v-avatar>
                            {{ data.item }}
                            </v-chip>
                        </template>
                    </v-select>
                    <v-select
                        v-if="cars.length > 0"
                        v-model="selectedCar"
                        :items="cars"
                        item-text="chassisnumber"
                        @change="carChanged"
                        chips
                        label="Please Select a Car"
                        >
                        <template v-slot:selection="data">
                            <v-chip
                            :key="JSON.stringify(data.item)"
                            :selected="data.selected"
                            :disabled="data.disabled"
                            class="v-chip--select-multi"
                            @click.stop="data.parent.selectedIndex = data.index"
                            @input="data.parent.selectItem(data.item)"
                            >
                            <v-avatar class="accent white--text">
                                {{ data.item.modelType.slice(0, 3) }}
                            </v-avatar>
                            {{ data.item.chassisnumber }}
                            </v-chip>
                        </template>
                    </v-select>
                </div>
                <div class="gmap_canvas">
                    <gmap-map
                            :center="center"
                            :zoom="12"
                            style="width:100%; height: 100%;">
                        <gmap-marker
                                :key="index"
                                v-for="(m, index) in markers"
                                :position="m.location"
                                :icon="m.icon"
                        ></gmap-marker>
                    </gmap-map>
                </div>
                </material-card>
                
            </v-flex>

            <v-flex md4 v-if="spaceAhead">
                <material-card>
                    <h6>Space Ahead: {{ spaceAhead }}</h6>
                    <h6>Space Behind: {{ spaceBehind }}</h6>
                    <h6>Speed: {{ speed }}</h6>
                    <h6>Passengers: {{ passengers }}</h6>
                </material-card>
            </v-flex>


            <v-snackbar
              color="error"
              :bottom="true"
              :top="false"
              :left="false"
              :right="true"
              v-model="snackbar"
              dark
            >
              <v-icon
                color="white"
                class="mr-3"
              >
                mdi-bell-plus
              </v-icon>
              <div v-if="notificationCrash">Crash in {{ getDistanceToCrash() }}m </div>
              <v-icon
                size="16"
                @click="snackbar = false;"
              >
                mdi-close-circle
              </v-icon>
            </v-snackbar>
        </v-layout>
    </v-container>

</template>
<script>
import {createLocation,toLatLng, moveTo, headingTo, distanceTo, getLatitude, getLongitude} from 'geolocation-utils'
import WebSocketClient from '../utils/WebSocketClient';
import { clearInterval, setInterval } from 'timers';
export default {
    data: () => ({
        selectedOem: null,
        oems: [],
        selectedCar: null,
        cars: [],
        myPosition: null,

        spaceAhead: null,
        spaceBehind: null,
        speed: null,
        passengers: null,

        center: { lat: 40.756, lng: -73.978 },
        crashes: [],
        markers: [],
        snackbar: null,
        notificationCrash: null,

        wsClient: null,
        wsPositionClient: null
    }),
    methods: {

        oemChanged: function() {
            // TODO: Load Cars from selected OEM
            console.info("OEM Changed: ", this.selectedOem);
            this.selectedCar = null;
            this.cars = [];
            fetch('/entitystorage/cars?oem=' + this.selectedOem)
                .then(resp => {
                    resp.json().then(data => {
                        console.info('Received oem cars', data);
                        this.cars = data;
                    });
                });
        },
        loadOems: function() {
            fetch('/entitystorage/oem')
                .then(resp => {
                    resp.json().then(data => {
                            console.info("Received oems events", data);
                            this.oems = data;
                    });
                });
        },
        carChanged: function() {
            console.info("Car Changed: ", this.selectedCar);
            if (this.wsClient !== null) {
                this.wsClient.close();
            }
            if (this.wsPositionClient !== null) {
                this.wsPositionClient.close();
            }
            this.wsClient = new WebSocketClient();
            this.wsClient.connectCar(this.selectedCar, this.receivedCrashData);
            this.wsPositionClient = new WebSocketClient();
            this.wsPositionClient.connectCarEvent(this.selectedCar, this.updateMyPosition);
            this.loadActualEventsForCar(this.selectedCar);
        },

        loadActualEventsForCar: function (car) {
            fetch('eventstorage/events?chassisnumber=' + car + '&limit=1')
                .then(resp => {
                    resp.json().then(data => {
                    let event = data[0];
                    console.log(event)
                    let loc = event.location;
                    //console.info('My position should be: ', loc);
                    this.myPosition = loc;
                    this.spaceAhead = event.spaceAhead;
                    this.spaceBehind = event.spaceBehind;
                    this.speed = event.speed;
                    this.passengers = event.passengers;
                    this.center = loc;
                    this.updateMarkers();
                });
            });
        },

        snack: function(crash) {
            console.log('Show snackbar babe', crash);
            this.snackbar = true;
            this.notificationCrash = crash;
        },
        
        updateMarkers: function() {
            this.markers = [];
            let count = 0;
            Array.from(this.crashes).forEach(cr => {
                if (!this.isInRadiusOf3Km(cr.location)) {
                    return;
                }
                count++;
                this.markers.push({
                    location: cr.location,
                    icon: {
                        url: "http://maps.google.com/mapfiles/ms/icons/red-dot.png"
                    }
                });
            });
            if (this.myPosition !== null) {
                this.markers.push({
                    location: this.myPosition,
                    icon: {
                        url: "https://cdn.pixabay.com/photo/2017/02/16/08/38/icon-2070748_960_720.png",
                        scaledSize: new google.maps.Size(22, 32)
                    }
                });
            }
            console.info("Updated " + count + " markers");
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
        isInRadiusOf3Km(location) {
            //headingDistanceTo
            if (this.myPosition === null) {
                return false;
            }
            let distance = distanceTo(this.myPosition, location);
            console.info('Position distance: ', distance);
            return distance < 3000;
        },
        getDistanceToCrash() {
            if (this.myPosition === null || this.notificationCrash === null) {
                return 0;
            }
            let distance = distanceTo(this.myPosition, this.notificationCrash.location);
            return Math.round(distance);
        },

        loadCrashData: function() {
            const v = this;
            fetch('/notificationstorage/notifications', {
                headers: {
                    'X-Client-Type': 'Car'
                }
            })
            .then(resp => {
                resp.json().then(data => {
                    console.info("Received car crash events", data);
                    this.crashes = data;
                    this.updateMarkers();
                });
            });
        },

        receivedCrashData: function(data) {
            let crash = JSON.parse(data.body);
            if (crash.active) {
                this.crashes.push(crash);
                this.snack(crash);
            } else {
                let cr = this.crashes.filter(cr => cr.location.lat === crash.location.lat && cr.location.lng === crash.location.lng).pop();
                if (cr !== undefined) {
                    this.crashes.splice(this.crashes.indexOf(cr), 1);
                }
            }
            console.info("Received Crash Event", crash);
            this.updateMarkers();            
        },

        updateMyPosition: function(data) {
            let event = JSON.parse(data.body);
            console.info('Received event from car', event);
            if (event.chassisNumber === this.selectedCar) {
                let loc = event.location;
                //console.info('My position should be: ', loc);
                this.myPosition = loc;
                this.spaceAhead = event.spaceAhead;
                this.spaceBehind = event.spaceBehind;
                this.speed = event.speed;
                this.passengers = event.passengers;
                this.center = loc;
                this.updateMarkers();
            }
        }

    },
    mounted: function() {
        this.loadCrashData();
        this.loadOems();
    },
    beforeDestroy: function() {
        this.wsClient.close();
        this.wsPositionClient.close();
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
