/**
 * Define all of your application routes here
 * for more information on routes, see the
 * official documentation https://router.vuejs.org/en/
 */
export default [
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
