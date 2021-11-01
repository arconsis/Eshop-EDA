const eventBusRepository = require('../../data/repositories/eventBus');
const {
  ORDERS_TOPIC,
  SHIPMENTS_TOPIC,
} = require('../../common/constants');
const logger = require('../../common/logger');
const {
  mapEventPayloadToEmailBody,
} = require('../mapper');

module.exports.init = (services) => {
  const handler = async ({ topic, partition, message }) => {
    logger.info('Topic: ', topic);
    logger.info('Message consumed: ', message);
    switch (topic) {
      case ORDERS_TOPIC:
      case SHIPMENTS_TOPIC: {
        const emailPayload = mapEventPayloadToEmailBody(message);
        await services.emailService.sendEmail({
          topic,
          eventType: message.type,
          receiverEmail: emailPayload.receiverEmail,
          ...emailPayload,
        })
          .catch((error) => {
            logger.error('handle UserRegistrer event error', error);
          });
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
        SHIPMENTS_TOPIC,
      ],
    }, handler);
  };

  const connectAsConsumer = async ({ groupId }) => {
    await eventBusRepository.connectAsConsumer({
      groupId,
    }).then(() => logger.info('Connected as consumer'));
  };

  return {
    connectAsConsumer,
    startConsume,
  };
};
