import { Injectable, NotFoundException } from '@nestjs/common';
import { Inventory as InventoryEntity } from '@prisma/client';
import { PrismaService } from 'src/common/prisma.service';
import { Inventory } from '../inventory';

// We should not call the database handling code directly in the Domain hide with the repository pattern
// Done only for simplicity here

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

  async checkIfProductIsOrderable(
    productId: string,
    count: number,
  ): Promise<boolean> {
    try {
      await this.prismaService.inventory.update({
        where: { productId },
        data: {
          stock: { decrement: count },
        },
      });
      return true;
    } catch (e) {
      return false;
    }
  }

  private static mapToInventory(inventoryEntity: InventoryEntity): Inventory {
    return new Inventory(
      inventoryEntity.id,
      inventoryEntity.productId,
      inventoryEntity.stock,
    );
  }
}
