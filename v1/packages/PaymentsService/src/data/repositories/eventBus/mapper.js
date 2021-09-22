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

const toEventBusMessage = function toEventBusMessage({
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
          orderNo,
        },
      }),
    },
  ];
};

module.exports = {
  toEventBusMessage,
};