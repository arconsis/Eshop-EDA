const eventBusRepositoryFactory = require('../../data/repositories/eventBus/repository');
const {
  kafka: kafkaConfig,
} = require('../../configuration');
const {
  ORDERS_TOPIC,
  ORDER_CONFIRMED_EVENT,
} = require('../../common/constants');
const logger = require('../../common/logger');

const eventBusRepository = eventBusRepositoryFactory.init(kafkaConfig);

module.exports.init = (services) => {
  const handler = async ({ topic, partition, message }) => {
    logger.info('Topic: ', topic);
    logger.info('Message consumed: ', message);
    switch (topic) {
      case ORDERS_TOPIC: {
        if (message.type === ORDER_CONFIRMED_EVENT) {
          const { payload } = message;
          await services.shipmentsService.prepareShipment({
            orderNo: payload.orderNo,
            userId: payload.userId,
            amount: payload.amount,
            currency: payload.currency,
            email: payload.email,
            firstName: payload.firstName,
            lastName: payload.lastName,
            topic,
            eventType: message.type,
          })
            .catch((error) => {
              logger.error('handle OrderConfirmed event error', error);
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
    await eventBusRepository.consumeStream({
      groupId: kafkaConfig.groupId,
      topics: [
        ORDERS_TOPIC,
      ],
    }, handler);
  };

  return {
    startConsume,
  };
};
