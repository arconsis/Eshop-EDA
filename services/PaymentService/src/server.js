const http = require('http');
const {
  httpPort,
  databaseUri,
  kafka: kafkaConfig,
} = require('./configuration');
const dbContainer = require('./data/infrastructure/database');
const transactionsRepositoryFactory = require('./data/repositories/transactions/repository');
const eventsBusRepository = require('./data/repositories/eventBus/repository');
const transactionsServiceFactory = require('./domain/transactions/service');
const eventBusContainer = require('./presentation/eventBus');
const logger = require('./common/logger');

const mainDb = dbContainer.init({
  connectionUri: databaseUri,
});
const transactionsRepository = transactionsRepositoryFactory.init(mainDb.entities);
const transactionsService = transactionsServiceFactory.init({
  eventsBusRepository,
  transactionsRepository,
});
const eventBus = eventBusContainer.init({ transactionsService });

(async () => {
  await mainDb.authenticate()
    .then(() => {
      mainDb.sync();
      logger.info('!!!! Connection to main database established !!!!!');
    })
    .catch((error) => {
      logger.error('Connection to main database error', error);
    });
  await Promise.all([
    eventBus.connectAsConsumer({
      groupId: kafkaConfig.groupId,
    }),
    eventBus.connectAsProducer(),
  ]);
  await eventBus.startConsume()
    .catch((error) => logger.error('Generic event bus consumer error', error));
})();
http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/html' });
  res.end('Hello!');
}).listen(httpPort);
