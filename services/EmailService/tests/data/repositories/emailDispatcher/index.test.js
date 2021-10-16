/* eslint-env mocha */
const { expect } = require('chai');
const sinon = require('sinon');
const mailgun = require('mailgun-js');
const emailDispatcherRepositoryFactory = require('../../../../src/data/repositories/emailDispatcher');

const sandbox = sinon.createSandbox();
const mailgunConfig = {
  apiKey: 'foo',
  domain: 'bar',
  host: 'baz',
};
const emailDispatcherRepository = emailDispatcherRepositoryFactory.init(mailgunConfig);

const stubs = {};
describe('emailDispatcherRepository test', () => {
  describe('emailDispatcherRepository exported object', () => {
    it('should return emailDispatcherRepository as object', () => {
      expect(emailDispatcherRepository).to.not.be.undefined;
      expect(emailDispatcherRepository).to.be.an('object');
    });
    it('should emailDispatcherRepository support specific methods', () => {
      expect(typeof (emailDispatcherRepository.sendEmail)).to.eql('function');
    });
  });

  describe('emailDispatcherRepository validate config file', () => {
    it('should return error because of missing file', () => {
      expect(() => {
        emailDispatcherRepositoryFactory.init();
      }).to.throw('Add mailgun configuration');
    });

    it('should return error because of missing apiKey', () => {
      expect(() => {
        emailDispatcherRepositoryFactory.init({
          domain: 'domain',
          host: 'host',
        });
      }).to.throw('Add mailgun apiKey');
    });

    it('should return error because of missing domain', () => {
      expect(() => {
        emailDispatcherRepositoryFactory.init({
          apiKey: 'apiKey',
          host: 'host',
        });
      }).to.throw('Add mailgun domain');
    });

    it('should return error because of missing file host', () => {
      expect(() => {
        emailDispatcherRepositoryFactory.init({
          apiKey: 'apiKey',
          domain: 'domain',
        });
      }).to.throw('Add mailgun host');
    });
  });

  describe('emailDispatcherRepository sendEmail method', () => {
    beforeEach(() => {
      stubs.mailgunSendStub = sinon.stub(
        { send: () => new Promise((resolve) => resolve(null)) },
        'send',
      );
      stubs.mailgunMessagesStub = sinon
        .stub(mailgun({ apiKey: 'k', domain: 'd' }).Mailgun.prototype, 'messages')
        .returns({
          send: stubs.mailgunSendStub,
        });
    });
    afterEach(() => {
      sandbox.restore();
    });
    // eslint-disable-next-line no-undef
    it('should call sendEmail method and dispatch an email', async () => {
      const emailProps = {
        senderEmail: 'botsaris.d@gmail.com',
        receiverEmail: 'botsaris.d@gmail.com',
        subject: 'Test subject',
        text: 'Test text',
      };
      await emailDispatcherRepository.sendEmail({ ...emailProps });
      expect(stubs.mailgunSendStub.callCount).to.eql(1);
      expect(stubs.mailgunSendStub.args[0][0].from).to.eql(emailProps.senderEmail);
      expect(stubs.mailgunSendStub.args[0][0].to).to.eql(emailProps.receiverEmail);
      expect(stubs.mailgunSendStub.args[0][0].subject).to.eql(emailProps.subject);
      expect(stubs.mailgunSendStub.args[0][0].text).to.eql(emailProps.text);
    });
  });
});
