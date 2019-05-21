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

        center: { lat: 40.756, lng: -73.978 },
        crashes: [],
        markers: [],

        wsClient: null,
        positionUpdateInterval: null
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
            //this.stopPositionUpdate();
            //this.startPositionUpdate();

        },
        
        updateMarkers: function() {
            this.markers = [];
            let count = 0;
            this.crashes.forEach(cr => {
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
            } else {
                let cr = this.crashes.filter(cr => cr.location.lat === crash.location.lat && cr.location.lng === crash.location.lng).pop();
                if (cr !== undefined) {
                    this.crashes.splice(this.crashes.indexOf(cr), 1);
                }
            }
            console.info("Received Socket Data", crash);
            this.updateMarkers();            
        },

        stopPositionUpdate: function() {
            console.info('Stoping position update of interval', this.positionUpdateInterval);
            clearInterval(this.positionUpdateInterval);
            this.positionUpdateInterval = null;
            if (this.positionUpdateInterval !== null) {
                console.info('Realy stopping!');
                
            }
        },
        startPositionUpdate: function() {
            //this.stopPositionUpdate();
            this.positionUpdateInterval = setInterval(this.updateMyPosition, 100);                
        },
        updateMyPosition: function() {
            const chassis = this.selectedCar;
            if (chassis === null) {
                return;
            }
            fetch('/eventstorage/events?limit=1&chassisnumber=' + chassis)
            .then(resp => {
                console.info("Received eventupdates", resp);
                resp.json().then(data => {
                    if (data.length > 0) {
                        let event = data[0];
                        console.info('Received event from car', event);
                        if (event.chassisNumber === this.selectedCar) {
                            let loc = event.location;
                            console.info('My position should be: ', loc);
                            this.myPosition = loc;
                            this.center = loc;
                            this.updateMarkers();
                        }
                    }
                });
            });
        }

    },
    mounted: function() {
        this.loadCrashData();
        this.loadOems();
        this.startPositionUpdate();
        this.wsClient = new WebSocketClient();
        this.wsClient.connectCar(this.receivedCrashData);
    },
    beforeDestroy: function() {
        this.wsClient.close();
        this.stopPositionUpdate();
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
