const { Kafka } = require('kafkajs');

module.exports.init = (kafkaConfig) => {
  const client = new Kafka({
    clientId: kafkaConfig.clientId,
    brokers: kafkaConfig.brokers,
    connectionTimeout: kafkaConfig.connectionTimeout,
    requestTimeout: kafkaConfig.requestTimeout,
  });

  const brokerInterface = {
    async connectAsConsumer(groupId) {
      this.consumer = client.consumer({ groupId });
      await this.consumer.connect();
    },
    async subscribeToTopics(topics, fromBeginning) {
      if (Array.isArray(topics)) {
        await Promise.all(topics.map(async (topic) => {
          await this.consumer.subscribe({ topic, fromBeginning });
        }));
      } else {
        await this.consumer.subscribe({ topics, fromBeginning });
      }
    },
    async consumeStream({
      groupId,
      topics,
      fromBeginning = true,
    }, handler) {
      await this.connectAsConsumer(groupId);
      await this.subscribeToTopics(topics, fromBeginning);
      await this.consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
          try {
            await handler({
              topic,
              partition,
              message: JSON.parse(message.value.toString()),
            });
          } catch (e) {
            console.error('unable to handle incoming message', e);
          }
        },
      });
    },
    async sendMessages(topic, messages) {
      const producer = client.producer();
      await producer.connect();
      await producer.send({
        topic,
        messages,
      });
    },
  };
  return Object.create(brokerInterface);
};
