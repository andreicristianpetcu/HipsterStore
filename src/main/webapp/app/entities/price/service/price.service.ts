import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPrice, NewPrice } from '../price.model';

export type PartialUpdatePrice = Partial<IPrice> & Pick<IPrice, 'id'>;

export type EntityResponseType = HttpResponse<IPrice>;
export type EntityArrayResponseType = HttpResponse<IPrice[]>;

@Injectable({ providedIn: 'root' })
export class PriceService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/prices');

  create(price: NewPrice): Observable<EntityResponseType> {
    return this.http.post<IPrice>(this.resourceUrl, price, { observe: 'response' });
  }

  update(price: IPrice): Observable<EntityResponseType> {
    return this.http.put<IPrice>(`${this.resourceUrl}/${this.getPriceIdentifier(price)}`, price, { observe: 'response' });
  }

  partialUpdate(price: PartialUpdatePrice): Observable<EntityResponseType> {
    return this.http.patch<IPrice>(`${this.resourceUrl}/${this.getPriceIdentifier(price)}`, price, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPrice>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPrice[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPriceIdentifier(price: Pick<IPrice, 'id'>): number {
    return price.id;
  }

  comparePrice(o1: Pick<IPrice, 'id'> | null, o2: Pick<IPrice, 'id'> | null): boolean {
    return o1 && o2 ? this.getPriceIdentifier(o1) === this.getPriceIdentifier(o2) : o1 === o2;
  }

  addPriceToCollectionIfMissing<Type extends Pick<IPrice, 'id'>>(
    priceCollection: Type[],
    ...pricesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const prices: Type[] = pricesToCheck.filter(isPresent);
    if (prices.length > 0) {
      const priceCollectionIdentifiers = priceCollection.map(priceItem => this.getPriceIdentifier(priceItem));
      const pricesToAdd = prices.filter(priceItem => {
        const priceIdentifier = this.getPriceIdentifier(priceItem);
        if (priceCollectionIdentifiers.includes(priceIdentifier)) {
          return false;
        }
        priceCollectionIdentifiers.push(priceIdentifier);
        return true;
      });
      return [...pricesToAdd, ...priceCollection];
    }
    return priceCollection;
  }
}
