/* eslint-disable no-useless-return */
const {
  PAYMENTS_TOPIC,
  ORDER_PAID_EVENT,
  ORDER_FAILED_PAYMENT_EVENT,
  SHIPMENTS_TOPIC,
  SHIPMENT_PREPARED_EVENT,
  SHIPMENT_SHIPPED_EVENT,
  WAREHOUSE_TOPIC,
  WAREHOUSE_ORDER_VALIDATED_EVENT,
  WAREHOUSE_ORDER_INVALID_EVENT,
} = require('../../common/constants');
const logger = require('../../common/logger');
const eventsBusRepository = require('../../data/repositories/eventsBus/repository');

module.exports.init = (services) => {
  async function handlePaymentsTopic(message) {
    switch (message.type) {
      case ORDER_PAID_EVENT: {
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
      case ORDER_FAILED_PAYMENT_EVENT: {
        const { payload } = message;
        await services.ordersService.updateFailedPaymentOrder({
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
      default:
        return;
    }
  }

  async function handleShipmentsTopic(message) {
    switch (message.type) {
      case SHIPMENT_PREPARED_EVENT: {
        const { payload } = message;
        await services.ordersService.updateShipmentPreparedOrder(payload.orderNo)
          .catch((error) => {
            logger.error('handle ShipmentPrepared event error', error);
          });
        return;
      }
      case SHIPMENT_SHIPPED_EVENT: {
        const { payload } = message;
        await services.ordersService.completeOrder(payload.orderNo)
          .catch((error) => {
            logger.error('handle ShipmentShipped event error', error);
          });
        return;
      }
      default:
        return;
    }
  }

  async function handleWarehouseTopic(message) {
    switch (message.type) {
      case WAREHOUSE_ORDER_VALIDATED_EVENT: {
        const { payload } = message;
        await services.ordersService.updateValidOrder(payload.orderNo)
          .catch((error) => {
            logger.error('handle OrderValidated event error', error);
          });
        return;
      }
      case WAREHOUSE_ORDER_INVALID_EVENT: {
        const { payload } = message;
        await services.ordersService.updateOutOfStockOrder(payload.orderNo)
          .catch((error) => {
            logger.error('handle OrderInvalid event error', error);
          });
        return;
      }
      default:
        return;
    }
  }

  const handler = async ({ topic, partition, message }) => {
    logger.info('Topic: ', topic);
    logger.info('Message consumed: ', message);
    switch (topic) {
      case PAYMENTS_TOPIC: {
        return handlePaymentsTopic(message);
      }
      case SHIPMENTS_TOPIC: {
        return handleShipmentsTopic(message);
      }
      case WAREHOUSE_TOPIC: {
        return handleWarehouseTopic(message);
      }
      default:
        throw new Error('Not supported topic event');
    }
  };

  const startConsume = async () => {
    logger.info('Start consume topics');
    await eventsBusRepository.startConsume({
      fromBeginning: true,
      topics: [
        PAYMENTS_TOPIC,
        SHIPMENTS_TOPIC,
        WAREHOUSE_TOPIC,
      ],
    }, handler);
  };

  const connectAsConsumer = async ({ groupId }) => {
    await eventsBusRepository.connectAsConsumer({
      groupId,
    }).then(() => logger.info('Connected as consumer'));
  };

  const connectAsProducer = async () => {
    await eventsBusRepository.connectAsProducer()
      .then(() => logger.info('Connected as producer'));
  };

  return {
    startConsume,
    connectAsConsumer,
    connectAsProducer,
  };
};
