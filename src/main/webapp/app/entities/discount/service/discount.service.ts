import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDiscount, NewDiscount } from '../discount.model';

export type PartialUpdateDiscount = Partial<IDiscount> & Pick<IDiscount, 'id'>;

export type EntityResponseType = HttpResponse<IDiscount>;
export type EntityArrayResponseType = HttpResponse<IDiscount[]>;

@Injectable({ providedIn: 'root' })
export class DiscountService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/discounts');

  create(discount: NewDiscount): Observable<EntityResponseType> {
    return this.http.post<IDiscount>(this.resourceUrl, discount, { observe: 'response' });
  }

  update(discount: IDiscount): Observable<EntityResponseType> {
    return this.http.put<IDiscount>(`${this.resourceUrl}/${this.getDiscountIdentifier(discount)}`, discount, { observe: 'response' });
  }

  partialUpdate(discount: PartialUpdateDiscount): Observable<EntityResponseType> {
    return this.http.patch<IDiscount>(`${this.resourceUrl}/${this.getDiscountIdentifier(discount)}`, discount, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDiscount>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDiscount[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDiscountIdentifier(discount: Pick<IDiscount, 'id'>): number {
    return discount.id;
  }

  compareDiscount(o1: Pick<IDiscount, 'id'> | null, o2: Pick<IDiscount, 'id'> | null): boolean {
    return o1 && o2 ? this.getDiscountIdentifier(o1) === this.getDiscountIdentifier(o2) : o1 === o2;
  }

  addDiscountToCollectionIfMissing<Type extends Pick<IDiscount, 'id'>>(
    discountCollection: Type[],
    ...discountsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const discounts: Type[] = discountsToCheck.filter(isPresent);
    if (discounts.length > 0) {
      const discountCollectionIdentifiers = discountCollection.map(discountItem => this.getDiscountIdentifier(discountItem));
      const discountsToAdd = discounts.filter(discountItem => {
        const discountIdentifier = this.getDiscountIdentifier(discountItem);
        if (discountCollectionIdentifiers.includes(discountIdentifier)) {
          return false;
        }
        discountCollectionIdentifiers.push(discountIdentifier);
        return true;
      });
      return [...discountsToAdd, ...discountCollection];
    }
    return discountCollection;
  }
}
