const { v4: uuidv4 } = require('uuid');
const {
  SHIPMENTS_TOPIC,
  SHIPMENT_PREPARED_EVENT,
} = require('../../common/constants');
const {
  toShipmentMessage,
} = require('../../data/repositories/eventBus/mapper');

function init({
  eventsBusRepository,
}) {
  async function prepareShipment({
    orderNo,
    userId,
    amount,
    currency,
    email,
    firstName,
    lastName,
  }) {
    await new Promise((resolve) => setTimeout(resolve, 11000));
    return eventsBusRepository.sendMessages(SHIPMENTS_TOPIC, toShipmentMessage({
      id: uuidv4(),
      orderNo,
      type: SHIPMENT_PREPARED_EVENT,
      amount,
      currency,
      userId,
      email,
      firstName,
      lastName,
    }));
  }

  return {
    prepareShipment,
  };
}


module.exports.init = init;
