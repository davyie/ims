export interface Money { amount: number; currency: string; }
export interface StoragePosition { zone: string; shelf: string; row: number; column: number; }

export interface Item {
  id: string;
  sku: string;
  name: string;
  description?: string;
  category: string;
  defaultPrice: number;
  currency: string;
  zone: string;
  shelf: string;
  row: number;
  column: number;
  totalStorageStock: number;
  createdAt: string;
  updatedAt: string;
}

export interface Market {
  id: string;
  name: string;
  place: string;
  openDate: string;
  closeDate: string;
  status: 'SCHEDULED' | 'OPEN' | 'CLOSED';
  createdAt: string;
}

export interface MarketItem {
  id: string;
  marketId: string;
  itemId: string;
  allocatedStock: number;
  currentStock: number;
  marketPrice: number;
  currency: string;
}

export type TransactionType =
  | 'SHIFT_TO_MARKET'
  | 'SALE'
  | 'RETURN_FROM_MARKET'
  | 'STOCK_ADJUSTMENT'
  | 'INCREMENT';

export interface Transaction {
  id: string;
  marketId?: string;
  itemId: string;
  type: TransactionType;
  quantityDelta: number;
  stockBefore: number;
  stockAfter: number;
  note?: string;
  occurredAt: string;
  createdBy: string;
}

export interface MarketItemSummary {
  itemId: string;
  itemName: string;
  sku: string;
  allocatedStock: number;
  currentStock: number;
  sold: number;
  revenue: number;
  currency: string;
}

export interface MarketSummary {
  marketId: string;
  marketName: string;
  totalItemTypes: number;
  totalAllocatedStock: number;
  totalCurrentStock: number;
  totalSold: number;
  totalRevenue: number;
  currency: string;
  items: MarketItemSummary[];
}

export interface AllMarketsSummary {
  totalMarkets: number;
  totalItemsSold: number;
  totalRevenue: number;
  currency: string;
  markets: MarketSummary[];
}

export interface StorageItem {
  itemId: string;
  sku: string;
  name: string;
  category: string;
  currentStock: number;
}

export interface StorageSummary {
  items: StorageItem[];
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

// ---- Request DTOs ----

export interface RegisterItemRequest {
  sku: string;
  name: string;
  description?: string;
  category: string;
  defaultPrice: number;
  currency: string;
  zone: string;
  shelf: string;
  row: number;
  column: number;
  initialStock: number;
}

export interface UpdateItemRequest {
  name: string;
  description?: string;
  category: string;
  defaultPrice: number;
  currency: string;
  zone: string;
  shelf: string;
  row: number;
  column: number;
}

export interface AdjustStockRequest {
  delta: number;
  note?: string;
  createdBy?: string;
}

export interface CreateMarketRequest {
  name: string;
  place: string;
  openDate: string;
  closeDate: string;
}

export interface ShiftItemRequest {
  itemId: string;
  quantity: number;
  marketPrice: number;
  currency: string;
  createdBy?: string;
}

export interface IncrementStockRequest {
  quantity: number;
  note?: string;
  createdBy?: string;
}

export interface DecrementStockRequest {
  quantity: number;
  note?: string;
  createdBy?: string;
}

export interface SetPriceRequest {
  price: number;
  currency: string;
}
