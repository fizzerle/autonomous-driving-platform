module.exports = {
  devServer: {
    disableHostCheck: true,

    // Various Dev Server settings
    host: 'localhost', // can be overwritten by process.env.HOST
    port: 3000, // can be overwritten by process.env.PORT, if port is in use, a free one will be determined

    proxy: {
      '^/eventstorage': {
        target: 'https://192.168.99.100/eventstorage',
        ws: true,
        changeOrigin: true
      },
      '/entitystorage': {
        target: 'https://192.168.99.100',
        ws: true,
        changeOrigin: true
      },
      '/notificationstorage': {
        target: 'https://192.168.99.100/notificationstorage',
        ws: true,
        changeOrigin: true
      }
    }
  }

}
