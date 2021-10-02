-- CreateTable
CREATE TABLE "Inventory" (
    "id" UUID NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,
    "stock" INTEGER NOT NULL CHECK ( stock >= 0 ),
    "productId" TEXT NOT NULL
);

-- CreateIndex
CREATE UNIQUE INDEX "Inventory_productId_key" ON "Inventory"("productId");
