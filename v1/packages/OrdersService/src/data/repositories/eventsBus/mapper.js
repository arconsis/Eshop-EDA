// const msg = {
//   id: "6be42504-f5b0-4548-b86f-8a5a57ff2921",
//   type: "UserRegistered",
//   version: "1.0.0",
//   payload: {
//     email: "botsaris.d@gmail.com",
//     firstName: "Dimos",
//     lastName: "Botsaris"
//   }
// }
// {"id":"ad84efc7-0253-41d8-85a5-aa5051aa4b18","type":"OrderCreated","version":"1.0.0","payload":{"email":"dimosthenis.botsaris@arconsis.com","firstName":"Dimos","lastName":"Botsaris","userId":"2f5acab8-8237-4841-a188-62af0bbbaac8","amount":100,"currency":"EUR"}

const toOrderRequestedMessage = function toOrderRequestedMessage({
  id,
  orderNo,
  type,
  version = '1.0.0',
  message,
  ...rest
}) {
  return [
    {
      key: orderNo,
      value: JSON.stringify({
        id,
        type,
        version,
        payload: {
          userId: rest.userId,
          productId: rest.productId,
          quantity: rest.quantity,
          amount: rest.amount,
          currency: rest.currency,
          orderNo,
        },
      }),
    },
  ];
};

const toCreateOrderMessage = function toCreateOrderMessage({
  id,
  orderNo,
  type,
  version = '1.0.0',
  message,
  ...rest
}) {
  return [
    {
      key: orderNo,
      value: JSON.stringify({
        id,
        type,
        version,
        payload: {
          email: rest.email,
          firstName: rest.firstName,
          lastName: rest.lastName,
          userId: rest.userId,
          amount: rest.amount,
          currency: rest.currency,
          productId: rest.productId,
          orderNo,
        },
      }),
    },
  ];
};

module.exports = {
  toOrderRequestedMessage,
  toCreateOrderMessage,
};
