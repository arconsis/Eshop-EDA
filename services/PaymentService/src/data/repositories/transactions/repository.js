const mapper = require('./mapper');

module.exports.init = function init({
  transactions: transactionsModel,
  sequelize,
}) {
  const transactionsRepo = {
    async createPaymentTransaction({
      amount,
      currency,
      userId,
      orderId,
      metadata,
      transaction,
    }) {
      const res = await transactionsModel.create({
        amount,
        currency,
        userId,
        orderId,
        metadata,
      }, {
        ...(transaction != null && { transaction }),
      });
      const doc = res.get({ plain: true });
      return mapper.toDomainModel(doc);
    },
  };
  return Object.freeze(Object.create(transactionsRepo));
};
