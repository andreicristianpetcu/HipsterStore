import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IDiscount } from '../discount.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../discount.test-samples';

import { DiscountService } from './discount.service';

const requireRestSample: IDiscount = {
  ...sampleWithRequiredData,
};

describe('Discount Service', () => {
  let service: DiscountService;
  let httpMock: HttpTestingController;
  let expectedResult: IDiscount | IDiscount[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DiscountService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Discount', () => {
      const discount = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(discount).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Discount', () => {
      const discount = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(discount).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Discount', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Discount', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Discount', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDiscountToCollectionIfMissing', () => {
      it('should add a Discount to an empty array', () => {
        const discount: IDiscount = sampleWithRequiredData;
        expectedResult = service.addDiscountToCollectionIfMissing([], discount);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(discount);
      });

      it('should not add a Discount to an array that contains it', () => {
        const discount: IDiscount = sampleWithRequiredData;
        const discountCollection: IDiscount[] = [
          {
            ...discount,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDiscountToCollectionIfMissing(discountCollection, discount);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Discount to an array that doesn't contain it", () => {
        const discount: IDiscount = sampleWithRequiredData;
        const discountCollection: IDiscount[] = [sampleWithPartialData];
        expectedResult = service.addDiscountToCollectionIfMissing(discountCollection, discount);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(discount);
      });

      it('should add only unique Discount to an array', () => {
        const discountArray: IDiscount[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const discountCollection: IDiscount[] = [sampleWithRequiredData];
        expectedResult = service.addDiscountToCollectionIfMissing(discountCollection, ...discountArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const discount: IDiscount = sampleWithRequiredData;
        const discount2: IDiscount = sampleWithPartialData;
        expectedResult = service.addDiscountToCollectionIfMissing([], discount, discount2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(discount);
        expect(expectedResult).toContain(discount2);
      });

      it('should accept null and undefined values', () => {
        const discount: IDiscount = sampleWithRequiredData;
        expectedResult = service.addDiscountToCollectionIfMissing([], null, discount, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(discount);
      });

      it('should return initial array if no Discount is added', () => {
        const discountCollection: IDiscount[] = [sampleWithRequiredData];
        expectedResult = service.addDiscountToCollectionIfMissing(discountCollection, undefined, null);
        expect(expectedResult).toEqual(discountCollection);
      });
    });

    describe('compareDiscount', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDiscount(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 31923 };
        const entity2 = null;

        const compareResult1 = service.compareDiscount(entity1, entity2);
        const compareResult2 = service.compareDiscount(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 31923 };
        const entity2 = { id: 30740 };

        const compareResult1 = service.compareDiscount(entity1, entity2);
        const compareResult2 = service.compareDiscount(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 31923 };
        const entity2 = { id: 31923 };

        const compareResult1 = service.compareDiscount(entity1, entity2);
        const compareResult2 = service.compareDiscount(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
