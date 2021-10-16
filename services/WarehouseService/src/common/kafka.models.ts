export enum Topic {
  Orders = 'Orders',
  Warehouse = 'Warehouse',
}

export enum OrderEventType {
  OrderRequested = 'OrderRequested',
}

export enum WarehouseEventType {
  OrderValidated = 'OrderValidated',
  OrderInvalid = 'OrderInvalid',
}

export const version = '1.0.0';
