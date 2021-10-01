import { Controller, Get, Param } from '@nestjs/common';
import { Inventory } from './inventory';
import { InventoryService } from './inventory.service';

@Controller('inventory')
export class InventoryController {
  constructor(private readonly inventoryService: InventoryService) {}
  @Get(':id')
  async findOne(@Param('id') id: string): Promise<Inventory> {
    return this.inventoryService.getInventory(id);
  }
}
