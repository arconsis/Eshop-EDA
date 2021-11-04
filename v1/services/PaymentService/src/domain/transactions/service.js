const { v4: uuidv4 } = require('uuid');
const {
  PAYMENTS_TOPIC,
  ORDER_PAID_EVENT,
} = require('../../common/constants');
const {
  toPaymentMessage,
} = require('../../data/repositories/eventBus/mapper');

function init({
  eventsBusRepository,
  transactionsRepository,
}) {
  // {"id":"ad84efc7-0253-41d8-85a5-aa5051aa4b18","type":"OrderCreated","version":"1.0.0","payload":{"email":"dimosthenis.botsaris@arconsis.com","firstName":"Dimos","lastName":"Botsaris","userId":"2f5acab8-8237-4841-a188-62af0bbbaac8","amount":100,"currency":"EUR"}
  async function payOrder({
    orderNo,
    userId,
    amount,
    currency,
  }) {
    // pay order via Stripe
    const paymentMetadata = {
      email: 'botsaris.d@gmail.com',
      firstName: 'Dimos',
      lastName: 'Botsaris',
      transactionId: uuidv4(),
      amount,
      currency,
      orderNo,
    };
    const payment = await transactionsRepository.createPaymentTransaction({
      orderId: orderNo,
      amount,
      currency,
      userId,
      metadata: paymentMetadata,
    });
    await eventsBusRepository.sendMessages(PAYMENTS_TOPIC, toPaymentMessage({
      id: uuidv4(),
      orderNo,
      type: ORDER_PAID_EVENT,
      amount,
      currency,
      userId,
      transactionId: payment.transactionId,
    }));
    return payment;
  }

  return {
    payOrder,
  };
}

module.exports.init = init;
