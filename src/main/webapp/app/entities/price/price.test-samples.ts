import { IPrice, NewPrice } from './price.model';

export const sampleWithRequiredData: IPrice = {
  id: 28782,
};

export const sampleWithPartialData: IPrice = {
  id: 9675,
  value: 9778.82,
};

export const sampleWithFullData: IPrice = {
  id: 24134,
  value: 18758.27,
};

export const sampleWithNewData: NewPrice = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
