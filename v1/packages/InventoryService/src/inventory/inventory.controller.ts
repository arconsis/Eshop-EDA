import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { Inventory } from './inventory';
import { InventoryDto } from './inventory.dto';
import { InventoryService } from './inventory.service';

@Controller('inventory')
export class InventoryController {
  constructor(private readonly inventoryService: InventoryService) {}

  @Get(':id')
  async findOne(@Param('id') id: string): Promise<InventoryDto> {
    const inventory = await this.inventoryService.getInventory(id);
    return this.mapToInventoryDto(inventory);
  }

  @Post('id')
  async updateInventory(
    @Body() inventoryDto: InventoryDto,
  ): Promise<InventoryDto> {
    const updatedInventory = await this.inventoryService.updateInventory(
      inventoryDto.productId,
      inventoryDto.stock,
    );
    return this.mapToInventoryDto(updatedInventory);
  }

  @Post()
  async createInventory(
    @Body() inventoryDto: InventoryDto,
  ): Promise<InventoryDto> {
    const updatedInventory = await this.inventoryService.createInventory(
      inventoryDto.productId,
      inventoryDto.stock,
    );
    return this.mapToInventoryDto(updatedInventory);
  }

  private mapToInventoryDto(inventory: Inventory): InventoryDto {
    return new InventoryDto(inventory.id, inventory.productId, inventory.stock);
  }
}
