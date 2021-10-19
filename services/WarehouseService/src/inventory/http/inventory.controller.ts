import { Body, Controller, Get, Param, Post, Put } from '@nestjs/common';
import { InventoryDto } from './inventory.dto';
import { InventoryService } from '../domain/inventory.service';
import { Inventory } from '../inventory';

@Controller('inventory')
export class InventoryController {
  constructor(private readonly inventoryService: InventoryService) {}

  @Get(':id')
  async findOne(@Param('id') id: string): Promise<InventoryDto> {
    const inventory = await this.inventoryService.getInventory(id);
    return InventoryController.mapToInventoryDto(inventory);
  }

  @Put(':id')
  async updateInventory(
    @Body() inventoryDto: InventoryDto,
  ): Promise<InventoryDto> {
    const updatedInventory = await this.inventoryService.updateInventory(
      inventoryDto.productId,
      inventoryDto.stock,
    );
    return InventoryController.mapToInventoryDto(updatedInventory);
  }

  @Post()
  async createInventory(
    @Body() inventoryDto: InventoryDto,
  ): Promise<InventoryDto> {
    const updatedInventory = await this.inventoryService.createInventory(
      inventoryDto.productId,
      inventoryDto.stock,
    );
    return InventoryController.mapToInventoryDto(updatedInventory);
  }

  private static mapToInventoryDto(inventory: Inventory): InventoryDto {
    return new InventoryDto(inventory.id, inventory.productId, inventory.stock);
  }
}
