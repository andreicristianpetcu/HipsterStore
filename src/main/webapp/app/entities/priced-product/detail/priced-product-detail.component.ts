import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IPricedProduct } from '../priced-product.model';

@Component({
  selector: 'jhi-priced-product-detail',
  templateUrl: './priced-product-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class PricedProductDetailComponent {
  pricedProduct = input<IPricedProduct | null>(null);

  previousState(): void {
    window.history.back();
  }
}
