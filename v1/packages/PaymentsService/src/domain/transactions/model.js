class Transaction {
  constructor({
    id,
    transactionId,
    orderId,
    amount,
    currency,
    createdAt,
  } = {}) {
    this.id = id;
    this.transactionId = transactionId;
    this.orderId = orderId;
    this.amount = amount;
    this.currency = currency;
    this.createdAt = createdAt;
  }
}


module.exports = Transaction;
