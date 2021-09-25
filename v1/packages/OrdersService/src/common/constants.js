const PENDING_ORDER_STATUS = 'pending';
const VALIDATED_ORDER_STATUS = 'valid';
const PAID_ORDER_STATUS = 'paid';
const SHIPMENT_PREPARED_ORDER_STATUS = 'shipment_prepared';
const SHIPPED_ORDER_STATUS = 'shipped';
const COMPLETED_ORDER_STATUS = 'completed';
const FAILED_ORDER_STATUS = 'failed';
const CANCELLED_ORDER_STATUS = 'cancelled';
const REFUNDED_ORDER_STATUS = 'refunded';
const ORDER_STATUSES = Object.freeze([
  PENDING_ORDER_STATUS,
  VALIDATED_ORDER_STATUS,
  PAID_ORDER_STATUS,
  SHIPMENT_PREPARED_ORDER_STATUS,
  SHIPPED_ORDER_STATUS,
  COMPLETED_ORDER_STATUS,
  FAILED_ORDER_STATUS,
  CANCELLED_ORDER_STATUS,
  REFUNDED_ORDER_STATUS,
]);

const ORDER_CREATED_EVENT_TYPE = 'OrderCreated';
const ORDERS_TOPIC = 'Orders';
const PAYMENTS_TOPIC = 'Payments';
const ORDER_PAID_EVENT = 'PaymentProcessed';
const ORDER_CONFIRMED_EVENT = 'OrderConfirmed';
const SHIPMENTS_TOPIC = 'Shipments';
const SHIPMENT_PREPARED_EVENT = 'ShipmentPrepared';

module.exports = {
  PENDING_ORDER_STATUS,
  VALIDATED_ORDER_STATUS,
  PAID_ORDER_STATUS,
  SHIPMENT_PREPARED_ORDER_STATUS,
  SHIPPED_ORDER_STATUS,
  COMPLETED_ORDER_STATUS,
  FAILED_ORDER_STATUS,
  CANCELLED_ORDER_STATUS,
  REFUNDED_ORDER_STATUS,
  ORDER_STATUSES,
  ORDERS_TOPIC,
  ORDER_CREATED_EVENT_TYPE,
  PAYMENTS_TOPIC,
  ORDER_PAID_EVENT,
  SHIPMENTS_TOPIC,
  ORDER_CONFIRMED_EVENT,
  SHIPMENT_PREPARED_EVENT,
};
