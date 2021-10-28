const ShipmentModel = require('../../../domain/shipments/model');

const toDomainModel = function toDomainModel(databaseDoc) {
  return new ShipmentModel(databaseDoc);
};

module.exports = {
  toDomainModel,
};
