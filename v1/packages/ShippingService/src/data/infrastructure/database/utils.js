const Sequelize = require('sequelize');
const { transform } = require('lodash');

const isObject = obj => (typeof obj === 'object' && typeof obj !== 'function' && obj !== null);

function includeDBCredentials(config) {
  if (config.database && config.username && config.password && config.host) {
    return true;
  }
  return false;
}

function includeDBUrl(config) {
  if (config.connectionUri) {
    return true;
  }
  return false;
}

function validateConfig(config) {
  if (!config) {
    throw new Error('Add database configuration');
  }
  if (!includeDBUrl(config) && !includeDBCredentials(config)) {
    throw new Error('Add database configuration');
  }
}

const sequelizeOperators = transform(Sequelize.Op, (o, v, k) => {
  if (typeof v !== 'symbol') {
    return;
  }
  o[k] = v;
});

function replaceKeyDeep(obj, keyMap) {
  return Object.getOwnPropertySymbols(obj).concat(Object.keys(obj)).reduce((memo, key)=> {
    // determine which key we are going to use
    const targetKey = keyMap[key] ? keyMap[key] : key;

    if (Array.isArray(obj[key])) {
      // recurse if an array
      memo[targetKey] = obj[key].map((val) => {
        if (Object.prototype.toString.call(val) === '[object Object]') {
          return replaceKeyDeep(val, keyMap);
        }
        return val;
      });
    } else if (Object.prototype.toString.call(obj[key]) === '[object Object]') {
      // recurse if Object
      memo[targetKey] = replaceKeyDeep(obj[key], keyMap);
    } else {
      // assign the new value
      memo[targetKey] = obj[key];
    }

    // return the modified object
    return memo;
  }, {});
}

function replaceFilterOperators(filter) {
  if (filter) return replaceKeyDeep(filter, sequelizeOperators);
  return {};
}

function getUpdatedDocResponse(res, message = 'Doc did not found.') {
  if (!res || res == 0) {
    throw new Error(message);
  }
  if (!Array.isArray(res) || res.length < 2 || !Array.isArray(res[1]) || res[1].length <= 0) {
    throw new Error(message);
  }
  return res[1][0].dataValues;
}

module.exports = {
  isObject,
  validateConfig,
  replaceFilterOperators,
  getUpdatedDocResponse,
};
