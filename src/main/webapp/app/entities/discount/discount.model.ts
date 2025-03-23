import { DiscountType } from 'app/entities/enumerations/discount-type.model';

export interface IDiscount {
  id: number;
  discountCode?: string | null;
  discountType?: keyof typeof DiscountType | null;
  used?: boolean | null;
  amount?: number | null;
}

export type NewDiscount = Omit<IDiscount, 'id'> & { id: null };
