const { randomUUID } = require('crypto');
const { Kafka } = require('kafkajs');
const logger = require('../../../common/logger');
const {
  kafka: kafkaConfig,
} = require('../../../configuration');

/*
    Note: Kafka requires that the transactional producer have the following configuration to guarantee EoS ("Exactly-once-semantics"):
      The producer must have a max in flight requests of 1
      The producer must wait for acknowledgement from all replicas (acks=-1)
      The producer must have unlimited retries
      The producer mush have the transaction id is distinct for each producer
    Note: Exactly-Once Consumer
      The consumer consumers can use isolation.level to read-only read_committed to make the whole process as an atomic operation
    https://stackoverflow.com/questions/58894281/difference-between-idempotence-and-exactly-once-in-kafka-stream
    https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/
    https://www.baeldung.com/kafka-exactly-once
*/
const DEFAULT_PRODUCER_MESSAGES_CONFIG = {
  acks: -1, // The producer must wait for acknowledgement from all replicas (acks=-1)
};

const DEFAULT_CONSUMER_CONFIG = {
  readUncommitted: false, // isolation lvl on consumer
};

const DEFAULT_PRODUCER_CONFIG = {
  maxInFlightRequests: 1, // Note that enabling idempotence requires MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to be less than or equal to 5,
  idempotent: true, // enable.idempotence=true”
  transactionalId: `${kafkaConfig.clientId}_producer_${randomUUID()}`,
};

class EventBusRepository {
  constructor() {
    this.client = new Kafka({
      clientId: kafkaConfig.clientId,
      brokers: kafkaConfig.brokers,
      connectionTimeout: kafkaConfig.connectionTimeout,
      requestTimeout: kafkaConfig.requestTimeout,
      ssl: process.env.NODE_ENV === 'production',
    });
  }

  async connectAsConsumer({ groupId }) {
    this.consumer = this.client.consumer({
      groupId,
      ...DEFAULT_CONSUMER_CONFIG,
    });
    await this.consumer.connect();
  }

  async connectAsProducer() {
    this.producer = this.client.producer(DEFAULT_PRODUCER_CONFIG);
    await this.producer.connect();
  }

  async _subscribeToTopics(topics, fromBeginning) {
    if (Array.isArray(topics)) {
      await Promise.all(topics.map(async (topic) => {
        await this.consumer.subscribe({ topic, fromBeginning });
      }));
    } else {
      await this.consumer.subscribe({ topics, fromBeginning });
    }
  }

  async startConsume({
    topics,
    fromBeginning = true,
  }, handler) {
    await this._subscribeToTopics(topics, fromBeginning);
    await this.consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        try {
          await handler({
            topic,
            partition,
            message: JSON.parse(message.value.toString()),
          });
        } catch (e) {
          logger.error('unable to handle incoming message', e);
        }
      },
    });
  }

  async sendMessages(topic, messages) {
    await this.producer.send({
      topic,
      messages,
      ...DEFAULT_PRODUCER_MESSAGES_CONFIG, // ACKS_CONFIG be 'all'.
    });
  }

  async sendInTransaction(events) {
    const transaction = await this.producer.transaction();
    const eventsToPublish = Array.isArray(events) ? events : [events];
    try {
      await Promise.all(eventsToPublish.map(async (event) => {
        await transaction.send({
          topic: event.topic,
          messages: event.messages,
          ...DEFAULT_PRODUCER_MESSAGES_CONFIG,
        });
      }));
      await transaction.commit();
    } catch (e) {
      await transaction.abort();
    }
  }
}

module.exports = new EventBusRepository();
