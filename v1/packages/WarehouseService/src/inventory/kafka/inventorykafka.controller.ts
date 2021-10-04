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
    const { id } = ordersEvent.value;
    const { productId, quantity, orderNo } = ordersEvent.value.payload;

    const stockUpdated = await this.inventoryService.tryUpdateStockForOrder(
      productId,
      quantity,
    );

    if (stockUpdated) {
      return this.client.emit(
        Topic.Warehouse,
        InventoryKafkaController.createEvent(
          orderNo,
          id,
          WarehouseEventType.OrderValidated,
          {
            productId,
            quantity,
            orderNo,
          },
        ),
      );
    } else {
      return this.client.emit(
        Topic.Warehouse,
        InventoryKafkaController.createEvent(
          orderNo,
          id,
          WarehouseEventType.OrderInvalid,
          {
            productId,
            quantity,
            orderNo,
          },
        ),
      );
    }
  }

  private static createEvent(
    orderNo: string,
    id: string,
    type: WarehouseEventType,
    payload: InventoryPayload,
  ): InventoryEvent {
    return {
      key: orderNo,
      value: {
        id,
        type,
        version: version,
        payload,
      },
    };
  }
}
