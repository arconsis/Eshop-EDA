const { v4: uuidv4 } = require('uuid');

function init({
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
    return transactionsRepository.createPaymentTransaction({
      orderId: orderNo,
      amount,
      currency,
      userId,
      metadata: paymentMetadata,
    });
  }

  return {
    payOrder,
  };
}

module.exports.init = init;
