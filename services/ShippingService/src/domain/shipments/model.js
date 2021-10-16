class Shipment {
  constructor({
    id,
    shipmentId,
    orderNo,
    status,
    createdAt,
  } = {}) {
    this.id = id;
    this.orderNo = orderNo;
    this.status = status;
    this.shipmentId = shipmentId;
    this.createdAt = createdAt;
  }
}


module.exports = Shipment;
