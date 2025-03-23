import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { DiscountType } from 'app/entities/enumerations/discount-type.model';
import { IDiscount } from '../discount.model';
import { DiscountService } from '../service/discount.service';
import { DiscountFormGroup, DiscountFormService } from './discount-form.service';

@Component({
  selector: 'jhi-discount-update',
  templateUrl: './discount-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DiscountUpdateComponent implements OnInit {
  isSaving = false;
  discount: IDiscount | null = null;
  discountTypeValues = Object.keys(DiscountType);

  protected discountService = inject(DiscountService);
  protected discountFormService = inject(DiscountFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DiscountFormGroup = this.discountFormService.createDiscountFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ discount }) => {
      this.discount = discount;
      if (discount) {
        this.updateForm(discount);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const discount = this.discountFormService.getDiscount(this.editForm);
    if (discount.id !== null) {
      this.subscribeToSaveResponse(this.discountService.update(discount));
    } else {
      this.subscribeToSaveResponse(this.discountService.create(discount));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDiscount>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(discount: IDiscount): void {
    this.discount = discount;
    this.discountFormService.resetForm(this.editForm, discount);
  }
}
