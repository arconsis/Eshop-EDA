const { getUpdatedDocResponse } = require('../../infrastructure/database/utils');
const mapper = require('./mapper');

module.exports.init = function init({
  orders: ordersModel,
  sequelize,
}) {
  const ordersRepo = {
    async getOrder({
      id,
      orderNo,
      attributes = [],
      lock,
      transaction,
    }) {
      if (!id && !orderNo) {
        throw new Error('Add id or orderNo to get order.');
      }
      const res = await ordersModel.findOne({
        where: {
          ...(id && { id }),
          ...(orderNo && { orderNo }),
        },
        attributes: attributes && Array.isArray(attributes) && attributes.length > 0
          ? attributes
          : { exclude: [] },
        ...(lock != null && { lock }),
        ...(transaction != null && { transaction }),
      });
      if (!res) {
        throw new Error('Order doc not found');
      }
      const doc = res.get({ plain: true });
      return mapper.toDomainModel(doc);
    },
    async createOrder({
      userId,
      transaction,
    }) {
      const res = await ordersModel.create({
        userId,
      }, {
        ...(transaction != null && { transaction }),
      });
      const doc = res.get({ plain: true });
      return mapper.toDomainModel(doc);
    },
    async updateUser({
      id,
      orderNo,
      status,
      lock,
      transaction,
    }) {
      if (!id && !orderNo) {
        throw new Error('Add id or orderNo to update order.');
      }
      const res = await ordersModel.update({
        ...(status && { status }),
      }, {
        where: {
          ...(id && { id }),
          ...(orderNo && { orderNo }),
        },
        returning: true,
        limit: 1,
        ...(lock != null && { lock }),
        ...(transaction != null && { transaction }),
      });
      const doc = getUpdatedDocResponse(res, 'The order did not find.');
      return mapper.toDomainModel(doc);
    },
  };

  return Object.freeze(Object.create(ordersRepo));
};
