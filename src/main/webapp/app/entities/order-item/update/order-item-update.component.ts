import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { IPrice } from 'app/entities/price/price.model';
import { PriceService } from 'app/entities/price/service/price.service';
import { OrderItemService } from '../service/order-item.service';
import { IOrderItem } from '../order-item.model';
import { OrderItemFormGroup, OrderItemFormService } from './order-item-form.service';

@Component({
  selector: 'jhi-order-item-update',
  templateUrl: './order-item-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class OrderItemUpdateComponent implements OnInit {
  isSaving = false;
  orderItem: IOrderItem | null = null;

  productsSharedCollection: IProduct[] = [];
  pricesSharedCollection: IPrice[] = [];

  protected orderItemService = inject(OrderItemService);
  protected orderItemFormService = inject(OrderItemFormService);
  protected productService = inject(ProductService);
  protected priceService = inject(PriceService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: OrderItemFormGroup = this.orderItemFormService.createOrderItemFormGroup();

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  comparePrice = (o1: IPrice | null, o2: IPrice | null): boolean => this.priceService.comparePrice(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderItem }) => {
      this.orderItem = orderItem;
      if (orderItem) {
        this.updateForm(orderItem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orderItem = this.orderItemFormService.getOrderItem(this.editForm);
    if (orderItem.id !== null) {
      this.subscribeToSaveResponse(this.orderItemService.update(orderItem));
    } else {
      this.subscribeToSaveResponse(this.orderItemService.create(orderItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrderItem>>): void {
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

  protected updateForm(orderItem: IOrderItem): void {
    this.orderItem = orderItem;
    this.orderItemFormService.resetForm(this.editForm, orderItem);

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      orderItem.product,
    );
    this.pricesSharedCollection = this.priceService.addPriceToCollectionIfMissing<IPrice>(this.pricesSharedCollection, orderItem.price);
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing<IProduct>(products, this.orderItem?.product)))
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));

    this.priceService
      .query()
      .pipe(map((res: HttpResponse<IPrice[]>) => res.body ?? []))
      .pipe(map((prices: IPrice[]) => this.priceService.addPriceToCollectionIfMissing<IPrice>(prices, this.orderItem?.price)))
      .subscribe((prices: IPrice[]) => (this.pricesSharedCollection = prices));
  }
}
