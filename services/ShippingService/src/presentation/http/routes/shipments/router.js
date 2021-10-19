const express = require('express');
const asyncWrapper = require('../../utils/asyncWrapper');

const router = express.Router({ mergeParams: true });

function init({ shipmentsService }) {
  router.get('/',
    asyncWrapper(async (req, res) => {
      const result = await shipmentsService.listShipments();
      return res.status(200).send(result);
    }));
  router.put('/:shipmentId',
    // (...args) => endpointValidator.checkParamsToRefreshToken(...args),
    asyncWrapper(async (req, res) => {
      const result = await shipmentsService.updateDeliveredShipment(req.params.shipmentId);
      return res.status(200).send({
        data: result,
      });
    }));

  return router;
}

module.exports.init = init;
