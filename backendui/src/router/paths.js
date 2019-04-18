/**
 * Define all of your application routes here
 * for more information on routes, see the
 * official documentation https://router.vuejs.org/en/
 */
export default [
  {
    path: '/dashboard',
    // Relative to /src/views
    view: 'Dashboard'
  },
  {
    path: '/user-profile',
    name: 'User Profile',
    view: 'UserProfile'
  },
  {
    path: '/table-list',
    name: 'Table List',
    view: 'TableList'
  },
  {
    path: '/typography',
    view: 'Typography'
  },
  {
    path: '/icons',
    view: 'Icons'
  },
  {
    path: '/maps',
    view: 'Maps'
  },
  {
    path: '/notifications',
    view: 'Notifications'
  },
  {
    path: '/ambulance-dashboard',
    name: 'Ambulance Dashboard',
    view: 'AmbulanceDashboard'
  },
  {
    path: '/car-manufacturer-dashboard',
    name: 'Car Manufacturer Dashboard',
    view: 'CarManufacturerDashboard'
  },
  {
    path: '/autonomous-cars-dashboard',
    name: 'Autonomous Cars Dashboard',
    view: 'AutonomousCarsDashboard'
  },
  {
    path: '/simulation',
    name: 'Simulation',
    view: 'Simulation'
  }
]
