import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IPrice } from '../price.model';

@Component({
  selector: 'jhi-price-detail',
  templateUrl: './price-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class PriceDetailComponent {
  price = input<IPrice | null>(null);

  previousState(): void {
    window.history.back();
  }
}
