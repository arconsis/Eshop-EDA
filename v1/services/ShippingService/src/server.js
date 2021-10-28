const {
  httpPort,
  databaseUri,
  kafka: kafkaConfig,
} = require('./configuration');
const dbContainer = require('./data/infrastructure/database');
const eventBusRepositoryFactory = require('./data/repositories/eventBus/repository');
const shipmentsRepositoryFactory = require('./data/repositories/shipments/repository');
const shipmentsServiceFactory = require('./domain/shipments/service');
const eventBusContainer = require('./presentation/eventBus');
const appContainer = require('./presentation/http/app');
const logger = require('./common/logger');

const mainDb = dbContainer.init({
  connectionUri: databaseUri,
});
const shipmentsRepository = shipmentsRepositoryFactory.init(mainDb.entities);
const eventsBusRepository = eventBusRepositoryFactory.init(kafkaConfig);
const shipmentsService = shipmentsServiceFactory.init({
  eventsBusRepository,
  shipmentsRepository,
});
const eventBus = eventBusContainer.init({ shipmentsService });
const app = appContainer.init({
  shipmentsService,
});

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
