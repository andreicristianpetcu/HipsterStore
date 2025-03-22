import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PricedProductResolve from './route/priced-product-routing-resolve.service';

const pricedProductRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/priced-product.component').then(m => m.PricedProductComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/priced-product-detail.component').then(m => m.PricedProductDetailComponent),
    resolve: {
      pricedProduct: PricedProductResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/priced-product-update.component').then(m => m.PricedProductUpdateComponent),
    resolve: {
      pricedProduct: PricedProductResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/priced-product-update.component').then(m => m.PricedProductUpdateComponent),
    resolve: {
      pricedProduct: PricedProductResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default pricedProductRoute;
