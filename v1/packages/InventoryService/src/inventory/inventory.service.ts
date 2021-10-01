import { Injectable, NotFoundException } from '@nestjs/common';
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
    return new Inventory(inventoryEntity.productId, inventoryEntity.stock);
  }
}
