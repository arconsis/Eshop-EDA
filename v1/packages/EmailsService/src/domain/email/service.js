const {
  isValidEmail,
} = require('../../common/utils');
const {
  SENDER_REGISTRATION_EMAIL,
  ORDERS_TOPIC,
  ORDER_CONFIRMED_EVENT_TYPE,
  ORDER_CONFIRMED_SUBJECT,
  ORDER_CREATED_EVENT_TYPE,
  ORDER_CREATED_SUBJECT,
} = require('../../common/constants');

function init(emailDispatcherRepository) {
  function validateReceiver(receiverEmail) {
    if (!isValidEmail(receiverEmail)) {
      throw new Error('Not valid receiver email address.');
    }
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
    if (eventType === ORDER_CONFIRMED_EVENT_TYPE || ORDER_CREATED_EVENT_TYPE) return validateOrderConfirmedPayload(receiverEmail, rest);
    throw new Error('Not supported event type.');
  }

  function getEmailPayload({
    eventType,
    ...rest
  }) {
    switch (eventType) {
      case ORDER_CONFIRMED_EVENT_TYPE:
        return {
          subject: ORDER_CONFIRMED_SUBJECT,
          text: getOrderConfirmedEmailText(rest.orderNo),
        };
      case ORDER_CREATED_EVENT_TYPE:
        return {
          subject: ORDER_CREATED_SUBJECT,
          text: getOrderCreatedEmailText(rest.orderNo),
        };
      default:
        throw new Error('Not supported event type.');
    }
  }

  async function sendEmail({
    topic,
    eventType,
    receiverEmail,
    ...rest
  }) {
    switch (eventType) {
      case ORDER_CONFIRMED_EVENT_TYPE:
      case ORDER_CREATED_EVENT_TYPE: {
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
        throw new Error('Not supported event type.');
    }
  }

  return {
    sendEmail,
  };
}

module.exports.init = init;
