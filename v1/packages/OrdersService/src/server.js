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

const mainDb = dbContainer.init({
  connectionUri: databaseUri,
});
const ordersRepository = ordersRepositoryContainer.init(mainDb.entities);
const eventsBusRepository = eventsBusRepositoryContainer.init(kafkaConfig);
const runBlockInsideTransaction = mainDb.runTransaction.bind(mainDb);

mainDb.authenticate()
  .then(() => {
    mainDb.sync();
    console.info('!!!! Connection to main database established !!!!!');
  })
  .catch((error) => {
    console.error('Connection to main database error', error);
  });

const ordersService = ordersServiceContainer.init({
  eventsBusRepository,
  ordersRepository,
  runBlockInsideTransaction,
});
const app = appContainer.init({
  ordersService,
});
const server = app.listen(httpPort, () => {
  console.info(`Listening on *:${httpPort}`);
});
