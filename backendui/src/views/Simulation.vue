<template>
    <v-container fill-height fluid grid-list-xl>
        <v-layout justify-center wrap>
            <!-- Simulation Console to control the simulation and view car-positions in map -->
            <v-flex md6 consoleContainer>
                <h2>Simulation Console</h2>
                <div class="gmap_canvas">
                    <gmap-map
                            :center="center"
                            :zoom="12"
                            style="width:100%;  height: 100%;"
                    >
                        <gmap-marker
                                :key="index"
                                v-for="(m, index) in markers"
                                :position="m.position"
                                :label="m.label"
                                @click="center=m.position"
                                :icon="m.icon"
                        ></gmap-marker>
                    </gmap-map>
                </div>
                <div class="simConsole">
                    <v-container grid-list-md text-xs-left>
                        <v-layout row wrap>
                            <v-flex xs6>
                                <p>Minutes/Step:</p>
                            </v-flex>
                            <v-flex xs6>
                                <input v-model.number="simStepSize" type="number" v-tooltip="'Minutes passed per simulation step.'">
                            </v-flex>
                            <v-flex xs6>
                                <p>Automated simulation speed:</p>
                            </v-flex>
                            <v-flex xs6>
                                <input v-model.number="simSpeed" type="number" v-tooltip="'Number of simulationsteps executed per minute.'">
                            </v-flex>
                            <v-btn @click="onStep">Step</v-btn>
                            <v-btn @click="onStart">Start</v-btn>
                            <v-btn @click="onReset">Reset</v-btn>
                            <p style="font-size: 20px; color: green; font-weight: bold; padding-left: 20px; padding-top: 15px" v-if="ended">Finished Simulation</p>
                        </v-layout>
                    </v-container>
                </div>
            </v-flex>

            <!-- Simulated Car configuration -->
            <v-flex md5 consoleContainer>
                <h2>Configuration</h2>
                <v-expansion-panel>
                    <v-expansion-panel-content v-for="(car, i) in cars" :key="i">
                        <template v-slot:header>
                            <div style="width: 80%">{{car.name}}</div>
                            <v-btn small icon @click.native.stop="removeCar(i)">
                                <v-icon>delete</v-icon>
                            </v-btn>
                        </template>
                        <v-card>
                            <v-card-text>Start: {{car.start}}<br>End: {{car.end}}<br>Speed: {{car.speed}}</v-card-text>
                        </v-card>
                    </v-expansion-panel-content>
                </v-expansion-panel>
                <v-form v-model="valid">
                    <v-container>
                        <v-layout>
                            <v-flex
                                    xs12
                                    md4
                            >
                                <v-text-field
                                        v-model="startCoord"
                                        :rules="coordRules"
                                        label="Start Coordinates"
                                        required
                                ></v-text-field>
                            </v-flex>

                            <v-flex
                                    xs12
                                    md4
                            >
                                <v-text-field
                                        v-model="endCoord"
                                        :rules="coordRules"
                                        label="End Coordinates"
                                        required
                                ></v-text-field>
                            </v-flex>

                            <v-flex
                                    xs12
                                    md4
                            >
                                <v-text-field
                                        v-model="speed"
                                        :rules="speedRules"
                                        label="Speed in km/h"
                                        required
                                ></v-text-field>
                            </v-flex>
                        </v-layout>
                    </v-container>
                    <v-btn @click="addCar">submit</v-btn>
                </v-form>
            </v-flex>
        </v-layout>
    </v-container>

</template>
<script>
import {createLocation,toLatLng, headingDistanceTo, moveTo, headingTo, distanceTo} from 'geolocation-utils'
export default {
    data: () => ({
        started: false,
        ended: false,
        running: false,
        valid: false,
        center: { lat: 40.756, lng: -73.978 },
        markers: [],
        simStepSize: 1,
        simSpeed: 10,
        speed:'',
        startCoord:'',
        endCoord:'',
        coordRules: [
            v => !!v || 'Coordinate is required',
            v => /^-?[0-9][0-9]?(\.[0-9]*)?, -?[0-9][0-9]?(\.[0-9]*)?$/.test(v) || 'Coordinates must be valid'
        ],
        speedRules: [
            v => !!v || 'Speed is required',
            v => /^[0-9][0-9]?[0-9]?$/.test(v) || 'Speed must be a number with maximal 3 digits.'
        ],
        cars: [
            {
                name: "car1",
                start: "40.731444, -73.996990",
                end:  "40.803244, -73.944627",
                speed: 40
            },
            {
                name: "car2",
                start: "40.803244, -73.944627",
                end:  "40.731444, -73.996990",
                speed: 50
            }
        ],
        selectedCar: null,
        simulationCars: [],


    }),
    methods: {
        removeCar: function (index) {
            this.cars.splice(index, 1);
        },
        addCar: function () {
            if (this.valid) {
                this.cars.push({name: "car"+ (this.cars.length+1), startCoord: this.start, endCoord: this.end, speed: this.speed});
            }
        },
        createSimulationCars: function () {
            this.simulationCars = [];
            for (const car of this.cars) {
                /* start and end */
                var start = this.locationFromString(car.start);
                var destination = this.locationFromString(car.end);
                var velocity = car.speed;
                var currentposition = start;
                var heading = headingTo(start,destination);
                var distance = distanceTo(start,destination);
                this.simulationCars.push({
                    label: car.name.substring(3),
                    start: start,
                    dest: destination,
                    vel: velocity,
                    cpos: currentposition,
                    heading: heading,
                    distance: distance,
                    reached: false
                });
            }
            this.markCurrentLocations();
        },
        locationFromString: function(string) {
            var help = string.split(', ');
            var lat = parseFloat(help[0]);
            var lng = parseFloat(help[1]);
            return createLocation(lat,lng,'LatLng')
        },
        markCurrentLocations: function() {
            this.markers = [];
            for (const car of this.simulationCars) {
                this.markers.push({
                    position: car.cpos,
                    label: car.label
                })
            }
        },
        simulateStepForCar: function(car) {
            if (!car.reached) {
                var distance = 1000 * (this.simStepSize/60) * car.vel;
                car.cpos = toLatLng(moveTo(car.cpos, {distance: distance, heading: car.heading}));
                if (car.distance <= distanceTo(car.start, car.cpos)) {
                    car.reached = true;
                    car.cpos = car.dest;
                }
            }
        },
        checkReached: function () {
            for (var i = 0; i < this.simulationCars.length; i++) {

                if (this.simulationCars[i].reached == false) {
                    return false;
                }
            }
            return true;
        },
        onStep: function () {
            if (!this.started) {
                this.createSimulationCars();
                this.started = true;
            } else {
                if (this.running) {

                } else {
                    for (var i = 0; i < this.simulationCars.length; i++) {
                        this.simulateStepForCar(this.simulationCars[i]);
                    }
                    this.markCurrentLocations();
                }
            }
            if (this.checkReached()) {
                this.ended = true;
            }
        },
        onStart: function () {

        },
        onStop: function () {

        },
        onReset: function() {
            this.running = false;
            this.createSimulationCars();
            this.started = true;
            this.ended = false;
        }


    },
    mounted:function() {
        this.createSimulationCars();
    },
}

</script>
<style>
    .gmap_canvas {
        height:30em;
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