import { IProduct } from 'app/entities/product/product.model';
import { IPrice } from 'app/entities/price/price.model';

export interface IOrderItem {
  id: number;
  quantity?: number | null;
  product?: IProduct | null;
  price?: IPrice | null;
}

export type NewOrderItem = Omit<IOrderItem, 'id'> & { id: null };
