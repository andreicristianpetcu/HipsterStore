import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPricedProduct } from '../priced-product.model';
import { PricedProductService } from '../service/priced-product.service';

const pricedProductResolve = (route: ActivatedRouteSnapshot): Observable<null | IPricedProduct> => {
  const id = route.params.id;
  if (id) {
    return inject(PricedProductService)
      .find(id)
      .pipe(
        mergeMap((pricedProduct: HttpResponse<IPricedProduct>) => {
          if (pricedProduct.body) {
            return of(pricedProduct.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default pricedProductResolve;
