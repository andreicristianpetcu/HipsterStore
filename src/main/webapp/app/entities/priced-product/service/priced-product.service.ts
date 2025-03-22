import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPricedProduct, NewPricedProduct } from '../priced-product.model';

export type PartialUpdatePricedProduct = Partial<IPricedProduct> & Pick<IPricedProduct, 'id'>;

type RestOf<T extends IPricedProduct | NewPricedProduct> = Omit<T, 'updatedDate'> & {
  updatedDate?: string | null;
};

export type RestPricedProduct = RestOf<IPricedProduct>;

export type NewRestPricedProduct = RestOf<NewPricedProduct>;

export type PartialUpdateRestPricedProduct = RestOf<PartialUpdatePricedProduct>;

export type EntityResponseType = HttpResponse<IPricedProduct>;
export type EntityArrayResponseType = HttpResponse<IPricedProduct[]>;

@Injectable({ providedIn: 'root' })
export class PricedProductService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/priced-products');

  create(pricedProduct: NewPricedProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pricedProduct);
    return this.http
      .post<RestPricedProduct>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(pricedProduct: IPricedProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pricedProduct);
    return this.http
      .put<RestPricedProduct>(`${this.resourceUrl}/${this.getPricedProductIdentifier(pricedProduct)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(pricedProduct: PartialUpdatePricedProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pricedProduct);
    return this.http
      .patch<RestPricedProduct>(`${this.resourceUrl}/${this.getPricedProductIdentifier(pricedProduct)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPricedProduct>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPricedProduct[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPricedProductIdentifier(pricedProduct: Pick<IPricedProduct, 'id'>): number {
    return pricedProduct.id;
  }

  comparePricedProduct(o1: Pick<IPricedProduct, 'id'> | null, o2: Pick<IPricedProduct, 'id'> | null): boolean {
    return o1 && o2 ? this.getPricedProductIdentifier(o1) === this.getPricedProductIdentifier(o2) : o1 === o2;
  }

  addPricedProductToCollectionIfMissing<Type extends Pick<IPricedProduct, 'id'>>(
    pricedProductCollection: Type[],
    ...pricedProductsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const pricedProducts: Type[] = pricedProductsToCheck.filter(isPresent);
    if (pricedProducts.length > 0) {
      const pricedProductCollectionIdentifiers = pricedProductCollection.map(pricedProductItem =>
        this.getPricedProductIdentifier(pricedProductItem),
      );
      const pricedProductsToAdd = pricedProducts.filter(pricedProductItem => {
        const pricedProductIdentifier = this.getPricedProductIdentifier(pricedProductItem);
        if (pricedProductCollectionIdentifiers.includes(pricedProductIdentifier)) {
          return false;
        }
        pricedProductCollectionIdentifiers.push(pricedProductIdentifier);
        return true;
      });
      return [...pricedProductsToAdd, ...pricedProductCollection];
    }
    return pricedProductCollection;
  }

  protected convertDateFromClient<T extends IPricedProduct | NewPricedProduct | PartialUpdatePricedProduct>(pricedProduct: T): RestOf<T> {
    return {
      ...pricedProduct,
      updatedDate: pricedProduct.updatedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPricedProduct: RestPricedProduct): IPricedProduct {
    return {
      ...restPricedProduct,
      updatedDate: restPricedProduct.updatedDate ? dayjs(restPricedProduct.updatedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPricedProduct>): HttpResponse<IPricedProduct> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPricedProduct[]>): HttpResponse<IPricedProduct[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
