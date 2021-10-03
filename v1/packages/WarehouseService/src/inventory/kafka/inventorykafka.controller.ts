import { Controller, Inject, OnModuleInit } from '@nestjs/common';
import { ClientKafka, MessagePattern, Payload } from '@nestjs/microservices';
import { InventoryService } from '../domain/inventory.service';
import {
  OrderEventType,
  Topic,
  version,
  WarehouseEventType,
} from '../../common/kafka.models';
import { InventoryEvent, InventoryPayload } from './inventory.event';

@Controller()
export class InventoryKafkaController implements OnModuleInit {
  constructor(
    @Inject('KAFKA_CLIENT') private readonly client: ClientKafka,
    private readonly inventoryService: InventoryService,
  ) {}

  async onModuleInit() {
    this.client.subscribeToResponseOf(Topic.Orders);
    await this.client.connect();
  }

  @MessagePattern(Topic.Orders) // Our topic name
  async getOrders(@Payload() ordersEvent: any) {
    if (ordersEvent.value.type !== OrderEventType.OrderRequested) {
      return;
    }

    console.log(ordersEvent.value);
    const orderId = ordersEvent.key;
    const { id } = ordersEvent.value;
    const { productId, count } = ordersEvent.value.payload;

    const stockUpdated = await this.inventoryService.tryUpdateStockForOrder(
      productId,
      count,
    );

    if (stockUpdated) {
      return this.client.emit(
        Topic.Warehouse,
        InventoryKafkaController.createEvent(
          orderId,
          id,
          WarehouseEventType.OrderValidated,
          {
            productId,
            count,
          },
        ),
      );
    } else {
      return this.client.emit(
        Topic.Warehouse,
        InventoryKafkaController.createEvent(
          orderId,
          id,
          WarehouseEventType.OrderInvalid,
          {
            productId,
            count,
          },
        ),
      );
    }
  }

  private static createEvent(
    orderId: string,
    id: string,
    type: WarehouseEventType,
    payload: InventoryPayload,
  ): InventoryEvent {
    return {
      key: orderId,
      value: {
        id,
        type,
        version: version,
        payload,
      },
    };
  }
}
