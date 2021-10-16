const { getUpdatedDocResponse } = require('../../infrastructure/database/utils');
const mapper = require('./mapper');

module.exports.init = function init({
  shipments: shipmentsModel,
  sequelize,
}) {
  const shipmentsRepo = {
    async listShipments({
      attributes,
      limit = 50,
      offset = 0,
      transaction,
    }) {
      const list = await shipmentsModel.findAndCountAll({
        order: [
          ['id', 'DESC'],
        ],
        attributes: attributes && Array.isArray(attributes) && attributes.length > 0
          ? attributes
          : { exclude: [] },
        limit,
        offset,
        ...(transaction != null && { transaction }),
      });
      return {
        data: list.rows?.map((el) => el.get({ plain: true })) || [],
        pagination: {
          limit,
          offset,
          total: list?.count || 0,
        },
      };
    },
    async createShipment({
      orderNo,
      transaction,
    }) {
      const res = await shipmentsModel.create({
        orderNo,
      }, {
        ...(transaction != null && { transaction }),
      });
      const doc = res.get({ plain: true });
      return mapper.toDomainModel(doc);
    },
    async updateShipment({
      id,
      shipmentId,
      status,
      lock,
      transaction,
    }) {
      if (!id && !shipmentId) {
        throw new Error('Add id or shipmentId to update shipment.');
      }
      const res = await shipmentsModel.update({
        ...(status && { status }),
      }, {
        where: {
          ...(id && { id }),
          ...(shipmentId && { shipmentId }),
        },
        returning: true,
        limit: 1,
        ...(lock != null && { lock }),
        ...(transaction != null && { transaction }),
      });
      const doc = getUpdatedDocResponse(res, 'The shipment did not find.');
      return mapper.toDomainModel(doc);
    },
  };
  return Object.freeze(Object.create(shipmentsRepo));
};
