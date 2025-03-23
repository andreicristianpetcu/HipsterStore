import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DiscountDetailComponent } from './discount-detail.component';

describe('Discount Management Detail Component', () => {
  let comp: DiscountDetailComponent;
  let fixture: ComponentFixture<DiscountDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiscountDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./discount-detail.component').then(m => m.DiscountDetailComponent),
              resolve: { discount: () => of({ id: 31923 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DiscountDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DiscountDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load discount on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DiscountDetailComponent);

      // THEN
      expect(instance.discount()).toEqual(expect.objectContaining({ id: 31923 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
