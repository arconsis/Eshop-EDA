import { Injectable, NotFoundException } from '@nestjs/common';
import { Inventory as InventoryEntity } from '@prisma/client';
import { PrismaService } from 'src/common/prisma.service';
import { Inventory } from './inventory';

@Injectable()
export class InventoryService {
  constructor(private readonly prismaService: PrismaService) {}

  async getInventory(productId: string): Promise<Inventory> {
    const inventoryEntity = await this.prismaService.inventory.findUnique({
      where: { productId },
    });
    if (!inventoryEntity) {
      throw new NotFoundException(
        `Inventory not found for product with id: ${productId}`,
      );
    }
    return InventoryService.mapToInventory(inventoryEntity);
  }

  async updateInventory(productId: string, stock: number): Promise<Inventory> {
    const inventoryEntity = await this.prismaService.inventory.update({
      where: { productId },
      data: { stock },
    });
    return InventoryService.mapToInventory(inventoryEntity);
  }

  async createInventory(productId: string, stock: number): Promise<Inventory> {
    const inventoryEntity = await this.prismaService.inventory.create({
      data: { stock, productId },
    });
    return InventoryService.mapToInventory(inventoryEntity);
  }

  private static mapToInventory(inventoryEntity: InventoryEntity): Inventory {
    return new Inventory(
      inventoryEntity.id,
      inventoryEntity.productId,
      inventoryEntity.stock,
    );
  }
}
