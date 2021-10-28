const {
  body,
  validationResult,
} = require('express-validator');
const errorHandler = require('../routes/errors/router');
const errors = require('../../../common/errors');

const requestOrderRules = () => [
  body('userId')
    .exists().withMessage('userId not provided. Make sure you have a "userId" property in your body params.'),
  body('amount')
    .exists()
    .withMessage('amount not provided. Make sure you have a "amount" property in your body params.')
    .isCurrency()
    .withMessage('amount not provided in correct format. Make sure you have a "amount" as numeric property in your body params.'),
  body('currency')
    .exists()
    .withMessage('currency not provided. Make sure you have a "currency" property in your body params.'),
  body('productId')
    .exists()
    .withMessage('productId not provided. Make sure you have a "productId" property in your body params.')
    .isNumeric()
    .withMessage('productId not provided in correct format. Make sure you have a "productId" as numeric property in your body params.'),
  body('quantity')
    .exists()
    .withMessage('quantity not provided. Make sure you have a "quantity" property in your body params.'),
];

const validate = (req, res, next) => {
  const validationErrors = validationResult(req);
  if (validationErrors.isEmpty()) {
    return next();
  }
  const errMessage = validationErrors
    && validationErrors.errors
    && Array.isArray(validationErrors.errors)
    && validationErrors.errors.length > 0
    ? validationErrors.errors[0].msg
    : 'Bad request';
  return errorHandler(new errors.BadRequest(errMessage, 'BAD_BODY_PARAMS'), req, res, next);
};

module.exports = {
  requestOrderRules,
  validate,
};
