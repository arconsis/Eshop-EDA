const { v4: uuidv4 } = require('uuid');
const {
  SHIPMENTS_TOPIC,
  SHIPMENT_PREPARED_EVENT,
  OUT_FOR_SHIPMENT_STATUS,
  SHIPPED_SHIPMENT_STATUS,
  SHIPMENT_SHIPPED_EVENT,
} = require('../../common/constants');
const {
  toShipmentMessage,
} = require('../../data/repositories/eventBus/mapper');

function init({
  eventsBusRepository,
  shipmentsRepository,
}) {
  async function listShipments() {
    return shipmentsRepository.listShipments({});
  }

  async function prepareShipment({
    orderNo,
    userId,
    amount,
    currency,
    email,
    firstName,
    lastName,
  }) {
    const shipment = await shipmentsRepository.createShipment({
      orderNo,
    });
    await new Promise((resolve) => setTimeout(resolve, 11000));
    await shipmentsRepository.updateShipment({
      id: shipment.id,
      status: OUT_FOR_SHIPMENT_STATUS,
    });
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

  async function updateDeliveredShipment(shipmentId) {
    const shipment = await shipmentsRepository.updateShipment({
      shipmentId,
      status: SHIPPED_SHIPMENT_STATUS,
    });
    return eventsBusRepository.sendMessages(SHIPMENTS_TOPIC, toShipmentMessage({
      id: uuidv4(),
      orderNo: shipment.orderNo,
      type: SHIPMENT_SHIPPED_EVENT,
    }));
  }

  return {
    listShipments,
    prepareShipment,
    updateDeliveredShipment,
  };
}

module.exports.init = init;
