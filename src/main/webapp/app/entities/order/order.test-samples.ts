import dayjs from 'dayjs/esm';

import { IOrder, NewOrder } from './order.model';

export const sampleWithRequiredData: IOrder = {
  id: 26110,
};

export const sampleWithPartialData: IOrder = {
  id: 21314,
  finalPrice: 15699.65,
  status: 'NEW',
};

export const sampleWithFullData: IOrder = {
  id: 27813,
  date: dayjs('2025-03-22T23:09'),
  subtotal: 7491.96,
  finalPrice: 3767.98,
  status: 'PAID',
};

export const sampleWithNewData: NewOrder = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
