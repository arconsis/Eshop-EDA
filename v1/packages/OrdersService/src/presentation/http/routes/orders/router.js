const express = require('express');
const asyncWrapper = require('../../utils/asyncWrapper');

const router = express.Router({ mergeParams: true });

function init({ ordersService }) {
  router.post('/',
    // (...args) => endpointValidator.checkParamsToRefreshToken(...args),
    asyncWrapper(async (req, res) => {
      const result = await ordersService.createOrder({
        userId: req.body.userId,
        amount: req.body.amount,
        currency: req.body.currency,
      });
      return res.status(201).send({
        data: result,
      });
    }));
  router.get('/:orderNo',
    // (...args) => endpointValidator.checkParamsToRefreshToken(...args),
    asyncWrapper(async (req, res) => {
      const result = await ordersService.getOrder(req.params.orderNo);
      return res.status(201).send({
        data: result,
      });
    }));
  return router;
}

module.exports.init = init;
