import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PriceResolve from './route/price-routing-resolve.service';

const priceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/price.component').then(m => m.PriceComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/price-detail.component').then(m => m.PriceDetailComponent),
    resolve: {
      price: PriceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/price-update.component').then(m => m.PriceUpdateComponent),
    resolve: {
      price: PriceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/price-update.component').then(m => m.PriceUpdateComponent),
    resolve: {
      price: PriceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default priceRoute;
