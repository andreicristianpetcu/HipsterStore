import { IOrderItem, NewOrderItem } from './order-item.model';

export const sampleWithRequiredData: IOrderItem = {
  id: 16549,
};

export const sampleWithPartialData: IOrderItem = {
  id: 27299,
};

export const sampleWithFullData: IOrderItem = {
  id: 6728,
  quantity: 17449,
};

export const sampleWithNewData: NewOrderItem = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
