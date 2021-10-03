import { Module } from '@nestjs/common';
import { CommonModule } from '../common/common.module';
import { InventoryService } from './domain/inventory.service';
import { InventoryController } from './http/inventory.controller';
import { InventoryKafkaController } from './kafka/inventorykafka.controller';
import { ClientsModule, Transport } from '@nestjs/microservices';

@Module({
  imports: [
    CommonModule,
    ClientsModule.register([
      {
        name: 'KAFKA_CLIENT',
        transport: Transport.KAFKA,
        options: {
          client: {
            clientId: 'inventory-service',
            brokers: ['localhost:9092'],
          },
          consumer: {
            groupId: 'inventory-consumer',
          },
        },
      },
    ]),
  ],
  providers: [InventoryService],
  controllers: [InventoryController, InventoryKafkaController],
})
export class InventoryModule {}
