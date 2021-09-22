require('dotenv').config();

const config = {
  appEnv: process.env.APP_ENV || 'development',
  httpPort: process.env.HTTP_PORT || 3333,
  kafka: {
    clientId: 'emailservice',
    groupId: 'emailservice',
    brokers: [process.env.KAFKA_BROKER],
    connectionTimeout: 3000,
    requestTimeout: 30000,
  },
};

module.exports = config;
