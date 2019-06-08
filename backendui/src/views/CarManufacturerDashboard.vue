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
        </v-select>
        <v-dialog
          v-model="dialog"
          width="600">
          <template v-slot:activator="{ on }">
            <v-btn
              color="success"
              dark
              v-on="on"
            >
            Create Car
            </v-btn>
          </template>

          <v-card>
          <v-form v-model="valid">
                  <v-container>
                      <v-layout row wrap>
                        <v-flex
                              xs12
                              md4
                        >
                          <v-text-field
                                  id="oemField"
                                  v-model="newOem"
                                  :rules="oemRule"
                                  label="OEM"
                                  required
                          ></v-text-field>
                        </v-flex>

                          <v-flex
                              xs12
                              md4
                          >
                            <v-text-field
                                  id="modelTypeField"
                                  v-model="newModelType"
                                  :rules="modelRule"
                                  label="Modeltype"
                                  required
                            ></v-text-field>
                          </v-flex>

                        <v-flex
                                xs12
                                md4
                        >
                            <v-text-field
                                    id="chassisNumberField"
                                    v-model="newChassisnumber"
                                    :rules="chassisRule"
                                    label="Chassis Number"
                                    required
                            ></v-text-field>
                        </v-flex>

                        <v-flex
                                xs12
                                md4
                        >
                            <v-btn @click="addCar">submit</v-btn>
                        </v-flex>
                      </v-layout>
                  </v-container>
                  
              </v-form>
          </v-card>
        </v-dialog>
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
                            :icon="getIcon(car)"
                    ></gmap-marker>
                </gmap-map>
              </div>
            </div>
          </div>
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
              <div v-if="notificationCrash">{{ notificationCrash.oem + " " + notificationCrash.modelType + ": " + notificationCrash.description}}</div>
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

    newOem: "",
    newChassisnumber:"",
    newModelType:"",
    newCar: null,
    valid: false,

    snackbar: null,
    notificationCrash: null,

    wsClientCrash: null,
    wsClientEvent: null,
    oemRule: [
        v => !!v || 'OEM is required.'
    ],
    chassisRule: [
        v => !!v || 'Chassis number is required.'
    ],
    modelRule: [
        v => !!v || 'Modeltype is required.'
    ],
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
                    this.sortCrashes();
                });
            });
    },
    loadActualEventsForCars: function(car) {
      this.cars.forEach(car => {
        fetch('eventstorage/events?chassisnumber=' + car.chassisnumber + '&limit=1')
        .then(resp => {
            resp.json().then(data => {
                  Vue.set(car, 'location', data[0].location);
                  Vue.set(car, 'crashEvent', event.crashEvent);
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
          this.crashes.splice(0,0,crash);
          this.snack(crash);
        } else {
          Vue.set(updateCrash, 'resolveTimestamp', crash.resolveTimestamp)
        }
        this.sortCrashes();
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
        Vue.set(updateCar, 'crashEvent', event.crashEvent);
        Vue.set(updateCar, 'location', event.location);
    },
    sortCrashes: function() {
      console.log("Sorting crashes....")
        this.crashes = this.crashes.sort( (a, b) => {
            if (a.timestamp > b.timestamp) {
                return -1;
            } else if (a.timestamp < b.timestamp) {
                return 1;
            }
            return 0;
        });
    },
    formatDateSmall: function(date) {
      if (date == null) {
        return "Not Resolved!"
      }
      return moment(date).format('D.M h:mm a');
    },
    getIcon: function(car) {
      let red = "http://maps.google.com/mapfiles/ms/icons/red-dot.png";
      let blue = "http://maps.google.com/mapfiles/ms/icons/blue-dot.png";

      if (car.crashEvent != null) {
        return red;
      }
      return blue;
    },

    snack: function(crash) {
        fetch('/entitystorage/cars/' + crash.chassisnumber).then(resp => {
            resp.json().then(car => {
              this.snackbar = true;
              this.notificationCrash = {
                  oem: car.oem,
                  chassisnumber: car.chassisnumber,
                  modelType: car.modelType,
                  description: crash.description,
                  location: crash.location
              };
              console.log('Show snackbar babe', this.notificationCrash);
            });
        });        
    },

    addCar: function () {
      if (this.valid) {
        this.newCar = {oem: this.newOem, chassisnumber: this.newChassisnumber, modelType: this.newModelType}
        fetch('entitystorage/cars', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.newCar)
        })
        .then(resp => {
            resp.json().then(data => {
              if(resp.ok) {
                alert("Created car");
                console.log(JSON.stringify(data));
                this.loadOems();
                if (this.selectedOem == this.newCar.oem) {
                  this.loadCarsOfOem(this.selectedOem);
                }
              } else {
                console.log(JSON.stringify(data));
                alert(data.status + ": " + data.message);
              }
            });            
        });
        
      }
    }
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