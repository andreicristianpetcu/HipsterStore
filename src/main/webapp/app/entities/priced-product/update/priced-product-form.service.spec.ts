import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../priced-product.test-samples';

import { PricedProductFormService } from './priced-product-form.service';

describe('PricedProduct Form Service', () => {
  let service: PricedProductFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PricedProductFormService);
  });

  describe('Service methods', () => {
    describe('createPricedProductFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPricedProductFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            active: expect.any(Object),
            updatedDate: expect.any(Object),
            product: expect.any(Object),
            price: expect.any(Object),
          }),
        );
      });

      it('passing IPricedProduct should create a new form with FormGroup', () => {
        const formGroup = service.createPricedProductFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            active: expect.any(Object),
            updatedDate: expect.any(Object),
            product: expect.any(Object),
            price: expect.any(Object),
          }),
        );
      });
    });

    describe('getPricedProduct', () => {
      it('should return NewPricedProduct for default PricedProduct initial value', () => {
        const formGroup = service.createPricedProductFormGroup(sampleWithNewData);

        const pricedProduct = service.getPricedProduct(formGroup) as any;

        expect(pricedProduct).toMatchObject(sampleWithNewData);
      });

      it('should return NewPricedProduct for empty PricedProduct initial value', () => {
        const formGroup = service.createPricedProductFormGroup();

        const pricedProduct = service.getPricedProduct(formGroup) as any;

        expect(pricedProduct).toMatchObject({});
      });

      it('should return IPricedProduct', () => {
        const formGroup = service.createPricedProductFormGroup(sampleWithRequiredData);

        const pricedProduct = service.getPricedProduct(formGroup) as any;

        expect(pricedProduct).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPricedProduct should not enable id FormControl', () => {
        const formGroup = service.createPricedProductFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPricedProduct should disable id FormControl', () => {
        const formGroup = service.createPricedProductFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
