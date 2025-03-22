import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PricedProductDetailComponent } from './priced-product-detail.component';

describe('PricedProduct Management Detail Component', () => {
  let comp: PricedProductDetailComponent;
  let fixture: ComponentFixture<PricedProductDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PricedProductDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./priced-product-detail.component').then(m => m.PricedProductDetailComponent),
              resolve: { pricedProduct: () => of({ id: 1737 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PricedProductDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PricedProductDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load pricedProduct on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PricedProductDetailComponent);

      // THEN
      expect(instance.pricedProduct()).toEqual(expect.objectContaining({ id: 1737 }));
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
