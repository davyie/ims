// ---- Backend domain models ----

export interface Item {
  itemId: string;
  userId: string;
  sku: string;
  name: string;
  description?: string;
  category?: string;
  unitOfMeasure?: string;
  unitPrice?: number;
  createdAt: string;
  updatedAt: string;
}

export interface Warehouse {
  warehouseId: string;
  userId: string;
  name: string;
  address?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'ARCHIVED';
  createdAt: string;
  updatedAt: string;
}

export interface WarehouseStock {
  stockId: string;
  warehouseId: string;
  itemId: string;
  quantity: number;
  reservedQty: number;
  binLocation?: string;
  reorderLevel: number;
  lastUpdated: string;
}

export type MarketType = 'FARMERS_MARKET' | 'RETAIL' | 'WHOLESALE' | 'POP_UP' | 'OTHER';
export type MarketStatus = 'SCHEDULED' | 'OPEN' | 'CLOSED' | 'ARCHIVED' | 'SUSPENDED';

export interface Market {
  marketId: string;
  userId: string;
  name: string;
  location?: string;
  marketType: MarketType;
  status: MarketStatus;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface MarketStock {
  marketStockId: string;
  marketId: string;
  itemId: string;
  quantity: number;
  lastUpdated: string;
}

export type LocationType = 'WAREHOUSE' | 'MARKET';
export type TransferStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';

export interface Transfer {
  transferId: string;
  userId: string;
  itemId: string;
  quantity: number;
  sourceType: LocationType;
  sourceId: string;
  destinationType: LocationType;
  destinationId: string;
  status: TransferStatus;
  failureReason?: string;
  correlationId: string;
  createdAt: string;
  updatedAt: string;
}

export interface TransactionRecord {
  recordId: string;
  eventId: string;
  correlationId?: string;
  eventType: string;
  originService: string;
  entityId?: string;
  userId?: string;
  occurredAt?: string;
  recordedAt: string;
  payload?: string;
  kafkaTopic?: string;
  kafkaPartition?: number;
}

export interface EventProjectionDocument {
  id: string;
  eventId: string;
  eventType: string;
  originService: string;
  entityId?: string;
  userId?: string;
  occurredAt?: string;
  recordedAt?: string;
  payload?: Record<string, unknown>;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

// Category — no backend support, managed locally
export interface Category {
  id: string;
  name: string;
}

// Combined view for storage overview (WarehouseStock + Item lookup)
export interface StorageItem {
  itemId: string;
  sku: string;
  name: string;
  category?: string;
  currentStock: number;
  binLocation?: string;
  warehouseId: string;
}

// ---- Request DTOs ----

export interface CreateItemRequest {
  sku: string;
  name: string;
  description?: string;
  category?: string;
  unitOfMeasure?: string;
  unitPrice?: number;
}

export interface UpdateItemRequest {
  name: string;
  description?: string;
  category?: string;
  unitOfMeasure?: string;
  unitPrice?: number;
}

export interface CreateWarehouseRequest {
  name: string;
  address?: string;
}

export interface AddStockRequest {
  itemId: string;
  quantity: number;
  binLocation?: string;
}

export interface RemoveStockRequest {
  itemId: string;
  quantity: number;
}

export interface WarehouseAdjustStockRequest {
  itemId: string;
  newQuantity: number;
}

export interface CreateMarketRequest {
  name: string;
  location?: string;
  marketType: MarketType;
  description?: string;
}

export interface UpdateMarketRequest {
  name?: string;
  location?: string;
  description?: string;
}

export interface StockOperationRequest {
  itemId: string;
  quantity: number;
}

export interface CreateTransferRequest {
  itemId: string;
  quantity: number;
  sourceType: LocationType;
  sourceId: string;
  destinationType: LocationType;
  destinationId: string;
}

// Aliases kept for component compatibility
export type RegisterItemRequest = CreateItemRequest;
