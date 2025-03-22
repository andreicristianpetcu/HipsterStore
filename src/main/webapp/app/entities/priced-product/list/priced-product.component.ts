import { Component, NgZone, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { IPricedProduct } from '../priced-product.model';
import { EntityArrayResponseType, PricedProductService } from '../service/priced-product.service';
import { PricedProductDeleteDialogComponent } from '../delete/priced-product-delete-dialog.component';

@Component({
  selector: 'jhi-priced-product',
  templateUrl: './priced-product.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, FormatMediumDatetimePipe],
})
export class PricedProductComponent implements OnInit {
  subscription: Subscription | null = null;
  pricedProducts = signal<IPricedProduct[]>([]);
  isLoading = false;

  sortState = sortStateSignal({});

  public readonly router = inject(Router);
  protected readonly pricedProductService = inject(PricedProductService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IPricedProduct): number => this.pricedProductService.getPricedProductIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.pricedProducts().length === 0) {
            this.load();
          } else {
            this.pricedProducts.set(this.refineData(this.pricedProducts()));
          }
        }),
      )
      .subscribe();
  }

  delete(pricedProduct: IPricedProduct): void {
    const modalRef = this.modalService.open(PricedProductDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pricedProduct = pricedProduct;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.pricedProducts.set(this.refineData(dataFromBody));
  }

  protected refineData(data: IPricedProduct[]): IPricedProduct[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IPricedProduct[] | null): IPricedProduct[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.pricedProductService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }
}
