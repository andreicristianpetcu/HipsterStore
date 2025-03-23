import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IDiscount } from 'app/entities/discount/discount.model';
import { DiscountService } from 'app/entities/discount/service/discount.service';
import { IOrder } from '../order.model';
import { OrderService } from '../service/order.service';
import { OrderFormService } from './order-form.service';

import { OrderUpdateComponent } from './order-update.component';

describe('Order Management Update Component', () => {
  let comp: OrderUpdateComponent;
  let fixture: ComponentFixture<OrderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderFormService: OrderFormService;
  let orderService: OrderService;
  let userService: UserService;
  let discountService: DiscountService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OrderUpdateComponent],
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
      .overrideTemplate(OrderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderFormService = TestBed.inject(OrderFormService);
    orderService = TestBed.inject(OrderService);
    userService = TestBed.inject(UserService);
    discountService = TestBed.inject(DiscountService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const order: IOrder = { id: 4253 };
      const user: IUser = { id: 3944 };
      order.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call discount query and add missing value', () => {
      const order: IOrder = { id: 4253 };
      const discount: IDiscount = { id: 31923 };
      order.discount = discount;

      const discountCollection: IDiscount[] = [{ id: 31923 }];
      jest.spyOn(discountService, 'query').mockReturnValue(of(new HttpResponse({ body: discountCollection })));
      const expectedCollection: IDiscount[] = [discount, ...discountCollection];
      jest.spyOn(discountService, 'addDiscountToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(discountService.query).toHaveBeenCalled();
      expect(discountService.addDiscountToCollectionIfMissing).toHaveBeenCalledWith(discountCollection, discount);
      expect(comp.discountsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const order: IOrder = { id: 4253 };
      const user: IUser = { id: 3944 };
      order.user = user;
      const discount: IDiscount = { id: 31923 };
      order.discount = discount;

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.discountsCollection).toContainEqual(discount);
      expect(comp.order).toEqual(order);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrder>>();
      const order = { id: 14644 };
      jest.spyOn(orderFormService, 'getOrder').mockReturnValue(order);
      jest.spyOn(orderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: order }));
      saveSubject.complete();

      // THEN
      expect(orderFormService.getOrder).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderService.update).toHaveBeenCalledWith(expect.objectContaining(order));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrder>>();
      const order = { id: 14644 };
      jest.spyOn(orderFormService, 'getOrder').mockReturnValue({ id: null });
      jest.spyOn(orderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: order }));
      saveSubject.complete();

      // THEN
      expect(orderFormService.getOrder).toHaveBeenCalled();
      expect(orderService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrder>>();
      const order = { id: 14644 };
      jest.spyOn(orderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareDiscount', () => {
      it('Should forward to discountService', () => {
        const entity = { id: 31923 };
        const entity2 = { id: 30740 };
        jest.spyOn(discountService, 'compareDiscount');
        comp.compareDiscount(entity, entity2);
        expect(discountService.compareDiscount).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
