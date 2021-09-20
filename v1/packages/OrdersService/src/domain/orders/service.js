function init({
  ordersRepository,
}) {
  async function createOrder(userId) {
    return ordersRepository.createOrder({
      userId,
    });
  }

  return {
    createOrder,
  };
}


module.exports.init = init;
