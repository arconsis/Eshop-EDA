const { v4: uuidv4 } = require('uuid');
const {
  ORDERS_TOPIC,
  ORDER_CREATED_EVENT_TYPE,
} = require('../../common/constants');
const {
  toEventBusMessage,
} = require('../../data/repositories/eventsBus/mapper');

function init({
  eventsBusRepository,
  ordersRepository,
}) {
  async function createOrder(userId) {
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
    await eventsBusRepository.sendMessages(ORDERS_TOPIC, toEventBusMessage({
      id: uuidv4(),
      orderNo: order.orderNo,
      type: ORDER_CREATED_EVENT_TYPE,
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      userId: user.userId,
    }));
    return order;
  }

  return {
    createOrder,
  };
}

module.exports.init = init;
