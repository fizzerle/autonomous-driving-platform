// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'

// Components
import './components'

// Plugins
import './plugins'

// Sync router with store
import { sync } from 'vuex-router-sync'

// Application imports
import App from './App'
import i18n from '@/i18n'
import router from '@/router'
import store from '@/store'

import VTooltip from 'v-tooltip'
import Vuetify from 'vuetify'
import 'material-design-icons-iconfont/dist/material-design-icons.css'
import * as VueGoogleMaps from 'vue2-google-maps'


// Sync store with router
sync(store, router)

Vue.config.productionTip = false
Vue.use(VTooltip)

/* eslint-disable no-new */
new Vue({
  i18n,
  router,
  store,
  render: h => h(App)
}).$mount('#app')

Vue.use(Vuetify, {
  iconfont: 'md'
})

Vue.use(VueGoogleMaps, {
  load: {
    key: 'AIzaSyD8-YcjQLE9ix9_nW5faST8jXrT_UREfzU',
    libraries: 'places',
  }
})
