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
  }) {
    return eventsBusRepository.sendMessages(SHIPMENTS_TOPIC, toShipmentMessage({
      id: uuidv4(),
      orderNo,
      type: SHIPMENT_PREPARED_EVENT,
      amount,
      currency,
      userId,
    }));
  }

  return {
    prepareShipment,
  };
}

module.exports.init = init;
