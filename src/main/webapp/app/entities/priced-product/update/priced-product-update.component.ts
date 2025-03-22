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
import { PricedProductService } from '../service/priced-product.service';
import { IPricedProduct } from '../priced-product.model';
import { PricedProductFormGroup, PricedProductFormService } from './priced-product-form.service';

@Component({
  selector: 'jhi-priced-product-update',
  templateUrl: './priced-product-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PricedProductUpdateComponent implements OnInit {
  isSaving = false;
  pricedProduct: IPricedProduct | null = null;

  productsSharedCollection: IProduct[] = [];
  pricesSharedCollection: IPrice[] = [];

  protected pricedProductService = inject(PricedProductService);
  protected pricedProductFormService = inject(PricedProductFormService);
  protected productService = inject(ProductService);
  protected priceService = inject(PriceService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PricedProductFormGroup = this.pricedProductFormService.createPricedProductFormGroup();

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  comparePrice = (o1: IPrice | null, o2: IPrice | null): boolean => this.priceService.comparePrice(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pricedProduct }) => {
      this.pricedProduct = pricedProduct;
      if (pricedProduct) {
        this.updateForm(pricedProduct);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pricedProduct = this.pricedProductFormService.getPricedProduct(this.editForm);
    if (pricedProduct.id !== null) {
      this.subscribeToSaveResponse(this.pricedProductService.update(pricedProduct));
    } else {
      this.subscribeToSaveResponse(this.pricedProductService.create(pricedProduct));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPricedProduct>>): void {
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

  protected updateForm(pricedProduct: IPricedProduct): void {
    this.pricedProduct = pricedProduct;
    this.pricedProductFormService.resetForm(this.editForm, pricedProduct);

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      pricedProduct.product,
    );
    this.pricesSharedCollection = this.priceService.addPriceToCollectionIfMissing<IPrice>(this.pricesSharedCollection, pricedProduct.price);
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing<IProduct>(products, this.pricedProduct?.product)),
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));

    this.priceService
      .query()
      .pipe(map((res: HttpResponse<IPrice[]>) => res.body ?? []))
      .pipe(map((prices: IPrice[]) => this.priceService.addPriceToCollectionIfMissing<IPrice>(prices, this.pricedProduct?.price)))
      .subscribe((prices: IPrice[]) => (this.pricesSharedCollection = prices));
  }
}
