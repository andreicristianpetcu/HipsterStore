import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPrice, NewPrice } from '../price.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPrice for edit and NewPriceFormGroupInput for create.
 */
type PriceFormGroupInput = IPrice | PartialWithRequiredKeyOf<NewPrice>;

type PriceFormDefaults = Pick<NewPrice, 'id'>;

type PriceFormGroupContent = {
  id: FormControl<IPrice['id'] | NewPrice['id']>;
  value: FormControl<IPrice['value']>;
};

export type PriceFormGroup = FormGroup<PriceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PriceFormService {
  createPriceFormGroup(price: PriceFormGroupInput = { id: null }): PriceFormGroup {
    const priceRawValue = {
      ...this.getFormDefaults(),
      ...price,
    };
    return new FormGroup<PriceFormGroupContent>({
      id: new FormControl(
        { value: priceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      value: new FormControl(priceRawValue.value),
    });
  }

  getPrice(form: PriceFormGroup): IPrice | NewPrice {
    return form.getRawValue() as IPrice | NewPrice;
  }

  resetForm(form: PriceFormGroup, price: PriceFormGroupInput): void {
    const priceRawValue = { ...this.getFormDefaults(), ...price };
    form.reset(
      {
        ...priceRawValue,
        id: { value: priceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PriceFormDefaults {
    return {
      id: null,
    };
  }
}
