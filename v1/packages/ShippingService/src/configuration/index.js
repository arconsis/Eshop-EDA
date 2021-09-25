require('dotenv').config();

const config = {
  appEnv: process.env.APP_ENV || 'development',
  httpPort: process.env.HTTP_PORT || 9999,
  kafka: {
    clientId: 'shipmentsservice',
    groupId: 'shipmentsservice',
    brokers: [process.env.KAFKA_BROKER],
    connectionTimeout: 3000,
    requestTimeout: 30000,
  },
};


module.exports = config;
