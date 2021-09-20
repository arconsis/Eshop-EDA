class Order {
  constructor({
    id,
    orderNo,
    status,
    userId,
    createdAt,
  } = {}) {
    this.id = id;
    this.orderNo = orderNo;
    this.status = status;
    this.userId = userId;
    this.createdAt = createdAt;
  }
}


module.exports = Order;
