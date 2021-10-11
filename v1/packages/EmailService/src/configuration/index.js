if (process.env.NODE_ENV !== 'production') {
  require('dotenv').config();
}

const config = {
  appEnv: process.env.APP_ENV,
  httpPort: process.env.HTTP_PORT,
  mailgun: {
    apiKey: process.env.MAILGUN_API_KEY,
    domain: process.env.MAILGUN_DOMAIN,
    host: process.env.MAILGUN_HOST,
  },
  kafka: {
    clientId: 'emailservice',
    groupId: 'emailservice',
    brokers: process.env.KAFKA_BROKER?.split(',') ?? [],
    connectionTimeout: 3000,
    requestTimeout: 30000,
  },
};

module.exports = config;
