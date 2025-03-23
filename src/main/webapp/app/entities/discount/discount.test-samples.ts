import { IDiscount, NewDiscount } from './discount.model';

export const sampleWithRequiredData: IDiscount = {
  id: 29363,
};

export const sampleWithPartialData: IDiscount = {
  id: 3137,
};

export const sampleWithFullData: IDiscount = {
  id: 4714,
  discountCode: 'd57028ee-6829-420c-a013-cd23ca0ddd3b',
  discountType: 'PERCENTAGE',
  used: false,
};

export const sampleWithNewData: NewDiscount = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
