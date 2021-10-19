const { v4: uuidv4 } = require('uuid');
const {
  SHIPMENTS_TOPIC,
  SHIPMENT_PREPARED_EVENT,
  OUT_FOR_SHIPMENT_STATUS,
  SHIPPED_SHIPMENT_STATUS,
  SHIPMENT_SHIPPED_EVENT,
} = require('../../common/constants');

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
  }

  async function updateDeliveredShipment(shipmentId) {
    await shipmentsRepository.updateShipment({
      shipmentId,
      status: SHIPPED_SHIPMENT_STATUS,
    });
  }

  return {
    listShipments,
    prepareShipment,
    updateDeliveredShipment,
  };
}

module.exports.init = init;
