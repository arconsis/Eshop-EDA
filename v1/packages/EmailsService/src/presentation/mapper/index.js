module.exports.mapEventPayloadToEmailBody = (messageBody) => ({
  receiverEmail: messageBody.payload.email,
  ...messageBody.payload,
});

// Example of UserRegistered event message
//
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
// {"id":"6be42504-f5b0-4548-b86f-8a5a57ff2921", "type":"UserRegistered", "version":"1.0.0", "payload":{ "email":"botsaris.d@gmail.com", "firstName":"Dimos", "lastName":"Botsaris" }}
