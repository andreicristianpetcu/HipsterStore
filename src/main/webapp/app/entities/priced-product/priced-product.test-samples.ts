import dayjs from 'dayjs/esm';

import { IPricedProduct, NewPricedProduct } from './priced-product.model';

export const sampleWithRequiredData: IPricedProduct = {
  id: 1465,
};

export const sampleWithPartialData: IPricedProduct = {
  id: 1454,
  updatedDate: dayjs('2025-03-22T11:58'),
};

export const sampleWithFullData: IPricedProduct = {
  id: 31483,
  active: true,
  updatedDate: dayjs('2025-03-22T13:44'),
};

export const sampleWithNewData: NewPricedProduct = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
