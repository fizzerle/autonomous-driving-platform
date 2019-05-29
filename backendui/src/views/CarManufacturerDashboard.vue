<template>
  <v-container
    fill-height
    fluid
    grid-list-xl
  >
    <v-layout
      justify-center
      wrap
    >
      <v-flex xs12>
        <v-select
          v-model="selectedOem"
          :items="oems"
          @change="oemChanged"
          chips
          label="Please Select a Car Manufacturer"
        >
          <!--<template v-slot:selection="data">
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
          </template>-->
        </v-select>
      </v-flex>
      <v-flex
        md6
      >
        <material-card
          :text="'This is the list of cars for car manufacturer '+ selectedOem"
          :title="'List of Cars for '+ selectedOem"
          color="green"
        >
          <v-data-table
            :headers="headers"
            :items="cars"
          >
            <template
              slot="headerCell"
              slot-scope="{ header }"
            >
              <span
                class="subheading font-weight-light text-success text--darken-3"
                v-text="header.text"
              />
            </template>
            <template
              slot="items"
              slot-scope="{ item }"
            >
              <td>{{ item.chassisnumber }}</td>
              <td>{{ item.modelType }}</td>
            </template>
          </v-data-table>
        </material-card>
      </v-flex>
      <v-flex
        md6
      >
        <material-card
          :text="'List of Crash Events for '+ selectedOem"
          :title="'This is the list of Crash Events for '+ selectedOem"
          color="red"
        >
          <v-data-table
            :headers="oemHeaders"
            :items="crashes"
          >
            <template
              slot="headerCell"
              slot-scope="{ header }"
            >
              <span
                class="subheading font-weight-light red--text text--darken-3"
                v-text="header.text"
              />
            </template>
            <template
              slot="items"
              slot-scope="{ item }"
            >
              <td>{{ item.chassisnumber }}</td>
              <td>{{ formatDateSmall(item.timestamp) }}</td>
              <td>{{ item.description }}</td>
              <td>{{ formatDateSmall(item.resolveTimestamp) }}</td>
            </template>
          </v-data-table>
        </material-card>
      </v-flex>
      <v-flex
        md12
      >
        <material-card
          :title="'Map for the manufacturer '+ selectedOem"
          color="blue"
        >
        <div>
          <div class="mapouter">
            <div class="gmap_canvas">
              <gmap-map
                      :center="center"
                      :zoom="12"
                      style="width:100%;  height: 100%;"
              >
                  <gmap-marker
                          :key="index"
                          v-for="(car, index) in cars"
                          :position="car.location"
                          :label="car.chassisnumber"
                          @click="center=car.location"
                  ></gmap-marker>
              </gmap-map>
            </div>
          </div>
        </div>
                </material-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import {createLocation,toLatLng, moveTo, headingTo, distanceTo, getLatitude, getLongitude} from 'geolocation-utils'
import WebSocketClient from '../utils/WebSocketClient';
import * as moment from 'moment'
import { setTimeout } from 'timers';
import { constants } from 'crypto';
import Vue from 'vue'

export default {
  data: () => ({
    selectedOem: null,
    oems: [],
    cars:[],
    crashes:[],
    center: { lat: 40.756, lng: -73.978 },

    wsClientCrash: null,
    wsClientEvent: null,

    headers: [
      {
        sortable: false,
        text: 'Fahrgestellnummer',
        value: 'chassisnumber'
      },
      {
        sortable: false,
        text: 'Modelltyp',
        value: 'modelType'
      }
    ],
    oemHeaders: [
      {
        sortable: false,
        text: 'Fahrgestellnummer',
        value: 'chassisnumber'
      },
      {
        sortable: false,
        text: 'Datum',
        value: 'timestamp'
      },
      {
        sortable: false,
        text: 'Beschreibung',
        value: 'description',
      }, 
      {
        sortable: false,
        text: 'Behoben',
        value: 'resolveTimestamp'
      }
    ],
    items: [
      {
        id: '1',
        type: 'B',
        people: '6',
        speed: '75 km/h'
      }
    ]
  }),
  methods: {

    oemChanged: function() {
      if (this.wsClientCrash) {
        this.wsClientCrash.close();
      }
      if (this.wsClientEvent) {
        this.wsClientEvent.close();
      }
      this.wsClientCrash = new WebSocketClient();
      this.wsClientCrash.connectOemCrash(this.selectedOem, this.receivedCrashData);
      this.wsClientEvent = new WebSocketClient();
      this.wsClientEvent.connectOemEvent(this.selectedOem, this.receivedEventData);
      this.loadCarsOfOem(this.selectedOem);
      this.loadNotificationsOfOem(this.selectedOem);
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
    loadCarsOfOem: function(oem) {
        fetch('entitystorage/cars?oem=' + oem)
        .then(resp => {
          resp.json().then(data => {
                this.cars = data;
                this.loadActualEventsForCars();
          });
      });
      
    },
    loadNotificationsOfOem: function(oem) {
      fetch('/notificationstorage/notifications?oem=' + oem, {
                headers: {
                    'X-Client-Type': 'OEM'
                }
            })
            .then(resp => {
                resp.json().then(data => {
                    console.info("Received bluelight crash events", data);
                    this.crashes = data;
                });
            });
    },
    loadActualEventsForCars: function(car) {
      this.cars.forEach(car => {
        fetch('eventstorage/events?chassisnumber=' + car.chassisnumber + '&limit=1')
        .then(resp => {
            resp.json().then(data => {
                  Vue.set(car, 'speed', data[0].speed + ' km/h');
                  Vue.set(car, 'passengers', data[0].passengers);
                  Vue.set(car, 'location', data[0].location);
            });
        });
      });
    },
    receivedCrashData: function(data) {
        let crash = JSON.parse(data.body);
        console.info('Received crash update', crash);
        let updateCrash = this.crashes.find(function(element) {
          return element.crashId == crash.crashId;
        })
        console.log("MATCH:",JSON.stringify(updateCrash))
        if (updateCrash == null) {
          this.crashes.push(crash)
        } else {
          Vue.set(updateCrash, 'resolveTimestamp', crash.resolveTimestamp)
        }
    },
    receivedEventData: function(data) {
        let event = JSON.parse(data.body);
        console.info('Received event update', event);
        let updateCar = this.cars.find(function(element) {
          return element.chassisnumber == event.chassisNumber;
        });
        if (updateCar == null) {
          this.cars.push(
            {
              chassisnumber: event.chassisNumber,
              modelType: event.modelType,
              oem: event.oem
            }
          )
        }
        Vue.set(updateCar, 'speed', event.speed + ' km/h');
        Vue.set(updateCar, 'passengers', event.passengers);
        Vue.set(updateCar, 'location', event.location);
    },
    formatDateSmall: function(date) {
      if (date == null) {
        return "Not Resolved!"
      }
      return moment(date).format('D.M h:mm a');
    },
  },
  
  mounted: function() {
      this.loadOems();
  },
  beforeDestroy: function() {
      if (this.wsClientCrash) {
        this.wsClientCrash.close();
      }
      if (this.wsClientEvent) {
        this.wsClientEvent.close();
      }
  }
}
</script>
<style>
.gmap_canvas {
  height:40em;
  width: auto;
}
</style>