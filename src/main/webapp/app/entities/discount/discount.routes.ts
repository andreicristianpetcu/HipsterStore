import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DiscountResolve from './route/discount-routing-resolve.service';

const discountRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/discount.component').then(m => m.DiscountComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/discount-detail.component').then(m => m.DiscountDetailComponent),
    resolve: {
      discount: DiscountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/discount-update.component').then(m => m.DiscountUpdateComponent),
    resolve: {
      discount: DiscountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/discount-update.component').then(m => m.DiscountUpdateComponent),
    resolve: {
      discount: DiscountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default discountRoute;
