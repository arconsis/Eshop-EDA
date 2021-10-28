module.exports = (sequelize, DataTypes) => {
  const Transaction = sequelize.define('transactions', {
    transactionId: {
      type: DataTypes.UUID,
      defaultValue: DataTypes.UUIDV4,
      allowNull: false,
      unique: true,
    },
    orderId: {
      type: DataTypes.UUID,
      allowNull: false,
      unique: false,
    },
    amount: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    currency: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    metadata: {
      type: DataTypes.JSONB,
    },
  }, {
    freezeTableName: true,
  });

  return Transaction;
};
