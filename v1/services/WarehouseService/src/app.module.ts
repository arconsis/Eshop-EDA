import { Module } from '@nestjs/common';
import { InventoryModule } from './inventory/inventory.module';

@Module({
  imports: [InventoryModule],
})
export class AppModule {}
