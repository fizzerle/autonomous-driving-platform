module.exports = {
  devServer: {
    disableHostCheck: true,

    // Various Dev Server settings
    host: 'localhost', // can be overwritten by process.env.HOST
    port: 3000, // can be overwritten by process.env.PORT, if port is in use, a free one will be determined

    proxy: {
      '^/eventstorage': {
        target: 'http://localhost:8081/',
        ws: true,
        changeOrigin: true
      },
      '/entitystorage': {
        target: 'http://localhost:8082/',
        ws: true,
        changeOrigin: true
      },
      '/notificationstorage': {
        target: 'http://localhost:8083/',
        ws: true,
        changeOrigin: true
      }
    }
  }

}
