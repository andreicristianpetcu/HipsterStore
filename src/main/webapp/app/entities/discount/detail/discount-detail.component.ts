import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IDiscount } from '../discount.model';

@Component({
  selector: 'jhi-discount-detail',
  templateUrl: './discount-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class DiscountDetailComponent {
  discount = input<IDiscount | null>(null);

  previousState(): void {
    window.history.back();
  }
}
