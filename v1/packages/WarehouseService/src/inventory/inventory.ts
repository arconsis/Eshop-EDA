export class Inventory {
  constructor(
    readonly id: string,
    readonly productId: string,
    readonly stock: number,
  ) {}
}
