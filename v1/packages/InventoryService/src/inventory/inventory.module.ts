import { Module } from '@nestjs/common';
import { CommonModule } from '../common/common.module';
import { InventoryController } from './inventory.controller';
import { InventoryService } from './inventory.service';

@Module({
  imports: [CommonModule],
  providers: [InventoryService],
  controllers: [InventoryController],
})
export class InventoryModule {}
