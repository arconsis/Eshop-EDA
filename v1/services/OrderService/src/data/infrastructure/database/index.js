/* eslint-disable prefer-object-spread */
const fs = require('fs');
const path = require('path');
const Sequelize = require('sequelize');

module.exports.init = function init(config) {
  const basename = path.basename(__filename);
  let sequelize;
  const defaultOptions = {
    dialect: 'postgres',
    operatorsAliases: false,
    logging: true,
    timezone: '+00:00',
    dialectOptions: {
      ssl: process.env.NODE_ENV === 'production'
        ? {
          require: process.env.NODE_ENV === 'production',
          rejectUnauthorized: false,
        }
        : undefined,
    },
    define: {
      freezeTableName: true,
    },
  };

  if (config.connectionUri) {
    sequelize = new Sequelize(config.connectionUri, { ...defaultOptions, ...config.settings });
  } else {
    sequelize = new Sequelize(config.database, config.username, config.password, Object.assign(
      {},
      {
        host: config.host,
        port: config.port,
      },
      { ...defaultOptions, ...config.settings },
    ));
  }

  const db = {
    entities: {},
    async authenticate() {
      return sequelize.authenticate();
    },
    async close() {
      await sequelize.close();
      return sequelize.connectionManager.close();
    },
    sync: (force = false, alter = false) => {
      sequelize.sync({
        force,
        alter,
      });
    },
    async createTransaction() {
      return sequelize.transaction();
    },
    runTransaction: sequelize.transaction.bind(sequelize),
  };

  const modelsPath = `${__dirname}/models`;

  fs
    .readdirSync(modelsPath)
    .filter((file) => (file.indexOf('.') !== 0) && (file !== basename) && (file.slice(-3) === '.js'))
    .forEach((file) => {
      const model = sequelize.import(path.join(modelsPath, file));
      db[model.name] = model;
      db.entities[model.name] = model;
    });

  Object.keys(db).forEach((modelName) => {
    if (db[modelName].associate) {
      db[modelName].associate(db);
      db.entities[modelName].associate(db);
    }
  });

  db.sequelize = sequelize;
  db.Sequelize = Sequelize;
  return db;
};
