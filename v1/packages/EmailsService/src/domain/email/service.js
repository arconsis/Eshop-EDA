const {
  isValidEmail,
} = require('../../common/utils');
const {
  SENDER_REGISTRATION_EMAIL,
  REGISTRATION_EMAIL_SUBJECT,
  USER_REGISTERED_EVENT,
  ORDER_CONFIRMED_EVENT,
  ORDER_CONFIRMED_SUBJECT,
  ORDER_CREATED_TOPIC,
  ORDER_CREATED_SUBJECT,
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
    if (!rest.orderNo) {
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

  function getOrderConfirmedEmailText(orderNo) {
    return `New order with number: ${orderNo} just confirmed!`;
  }

  function getOrderCreatedEmailText(orderNo) {
    return `New order with number: ${orderNo} just placed!`;
  }

  function validateEventPaylod({
    eventType,
    receiverEmail,
    ...rest
  }) {
    if (eventType === USER_REGISTERED_EVENT) return validateUserRegistrationEventPayload(receiverEmail);
    if (eventType === ORDER_CONFIRMED_EVENT || ORDER_CREATED_TOPIC) return validateOrderConfirmedPayload(receiverEmail, rest);
    throw new Error('Not supported topic event');
  }

  function getEmailPayload({
    eventType,
    ...rest
  }) {
    switch (eventType) {
      case USER_REGISTERED_EVENT:
        return {
          subject: REGISTRATION_EMAIL_SUBJECT,
          text: getRegistrationEmailText(rest.firstName, rest.lastName),
        };
      case ORDER_CONFIRMED_EVENT:
        return {
          subject: ORDER_CONFIRMED_SUBJECT,
          text: getOrderConfirmedEmailText(rest.orderNo),
        };
      case ORDER_CREATED_TOPIC:
        return {
          subject: ORDER_CREATED_SUBJECT,
          text: getOrderCreatedEmailText(rest.orderNo),
        };
      default:
        throw new Error('Not supported topic event');
    }
  }

  async function sendEmail({
    eventType,
    receiverEmail,
    ...rest
  }) {
    switch (eventType) {
      case USER_REGISTERED_EVENT:
      case ORDER_CONFIRMED_EVENT:
      case ORDER_CREATED_TOPIC: {
        validateEventPaylod({
          eventType,
          receiverEmail,
          ...rest,
        });
        return emailDispatcherRepository.sendEmail({
          senderEmail: SENDER_REGISTRATION_EMAIL,
          receiverEmail,
          subject: getEmailPayload({ eventType, ...rest }).subject,
          text: getEmailPayload({ eventType, ...rest }).text,
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
