import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDiscount } from '../discount.model';
import { DiscountService } from '../service/discount.service';

const discountResolve = (route: ActivatedRouteSnapshot): Observable<null | IDiscount> => {
  const id = route.params.id;
  if (id) {
    return inject(DiscountService)
      .find(id)
      .pipe(
        mergeMap((discount: HttpResponse<IDiscount>) => {
          if (discount.body) {
            return of(discount.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default discountResolve;
