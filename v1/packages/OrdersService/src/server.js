const {
  databaseUri,
  httpPort,
  kafka: kafkaConfig,
} = require('./configuration');
const dbContainer = require('./data/infrastructure/database');
const eventsBusRepositoryContainer = require('./data/repositories/eventsBus/repository');
const ordersRepositoryContainer = require('./data/repositories/orders/repository');
const ordersServiceContainer = require('./domain/orders/service');
const appContainer = require('./presentation/http/app');
const eventBusContainer = require('./presentation/eventBus');
const logger = require('./common/logger');

const mainDb = dbContainer.init({
  connectionUri: databaseUri,
});
const ordersRepository = ordersRepositoryContainer.init(mainDb.entities);
const eventsBusRepository = eventsBusRepositoryContainer.init(kafkaConfig);
const runBlockInsideTransaction = mainDb.runTransaction.bind(mainDb);
const ordersService = ordersServiceContainer.init({
  eventsBusRepository,
  ordersRepository,
  runBlockInsideTransaction,
});
const app = appContainer.init({
  ordersService,
});
const eventBus = eventBusContainer.init({ ordersService });

(async () => {
  await mainDb.authenticate()
    .then(() => {
      mainDb.sync();
      logger.info('!!!! Connection to main database established !!!!!');
    })
    .catch((error) => {
      logger.error('Connection to main database error', error);
    });
  await eventBus.startConsume()
    .catch((error) => logger.error('Generic event bus consumer error', error));
})();
const server = app.listen(httpPort, () => {
  logger.info(`Listening on *:${httpPort}`);
});
