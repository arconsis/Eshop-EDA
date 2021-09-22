class Transaction {
  constructor({
    id,
    transactionId,
    orderId,
    createdAt,
  } = {}) {
    this.id = id;
    this.transactionId = transactionId;
    this.orderId = orderId;
    this.createdAt = createdAt;
  }
}


module.exports = Transaction;
