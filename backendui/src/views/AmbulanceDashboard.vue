<template>

    <v-container fill-height fluid grid-list-xl>
        <v-layout justify-center wrap>
            
            <v-flex md6>
                <v-flex>
                    <material-card>
                    <div class="gmap_canvas">
                        <gmap-map
                                :center="center"
                                :zoom="12"
                                style="width:100%;  height: 100%;">
                            <gmap-marker
                                    :key="index"
                                    v-for="(m, index) in markers"
                                    :position="m.crash.location"
                                    @click="select(m.crash)"
                                    :icon="m.icon"
                            ></gmap-marker>
                        </gmap-map>
                    </div>
                    </material-card>
                    
                </v-flex>

                <v-flex v-if="selected != null">
                    <material-card :color="selected.resolveTimestamp === null ? 'red' : 'blue'" 
                        :title="selected.oem + ' ' + selected.modeltype"
                        :text="crashTimestamps()">
                        <h4>Occupants: {{ selected.passengers }}</h4>
                        <h6>Chassis Number: {{ selected.chassisnumber }}</h6>
                        <v-btn v-if="selected.resolveTimestamp === null" class="mx-0 font-weight-light" color="success" @click="resolveCrash()">Resolve Crash</v-btn>
                    </material-card>
                </v-flex>
            </v-flex>

            <v-flex md4>
                <material-card
                        class="card-tabs"
                        color="green">
                <v-flex slot="header">
                    <v-tabs
                    v-model="tabs"
                    color="transparent"
                    slider-color="white">
                    <v-tab class="mr-3" @click="listAll()">All</v-tab>
                    <v-tab class="mr-3" @click="listActive()">Active</v-tab>
                    <v-tab class="mr-3" @click="listInactive()">Inactive</v-tab>
                    </v-tabs>
                </v-flex>

                <v-tabs-items v-model="tabs">
                    <v-tab-item>
                    <v-list three-line>
                        <v-list-tile v-for="(cr, i) in list" :key="i" @click="select(cr)">
                        <v-list-tile-action>
                            <v-icon v-if="cr.resolveTimestamp === null" slot="activator">mdi-alarm-light</v-icon>
                        </v-list-tile-action>
                        <v-list-tile-title>
                            {{ formatDateSmall(cr.timestamp) }}
                        </v-list-tile-title>
                        <v-list-tile-sub-title>
                            {{ cr.oem }} {{ cr.modeltype }}
                        </v-list-tile-sub-title>
                        </v-list-tile>
                        <v-divider/>
                    </v-list>

                    </v-tab-item>
                </v-tabs-items>
                </material-card>
            </v-flex>

            <v-snackbar
              color="error"
              :bottom="true"
              :top="false"
              :left="false"
              :right="true"
              v-model="snackbar"
              @click="select(notificationCrash)"
              dark
            >
              <v-icon
                color="white"
                class="mr-3"
              >
                mdi-bell-plus
              </v-icon>
              <div v-if="notificationCrash">{{ notificationCrash.oem + " " + notificationCrash.modeltype }} (Occupants: {{notificationCrash.passengers}})</div>
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
import * as moment from 'moment'
import WebSocketClient from '../utils/WebSocketClient';

const FILTER_ALL = 0;
const FILTER_ACTIVE = 1;
const FILTER_INACTIVE = 2;

