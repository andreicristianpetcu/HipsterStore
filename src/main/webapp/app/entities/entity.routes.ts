import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'product',
    data: { pageTitle: 'Products' },
    loadChildren: () => import('./product/product.routes'),
  },
  {
    path: 'price',
    data: { pageTitle: 'Prices' },
    loadChildren: () => import('./price/price.routes'),
  },
  {
    path: 'priced-product',
    data: { pageTitle: 'PricedProducts' },
    loadChildren: () => import('./priced-product/priced-product.routes'),
  },
  {
    path: 'order-item',
    data: { pageTitle: 'OrderItems' },
    loadChildren: () => import('./order-item/order-item.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
