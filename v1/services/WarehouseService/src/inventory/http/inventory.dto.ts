export class InventoryDto {
  constructor(
    readonly id: string,
    readonly productId: string,
    readonly stock: number,
  ) {}
}
