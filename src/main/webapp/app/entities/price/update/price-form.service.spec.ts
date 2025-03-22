import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../price.test-samples';

import { PriceFormService } from './price-form.service';

describe('Price Form Service', () => {
  let service: PriceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PriceFormService);
  });

  describe('Service methods', () => {
    describe('createPriceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPriceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            value: expect.any(Object),
          }),
        );
      });

      it('passing IPrice should create a new form with FormGroup', () => {
        const formGroup = service.createPriceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            value: expect.any(Object),
          }),
        );
      });
    });

    describe('getPrice', () => {
      it('should return NewPrice for default Price initial value', () => {
        const formGroup = service.createPriceFormGroup(sampleWithNewData);

        const price = service.getPrice(formGroup) as any;

        expect(price).toMatchObject(sampleWithNewData);
      });

      it('should return NewPrice for empty Price initial value', () => {
        const formGroup = service.createPriceFormGroup();

        const price = service.getPrice(formGroup) as any;

        expect(price).toMatchObject({});
      });

      it('should return IPrice', () => {
        const formGroup = service.createPriceFormGroup(sampleWithRequiredData);

        const price = service.getPrice(formGroup) as any;

        expect(price).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPrice should not enable id FormControl', () => {
        const formGroup = service.createPriceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPrice should disable id FormControl', () => {
        const formGroup = service.createPriceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
