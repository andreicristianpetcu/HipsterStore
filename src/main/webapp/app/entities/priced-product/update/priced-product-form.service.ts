import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPricedProduct, NewPricedProduct } from '../priced-product.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPricedProduct for edit and NewPricedProductFormGroupInput for create.
 */
type PricedProductFormGroupInput = IPricedProduct | PartialWithRequiredKeyOf<NewPricedProduct>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPricedProduct | NewPricedProduct> = Omit<T, 'updatedDate'> & {
  updatedDate?: string | null;
};

type PricedProductFormRawValue = FormValueOf<IPricedProduct>;

type NewPricedProductFormRawValue = FormValueOf<NewPricedProduct>;

type PricedProductFormDefaults = Pick<NewPricedProduct, 'id' | 'active' | 'updatedDate'>;

type PricedProductFormGroupContent = {
  id: FormControl<PricedProductFormRawValue['id'] | NewPricedProduct['id']>;
  active: FormControl<PricedProductFormRawValue['active']>;
  updatedDate: FormControl<PricedProductFormRawValue['updatedDate']>;
  product: FormControl<PricedProductFormRawValue['product']>;
  price: FormControl<PricedProductFormRawValue['price']>;
};

export type PricedProductFormGroup = FormGroup<PricedProductFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PricedProductFormService {
  createPricedProductFormGroup(pricedProduct: PricedProductFormGroupInput = { id: null }): PricedProductFormGroup {
    const pricedProductRawValue = this.convertPricedProductToPricedProductRawValue({
      ...this.getFormDefaults(),
      ...pricedProduct,
    });
    return new FormGroup<PricedProductFormGroupContent>({
      id: new FormControl(
        { value: pricedProductRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      active: new FormControl(pricedProductRawValue.active),
      updatedDate: new FormControl(pricedProductRawValue.updatedDate),
      product: new FormControl(pricedProductRawValue.product),
      price: new FormControl(pricedProductRawValue.price),
    });
  }

  getPricedProduct(form: PricedProductFormGroup): IPricedProduct | NewPricedProduct {
    return this.convertPricedProductRawValueToPricedProduct(form.getRawValue() as PricedProductFormRawValue | NewPricedProductFormRawValue);
  }

  resetForm(form: PricedProductFormGroup, pricedProduct: PricedProductFormGroupInput): void {
    const pricedProductRawValue = this.convertPricedProductToPricedProductRawValue({ ...this.getFormDefaults(), ...pricedProduct });
    form.reset(
      {
        ...pricedProductRawValue,
        id: { value: pricedProductRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PricedProductFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      updatedDate: currentTime,
    };
  }

  private convertPricedProductRawValueToPricedProduct(
    rawPricedProduct: PricedProductFormRawValue | NewPricedProductFormRawValue,
  ): IPricedProduct | NewPricedProduct {
    return {
      ...rawPricedProduct,
      updatedDate: dayjs(rawPricedProduct.updatedDate, DATE_TIME_FORMAT),
    };
  }

  private convertPricedProductToPricedProductRawValue(
    pricedProduct: IPricedProduct | (Partial<NewPricedProduct> & PricedProductFormDefaults),
  ): PricedProductFormRawValue | PartialWithRequiredKeyOf<NewPricedProductFormRawValue> {
    return {
      ...pricedProduct,
      updatedDate: pricedProduct.updatedDate ? pricedProduct.updatedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
