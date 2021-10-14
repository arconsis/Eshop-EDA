const TransactionModel = require('../../../domain/transactions/model');

const toDomainModel = function toDomainModel(databaseDoc) {
  return new TransactionModel(databaseDoc);
};

module.exports = {
  toDomainModel,
};
