const eventBusRepository = require('../../data/repositories/eventBus/repository');
const {
  ORDERS_TOPIC,
  ORDER_CREATED_EVENT_TYPE,
} = require('../../common/constants');
const logger = require('../../common/logger');

module.exports.init = (services) => {
  const handler = async ({ topic, partition, message }) => {
    logger.info('Topic: ', topic);
    logger.info('Message consumed: ', message);
    switch (topic) {
      case ORDERS_TOPIC: {
        if (message.type === ORDER_CREATED_EVENT_TYPE) {
          const { payload } = message;
          await services.transactionsService.payOrder({
            orderNo: payload.orderNo,
            userId: payload.userId,
            amount: payload.amount,
            currency: payload.currency,
            topic,
            eventType: message.type,
          })
            .catch((error) => {
              logger.error('handle UserRegistrer event error', error);
            });
          return;
        }
        return;
      }
      default:
        throw new Error('Not supported topic event');
    }
  };

  const startConsume = async () => {
    logger.info('Start consume topics');
    await eventBusRepository.startConsume({
      fromBeginning: true,
      topics: [
        ORDERS_TOPIC,
      ],
    }, handler);
  };

  const connectAsConsumer = async ({ groupId }) => {
    await eventBusRepository.connectAsConsumer({
      groupId,
    }).then(() => logger.info('Connected as consumer'));
  };

  const connectAsProducer = async () => {
    await eventBusRepository.connectAsProducer()
      .then(() => logger.info('Connected as producer'));
  };

  return {
    startConsume,
    connectAsConsumer,
    connectAsProducer,
  };
};
