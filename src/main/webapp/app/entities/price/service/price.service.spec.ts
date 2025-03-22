import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IPrice } from '../price.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../price.test-samples';

import { PriceService } from './price.service';

const requireRestSample: IPrice = {
  ...sampleWithRequiredData,
};

describe('Price Service', () => {
  let service: PriceService;
  let httpMock: HttpTestingController;
  let expectedResult: IPrice | IPrice[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PriceService);
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

    it('should create a Price', () => {
      const price = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(price).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Price', () => {
      const price = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(price).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Price', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Price', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Price', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPriceToCollectionIfMissing', () => {
      it('should add a Price to an empty array', () => {
        const price: IPrice = sampleWithRequiredData;
        expectedResult = service.addPriceToCollectionIfMissing([], price);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(price);
      });

      it('should not add a Price to an array that contains it', () => {
        const price: IPrice = sampleWithRequiredData;
        const priceCollection: IPrice[] = [
          {
            ...price,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPriceToCollectionIfMissing(priceCollection, price);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Price to an array that doesn't contain it", () => {
        const price: IPrice = sampleWithRequiredData;
        const priceCollection: IPrice[] = [sampleWithPartialData];
        expectedResult = service.addPriceToCollectionIfMissing(priceCollection, price);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(price);
      });

      it('should add only unique Price to an array', () => {
        const priceArray: IPrice[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const priceCollection: IPrice[] = [sampleWithRequiredData];
        expectedResult = service.addPriceToCollectionIfMissing(priceCollection, ...priceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const price: IPrice = sampleWithRequiredData;
        const price2: IPrice = sampleWithPartialData;
        expectedResult = service.addPriceToCollectionIfMissing([], price, price2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(price);
        expect(expectedResult).toContain(price2);
      });

      it('should accept null and undefined values', () => {
        const price: IPrice = sampleWithRequiredData;
        expectedResult = service.addPriceToCollectionIfMissing([], null, price, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(price);
      });

      it('should return initial array if no Price is added', () => {
        const priceCollection: IPrice[] = [sampleWithRequiredData];
        expectedResult = service.addPriceToCollectionIfMissing(priceCollection, undefined, null);
        expect(expectedResult).toEqual(priceCollection);
      });
    });

    describe('comparePrice', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePrice(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 10065 };
        const entity2 = null;

        const compareResult1 = service.comparePrice(entity1, entity2);
        const compareResult2 = service.comparePrice(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 10065 };
        const entity2 = { id: 509 };

        const compareResult1 = service.comparePrice(entity1, entity2);
        const compareResult2 = service.comparePrice(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 10065 };
        const entity2 = { id: 10065 };

        const compareResult1 = service.comparePrice(entity1, entity2);
        const compareResult2 = service.comparePrice(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
