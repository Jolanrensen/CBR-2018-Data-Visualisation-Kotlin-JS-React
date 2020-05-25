config.devServer = config.devServer || {}; // create devServer in case it is undefined
config.devServer.watchOptions = {
    "aggregateTimeout": 7000,
    "poll": 3000
};