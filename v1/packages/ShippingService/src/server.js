const http = require('http');
const {
  httpPort,
  databaseUri,
  kafka: kafkaConfig,
} = require('./configuration');
const eventBusRepositoryFactory = require('./data/repositories/eventBus/repository');
const shipmentsServiceFactory = require('./domain/shipments/service');
const eventBusContainer = require('./presentation/eventBus');
const logger = require('./common/logger');

const eventsBusRepository = eventBusRepositoryFactory.init(kafkaConfig);
const shipmentsService = shipmentsServiceFactory.init({
  eventsBusRepository,
});
const eventBus = eventBusContainer.init({ shipmentsService });

(async () => {
  await eventBus.startConsume()
    .catch((error) => logger.error('Generic event bus consumer error', error));
})();

http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/html' });
  res.end('Hello!');
}).listen(httpPort);
