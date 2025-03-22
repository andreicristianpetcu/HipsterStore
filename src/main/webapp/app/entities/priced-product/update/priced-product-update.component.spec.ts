import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { IPrice } from 'app/entities/price/price.model';
import { PriceService } from 'app/entities/price/service/price.service';
import { IPricedProduct } from '../priced-product.model';
import { PricedProductService } from '../service/priced-product.service';
import { PricedProductFormService } from './priced-product-form.service';

import { PricedProductUpdateComponent } from './priced-product-update.component';

describe('PricedProduct Management Update Component', () => {
  let comp: PricedProductUpdateComponent;
  let fixture: ComponentFixture<PricedProductUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let pricedProductFormService: PricedProductFormService;
  let pricedProductService: PricedProductService;
  let productService: ProductService;
  let priceService: PriceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PricedProductUpdateComponent],
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
      .overrideTemplate(PricedProductUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PricedProductUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pricedProductFormService = TestBed.inject(PricedProductFormService);
    pricedProductService = TestBed.inject(PricedProductService);
    productService = TestBed.inject(ProductService);
    priceService = TestBed.inject(PriceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Product query and add missing value', () => {
      const pricedProduct: IPricedProduct = { id: 15268 };
      const product: IProduct = { id: 21536 };
      pricedProduct.product = product;

      const productCollection: IProduct[] = [{ id: 21536 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pricedProduct });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(expect.objectContaining),
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Price query and add missing value', () => {
      const pricedProduct: IPricedProduct = { id: 15268 };
      const price: IPrice = { id: 10065 };
      pricedProduct.price = price;

      const priceCollection: IPrice[] = [{ id: 10065 }];
      jest.spyOn(priceService, 'query').mockReturnValue(of(new HttpResponse({ body: priceCollection })));
      const additionalPrices = [price];
      const expectedCollection: IPrice[] = [...additionalPrices, ...priceCollection];
      jest.spyOn(priceService, 'addPriceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pricedProduct });
      comp.ngOnInit();

      expect(priceService.query).toHaveBeenCalled();
      expect(priceService.addPriceToCollectionIfMissing).toHaveBeenCalledWith(
        priceCollection,
        ...additionalPrices.map(expect.objectContaining),
      );
      expect(comp.pricesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const pricedProduct: IPricedProduct = { id: 15268 };
      const product: IProduct = { id: 21536 };
      pricedProduct.product = product;
      const price: IPrice = { id: 10065 };
      pricedProduct.price = price;

      activatedRoute.data = of({ pricedProduct });
      comp.ngOnInit();

      expect(comp.productsSharedCollection).toContainEqual(product);
      expect(comp.pricesSharedCollection).toContainEqual(price);
      expect(comp.pricedProduct).toEqual(pricedProduct);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPricedProduct>>();
      const pricedProduct = { id: 1737 };
      jest.spyOn(pricedProductFormService, 'getPricedProduct').mockReturnValue(pricedProduct);
      jest.spyOn(pricedProductService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pricedProduct });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pricedProduct }));
      saveSubject.complete();

      // THEN
      expect(pricedProductFormService.getPricedProduct).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(pricedProductService.update).toHaveBeenCalledWith(expect.objectContaining(pricedProduct));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPricedProduct>>();
      const pricedProduct = { id: 1737 };
      jest.spyOn(pricedProductFormService, 'getPricedProduct').mockReturnValue({ id: null });
      jest.spyOn(pricedProductService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pricedProduct: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pricedProduct }));
      saveSubject.complete();

      // THEN
      expect(pricedProductFormService.getPricedProduct).toHaveBeenCalled();
      expect(pricedProductService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPricedProduct>>();
      const pricedProduct = { id: 1737 };
      jest.spyOn(pricedProductService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pricedProduct });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pricedProductService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProduct', () => {
      it('Should forward to productService', () => {
        const entity = { id: 21536 };
        const entity2 = { id: 11926 };
        jest.spyOn(productService, 'compareProduct');
        comp.compareProduct(entity, entity2);
        expect(productService.compareProduct).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePrice', () => {
      it('Should forward to priceService', () => {
        const entity = { id: 10065 };
        const entity2 = { id: 509 };
        jest.spyOn(priceService, 'comparePrice');
        comp.comparePrice(entity, entity2);
        expect(priceService.comparePrice).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
