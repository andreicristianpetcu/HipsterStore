import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../discount.test-samples';

import { DiscountFormService } from './discount-form.service';

describe('Discount Form Service', () => {
  let service: DiscountFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DiscountFormService);
  });

  describe('Service methods', () => {
    describe('createDiscountFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDiscountFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            discountCode: expect.any(Object),
            discountType: expect.any(Object),
            used: expect.any(Object),
          }),
        );
      });

      it('passing IDiscount should create a new form with FormGroup', () => {
        const formGroup = service.createDiscountFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            discountCode: expect.any(Object),
            discountType: expect.any(Object),
            used: expect.any(Object),
          }),
        );
      });
    });

    describe('getDiscount', () => {
      it('should return NewDiscount for default Discount initial value', () => {
        const formGroup = service.createDiscountFormGroup(sampleWithNewData);

        const discount = service.getDiscount(formGroup) as any;

        expect(discount).toMatchObject(sampleWithNewData);
      });

      it('should return NewDiscount for empty Discount initial value', () => {
        const formGroup = service.createDiscountFormGroup();

        const discount = service.getDiscount(formGroup) as any;

        expect(discount).toMatchObject({});
      });

      it('should return IDiscount', () => {
        const formGroup = service.createDiscountFormGroup(sampleWithRequiredData);

        const discount = service.getDiscount(formGroup) as any;

        expect(discount).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDiscount should not enable id FormControl', () => {
        const formGroup = service.createDiscountFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDiscount should disable id FormControl', () => {
        const formGroup = service.createDiscountFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
