import { randomUUID } from 'crypto';
import { Module } from '@nestjs/common';
import { CommonModule } from '../common/common.module';
import { InventoryService } from './domain/inventory.service';
import { InventoryController } from './http/inventory.controller';
import { InventoryKafkaController } from './kafka/inventorykafka.controller';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { ConfigService } from '@nestjs/config';

@Module({
  imports: [
    CommonModule,
    ClientsModule.registerAsync([
      {
        name: 'KAFKA_CLIENT',
        imports: [CommonModule],
        useFactory: (configService: ConfigService) => ({
          transport: Transport.KAFKA,
          options: {
            client: {
              clientId: 'inventory-service',
              brokers:
                configService.get<string>('KAFKA_BROKER')?.split(',') ?? [],
              ssl: process.env.NODE_ENV !== 'production' ? false : true,
            },
            consumer: {
              groupId: 'inventory-consumer',
              readUncommitted: false
            },
            producer: {
              maxInFlightRequests: 1,
              idempotent: true,
              transactionalId: `inventory_producer_${randomUUID()}`,
            }
          },
        }),
        inject: [ConfigService],
      },
    ]),
  ],
  providers: [InventoryService],
  controllers: [InventoryController, InventoryKafkaController],
})
export class InventoryModule {}
