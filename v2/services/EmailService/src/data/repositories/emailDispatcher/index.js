const mailgun = require('mailgun-js');
const {
  isString,
} = require('../../../common/utils');

module.exports.init = (mailgunConfig) => {
  (function validateConfig() {
    if (!mailgunConfig) {
      throw new Error('Add mailgun configuration');
    }
    if (!mailgunConfig.apiKey || !isString(mailgunConfig.apiKey)) {
      throw new Error('Add mailgun apiKey');
    }
    if (!mailgunConfig.domain || !isString(mailgunConfig.domain)) {
      throw new Error('Add mailgun domain');
    }
    // if (!mailgunConfig.host || !isString(mailgunConfig.host)) {
    //   throw new Error('Add mailgun host');
    // }
  }());

  function createMailgunClient() {
    return mailgun({
      apiKey: mailgunConfig.apiKey,
      domain: mailgunConfig.domain,
      // host: mailgunConfig.host,
    });
  }

  function validateEmailMessage({
    senderEmail,
    receiverEmail,
    subject,
    text,
  }) {
    if (!senderEmail) {
      throw new Error('senderEmail not provided. Make sure you have a "senderEmail" property in your data');
    }
    if (!receiverEmail) {
      throw new Error('receiverEmail not provided. Make sure you have a "receiverEmail" property in your data');
    }
    if (!subject) {
      throw new Error('subject not provided. Make sure you have a "subject" property in your data');
    }
    if (!text) {
      throw new Error('text not provided. Make sure you have a "text" or "html" property in your data');
    }
  }

  const client = createMailgunClient();

  const emailDispatcherRepository = {
    async sendEmail({
      senderEmail,
      receiverEmail,
      subject,
      text,
    } = {}) {
      validateEmailMessage({
        senderEmail, receiverEmail, subject, text,
      });
      return client.messages().send({
        from: senderEmail,
        to: receiverEmail,
        subject,
        text,
      });
    },
  };

  return Object.create(emailDispatcherRepository);
};
