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
        <v-combobox
          v-model="select"
          :items="itemsCombo"
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
        </v-combobox>
      </v-flex>
      <v-flex
        md6
      >
        <material-card
          :text="'This is the list of cars for car manufacturer '+ select"
          :title="'List of Cars for '+ select"
          color="green"
        >
          <v-data-table
            :headers="headers"
            :items="items"
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
              <td>{{ item.id }}</td>
              <td>{{ item.type }}</td>
              <td>{{ item.people }}</td>
              <td class="text-xs-right">{{ item.speed }}</td>
            </template>
          </v-data-table>
        </material-card>
      </v-flex>
      <v-flex
        md6
      >
        <material-card
          :text="'List of Crash Events for '+ select"
          :title="'This is the list of Crash Events for '+ select"
          color="red"
        >
          <v-data-table
            :headers="headers"
            :items="items"
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
              <td>{{ item.id }}</td>
              <td>{{ item.type }}</td>
              <td>{{ item.people }}</td>
              <td class="text-xs-right">{{ item.speed }}</td>
            </template>
          </v-data-table>
        </material-card>
      </v-flex>
      <v-flex
        md12
      >
        <material-card
          :text="'List of Crash Events for '+ select"
          :title="'This is the list of Crash Events for '+ select"
          color="red"
        >
        <div>
          <div class="mapouter">
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
          </div>
        </div>
                </material-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
export default {
  data: () => ({
    select: 'Audi',
    itemsCombo: [
      'Audi',
      'BMW',
      'Lamborghini',
      'Lotus'
    ],
    headers: [
      {
        sortable: false,
        text: 'Fahrgestellnummer',
        value: 'id'
      },
      {
        sortable: false,
        text: 'Modelltyp',
        value: 'type'
      },
      {
        sortable: false,
        text: 'Insassen',
        value: 'people'
      },
      {
        sortable: false,
        text: 'Geschwindigkeit',
        value: 'speed',
        align: 'right'
      }
    ],
    items: [
      {
        id: '1',
        type: 'B',
        people: '6',
        speed: '75 km/h'
      },
      {
        id: '2',
        type: 'A',
        people: '2',
        speed: '25 km/h'
      },
      {
        id: '3',
        type: 'D',
        people: '1',
        speed: '120 km/h'
      },
      {
        id: '4',
        type: 'A',
        people: '2',
        speed: '25 km/h'
      },
      {
        id: '5',
        type: 'D',
        people: '1',
        speed: '120 km/h'
      },
      {
        id: '6',
        type: 'A',
        people: '2',
        speed: '25 km/h'
      },
      {
        id: '7',
        type: 'D',
        people: '1',
        speed: '120 km/h'
      },
      {
        id: '8',
        type: 'A',
        people: '2',
        speed: '25 km/h'
      },
      {
        id: '9',
        type: 'D',
        people: '1',
        speed: '120 km/h'
      },
      {
        id: '10',
        type: 'A',
        people: '2',
        speed: '25 km/h'
      },
      {
        id: '11',
        type: 'D',
        people: '1',
        speed: '120 km/h'
      },
      {
        id: '12',
        type: 'A',
        people: '2',
        speed: '25 km/h'
      },
      {
        id: '13',
        type: 'D',
        people: '1',
        speed: '120 km/h'
      },
      {
        id: '14',
        type: 'A',
        people: '2',
        speed: '25 km/h'
      },
      {
        id: '15',
        type: 'D',
        people: '1',
        speed: '120 km/h'
      },
      {
        id: '16',
        type: 'A',
        people: '2',
        speed: '25 km/h'
      },
      {
        id: '17',
        type: 'D',
        people: '1',
        speed: '120 km/h'
      }
    ]
  })
}
</script>
<style>
</style>
