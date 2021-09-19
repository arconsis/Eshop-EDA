const eventBusRepositoryFactory = require('../../data/repositories/eventBus');
const {
  kafka: kafkaConfig,
} = require('../../configuration');
const {
  USER_REGISTERED_EVENT,
  ORDER_CONFIRMED_EVENT,
} = require('../../common/constants');
const logger = require('../../common/logger');
const {
  mapEventPayloadToEmailBody,
} = require('../mapper');

const eventBusRepository = eventBusRepositoryFactory.init(kafkaConfig);

module.exports.init = (services) => {
  function handleNewUserRegistration() {
    
  }
  const handler = async ({ topic, partition, message }) => {
    logger.info('Message consumed: ', message);
    switch (topic) {
      case USER_REGISTERED_EVENT, ORDER_CONFIRMED_EVENT : {
        const emailPayload = mapEventPayloadToEmailBody(message);
        await services.emailService.sendEmail({
          eventType: topic,
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
      topics: [USER_REGISTERED_EVENT],
    }, handler);
  };

  return {
    startConsume,
  };
};
