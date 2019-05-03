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
                            <v-btn v-if="!running" @click="onStart">Start</v-btn>
                            <v-btn v-if="running" @click="onStop">Stop</v-btn>
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
                            <v-card-text>Start: {{car.start}}<br>
                                End: {{car.end}}<br>
                                Speed: {{car.speed}}<br>
                                OEM: {{car.oem}}<br>
                                Chassis number: {{car.chassis}}<br>
                                <template v-if="car.crash">Crashes at destination</template></v-card-text>
                        </v-card>
                    </v-expansion-panel-content>
                </v-expansion-panel>
                <v-form v-model="valid">
                    <v-container>
                        <v-layout row wrap>
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



                        <v-flex
                                xs12
                                md4
                        >
                            <v-text-field
                                    v-model="oem"
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
                                    v-model="model"
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
                                    v-model="chassis"
                                    :rules="chassisRule"
                                    label="Chassis Number"
                                    required
                            ></v-text-field>
                        </v-flex>

                        <v-flex
                                xs12
                                md4
                        >
                            <input type="checkbox" id="crashCheckbox" v-model="crash">
                            <label for="crashCheckbox">crashes</label>
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
/* eslint-disable */
import {createLocation,toLatLng, moveTo, headingTo, distanceTo, getLatitude, getLongitude} from 'geolocation-utils';
import WebSocketClient from '../utils/WebSocketClient';
export default {
    data: () => ({
        started: false,
        ended: false,
        running: false,
        valid: false,
        center: { lat: 40.756, lng: -73.978 },
        markers: [],
        simStepSize: 1,
        simSpeed: 120,
        speed:'',
        crash:'',
        startCoord:'',
        endCoord:'',
        oem:'',
        chassis:'',
        model:'',
        coordRules: [
            v => !!v || 'Coordinate is required',
            v => /^-?[0-9][0-9]?(\.[0-9]*)?, -?[0-9][0-9]?(\.[0-9]*)?$/.test(v) || 'Coordinates must be valid'
        ],
        speedRules: [
            v => !!v || 'Speed is required',
            v => /^[0-9][0-9]?[0-9]?$/.test(v) || 'Speed must be a number with maximal 3 digits.'
        ],
        crashRules: [
            v => /^[0-9]*$/.test(v) || 'Crash must be a number.'
        ],
        oemRule: [
            v => !!v || 'OEM is required.'
        ],
        chassisRule: [
            v => !!v || 'Chassis number is required.'
        ],
        modelRule: [
            v => !!v || 'Modeltype is required.'
        ],
        cars: [
            {
                name: "car1",
                start: "40.731444, -73.996990",
                end:  "40.803244, -73.944627",
                speed: 40,
                crash: false,
                oem: "Audi",
                model: "A8",
                chassis: "000"
            },
            {
                name: "car2",
                start: "40.731501, -73.996928",
                end:  "40.803244, -73.944627",
                speed: 40,
                crash: false,
                oem: "BMW",
                model: "i8 Coupe",
                chassis: "001"
            },
            {
                name: "car3",
                start: "40.731472, -73.996947",
                end:  "40.767396, -73.970741",
                speed: 40,
                crash: true,
                oem: "Opel",
                model: "Astra",
                chassis: "002"
            },
            {
                name: "car4",
                start: " 40.742530, -73.988865",
                end:  "40.803244, -73.944627",
                speed: 40,
                crash: false,
                oem: "Audi",
                model: "Q2",
                chassis: "003"
            },
            {
                name: "car5",
                start: " 40.746545, -73.986014",
                end:  "40.803244, -73.944627",
                speed: 40,
                crash: false,
                model: "r8",
                oem: "Audi",
                chassis: "004"
            },
            {
                name: "car6",
                start: " 40.709557, -73.297039",
                end:  "40.719489, -73.267445",
                speed: 10,
                crash: false,
                model: "x7",
                oem: "BMW",
                chassis: "005"
            },
        ],
        selectedCar: null,
        simulationCars: [],
        autoSimulation: null,
        crashTypes: [
            "Crash with a cyclist",
            "Collision with other car",
            "Dodged an elephant and hit a street lamp",
            "Hit by a bird",
            "Tire blow-out"
        ]

    }),
    methods: {
        removeCar: function (index) {
            this.cars.splice(index, 1);
        },
        addCar: function () {
            if (this.valid) {
                this.cars.push({name: "car"+ (this.cars.length+1), startCoord: this.start, endCoord: this.end, speed: this.speed, crash: this.crash, oem: this.oem, chassis: this.chassis});
            }
        },
        createSimulationCars: function () {
            this.simulationCars = [];
            for (const car of this.cars) {
                /* start and end */
                let start = this.locationFromString(car.start);
                let destination = this.locationFromString(car.end);
                let velocity = car.speed;
                let currentposition = start;
                let heading = headingTo(start,destination);
                let distance = distanceTo(start,destination);
                this.simulationCars.push({
                    label: car.name.substring(3),
                    start: start,
                    dest: destination,
                    vel: velocity,
                    cpos: currentposition,
                    heading: heading,
                    distance: distance,
                    reached: false,
                    crash: car.crash,
                    oem: car.oem,
                    model: car.model,
                    chassis: car.chassis,
                    passengers: Math.floor(Math.random() * 5) + 1
                });
            }
            this.markCurrentLocations();
        },
        locationFromString: function(string) {
            let help = string.split(', ');
            let lat = parseFloat(help[0]);
            let lng = parseFloat(help[1]);
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
                let distance = 1000 * (this.simStepSize/60) * car.vel;
                car.cpos = toLatLng(moveTo(car.cpos, {distance: distance, heading: car.heading}));
                if (car.distance <= distanceTo(car.start, car.cpos)) {
                    car.reached = true;
                    car.cpos = car.dest;
                }
                let eventData = {
                    oem: car.oem,
                    chassis: car.chassis,
                    model: car.model,
                    passengers: car.passengers,
                    location: {
                        lat: getLatitude(car.cpos),
                        lng: getLongitude(car.cpos)
                    },
                    speed: car.speed,
                };

                let sensorFront = this.getRandomSensorData();
                let sensorBack = this.getRandomSensorData();
                if (sensorFront !== 0) {
                    eventData.spaceAhead = sensorFront;
                }
                if (sensorBack !== 0) {
                    eventData.spaceBack = sensorBack;
                }

                if (car.reached && car.crash) {
                    eventData.crash = this.crashTypes[Math.floor(Math.random()*this.crashTypes.length)];
                    console.log(JSON.stringify(eventData));

                    //TODO rest call
                } else {
                    console.log(JSON.stringify(eventData));
                    //TODO rest call
                }
            }
        },
        checkReached: function () {
            for (let i = 0; i < this.simulationCars.length; i++) {

                if (this.simulationCars[i].reached === false) {
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
                    this.onStop()
                } else {
                    for (let i = 0; i < this.simulationCars.length; i++) {
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
            this.running = true;
            this.started = true;
            this.runSim();
        },
        onStop: function () {
            this.running = false;
            clearInterval(this.autoSimulation)
        },
        onReset: function() {
            this.running = false;
            this.createSimulationCars();
            this.started = true;
            this.ended = false;
        },
        runSim () {
            if (this.ended) {
                this.onStop();
                return;
            }
            if (!this.started) {
                this.ended = false;
                this.createSimulationCars();
                this.started = true;
            } else {
                this.autoSimulation = setInterval(() => {
                    for (let i = 0; i < this.simulationCars.length; i++) {
                        this.simulateStepForCar(this.simulationCars[i]);
                    }
                    this.markCurrentLocations();
                    if (this.checkReached()) {
                        this.ended = true;
                        this.onStop()
                    }
                }, 60000 / this.simSpeed)
            }

        },
        getRandomSensorData: function () {
            return Math.floor(Math.round(Math.random()-0.3) * (Math.random() * 10) + 1)
        }

    },
    mounted:function() {
        this.createSimulationCars();
    }    
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
