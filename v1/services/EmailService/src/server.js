const http = require('http');
const {
  httpPort,
  mailgun: mailgunConfig,
  kafka: kafkaConfig,
} = require('./configuration');
const emailDispatcherRepositoryFactory = require('./data/repositories/emailDispatcher');
const emailServiceFactory = require('./domain/email/service');
const eventBusContainer = require('./presentation/eventBus');
const logger = require('./common/logger');

const emailDispatcherRepository = emailDispatcherRepositoryFactory.init(mailgunConfig);
const emailService = emailServiceFactory.init(emailDispatcherRepository);
const eventBus = eventBusContainer.init({ emailService });

(async () => {
  await Promise.all([
    eventBus.connectAsConsumer({
      groupId: kafkaConfig.groupId,
    }),
  ]);
  await eventBus.startConsume()
    .catch((error) => logger.error('Generic event bus consumer error', error));
})();

http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/html' });
  res.end('Hello!');
}).listen(httpPort);
