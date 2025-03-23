import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { DiscountService } from '../service/discount.service';
import { IDiscount } from '../discount.model';
import { DiscountFormService } from './discount-form.service';

import { DiscountUpdateComponent } from './discount-update.component';

describe('Discount Management Update Component', () => {
  let comp: DiscountUpdateComponent;
  let fixture: ComponentFixture<DiscountUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let discountFormService: DiscountFormService;
  let discountService: DiscountService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DiscountUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(DiscountUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DiscountUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    discountFormService = TestBed.inject(DiscountFormService);
    discountService = TestBed.inject(DiscountService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const discount: IDiscount = { id: 30740 };

      activatedRoute.data = of({ discount });
      comp.ngOnInit();

      expect(comp.discount).toEqual(discount);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiscount>>();
      const discount = { id: 31923 };
      jest.spyOn(discountFormService, 'getDiscount').mockReturnValue(discount);
      jest.spyOn(discountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ discount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: discount }));
      saveSubject.complete();

      // THEN
      expect(discountFormService.getDiscount).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(discountService.update).toHaveBeenCalledWith(expect.objectContaining(discount));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiscount>>();
      const discount = { id: 31923 };
      jest.spyOn(discountFormService, 'getDiscount').mockReturnValue({ id: null });
      jest.spyOn(discountService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ discount: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: discount }));
      saveSubject.complete();

      // THEN
      expect(discountFormService.getDiscount).toHaveBeenCalled();
      expect(discountService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiscount>>();
      const discount = { id: 31923 };
      jest.spyOn(discountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ discount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(discountService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
