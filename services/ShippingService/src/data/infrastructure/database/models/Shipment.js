const {
  SHIPMENT_STATUSES,
  PREPERING_SHIPMENT_STATUS,
} = require('../../../../common/constants');

module.exports = (sequelize, DataTypes) => {
  const Shipment = sequelize.define('shipments', {
    shipmentId: {
      type: DataTypes.UUID,
      defaultValue: DataTypes.UUIDV4,
      allowNull: false,
      unique: true,
    },
    orderNo: {
      type: DataTypes.UUID,
      allowNull: false,
    },
    status: {
      type: DataTypes.ENUM,
      allowNull: false,
      defaultValue: PREPERING_SHIPMENT_STATUS,
      values: SHIPMENT_STATUSES,
    },
  }, {
    freezeTableName: true,
  });
  return Shipment;
};
