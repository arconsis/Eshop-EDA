const {
  ORDER_STATUSES,
  PENDING_ORDER_STATUS,
} = require('../../../../common/constants');

module.exports = (sequelize, DataTypes) => {
  const Order = sequelize.define('orders', {
    orderNo: {
      type: DataTypes.UUID,
      defaultValue: DataTypes.UUIDV4,
      allowNull: false,
      unique: true,
    },
    userId: {
      type: DataTypes.UUID,
      allowNull: false,
    },
    status: {
      type: DataTypes.ENUM,
      allowNull: false,
      defaultValue: PENDING_ORDER_STATUS,
      values: ORDER_STATUSES,
    },
  }, {
    freezeTableName: true,
  });
  return Order;
};
