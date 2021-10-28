const OrderModel = require('../../../domain/orders/model');

const toDomainModel = function toDomainModel(databaseDoc) {
  return new OrderModel(databaseDoc);
};

module.exports = {
  toDomainModel,
};
