import { WarehouseEventType } from '../../common/kafka.models';

export interface InventoryEvent {
  key: string;
  value: {
    id: string;
    type: WarehouseEventType;
    version: string;
    payload: InventoryPayload;
  };
}

export interface InventoryPayload {
  productId: string;
  quantity: number;
  orderNo: string;
}
