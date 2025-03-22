import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPricedProduct } from '../priced-product.model';
import { PricedProductService } from '../service/priced-product.service';

@Component({
  templateUrl: './priced-product-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PricedProductDeleteDialogComponent {
  pricedProduct?: IPricedProduct;

  protected pricedProductService = inject(PricedProductService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.pricedProductService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
