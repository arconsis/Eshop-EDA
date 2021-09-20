require('dotenv').config();

const config = {
  httpPort: process.env.HTTP_PORT || 5050,
  databaseUri: `postgres://${process.env.DB_USER}:${process.env.DB_PASS}@${process.env.DB_HOST}:${process.env.DB_PORT}/${process.env.DB_NAME}`,
  kafka: {
    clientId: 'ordersservice',
    groupId: 'ordersservice',
    brokers: [process.env.KAFKA_BROKER],
    connectionTimeout: 3000,
    requestTimeout: 30000,
  },
};

module.exports = config;
