const { v4: uuidv4 } = require('uuid');
const {
  ORDERS_TOPIC,
  ORDER_CREATED_EVENT_TYPE,
  ORDER_REQUESTED_EVENT_TYPE,
  VALIDATED_ORDER_STATUS,
  PAID_ORDER_STATUS,
  PAYMENT_FAILED_ORDER_STATUS,
  ORDER_CONFIRMED_EVENT,
  OUT_FOR_SHIPMENT_ORDER_STATUS,
  COMPLETED_ORDER_STATUS,
  OUT_OF_STOCK_STATUS,
} = require('../../common/constants');
const {
  toCreateOrderMessage,
  toOrderRequestedMessage,
} = require('../../data/repositories/eventsBus/mapper');

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
    await eventsBusRepository.sendInTransaction([
      {
        topic: ORDERS_TOPIC,
        messages: toOrderRequestedMessage({
          id: uuidv4(),
          orderNo: order.orderNo,
          type: ORDER_REQUESTED_EVENT_TYPE,
          userId: user.userId,
          amount,
          currency,
          productId,
          quantity,
        }),
      },
    ]);
    return order;
  }

  async function updateValidOrder(orderNo) {
    const order = await ordersRepository.updateOrder({
      orderNo,
      status: VALIDATED_ORDER_STATUS,
    });
    // fetch / find user from order.userid
    const user = {
      email: 'botsaris.d@gmail.com',
      firstName: 'Dimos',
      lastName: 'Botsaris',
      userId: '2f5acab8-8237-4841-a188-62af0bbbaac8',
    };
    await eventsBusRepository.sendInTransaction([
      {
        topic: ORDERS_TOPIC,
        messages: toCreateOrderMessage({
          id: uuidv4(),
          orderNo: order.orderNo,
          type: ORDER_CREATED_EVENT_TYPE,
          email: user.email,
          firstName: user.firstName,
          lastName: user.lastName,
          userId: user.userId,
          amount: order.amount,
          currency: order.currency,
        }),
      },
    ]);
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
    await eventsBusRepository.sendInTransaction([
      {
        topic: ORDERS_TOPIC,
        messages: toCreateOrderMessage({
          id: uuidv4(),
          orderNo: order.orderNo,
          type: ORDER_CONFIRMED_EVENT,
          email: user.email,
          firstName: user.firstName,
          lastName: user.lastName,
          userId: user.userId,
          amount,
          currency,
        }),
      },
    ]);
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