export default {
    data: () => ({        
        center: { lat: 40.756, lng: -73.978 },
        crashes: [],
        list: [],
        filter: FILTER_ALL,
        markers: [],
        selected: null,
        snackbar: null,
        notificationCrash: null,

        wsClient: null
    }),
    methods: {

        listAll: function() {
            this.list = [];
            this.crashes.forEach(cr => {
                this.list.push({
                    crashId: cr.crashId,
                    resolveTimestamp: cr.resolveTimestamp,
                    oem: cr.oem,
                    modeltype: cr.modeltype,
                    chassisnumber: cr.chassisnumber,
                    passengers: cr.passengers,
                    timestamp: cr.timestamp,
                    location: this.locationFromString(cr.location)
                });
            });
            this.filter = FILTER_ALL;
            this.sortList();
            this.updateMarkers();
        },
        listActive: function() {
            this.list = [];
            this.crashes.forEach(cr => {
                if (cr.resolveTimestamp === null) {
                    this.list.push({
                        crashId: cr.crashId,
                        resolveTimestamp: null,
                        oem: cr.oem,
                        modeltype: cr.modeltype,
                        chassisnumber: cr.chassisnumber,
                        passengers: cr.passengers,
                        timestamp: cr.timestamp,
                        location: this.locationFromString(cr.location)
                    });
                }
            });
            if (this.selected !== null && this.selected.resolveTimestamp !== null) {
                this.selected = null;
            }
            this.filter = FILTER_ACTIVE;
            this.sortList();
            this.updateMarkers();
        },
        listInactive: function() {
            this.list = [];
            this.crashes.forEach(cr => {
                if (cr.resolveTimestamp !== null) {
                    this.list.push({
                        crashId: cr.crashId,
                        resolveTimestamp: cr.resolveTimestamp,
                        oem: cr.oem,
                        modeltype: cr.modeltype,
                        chassisnumber: cr.chassisnumber,
                        passengers: cr.passengers,
                        timestamp: cr.timestamp,
                        location: this.locationFromString(cr.location)
                    });
                }
            });
            if (this.selected !== null && this.selected.resolveTimestamp === null) {
                this.selected = null;
            }
            this.filter = FILTER_INACTIVE;
            this.sortList();
            this.updateMarkers();
        },
        sortList: function() {
            this.list = this.list.sort( (a, b) => {
                if (a.timestamp > b.timestamp) {
                    return -1;
                } else if (a.timestamp < b.timestamp) {
                    return 1;
                }
                return 0;
            });
        },
        updateMarkers: function() {
            this.markers = [];
            for (const cr of this.list) {
                this.markers.push({
                    crash: cr,
                    icon: {                             
                        url: this.getIcon(cr)
                    }
                });
            }
        },
        updateList: function() {
            if (this.filter === FILTER_ALL) {
                this.listAll();
            } else if (this.filter === FILTER_ACTIVE) {
                this.listActive();
            } else if (this.filter === FILTER_INACTIVE) {
                this.listInactive();
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
        formatDateSmall: function(date) {
            return moment(date).format('D.M h:mm a');
        },
        formatDateFull: function(date) {
            return moment(date).format('DD.MM.YYYY hh:mm:ss a');
        },
        crashTimestamps: function() {
            let start = this.formatDateFull(this.selected.timestamp);
            if (this.selected.resolveTimestamp) {
                return start + ' - ' + this.formatDateFull(this.selected.resolveTimestamp);
            }
            return start;
        },
        select: function(item) {
            this.center = item.location;
            this.selected = item;
            
            this.markers.forEach(m => {
                m.icon = this.getIcon(m.crash);
            });
        },
        getIcon: function(cr) {
            let red = "http://maps.google.com/mapfiles/ms/icons/red-dot.png";
            let blue = "http://maps.google.com/mapfiles/ms/icons/blue-dot.png";
            let yellow = "http://maps.google.com/mapfiles/ms/icons/yellow-dot.png";

            if (this.selected && this.selected.crashId === cr.crashId) {
                return yellow;
            } else if (cr.resolveTimestamp) {
                return blue;
            }
            return red;
        },
        resolveCrash: function() {
            if (!this.selected) {
                return;
            }

            fetch('/notificationstorage/notifications/' + this.selected.crashId, {
                method: 'PATCH'
            })
            .then(resp => {
                this.loadCrashData();
                this.selected.resolveTimestamp = new Date();
            });
            /* // OLD:
            this.crashes.forEach(cr => {
                if (cr.crashId === this.selected.crashId){
                    cr.resolveTimestamp = new Date();
                    // TODO: Send Request to resolve selected crash
                }
            });
            this.updateList(); */
        },

        loadCrashData: function() {
            const v = this;
            fetch('/notificationstorage/notifications', {
                headers: {
                    'X-Client-Type': 'Bluelight'
                }
            })
            .then(resp => {
                resp.json().then(data => {
                    console.info("Received bluelight crash events", data);
                    this.crashes = data;
                    this.updateList();
                });
            });
        },

        snack: function(crash) {
            console.log('Show snackbar babe', crash);
            this.snackbar = true;
            this.notificationCrash = crash;
        },

        testMeth: function() {
            console.info('TEST ASSDF');
        },

        receivedCrashData: function(data) {
            let crash = JSON.parse(data.body);
            crash.timestamp = new Date(crash.timestamp);
            if (crash.resolveTimestamp === null) {
                this.crashes.push(crash);
                this.snack(crash);
            } else {
                let cr = this.crashes.filter(cr => cr.crashId === crash.crashId).pop();
                console.info("found crash with id", cr);
                if (cr !== undefined) {
                    this.crashes.splice(this.crashes.indexOf(cr), 1);
                    this.crashes.push(crash);
                    console.info("updated crash", crash);
                }
            }
            this.updateList();
            console.info("Received Socket Data", crash);
        }

    },
    mounted:function() {
        this.loadCrashData();
        this.wsClient = new WebSocketClient();
        this.wsClient.connectBluelight(this.receivedCrashData);
    },
    beforeDestroy: function() {
        this.wsClient.close();
    }
}

</script>
<style>
    .gmap_canvas {
        height:25em;
        width: auto;
    }
    .simConsole{
        border: lightgray;
        border-width: 1px;
        border-style: solid;
        border-radius:2px;
        width: auto;
    }
    .consoleContainer {
        border: black;
        border-width: 1px;
        border-radius: 5px;
        border-style: solid;
        background: white;
        margin: 5px;
    }
</style>
