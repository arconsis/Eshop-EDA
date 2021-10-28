const eventBusRepositoryFactory = require('../../data/repositories/eventBus');
const {
  kafka: kafkaConfig,
} = require('../../configuration');
const {
  ORDERS_TOPIC,
  SHIPMENTS_TOPIC,
} = require('../../common/constants');
const logger = require('../../common/logger');
const {
  mapEventPayloadToEmailBody,
} = require('../mapper');

const eventBusRepository = eventBusRepositoryFactory.init(kafkaConfig);


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
    await eventBusRepository.consumeStream({
      groupId: kafkaConfig.groupId,
      topics: [
        ORDERS_TOPIC,
        SHIPMENTS_TOPIC,
      ],
    }, handler);
  };

  return {
    startConsume,
  };
};
