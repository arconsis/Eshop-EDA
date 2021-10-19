const { v4: uuidv4 } = require('uuid');
const {
  PAYMENTS_TOPIC,
  ORDER_PAID_EVENT,
} = require('../../common/constants');

function init({
  eventsBusRepository,
  transactionsRepository,
}) {
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
    return payment;
  }

  return {
    payOrder,
  };
}

module.exports.init = init;
