const express = require('express');
const asyncWrapper = require('../../utils/asyncWrapper');
const {
  requestOrderRules,
  validate,
} = require('../../middleware/routerValidator');

const router = express.Router({ mergeParams: true });

function init({ ordersService }) {
  router.post('/',
    requestOrderRules(),
    validate,
    asyncWrapper(async (req, res) => {
      const result = await ordersService.requestOrder({
        userId: req.body.userId,
        amount: req.body.amount,
        currency: req.body.currency,
        productId: req.body.productId,
        quantity: req.body.quantity,
      });
      return res.status(201).send({
        data: result,
      });
    }));
  router.get('/:orderNo',
    asyncWrapper(async (req, res) => {
      const result = await ordersService.getOrder(req.params.orderNo);
      return res.status(200).send({
        data: result,
      });
    }));
  return router;
}

module.exports.init = init;
