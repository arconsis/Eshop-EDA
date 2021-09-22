module.exports = (sequelize, DataTypes) => {
  const Transaction = sequelize.define('transaction', {
    transactionId: {
      type: DataTypes.UUID,
      defaultValue: DataTypes.UUIDV4,
      allowNull: false,
      unique: true,
    },
    orderId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      unique: false,
    },
    metadata: {
      type: DataTypes.JSONB,
    },
  }, {
    freezeTableName: true,
  });

  return Transaction;
};
