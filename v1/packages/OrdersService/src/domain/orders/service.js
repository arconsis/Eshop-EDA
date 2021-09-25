const { v4: uuidv4 } = require('uuid');
const {
  ORDERS_TOPIC,
  ORDER_CREATED_EVENT_TYPE,
  PAID_ORDER_STATUS,
  ORDER_CONFIRMED_EVENT,
  SHIPMENT_PREPARED_ORDER_STATUS,
} = require('../../common/constants');
const {
  toCreateOrderMessage,
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

  async function createOrder({
    userId,
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
    });
    await eventsBusRepository.sendMessages(ORDERS_TOPIC, toCreateOrderMessage({
      id: uuidv4(),
      orderNo: order.orderNo,
      type: ORDER_CREATED_EVENT_TYPE,
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      userId: user.userId,
      amount,
      currency,
    }));
    return order;
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
    await eventsBusRepository.sendMessages(ORDERS_TOPIC, toCreateOrderMessage({
      id: uuidv4(),
      orderNo: order.orderNo,
      type: ORDER_CONFIRMED_EVENT,
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      userId: user.userId,
      amount,
      currency,
    }));
    return order;
  }

  async function updateShipmentPreparedOrder(orderNo) {
    const order = await ordersRepository.updateOrder({
      orderNo,
      status: SHIPMENT_PREPARED_ORDER_STATUS,
    });
    return order;
  }

  return {
    getOrder,
    createOrder,
    updatePaidOrder,
    updateShipmentPreparedOrder,
  };
}


module.exports.init = init;
