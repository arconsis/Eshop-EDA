const validator = require('validator');

const isValidEmail = (emailInput) => validator.isEmail(emailInput);

const isString = (val) => (typeof val === 'string' || val instanceof String);

module.exports = {
  isValidEmail,
  isString,
};
