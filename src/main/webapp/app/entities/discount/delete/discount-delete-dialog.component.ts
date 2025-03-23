import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDiscount } from '../discount.model';
import { DiscountService } from '../service/discount.service';

@Component({
  templateUrl: './discount-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DiscountDeleteDialogComponent {
  discount?: IDiscount;

  protected discountService = inject(DiscountService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.discountService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
