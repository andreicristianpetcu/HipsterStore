import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IDiscount } from 'app/entities/discount/discount.model';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';

export interface IOrder {
  id: number;
  date?: dayjs.Dayjs | null;
  subtotal?: number | null;
  finalPrice?: number | null;
  status?: keyof typeof OrderStatus | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  discount?: IDiscount | null;
}

export type NewOrder = Omit<IOrder, 'id'> & { id: null };
