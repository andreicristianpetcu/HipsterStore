import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPrice } from '../price.model';
import { PriceService } from '../service/price.service';

const priceResolve = (route: ActivatedRouteSnapshot): Observable<null | IPrice> => {
  const id = route.params.id;
  if (id) {
    return inject(PriceService)
      .find(id)
      .pipe(
        mergeMap((price: HttpResponse<IPrice>) => {
          if (price.body) {
            return of(price.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default priceResolve;
