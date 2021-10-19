const { v4: uuidv4 } = require('uuid');
const {
  PAID_ORDER_STATUS,
  PAYMENT_FAILED_ORDER_STATUS,
  OUT_FOR_SHIPMENT_ORDER_STATUS,
  COMPLETED_ORDER_STATUS,
  OUT_OF_STOCK_STATUS,
} = require('../../common/constants');

function init({
  eventsBusRepository,
  ordersRepository,
}) {
  async function getOrder(orderNo) {
    return ordersRepository.getOrder({
      orderNo,
    });
  }

  async function requestOrder({
    userId,
    productId,
    quantity,
    amount,
    currency,
  }) {
    // fetch / find user with userid
    const user = {
      email: 'botsaris.d@gmail.com',
      firstName: 'Dimos',
      lastName: 'Botsaris',
      userId: '2f5acab8-8237-4841-a188-62af0bbbaac8',
    };
    const order = await ordersRepository.createOrder({
      userId: user.userId,
      productId,
      quantity,
      amount,
      currency,
    });
    return order;
  }

  async function updateValidOrder(orderNo) {
    const order = await ordersRepository.updateOrder({
      orderNo,
      status: PAID_ORDER_STATUS,
    });
    // fetch / find user from order.userid
    const user = {
      email: 'botsaris.d@gmail.com',
      firstName: 'Dimos',
      lastName: 'Botsaris',
      userId: '2f5acab8-8237-4841-a188-62af0bbbaac8',
    };
    return order;
  }

  async function updateOutOfStockOrder(orderNo) {
    return ordersRepository.updateOrder({
      orderNo,
      status: OUT_OF_STOCK_STATUS,
    });
  }

  async function updatePaidOrder({
    userId,
    orderNo,
    amount,
    currency,
  }) {
    // fetch / find user with userid
    const user = {
      email: 'botsaris.d@gmail.com',
      firstName: 'Dimos',
      lastName: 'Botsaris',
      userId: '2f5acab8-8237-4841-a188-62af0bbbaac8',
    };
    const order = await ordersRepository.updateOrder({
      orderNo,
      status: PAID_ORDER_STATUS,
    });
    return order;
  }

  async function updateFailedPaymentOrder(orderNo) {
    return ordersRepository.updateOrder({
      orderNo,
      status: PAYMENT_FAILED_ORDER_STATUS,
    });
  }

  async function updateShipmentPreparedOrder(orderNo) {
    return ordersRepository.updateOrder({
      orderNo,
      status: OUT_FOR_SHIPMENT_ORDER_STATUS,
    });
  }

  async function completeOrder(orderNo) {
    return ordersRepository.updateOrder({
      orderNo,
      status: COMPLETED_ORDER_STATUS,
    });
  }

  return {
    getOrder,
    requestOrder,
    updateValidOrder,
    updateOutOfStockOrder,
    updatePaidOrder,
    updateShipmentPreparedOrder,
    completeOrder,
  };
}

module.exports.init = init;
