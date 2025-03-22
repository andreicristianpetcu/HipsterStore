import dayjs from 'dayjs/esm';
import { IProduct } from 'app/entities/product/product.model';
import { IPrice } from 'app/entities/price/price.model';

export interface IPricedProduct {
  id: number;
  active?: boolean | null;
  updatedDate?: dayjs.Dayjs | null;
  product?: IProduct | null;
  price?: IPrice | null;
}

export type NewPricedProduct = Omit<IPricedProduct, 'id'> & { id: null };
