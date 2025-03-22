import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPrice } from '../price.model';
import { PriceService } from '../service/price.service';

@Component({
  templateUrl: './price-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PriceDeleteDialogComponent {
  price?: IPrice;

  protected priceService = inject(PriceService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.priceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
