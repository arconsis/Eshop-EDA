const express = require('express');
const asyncWrapper = require('../../utils/asyncWrapper');

const router = express.Router({ mergeParams: true });

function init({ ordersService }) {
  router.post('/',
    // (...args) => endpointValidator.checkParamsToRefreshToken(...args),
    asyncWrapper(async (req, res) => {
      const result = await ordersService.createOrder(req.body.userId);
      return res.status(201).send({
        data: result,
      });
    }));
  return router;
}

module.exports.init = init;
