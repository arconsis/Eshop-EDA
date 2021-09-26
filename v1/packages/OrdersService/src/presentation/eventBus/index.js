const eventBusRepositoryFactory = require('../../data/repositories/eventsBus/repository');
const {
  kafka: kafkaConfig,
} = require('../../configuration');
const {
  PAYMENTS_TOPIC,
  ORDER_PAID_EVENT,
  SHIPMENTS_TOPIC,
  SHIPMENT_PREPARED_EVENT,
  SHIPMENT_SHIPPED_EVENT,
} = require('../../common/constants');
const logger = require('../../common/logger');

const eventBusRepository = eventBusRepositoryFactory.init(kafkaConfig);

module.exports.init = (services) => {
  const handler = async ({ topic, partition, message }) => {
    logger.info('Topic: ', topic);
    logger.info('Message consumed: ', message);
    switch (topic) {
      case PAYMENTS_TOPIC: {
        if (message.type === ORDER_PAID_EVENT) {
          const { payload } = message;
          await services.ordersService.updatePaidOrder({
            orderNo: payload.orderNo,
            userId: payload.userId,
            amount: payload.amount,
            currency: payload.currency,
          })
            .catch((error) => {
              logger.error('handle OrderPaid event error', error);
            });
          return;
        }
        return;
      }
      case SHIPMENTS_TOPIC: {
        if (message.type === SHIPMENT_PREPARED_EVENT) {
          const { payload } = message;
          await services.ordersService.updateShipmentPreparedOrder(payload.orderNo)
            .catch((error) => {
              logger.error('handle OrderPaid event error', error);
            });
          return;
        }
        if (message.type === SHIPMENT_SHIPPED_EVENT) {
          const { payload } = message;
          await services.ordersService.completeOrder(payload.orderNo)
            .catch((error) => {
              logger.error('handle OrderPaid event error', error);
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
        PAYMENTS_TOPIC,
        SHIPMENTS_TOPIC,
      ],
    }, handler);
  };

  return {
    startConsume,
  };
};
