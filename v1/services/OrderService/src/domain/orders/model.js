class Order {
  constructor({
    id,
    orderNo,
    status,
    userId,
    amount,
    currency,
    productId,
    quantity,
    createdAt,
  } = {}) {
    this.id = id;
    this.orderNo = orderNo;
    this.status = status;
    this.userId = userId;
    this.amount = amount;
    this.currency = currency;
    this.productId = productId;
    this.quantity = quantity;
    this.createdAt = createdAt;
  }
}


module.exports = Order;
