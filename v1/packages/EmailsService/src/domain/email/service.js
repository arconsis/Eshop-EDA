const {
  isValidEmail,
} = require('../../common/utils');
const {
  SENDER_REGISTRATION_EMAIL,
  REGISTRATION_EMAIL_SUBJECT,
  USER_REGISTERED_EVENT,
  ORDER_CONFIRMED_EVENT,
  ORDER_CONFIRMED_SUBJECT,
} = require('../../common/constants');

function init(emailDispatcherRepository) {
  function validateReceiver(receiverEmail) {
    if (!isValidEmail(receiverEmail)) {
      throw new Error('Not valid receiver email address.');
    }
  }

  function validateUserRegistrationEventPayload(receiverEmail) {
    if (!receiverEmail) {
      throw new Error('User email not provided.');
    }
    validateReceiver(receiverEmail);
  }

  function validateOrderConfirmedPayload(receiverEmail, rest) {
    if (!receiverEmail) {
      throw new Error('User email not provided.');
    }
    validateReceiver(receiverEmail);
    if (!rest.orderNumber) {
      throw new Error('Order number not provided.');
    }
  }

  function getRegistrationEmailText(firstName, lastName) {
    if (firstName && lastName) {
      return `Welcome ${firstName} ${lastName} on arconsis EDA experience!`;
    }
    if (firstName) {
      return `Welcome ${firstName} on arconsis EDA experience!`;
    }
    return `Welcome ${lastName} on arconsis EDA experience!`;
  }

  function getOrderConfirmedEmailText(orderNumber) {
    return `New order with number: ${orderNumber} just confirmed!`;
  }

  function validateEventPaylod({
    eventType,
    receiverEmail,
    ...rest
  }) {
    if (eventType == USER_REGISTERED_EVENT) return validateUserRegistrationEventPayload(receiverEmail);
    if (eventType == ORDER_CONFIRMED_EVENT) return validateOrderConfirmedPayload(receiverEmail);
    throw new Error('Not supported topic event');
  }

  async function sendEmail({
    eventType,
    receiverEmail,
    ...rest
  }) {
    switch (eventType) {
      case USER_REGISTERED_EVENT, ORDER_CONFIRMED_EVENT: {
        validateEventPaylod({
          eventType,
          receiverEmail,
          ...rest
        });
        return emailDispatcherRepository.sendEmail({
          senderEmail: SENDER_REGISTRATION_EMAIL,
          receiverEmail,
          subject: eventType === USER_REGISTERED_EVENT ? REGISTRATION_EMAIL_SUBJECT : ORDER_CONFIRMED_SUBJECT,
          text: eventType === USER_REGISTERED_EVENT 
            ? getRegistrationEmailText(rest.firstName, rest.lastName)
            : getOrderConfirmedEmailText(rest.orderNumber),
        });
      }
      default:
        throw new Error('Not supported topic event');
    }
  }

  return {
    sendEmail,
  };
}

module.exports.init = init;
