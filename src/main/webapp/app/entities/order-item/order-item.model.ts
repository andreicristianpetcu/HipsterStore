import { IProduct } from 'app/entities/product/product.model';
import { IPrice } from 'app/entities/price/price.model';
import { IOrder } from 'app/entities/order/order.model';

export interface IOrderItem {
  id: number;
  quantity?: number | null;
  product?: IProduct | null;
  price?: IPrice | null;
  order?: IOrder | null;
}

export type NewOrderItem = Omit<IOrderItem, 'id'> & { id: null };
