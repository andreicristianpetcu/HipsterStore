import { IProduct, NewProduct } from './product.model';

export const sampleWithRequiredData: IProduct = {
  id: 11737,
};

export const sampleWithPartialData: IProduct = {
  id: 22728,
  description: 'sore ack unsteady',
};

export const sampleWithFullData: IProduct = {
  id: 4403,
  name: 'cafe',
  description: 'along amongst',
};

export const sampleWithNewData: NewProduct = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
