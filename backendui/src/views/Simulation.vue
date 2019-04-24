<template>
    <v-container fill-height fluid grid-list-xl>
        <v-layout justify-center wrap>
            <!-- Simulation Console to control the simulation and view car-positions in map -->
            <v-flex md6 consoleContainer>
                <h2>Simulation Console</h2>
                <div class="gmap_canvas">
                    <iframe
                            id="gmap_canvas"
                            width="100%"
                            height="100%"
                            src="https://maps.google.com/maps?q=google&t=&z=13&ie=UTF8&iwloc=&output=embed"
                            frameborder="0"
                            scrolling="no"
                            marginheight="0"
                            marginwidth="0"
                    />
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
                            <v-btn>Step</v-btn>
                            <v-btn>Start</v-btn>
                            <v-btn>Reset</v-btn>
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
export default {
    data: () => ({
        valid: false,
        simStepSize: 1,
        simSpeed: 10,
        speed:'',
        startCoord:'',
        endCoord:'',
        coordRules: [
            v => !!v || 'Start is required',
            v => /^[0-9][0-9]?(\.[0-9]*)?, [0-9][0-9]?(\.[0-9]*)?$/.test(v) || 'Coordinates must be valid'
        ],
        speedRules: [
            v => !!v || 'Start is required',
            v => /^[0-9][0-9]?[0-9]?$/.test(v) || 'Speed must be a number with maximal 3 digits.'
        ],
        cars: [
            {
                name: "car1",
                start: "12.354, 45.1156",
                end:  "12.354, 45.1156",
                speed: 40
            },
            {
                name: "car2",
                start: "12.354, 45.1156",
                end:  "12.354, 45.1156",
                speed: 50
            }
        ],
        selectedCar: null,
    }),
    methods: {
        removeCar: function (index) {
            this.cars.splice(index, 1);
        },
        addCar: function () {
            if (this.valid) {
                this.cars.push({name: "car"+ (this.cars.length+1), startCoord: this.start, endCoord: this.end, speed: this.speed});
            }
        }
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