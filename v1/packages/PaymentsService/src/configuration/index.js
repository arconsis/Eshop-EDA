require('dotenv').config();

const config = {
  appEnv: process.env.APP_ENV || 'development',
  httpPort: process.env.HTTP_PORT || 3333,
  kafka: {
    clientId: 'paymentsservice',
    groupId: 'paymentsservice',
    brokers: [process.env.KAFKA_BROKER],
    connectionTimeout: 3000,
    requestTimeout: 30000,
  },
  databaseUri: `postgres://${process.env.DB_USER}:${process.env.DB_PASS}@${process.env.DB_HOST}:${process.env.DB_PORT}/${process.env.DB_NAME}`,
};

module.exports = config;
